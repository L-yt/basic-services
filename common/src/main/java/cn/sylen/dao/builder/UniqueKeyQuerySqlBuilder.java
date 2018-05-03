package cn.sylen.dao.builder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.sylen.dao.mapping.FieldMapper;

import cn.sylen.common.exception.CoreException;
import cn.sylen.common.util.CollectionUtils;
import cn.sylen.dao.mapping.ObjectTableMapper;

/**
 * 生成query语句的sql生成器
 * @author hongwm
 * @since 2013-8-25
 */
public class UniqueKeyQuerySqlBuilder extends QuerySqlBuilderBase implements SqlBuilder {
	private Logger logger = LoggerFactory.getLogger(UniqueKeyQuerySqlBuilder.class);

	/**
	 * 主表的meta信息
	 */
	private ObjectTableMapper objectTableMapper;

	private Object uniqueKey;
	private String keyName;

	public UniqueKeyQuerySqlBuilder(ObjectTableMapper objectTableMapper, Object uniqueKey) {
		this.objectTableMapper = objectTableMapper;
		this.uniqueKey = uniqueKey;
	}

	public UniqueKeyQuerySqlBuilder(ObjectTableMapper objectTableMapper, Object uniqueKey, String keyName) {
		this.objectTableMapper = objectTableMapper;
		this.uniqueKey = uniqueKey;
		this.keyName = keyName;
	}

	@Override
	public void buildSql(Map<String, Object> parameterMap) {
		List<FieldMapper> mappedFields = objectTableMapper.getMappedFields();
		List<FieldMapper> uniqueFields = new ArrayList<FieldMapper>();
		parameterMap.put("uniqueKey", uniqueKey);

		for(FieldMapper field : mappedFields) {
			if(!field.isUniqueKey()) {
				continue;
			}

			if(keyName != null) {
				if(field.matchName(keyName)) {
					uniqueFields.clear();
					uniqueFields.add(field);
					break;
				}
			}

			uniqueFields.add(field);
		}

		if(CollectionUtils.isEmpty(uniqueFields)) {
			throw new CoreException("not unique key defined for table " + objectTableMapper.getTableSchema().getTableName());
		}

		StringBuffer sqlSB = new StringBuffer();
		String tableName = objectTableMapper.getTableSchema().getTableName();

		// 生成要查询的column列
		String queryColumn = getQueryColumnsByTableMapper(objectTableMapper, tableName, new HashSet<String>());
		StringBuffer queryParamSB = new StringBuffer();

		for(FieldMapper field : uniqueFields) {
			if(queryParamSB.length() > 0) {
				queryParamSB.append(" or ");
			}
			queryParamSB.append(getTableNameMap(tableName)).append(".")
						.append(field.getColumnFieldName()).append(" = #{uniqueKey}");
		}

		sqlSB.append("select ").append(queryColumn)
			 .append(" from ")
			 .append(tableName).append(" ").append(getTableNameMap(tableName))
			 .append(" where ").append(queryParamSB.toString());

		parameterMap.put("sql", sqlSB.toString());

		setupThreadLocal(parameterMap);
//		ThreadLocalUtil.addSql(sqlSB.toString());
		if(logger.isDebugEnabled()){
			logger.debug(sqlSB.toString());
		}
	}
}
