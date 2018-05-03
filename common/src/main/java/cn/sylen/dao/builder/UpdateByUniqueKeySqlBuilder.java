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
 * 生成update语句的sql生成器
 * @author hongwm
 * @since 2013-8-25
 */
public class UpdateByUniqueKeySqlBuilder extends QuerySqlBuilderBase implements SqlBuilder {

	private static Logger logger = LoggerFactory.getLogger(UpdateByUniqueKeySqlBuilder.class);

	private ObjectTableMapper objectTableMapper;

	private boolean ignoreNullColumn = false;

	public UpdateByUniqueKeySqlBuilder(ObjectTableMapper objectTableMapper, boolean ignoreNullColumn) {
		this.objectTableMapper = objectTableMapper;
		this.ignoreNullColumn = ignoreNullColumn;
	}

	@Override
	public void buildSql(Map<String, Object> parameterMap) {
		FieldMapper primaryFeild = null;
		FieldMapper uniqueFeild = null;

		List<FieldMapper> mappedFields = objectTableMapper.getMappedFields();
		boolean nullDateCreateColumn = false;
		for(FieldMapper field : mappedFields) {
			if(field.isDateUpdateColumn()) {
				// 是delete_update列,置为当前时间
				field.getObjectField().setValue(new Date());
			}

			if(field.isDateCreateColumn() && field.isNullObject()) {
				// date_create字段为null
				nullDateCreateColumn = true;
			}

			if(!field.isNullObject()) {
				if(field.isPrimaryKey()) {
					primaryFeild = field;
				} else if(field.isUniqueKey()) {
					uniqueFeild = field;
				}

				parameterMap.put(field.getObjectField().getUpdateFieldName(),
						field.getObjectField().getValue());
			}
		}

		StringBuffer sqlSB = new StringBuffer();
		sqlSB.append("update ")
			 .append(objectTableMapper.getTableSchema().getTableName())
			 .append(" set ")
			 .append(getUpdateParams(nullDateCreateColumn))
			 .append(" where ");

		FieldMapper queryField = null;
		if(primaryFeild != null) {
			queryField = primaryFeild;
		} else if(uniqueFeild != null) {
			queryField = uniqueFeild;
		}

		if(queryField == null) {
			throw new RuntimeException("not legal uniq key set");
		}

		parameterMap.put(queryField.getObjectField().getUpdateFieldName(), queryField.getObjectField().getValue());
		sqlSB.append(queryField.getColumnFieldName())
			.append(StringUtil.formatString(" = #{%s}",
					queryField.getObjectField().getUpdateFieldName()));
		parameterMap.put("sql", sqlSB.toString());
		if(logger.isDebugEnabled()){
			logger.debug(sqlSB.toString());
		}

		setupThreadLocal(parameterMap);
//		ThreadLocalUtil.addSql(sqlSB.toString());

	}

	public String getUniqueParams() {

		return null;
	}

	public String getUpdateParams(boolean nullDateCreateColumn) {
		List<FieldMapper> mappedFields = objectTableMapper.getMappedFields();
		if(CollectionUtils.isEmpty(mappedFields)) {
			return null;
		}

		StringBuffer sb = new StringBuffer();
		for(FieldMapper mappedField : mappedFields) {
			if(!mappedField.isLegalUpdateColumn()) {
			    if(ignoreNullColumn || nullDateCreateColumn)
				// 如果dateCreate字段为null，则只更新有效(不为null)的字段
				continue;
			}

			if(sb.length() > 0) {
				sb.append(", ");
			}

            String fieldName = mappedField.getColumnFieldNameWithSymbol();
            if(objectTableMapper.getTableSchema().isPGSchema()) {
                fieldName = mappedField.getColumnFieldNameWithQuote();
            }

			sb.append(fieldName).append(" = ");
			if(mappedField.isNullObject()) {
				String defaultValue = mappedField.getObjectField().getUpdateValue();

				if(defaultValue == null || "".equals(defaultValue)) {
					sb.append("null");
				} else if(defaultValue.indexOf("(") != -1) {
					// 包含sql函数标识
					sb.append(defaultValue);
				} else {
					sb.append(StringUtil.formatString("'%s'", defaultValue));
				}
			} else {
				sb.append(StringUtil.formatString("#{%s}", mappedField.getObjectField().getUpdateFieldName()));
			}
		}
		return sb.toString();
	}

	public void setIgnoreNullColumn(boolean ignoreNullColumn) {
        this.ignoreNullColumn = ignoreNullColumn;
    }
}
