package cn.sylen.dao.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.sylen.dao.mapping.FieldMapper;

import cn.sylen.common.exception.CoreException;
import cn.sylen.common.util.CollectionUtils;
import cn.sylen.common.util.StringUtil;
import cn.sylen.dao.mapping.ObjectTableMapper;

/**
 * 生成删除语句的sql生成器
 * @author hongwm
 * @since 2013-8-29
 */
public class DeleteSqlBuilder extends QuerySqlBuilderBase implements SqlBuilder {
	private Logger logger = LoggerFactory.getLogger(DeleteSqlBuilder.class);

	/**
	 * 主表的meta信息
	 */
	private ObjectTableMapper objectTableMapper;
	private boolean softDel;

	public DeleteSqlBuilder(ObjectTableMapper objectTableMapper, boolean softDel) {
		this.objectTableMapper = objectTableMapper;
		this.softDel = softDel;
	}

	@Override
	public void buildSql(Map<String, Object> parameterMap) {
		List<FieldMapper> mappedFields = objectTableMapper.getMappedFields();
		List<FieldMapper> legalFields = new ArrayList<FieldMapper>();

		String tableName = objectTableMapper.getTableSchema().getTableName();
		for(FieldMapper field : mappedFields) {
			if(!field.isPrimaryKey() && !field.isUniqueKey()) {
				continue;
			}

			if(!field.isNullObject()) {
				legalFields.add(field);
			}
			parameterMap.put(field.getObjectField().getUpdateFieldName(),
					field.getObjectField().getValue());
		}

		if(CollectionUtils.isEmpty(legalFields)) {
			throw new CoreException("not legal unique key get when del object from " + objectTableMapper.getTableSchema().getTableName());
		}

		StringBuffer delparamSB = new StringBuffer();
		for(FieldMapper field : legalFields) {
			if(delparamSB.length() > 0) {
				delparamSB.append(" or ");
			}
			delparamSB.append(field.getColumnFieldName()).append(" = ")
					  .append(StringUtil.formatString("#{%s}",
							  field.getObjectField().getUpdateFieldName()));
		}

		StringBuffer sqlSB = new StringBuffer();
		if(softDel) {
			sqlSB.append("update ").append(tableName)
				 .append(" set deleted = now()");
		} else {
			sqlSB.append("delete from ").append(tableName);
		}

		sqlSB.append(" where ").append(delparamSB.toString());
		parameterMap.put("sql", sqlSB.toString());

		setupThreadLocal(parameterMap);
//		ThreadLocalUtil.addSql(sqlSB.toString());

		if(logger.isDebugEnabled()){
			logger.debug(sqlSB.toString());
		}
	}

}
