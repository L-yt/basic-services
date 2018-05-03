package cn.sylen.dao.builder;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.sylen.dao.mapping.FieldMapper;

import cn.sylen.common.util.CollectionUtils;
import cn.sylen.common.util.StringUtil;
import cn.sylen.dao.mapping.ObjectTableMapper;

/**
 * 生成insert语句的sql生成器
 * @author hongwm
 * @since 2013-8-25
 */
public class InsertSqlBuilder extends QuerySqlBuilderBase implements SqlBuilder {
	private final static Logger logger = LoggerFactory.getLogger(InsertSqlBuilder.class);
	private ObjectTableMapper objectTableMapper;
	private String tableName;

	public InsertSqlBuilder(ObjectTableMapper objectTableMapper) {
		this.objectTableMapper = objectTableMapper;
		this.tableName = objectTableMapper.getTableSchema().getTableName();
	}

	@Override
	public void buildSql(Map<String, Object> parameterMap) {
		StringBuffer sqlSB = new StringBuffer();

		for(FieldMapper mappedField : objectTableMapper.getMappedFields()) {
			if(mappedField.isNullObject()) {
				if(mappedField.isDateCreateColumn() || mappedField.isDateUpdateColumn()) {
					// 设置date_create及date_update为当前时间
					mappedField.getObjectField().setValue(new Date());
				} else {
					continue;
				}
			}
			parameterMap.put(mappedField.getObjectField().getUpdateFieldName(),
					mappedField.getObjectField().getValue());
		}

		sqlSB.append("insert into ")
			 .append(objectTableMapper.getTableSchema().getTableName())
			 .append("(").append(getInsertColumns()).append(")")
			 .append(" values (")
			 .append(getInsertObjects()).append(")");
		parameterMap.put("sql", sqlSB.toString());

		setupThreadLocal(parameterMap);
//		ThreadLocalUtil.addSql(sqlSB.toString());
		if(logger.isDebugEnabled()){
			logger.debug(sqlSB.toString());
		}

	}

	/**
	 * 获取插入的字段
	 * @return
	 */
	public String getInsertColumns() {
		List<FieldMapper> mappedFields = objectTableMapper.getMappedFields();
		if(CollectionUtils.isEmpty(mappedFields)) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		for(FieldMapper mappedField : mappedFields) {
			if(!mappedField.isLegalInsertColumn()) {
				continue;
			}

			if(!tableName.equals(mappedField.getColumn().getTableSchema().getTableName())) {
				continue;
			}

			if(sb.length() > 0) {
				sb.append(", ");
			}

			String fieldName = mappedField.getColumnFieldNameWithSymbol();
			if(objectTableMapper.getTableSchema().isPGSchema()) {
			    fieldName = mappedField.getColumnFieldNameWithQuote();
			}

			sb.append(fieldName);
		}
		return sb.toString();
	}

	/**
	 * 获取插入的值
	 * @return
	 */
	public String getInsertObjects() {
		List<FieldMapper> mappedFields = objectTableMapper.getMappedFields();
		if(CollectionUtils.isEmpty(mappedFields)) {
			return null;
		}

		StringBuffer sb = new StringBuffer();
		for(FieldMapper mappedField : mappedFields) {
			if(!mappedField.isLegalInsertColumn()) {
				continue;
			}

			if(!tableName.equals(mappedField.getColumn().getTableSchema().getTableName())) {
				continue;
			}

			if(sb.length() > 0) {
				sb.append(", ");
			}

			if(!mappedField.isNullObject()) {
				sb.append(StringUtil.formatString("#{%s}", mappedField.getObjectField().getUpdateFieldName()));
			} else {
				String defaultValue = mappedField.getObjectField().getDefaultValue();

				// 包含sql函数标识
				if(defaultValue.indexOf("(") != -1) {
					sb.append(defaultValue);
				} else {
					sb.append(StringUtil.formatString("'%s'", defaultValue));
				}
			}
		}
		return sb.toString();
	}

}
