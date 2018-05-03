package cn.sylen;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import cn.sylen.common.page.Page;

import cn.sylen.dao.query.QueryObj;

/**
 * 基础查询dao
 * @author Sylen
 * @since 2018-4-4
 */
public interface BasicDao {
	/**
	 * 插入一条记录
	 * @param obj 要插入的实体
	 */
	int insert(Object obj);
	/**
	 * @param obj　要插入的实体
	 * @param cls 实体的class
	 */
	int insert(Object obj, Class<?> cls);

	/**
	 * 根据唯一键进行更新
	 * @param obj
	 * @return int 更新影响到的条数
	 */
	int update(Object obj);

	   /**
     * 根据唯一键进行更新
     * @param obj
     * @param ignoreNullColumn 是否忽略空的column列，true-忽略null值的列，false-不忽略
     * @return int 更新影响到的条数
     */
    int update(Object obj, boolean ignoreNullColumn);

	/**
	 * 根据唯一键进行更新
	 * @param obj
	 * @param cls 要操作的表对应的class
	 * @return int 更新影响到的条数
	 */
	int update(Object obj, Class<?> cls);

    /**
     * 根据唯一键进行更新
     * @param obj
     * @param cls 要操作的表对应的class
     * @param ignoreNullColumn 是否忽略空的column列，true-忽略null值的列，false-不忽略
     * @return int 更新影响到的条数
     */
    int update(Object obj, Class<?> cls, boolean ignoreNullColumn);

	/**
	 * 根据query查询条件，更新obj中非空字段的字段及对应的value
	 * @param obj 要更新的object（更新obj中的非空记录字段信息）
	 * @param query 查询条件
	 * @return 更新的记录数目
	 */
	int updateByQuery(Object obj, QueryObj query);

	/**
	 * 执行一条sql语句
	 * @param sql
	 * @param objs
	 */
	int execSql(String sql, Map<String, Object> objs);

	/**
	 * 根据唯一键/主键进行删除,默认只是把date_delete里面的值置上
	 * @param obj
	 */
	int softDelete(Object obj);

	/**
	 * 根据唯一键/主键进行删除,默认只是把date_delete里面的值置上
	 * @param obj
	 */
	int softDelete(Object obj, Class<?> cls);

	/**
	 * 根据cls对应的表里的主键，硬删除相应的记录
	 * @param primaryKey 表的主键
	 * @param cls 要操作的表对应的class
	 * @return
	 */
	int realDeleteByPrimaryKey(Serializable primaryKey, Class<?> cls);

    /**
     * 根据cls对应的表里的主键，软删除相应的记录
     * @param primaryKey 表的主键
     * @param cls 要操作的表对应的class
     * @return
     */
    int softDeleteByPrimaryKey(Serializable primaryKey, Class<?> cls);

	/**
	 * 根据唯一键/主键进行删除,这个删除是物理删除
	 * @param obj
	 */
	int realDelete(Object obj);

	/**
	 * 根据唯一键/主键进行删除,这个删除是物理删除
	 * @param obj
	 */
	int realDelete(Object obj, Class<?>cls);

	/**
	 * @param uniqueKey
	 * @param keyName
	 * @param cls
	 * @param keyName
	 * @return
	 */
	<E> E findObjectByUniqueKey(Serializable uniqueKey, String keyName, Class<?extends E> cls);

	/**
	 * 通过唯一键查询
	 * @param uniqueKey
	 * @param cls
	 * @return
	 */
	<E> E findObjectByUniqueKey(Serializable uniqueKey, Class<?extends E> cls);

	/**
	 * 通过主键查询
	 * @param primaryKey
	 * @param cls
	 * @return
	 */
	<E> E findObjectByPrimaryKey(Serializable primaryKey, Class<?extends E> cls);

	/**
	 * 通过主键判断一个object是否存在
	 * @param primaryKey
	 * @param cls
	 * @return
	 */
	boolean objectExists(Serializable primaryKey, Class<?> cls);

	/**
	 * 通过query查询列表
	 */
	<E> List<E> findListByQuery(QueryObj query, Class<? extends E> cls);

	<E> Page<E> queryPage(QueryObj query, Class<? extends E> cls);

	<E> List<E> findListBySample(Object obj, Class<? extends E> cls);

	<E> E findObjectBySample(Object obj, Class<? extends E> cls);

	/**
	 * 根据query, 统计数目
	 * @param query
	 * @return
	 */
	int countByQuery(QueryObj query);

	/**
	 * 根据查询条件获取对象
	 * @param query
	 * @param cls
	 * @return
	 */
    <E> E findObjectByQuery(QueryObj query, Class<? extends E> cls);

    /**
     * 插入或更新
     * @param obj
     * @param cls
     */
    void insertOrUpdate(Object obj, Class<?> cls);

    /**
     * 根据sql执行select one的语句
     * @param sql
     * @param objs
     */
    <E> E selectOneBySql(String sql, Map<String, Object> objs, Class<? extends E> cls);

    /**
     * 根据sql执行select one的语句
     * @param sql
     * @param objs
     */
    <E> List<E> selectListBySql(String sql, Map<String, Object> objs, Class<? extends E> cls);

}
