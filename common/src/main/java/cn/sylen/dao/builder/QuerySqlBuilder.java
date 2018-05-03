package cn.sylen.dao.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import cn.sylen.dao.mapping.FieldMapper;
import cn.sylen.dao.query.DistinctParam;

import cn.sylen.common.util.CollectionUtils;
import cn.sylen.common.util.Entry;
import cn.sylen.common.util.StringUtil;
import cn.sylen.dao.annotation.SqlAnnotationResolver;
import cn.sylen.dao.mapping.ObjectTableMapper;
import cn.sylen.dao.mapping.TableMappingFactory;
import cn.sylen.dao.mapping.TableSchema;
import cn.sylen.dao.query.OrderParam;
import cn.sylen.dao.query.QueryObj;
import cn.sylen.dao.query.QueryParam;
import cn.sylen.dao.query.join.JoinPair;
import cn.sylen.dao.query.join.JoinParam;
import cn.sylen.dao.query.join.JoinType;

/**
 * 生成query语句的sql生成器
 * @author hongwm
 * @since 2013-8-25
 */
public class QuerySqlBuilder extends QuerySqlBuilderBase implements SqlBuilder{
    private Logger logger = LoggerFactory.getLogger(QuerySqlBuilder.class);
    /**
     * 查询的object
     */
    private QueryObj queryObj;

    private Class<?> retCls;

    /**
     * 主表的meta查询信息
     */
    private ObjectTableMapper objectTableMapper;

    /**
     * 存储数据库表与retCls之间的映射关系
     */
    private Map<String, ObjectTableMapper> tableMetaMap =
            new HashMap<String, ObjectTableMapper>();

    public QuerySqlBuilder(QueryObj queryObj) {
        this(queryObj, queryObj.getQuerydo().getClass());
    }

    public QuerySqlBuilder(QueryObj queryObj, Class<?> retCls) {
        this.queryObj = queryObj;
        if(retCls != null) {
            this.retCls = retCls;
        } else {
            this.retCls = queryObj.getQuerydo().getClass();
        }
        this.objectTableMapper = ObjectTableMapper.buildTableMapper(queryObj.getQuerydo());
    }

    @Override
    public void buildSql(Map<String, Object> parameterMap) {
        List<FieldMapper> mappedFields = objectTableMapper.getMappedFields();
        for(FieldMapper field : mappedFields) {
            if(field.isNullObject()) {
                continue;
            }

            parameterMap.put(field.getObjectField().getFieldName(),
                    field.getObjectField().getValue());
        }

        if(queryObj.getQueryParam() != null) {
            parameterMap.putAll(queryObj.getQueryParam().getParameterMap());
        }
        for(JoinParam joinParam : queryObj.getJoinParams()) {
            QueryParam q = joinParam.getQueryParam();
            if(q != null) {
                parameterMap.putAll(q.getParameterMap());
            }
        }

        StringBuffer sqlSB = new StringBuffer();

        String queryColumn = genQueryColumns();		// 生成要查询的column列
        String queryParams = genQueryParams();		// 生成查询参数
        String orderParams = genOrderParam();		// 生成排序参数
        String distinctParams = genDistinctParam(); //生成去重参数
        String joinTables = genJoinTables();		// 生成要join的表参数

        // 如果有包含 in #{xxx} 的条件，合并in查询语句
        queryParams = SqlUtil.genInParams(queryParams, parameterMap);

        String tableName = objectTableMapper.getTableSchema().getTableName();

        sqlSB.append("select ").append(queryColumn)
        .append(" from ")
        .append(tableName).append(" ").append(getTableNameMap(tableName));

        if(joinTables != null){
            joinTables = SqlUtil.genInParams(joinTables, parameterMap);
            sqlSB.append(" ").append(joinTables);
        }

        if(StringUtil.isNotEmpty(queryParams)) {
            sqlSB.append(" where ").append(queryParams);
        }

        if(StringUtil.isNotEmpty(distinctParams)){
            sqlSB.append(" group by ").append(distinctParams);
        }

        if(StringUtil.isNotEmpty(orderParams)) {
            sqlSB.append(" order by ").append(orderParams);
        }

        sqlSB.append(" limit ")
        .append(queryObj.getPageInfo().getStartPos())
        .append(", ").append(queryObj.getPageInfo().getPageSize());

        parameterMap.put("sql", sqlSB.toString());

        setupThreadLocal(parameterMap);
        //        ThreadLocalUtil.addSql(sqlSB.toString());

        if(logger.isDebugEnabled()){
            logger.debug(sqlSB.toString());
        }
    }

    protected Map<String, Object> getQueryParameterMap() {
        Map<String, Object> parameterMap = Maps.newHashMap();
        List<FieldMapper> mappedFields = objectTableMapper.getMappedFields();
        for(FieldMapper field : mappedFields) {
            if(field.isNullObject()) {
                continue;
            }

            parameterMap.put(field.getObjectField().getFieldName(),
                    field.getObjectField().getValue());
        }

        if(queryObj.getQueryParam() != null) {
            parameterMap.putAll(queryObj.getQueryParam().getParameterMap());
        }
        return parameterMap;
    }

    /**
     * 获取表join的参数
     * 如果joinParam不是distinct的,生成 "INNER JOIN table1 b on a.id = b.id"
     * 如果joinParam是distinct的,生成 "INNER JOIN (select distinct * from table1 group by id) b on a.id = b.id"
     */
    public String genJoinTables() {
        List<JoinParam> joinParams = queryObj.getJoinParams();
        if(CollectionUtils.isEmpty(joinParams)) {
            return null;
        }

        StringBuffer sb = new StringBuffer();
        String tableName = objectTableMapper.getTableSchema().getTableName();
        for(JoinParam joinParam : joinParams) {
            Class<?> joinCls = joinParam.getClass();
            if(joinCls == null) {
                continue;
            }

            // join表名
            String joinTable = SqlAnnotationResolver.getAnnotationTableName(joinParam.getJoinClass());
            ObjectTableMapper joinMapper = getRetTableMapper(joinTable);
            if(joinMapper == null) {
                continue;
            }

            JoinType joinType = joinParam.getJoinType();
            List<JoinPair> joinPairs = joinParam.getJoinPairs();

            TableSchema joinSchema = joinMapper.getTableSchema();
            if(sb.length() > 0) {
                sb.append(" ");
            }

            sb.append(joinType.toString()).append(" ");

            String joinQuery = null;
            if(joinParam.isDistinct()) {
                // 需要distinct 第一个join条件
                // 生成"(select distinct * from table1 where 1=1 and t>1 group by id)"这样的语句
                sb.append("(select distinct * from ")
                .append(joinSchema.getTableName());
                if(joinParam.getQueryParam() != null && StringUtil.isNotEmpty(joinParam.getQueryParam().toString())) {
                    sb.append(" where ").append(normalizeQueryParam(joinParam.getQueryParam(), joinParam.getJoinClass(), false));
                }
                String key2 = SqlUtil.splitTableColumn(joinPairs.get(0).getRightKey()).getValue();
                sb.append(" group by ").append(joinSchema.getMatchColumnName(key2)).append(") ");
            } else {
                sb.append(joinSchema.getTableName()).append(" ");
                if(joinParam.getQueryParam() != null && StringUtil.isNotEmpty(joinParam.getQueryParam().toString())) {
                    joinQuery = normalizeQueryParam(joinParam.getQueryParam(), joinParam.getJoinClass(), true);
                }

            }

            sb.append(getTableNameMap(joinSchema.getTableName()))
            .append(" on ");

            StringBuffer joinParamSB = new StringBuffer();
            for(JoinPair pair : joinPairs) {
                String key1 = pair.getLeftKey();
                String key2 = pair.getRightKey();

                String name1 = tableName;
                String name2 = joinSchema.getTableName();
                Entry<String, String> tb1 = SqlUtil.splitTableColumn(key1);
                Entry<String, String> tb2 = SqlUtil.splitTableColumn(key2);

                if(tb1.getKey() != null) {
                    name1 = TableMappingFactory.getTableName(tb1.getKey());
                    key1 = tb1.getValue();
                }
                if(tb2.getKey() != null) {
                    name2 = TableMappingFactory.getTableName(tb2.getKey());
                    key2 = tb2.getValue();
                }

                if(joinParamSB.length() > 0) {
                    joinParamSB.append(" and ");
                }

                TableSchema mainTableSchema = TableMappingFactory.getTableSchema(name1);
                joinSchema = TableMappingFactory.getTableSchema(name2);

                joinParamSB.append(getTableNameMap(name1)).append(".").append(mainTableSchema.getMatchColumnName(key1))
                .append(" = ").append(getTableNameMap(name2)).append(".").append(joinSchema.getMatchColumnName(key2));
            }

            if(joinQuery != null && !joinQuery.isEmpty()) {
                joinParamSB.append(" and ").append(joinQuery);
            }
            sb.append(joinParamSB.toString());
        }
        return sb.toString();
    }

    /**
     * 获取查询参数
     */
    public String genQueryParams() {
        StringBuffer paramSB = new StringBuffer();

        List<FieldMapper> mappedFields = objectTableMapper.getMappedFields();
        String tableName = objectTableMapper.getTableSchema().getTableName();

        paramSB.append(" 1=1 ");

        for(FieldMapper field : mappedFields) {
            if(!field.isNullObject() && !field.getObjectField().isDefaultPrimitiveValue()) {

                paramSB.append(" and ");

                paramSB.append(StringUtil.formatString("%s.%s = #{%s}",
                        getTableNameMap(tableName),
                        field.getColumnFieldName(),
                        field.getObjectField().getFieldName()));
            }
        }

        if(queryObj.getQueryParam() != null) {

            String paramStr = normalizeQueryParam(queryObj.getQueryParam(), queryObj.getQuerydo().getClass());
            if(paramStr != null && !"".equals(paramStr.trim())){
                paramSB.append(" and ");
                paramSB.append(paramStr);
            }
        }

        return paramSB.toString();
    }


    public String normalizeQueryParam(QueryParam queryParam, Class<?> clazz) {
        return normalizeQueryParam(queryParam, clazz, true);
    }
    /**
     * 对query param进行normalize,组装出查询串
     * @return
     */
    public String normalizeQueryParam(QueryParam queryParam, Class<?> clazz, boolean addTableHead) {
        String sql = queryParam.toString();
        if(StringUtil.isEmpty(sql)) {
            return null;
        }

        String table = SqlAnnotationResolver.getAnnotationTableName(clazz);
        ObjectTableMapper tableMapper = getRetTableMapper(table);

        List<String> sqls = SqlUtil.generateSqlParams(sql);
        List<String> params = new ArrayList<String>();
        for(int i=1; i<sqls.size(); i++) {
            TableSchema tableSchema = tableMapper.getTableSchema();
            String param = sqls.get(i);
            String tableName = tableSchema.getTableName();
            String key = param;

            Entry<String, String> tb = SqlUtil.splitTableColumn(param);

            if(tb.getKey() != null) {
                tableName = TableMappingFactory.getTableName(tb.getKey());
                key = tb.getValue();
                tableSchema = TableMappingFactory.getTableSchema(tableName);
            }

            String subPgQuery = null;
            // 兼容pgsql的子查询语句，如(ext->>'site_id' = #{siteId})
            if(key.contains("->>")) {
                subPgQuery = StringUtil.extractString(key, "->>", null);
                key = StringUtil.extractString(key, null, "->>");
            }

            String columnName = tableSchema.getMatchColumnName(key);
            if(columnName != null) {
                key = columnName;
            }

            if(subPgQuery != null) {
                key = key + "->>" + subPgQuery;
            }

            if(addTableHead) {
                params.add(getTableNameMap(tableName) + "." + key);
            } else {
                params.add(key);
            }
        }

        return StringUtil.formatString(sqls.get(0), params.toArray());
    }

    /**
     * 生成排序参数
     */
    public String genOrderParam() {
        TableSchema tableSchema = objectTableMapper.getTableSchema();
        List<OrderParam> orderParams = queryObj.getOrderParams();
        if(CollectionUtils.isEmpty(orderParams)) {
            if(tableSchema.getMatchColumn("dateCreate") != null) {
                orderParams = new ArrayList<OrderParam>();
                orderParams.add(new OrderParam("dateCreate", "desc"));
            } else {
                return null;
            }
        }

        String tableName = tableSchema.getTableName();
        StringBuffer ordersb = new StringBuffer();
        for(OrderParam orderParam : orderParams) {
            if(!orderParam.isValid()) {
                continue;
            }

            if(ordersb.length() > 0) {
                ordersb.append(", ");
            }

            String orderName = orderParam.getOrderName();
            String orderType = orderParam.getOrderType();

            if(orderParam.isToGenOrderTableHead()) {
                Entry<String, String> tb = SqlUtil.splitTableColumn(orderName);
                if(tb.getKey() != null) {
                    tableName = TableMappingFactory.getTableName(tb.getKey());
                    orderName = tb.getValue();
                }

                TableSchema schema = TableMappingFactory.getTableSchema(tableName);
                if(schema != null) {
                    tableSchema = schema;
                }

                String tablemap = getTableNameMap(tableName);
                String column = tableSchema.getMatchColumnName(orderName);
                if(column == null) {
                    // 该列在数据库表中不存在,忽略
                    continue;
                }

                ordersb.append(tablemap).append(".").append(column).append(" ").append(orderType);
            } else {
                ordersb.append(orderName).append(' ').append(orderType);
            }
        }

        return ordersb.toString();
    }

    /**
     * 生成去重参数
     */
    public String genDistinctParam() {
        TableSchema tableSchema = objectTableMapper.getTableSchema();
        List<DistinctParam> distinctParams = queryObj.getDistinctParams();
        if(CollectionUtils.isEmpty(distinctParams)) {
            return null;
        }

        String tableName = tableSchema.getTableName();
        StringBuffer distinctsb = new StringBuffer();
        for(DistinctParam distinctParam : distinctParams) {
            if(!distinctParam.isValid()) {
                continue;
            }

            if(distinctsb.length() > 0) {
                distinctsb.append(", ");
            }

            String distinctName = distinctParam.getDistinctName();

            if(distinctParam.isToGenDistinctTableHead()) {
                Entry<String, String> tb = SqlUtil.splitTableColumn(distinctName);
                if(tb.getKey() != null) {
                    tableName = TableMappingFactory.getTableName(tb.getKey());
                    distinctName = tb.getValue();
                }

                TableSchema schema = TableMappingFactory.getTableSchema(tableName);
                if(schema != null) {
                    tableSchema = schema;
                }

                String tablemap = getTableNameMap(tableName);
                String column = tableSchema.getMatchColumnName(distinctName);
                if(column == null) {
                    // 该列在数据库表中不存在,忽略
                    continue;
                }

                distinctsb.append(tablemap).append(".").append(column);
            } else {
                distinctsb.append(distinctName).append(' ');
            }
        }

        return distinctsb.toString();
    }

    /**
     * 获取查询参数
     * @return
     */
    public String genQueryColumns() {
        Set<String> existedFieldSet = new HashSet<String>();
        StringBuffer queryColumnSB = new StringBuffer();

        List<String> tables = new ArrayList<String>();
        String mainTableName = SqlAnnotationResolver.getAnnotationTableName(queryObj.getQuerydo().getClass());
        if(mainTableName != null) {
            // 加入主表
            tables.add(mainTableName);
        }
        for(JoinParam joinParam : queryObj.getJoinParams()) {
            // 加入join 表
            String table = SqlAnnotationResolver.getAnnotationTableName(joinParam.getJoinClass());
            if(table != null) {
                tables.add(table);
            }
        }

        for(String table : tables) {
            ObjectTableMapper mainTableMapper = getRetTableMapper(table);
            String joinColumn = getQueryColumnsByTableMapper(
                    mainTableMapper, table, existedFieldSet);
            if(StringUtil.isNotEmpty(joinColumn)) {
                if(queryColumnSB.length() > 0) {
                    queryColumnSB.append(", ");
                }
                queryColumnSB.append(joinColumn);
            }
        }

        return queryColumnSB.toString();
    }


    /**
     * 根据输入表名,获取表与返回结果之间的映射关系
     * @param cls
     * @return
     */
    protected ObjectTableMapper getRetTableMapper(String tableName) {
        ObjectTableMapper retObjectTableMapper = tableMetaMap.get(tableName);
        if(retObjectTableMapper == null) {
            retObjectTableMapper = ObjectTableMapper.buildTableMapper(retCls, tableName);
            if(retObjectTableMapper == null) {
                return null;
            }

            tableMetaMap.put(tableName, retObjectTableMapper);
        }

        return retObjectTableMapper;
    }

}
