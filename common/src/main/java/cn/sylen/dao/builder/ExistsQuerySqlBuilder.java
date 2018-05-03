package cn.sylen.dao.builder;

import java.util.List;
import java.util.Map;

import cn.sylen.dao.mapping.FieldMapper;

import cn.sylen.common.exception.CoreException;
import cn.sylen.common.util.StringUtil;
import cn.sylen.dao.mapping.ObjectTableMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 生成query语句的sql生成器
 * @author hongwm
 * @since 2013-9-16
 */
public class ExistsQuerySqlBuilder extends QuerySqlBuilderBase implements SqlBuilder {
	private Logger logger = LoggerFactory.getLogger(ExistsQuerySqlBuilder.class);

	/**
	 * 主表的meta信息
	 */
	private ObjectTableMapper objectTableMapper;

	private Object primaryKey;

	public ExistsQuerySqlBuilder(ObjectTableMapper objectTableMapper, Object primaryKey) {
		this.objectTableMapper = objectTableMapper;
		this.primaryKey = primaryKey;
	}

	@Override
	public void buildSql(Map<String, Object> parameterMap) {
		List<FieldMapper> mappedFields = objectTableMapper.getMappedFields();
		FieldMapper primaryField = null;
		for(FieldMapper field : mappedFields) {
			if(!field.isPrimaryKey()) {
				continue;
			}

			primaryField = field;
			parameterMap.put(field.getObjectField().getUpdateFieldName(),
					primaryKey);
		}

		if(primaryField == null) {
			throw new CoreException("not primary key defined for table " + objectTableMapper.getTableSchema().getTableName());
		}

		StringBuffer sqlSB = new StringBuffer();
		String tableName = objectTableMapper.getTableSchema().getTableName();

		sqlSB.append("select ").append(primaryField.getColumnFieldName())
			 .append(" from ").append(tableName)
			 .append(" where ").append(primaryField.getColumnFieldName())
			 .append(" = ").append(StringUtil.formatString("#{%s}", primaryField.getObjectField().getUpdateFieldName()));

		parameterMap.put("sql", sqlSB.toString());
        setupThreadLocal(parameterMap);
//		ThreadLocalUtil.addSql(sqlSB.toString());
		if(logger.isDebugEnabled()){
			logger.debug(sqlSB.toString());
		}
	}

}
