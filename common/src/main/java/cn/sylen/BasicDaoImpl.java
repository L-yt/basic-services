package cn.sylen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.sylen.common.page.Page;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Maps;
import cn.sylen.dao.builder.DeleteSqlBuilder;
import cn.sylen.dao.mapping.FieldMapper;

import cn.sylen.common.util.CollectionUtils;
import cn.sylen.common.util.CommonUtil;
import cn.sylen.common.util.ObjectUtil;
import cn.sylen.common.util.StringUtil;
import cn.sylen.common.util.ThreadLocalUtil;
import cn.sylen.dao.annotation.SqlAnnotationResolver;
import cn.sylen.dao.builder.ExistsQuerySqlBuilder;
import cn.sylen.dao.builder.InsertSqlBuilder;
import cn.sylen.dao.builder.PrimaryQuerySqlBuilder;
import cn.sylen.dao.builder.QuerySqlBuilder;
import cn.sylen.dao.builder.SqlBuilder;
import cn.sylen.dao.builder.SqlUtil;
import cn.sylen.dao.builder.UniqueKeyQuerySqlBuilder;
import cn.sylen.dao.builder.UpdateByQuerySqlBuilder;
import cn.sylen.dao.builder.UpdateByUniqueKeySqlBuilder;
import cn.sylen.dao.mapping.ObjectTableMapper;
import cn.sylen.dao.mapping.TableMappingFactory;
import cn.sylen.dao.query.QueryObj;


@Repository
public class BasicDaoImpl implements BasicDao {
    private Logger logger = LoggerFactory.getLogger(BasicDaoImpl.class);

    @Autowired
    private SqlSession sqlSession;

    private boolean usePostgresql = false;

    @Override
    public int insert(Object obj) {
        return insert(obj, obj.getClass());
    }

    @Override
    public int insert(Object obj, Class<?> cls) {
        ThreadLocalUtil.setSqlSession(sqlSession);

        if(obj == null) {
            return 0;
        }
        Map<String, Object> params = new HashMap<String, Object>();
        ObjectTableMapper tableMapper = ObjectTableMapper.buildTableMapper(obj, cls);
        SqlBuilder sqlBuilder = new InsertSqlBuilder(tableMapper);

        sqlBuilder.buildSql(params);

        Integer insertId = 0;
        sqlSession.insert("sylenorm.insert", params);
        Integer primaryId = ObjectUtil.convertValue(params.get("__pid"), Integer.class);
        if(primaryId != null && primaryId > 0) {
            insertId = primaryId;
            Object idObj = params.get("id");
            if(idObj == null || (idObj instanceof Number) && ((Number)idObj).intValue() == 0) {
                Class<?> idCls = insertId.getClass();
                if(idObj != null) {
                    idCls = idObj.getClass();
                }
                CommonUtil.invokeMethod(obj, "setId", ObjectUtil.convertValue(insertId, idCls));
            }
        }

        return insertId;
    }

    public int getLastInsertId() {
        if(usePostgresql) {
            return 0;
        }

        ThreadLocalUtil.setSqlSession(sqlSession);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("sql", "select LAST_INSERT_ID() as id");

        Map<String, Object> obj = sqlSession.selectOne("sylenorm.select", params);
        Integer id = ObjectUtil.convertValue(obj.get("id"), Integer.class);
        return id;
    }

    @Override
    public int update(Object obj) {
        return update(obj, null, false);
    }

    @Override
    public int update(Object obj, boolean ignoreNullColumn) {
        return update(obj, null, ignoreNullColumn);
    }

    @Override
    public int update(Object obj, Class<?> cls, boolean ignoreNullColumn) {
        if(obj == null) {
            return 0;
        }
        int updateStatus = 0;
        Map<String, Object> params = new HashMap<String, Object>();
        ThreadLocalUtil.setSqlSession(sqlSession);
        ObjectTableMapper tableMapper = ObjectTableMapper.buildTableMapper(obj, cls);
    

        SqlBuilder sqlBuilder = new UpdateByUniqueKeySqlBuilder(tableMapper, ignoreNullColumn);

        sqlBuilder.buildSql(params);

        updateStatus = sqlSession.update("sylenorm.update", params);

        return updateStatus;
    }

    @Override
    public int update(Object obj, Class<?> cls) {
        return update(obj, cls, false);
    }

    @Override
    public int updateByQuery(Object obj, QueryObj query) {
        ThreadLocalUtil.setSqlSession(sqlSession);
        int count = this.countByQuery(query);

        if(count <= 50) {
            // 50条影响范围以下的sql，单条分别执行
            Class<?> cls = obj.getClass();
            List<?> objs = this.findListByQuery(query, cls);
            if(CollectionUtils.isEmpty(objs)) {
                logger.info("can not find objs on query [{}], params: {}",
                        StringUtil.extractString(ThreadLocalUtil.getSql(), " where ", " order "), ThreadLocalUtil.getSqlQueryMap());
                return 0;
            }


            for(Object toUpdateObj : objs) {
                // 把obj中不为null的field，更新到toUpdateObj中间
                ObjectUtil.updateObjectValue(toUpdateObj, obj);
                this.update(toUpdateObj);
            }

            logger.info("update effect num: {}", objs.size());

            return objs.size();

        } else {

            Map<String, Object> parameterMap = Maps.newHashMap();
            ObjectTableMapper tableMapper = ObjectTableMapper.buildTableMapper(obj, obj.getClass());
            SqlBuilder sqlBuilder = new UpdateByQuerySqlBuilder(tableMapper, query);
            sqlBuilder.buildSql(parameterMap);
            logger.info("start to update on sql: {}, params: {}", ThreadLocalUtil.getSql(), ThreadLocalUtil.getSqlQueryMap());

            int updateStatus = sqlSession.update("sylenorm.update", parameterMap);
            logger.info("update effect num: {}", updateStatus);
            return updateStatus;

        }
    }


    @Override
    public int softDelete(Object obj) {
        return softDelete(obj, obj.getClass());
    }

    @Override
    public int realDelete(Object obj) {
        if(obj == null) {
            return 0;
        }
        return realDelete(obj, obj.getClass());
    }
    @Override
    public <E> E findObjectByQuery(QueryObj query,Class<? extends E> cls){
        query.setPage(1);
        query.setPageSize(1);
        List<E> rets= this.findListByQuery(query, cls);
        if(rets.size()>0){
            return rets.get(0);
        }else{
            return null;
        }
    }

    @Override
    public <E> List<E> findListByQuery(QueryObj query, Class<? extends E> cls) {
        ThreadLocalUtil.setSqlSession(sqlSession);

        Map<String, Object> queryMap = Maps.newHashMap();
        // 构建查询的sql生成器
        SqlBuilder sqlBuilder = new QuerySqlBuilder(query, cls);
        // queryMap中，产生sql语句及要送入mybatis中的查询参数
        sqlBuilder.buildSql(queryMap);

        resetPostgresqlLimitString(queryMap);

        // 执行mybatis中的orm.select语句，得到返回结果
        List<Map<String, Object>> retMaps = sqlSession.selectList("sylenorm.select", queryMap);
        List<E> rets = new ArrayList<E>();
        for(Map<String, Object> map : retMaps) {
            // 返回结果映射为cls类
            E ret = SqlAnnotationResolver.convertToObject(map, cls);
            if(ret != null) {
                rets.add(ret);
            }
        }
        return rets;
    }

    @Override
    public <E> E findObjectByUniqueKey(Serializable uniqueKey,
            Class<? extends E> cls) {
        return findObjectByUniqueKey(uniqueKey, null, cls);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> E findObjectByUniqueKey(Serializable uniqueKey, String keyName,
            Class<? extends E> cls) {

        ThreadLocalUtil.setSqlSession(sqlSession);

        E obj = null;
        Map<String, Object> params = new HashMap<String, Object>();

        ObjectTableMapper objectTableMapper = ObjectTableMapper.buildTableMapper(cls);

        SqlBuilder sqlBuilder = new UniqueKeyQuerySqlBuilder(objectTableMapper, uniqueKey, keyName);

        sqlBuilder.buildSql(params);

        Map<String, Object> ret = (Map<String, Object>) sqlSession.selectOne("sylenorm.select", params);
        obj = SqlAnnotationResolver.convertToObject(ret, cls);

        updateObjectCache(obj);

        return obj;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> E findObjectByPrimaryKey(Serializable primaryKey,
            Class<? extends E> cls) {
        if(primaryKey == null) {
            return null;
        }

        ThreadLocalUtil.setSqlSession(sqlSession);

        E obj = null;
        if(obj == null) {

            Map<String, Object> params = new HashMap<String, Object>();
            ObjectTableMapper objectTableMapper = ObjectTableMapper.buildTableMapper(cls);
            SqlBuilder sqlBuilder = new PrimaryQuerySqlBuilder(objectTableMapper, primaryKey);

            sqlBuilder.buildSql(params);

            Map<String, Object> ret = (Map<String, Object>) sqlSession.selectOne("sylenorm.select", params);
            obj = SqlAnnotationResolver.convertToObject(ret, cls);

            updateObjectCache(obj);
        }

        return obj;
    }

    public SqlSession getSqlSession() {
        return sqlSession;
    }

    public void setSqlSession(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
        TableMappingFactory.setSqlSession(sqlSession);
        this.usePostgresql = TableMappingFactory.isPostgresqlConnection(sqlSession);
    }

    @Override
    public int countByQuery(QueryObj query) {

        ThreadLocalUtil.setSqlSession(sqlSession);

        QuerySqlBuilder sqlBuilder = new QuerySqlBuilder(query);
        Map<String, Object> querys = new HashMap<String, Object>();

        sqlBuilder.buildSql(querys);

        String currentSql = ThreadLocalUtil.getSql();

        String countSql = SqlUtil.convertToCountSql(currentSql);
        if(currentSql.toLowerCase().indexOf("group by") == -1) {
            querys.put("sql", countSql);
            @SuppressWarnings("unchecked")
            Map<String, Object> ret = (Map<String, Object>) sqlSession.selectOne("select", querys);
            int count = CommonUtil.getIntValue(ret.get("count"));
            return count;
        } else {
            // 包含group by语句，用子查询
            querys.put("SOURCE_SQL", countSql);

            Integer count = (Integer)this.sqlSession.selectOne("COUNT_SQL", querys);
            return count;
        }
    }

    @Override
    public <E> Page<E> queryPage(QueryObj query, Class<? extends E> cls) {
        Integer count = countByQuery(query);
        List<E> list = findListByQuery(query, cls);

        Page<E> page = new Page<E>(count, query.getPageInfo().getPage(), query.getPageInfo().getPageSize(), list);
        return page;
    }

    @Override
    public int softDelete(Object obj, Class<?> cls) {
        if(obj == null) {
            return 0;
        }

        ThreadLocalUtil.setSqlSession(sqlSession);

        ObjectTableMapper tableMapper = ObjectTableMapper.buildTableMapper(obj, cls);
        if(tableMapper != null) {
            Map<String, Object> params = new HashMap<String, Object>();
            SqlBuilder sqlBuilder = new DeleteSqlBuilder(tableMapper, true);
            sqlBuilder.buildSql(params);

            int delStatus = sqlSession.update("sylenorm.update", params);

            return delStatus;
        }
        return 0;
    }

    @Override
    public int realDelete(Object obj, Class<?> cls) {
        if(obj == null) {
            return 0;
        }

        int delStatus = 0;
        ThreadLocalUtil.setSqlSession(sqlSession);

        ObjectTableMapper tableMapper = ObjectTableMapper.buildTableMapper(obj, cls);
        Map<String, Object> params = new HashMap<String, Object>();
        if(tableMapper != null) {
            SqlBuilder sqlBuilder = new DeleteSqlBuilder(tableMapper, false);
            sqlBuilder.buildSql(params);

            delStatus = sqlSession.delete("sylenorm.update", params);

        }
        return delStatus;
    }

    @Override
    public boolean objectExists(Serializable primaryKey, Class<?> cls) {
        ThreadLocalUtil.setSqlSession(sqlSession);

        Map<String, Object> params = new HashMap<String, Object>();
        ObjectTableMapper objectTableMapper = ObjectTableMapper.buildTableMapper(cls);

        SqlBuilder sqlBuilder = new ExistsQuerySqlBuilder(objectTableMapper, primaryKey);

        sqlBuilder.buildSql(params);

        @SuppressWarnings("unchecked")
        Map<String, Object> ret = (Map<String, Object>) sqlSession.selectOne("sylenorm.select", params);

        return CollectionUtils.isNotEmpty(ret);
    }

    /**
     * 根据object与表的映射关系，更新缓存
     * @param obj
     */
    protected void updateObjectCache(Object obj) {
        if(obj == null) {
            return;
        }

        if(!(obj instanceof Serializable)) {
            //            logger.warn("obj class " + obj.getClass().toString() + " is not a Serializable class");
            return;
        }

        ObjectTableMapper tableMapper = ObjectTableMapper.buildTableMapper(obj);

        List<FieldMapper> toUpdateFields = new ArrayList<FieldMapper>();
        toUpdateFields.add(tableMapper.getPrimarykeyField());
        toUpdateFields.addAll(tableMapper.getUniqkeyFields());

    }

    @Override
    public <E> List<E> findListBySample(Object obj, Class<? extends E> cls) {
        QueryObj queryObj = new QueryObj();
        queryObj.setQuerydo(obj);

        return findListByQuery(queryObj, cls);
    }

    @Override
    public <E> E findObjectBySample(Object obj, Class<? extends E> cls) {
        QueryObj queryObj = new QueryObj();
        queryObj.setQuerydo(obj);

        return findObjectByQuery(queryObj, cls);
    }

    @Override
    public void insertOrUpdate(Object obj, Class<?> cls) {
        ThreadLocalUtil.setSqlSession(sqlSession);
        ObjectTableMapper objectTableMapper = ObjectTableMapper.buildTableMapper(obj, cls);
        FieldMapper field = objectTableMapper.getPrimarykeyField();
        if(field != null) {
            Object primaryKey = objectTableMapper.getPrimarykeyField().getObjectField().getValue();

            if(objectExists((Serializable) primaryKey, cls)) {
                update(obj, cls);
            } else {
                insert(obj, cls);
            }
        } else {
            throw new RuntimeException("primary key for " + obj.getClass() + " not defined");
        }
    }

    @Override
    public int execSql(String sql, Map<String, Object> objs) {
        if(objs == null) {
            objs = new HashMap<String, Object>();
        }

        objs.put("sql", sql);

        int ret = sqlSession.update("sylenorm.update", objs);

        return ret;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> E selectOneBySql(String sql, Map<String, Object> objs,
            Class<? extends E> cls) {
        if(objs == null) {
            objs = new HashMap<String, Object>();
        }

        ThreadLocalUtil.setSqlSession(sqlSession);
        objs.put("sql", sql);
        Map<String, Object> ret = (Map<String, Object>) sqlSession.selectOne("sylenorm.select", objs);
        if(cls == null || cls.isAssignableFrom(Map.class)) {
            return (E) ret;
        }
        E obj = SqlAnnotationResolver.convertToObject(ret, cls);
        return obj;
    }

    protected void resetPostgresqlLimitString(Map<String, Object> querys) {
        String sql = (String) querys.get("sql");
        if(sql == null) {
            return;
        }
        if(!usePostgresql) {
            return;
        }

        if(!sql.contains(" limit ")) {
            return;
        }

        String sqlHead = StringUtil.extractString(sql, null, " limit ");
        String limitStr = StringUtil.extractString(sql, " limit ", null);
        if(!limitStr.contains(",")) {
            return;
        }

        limitStr = limitStr.replace(" ", "");

        String offset = StringUtil.extractString(limitStr, null, ",").trim();
        String limitNum = StringUtil.extractString(limitStr, ",", null).trim();

        sql = sqlHead + " limit " + limitNum + " offset " + offset;
        querys.put("sql", sql);
    }


    @SuppressWarnings("unchecked")
    @Override
    public <E> List<E> selectListBySql(String sql, Map<String, Object> objs,
            Class<? extends E> cls) {
        if(objs == null) {
            objs = new HashMap<String, Object>();
        }

        if(!sql.toLowerCase().contains(" limit ")) {
            sql += " limit 0, 10000";
        }

        ThreadLocalUtil.setSqlSession(sqlSession);

        objs.put("sql", sql);

        resetPostgresqlLimitString(objs);

        List<Map<String, Object>> retMaps = sqlSession.selectList("sylenorm.select", objs);
        List<E> rets = new ArrayList<E>();
        for(Map<String, Object> map : retMaps) {
            if(cls == null || cls.isAssignableFrom(Map.class)) {
                rets.add((E) map);
            } else {
                E ret = SqlAnnotationResolver.convertToObject(map, cls);
                if(ret != null) {
                    rets.add(ret);
                }
            }
        }

        return rets;
    }

    public void setUsePostgresql(boolean usePostgresql) {
        this.usePostgresql = usePostgresql;
    }

    @Override
    public int realDeleteByPrimaryKey(Serializable primaryKey, Class<?> cls) {
        Object obj = findObjectByPrimaryKey(primaryKey, cls);
        return realDelete(obj);
    }

    @Override
    public int softDeleteByPrimaryKey(Serializable primaryKey, Class<?> cls) {
        Object obj = findObjectByPrimaryKey(primaryKey, cls);
        return softDelete(obj);
    }

}
