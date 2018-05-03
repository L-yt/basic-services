package cn.sylen.dao.builder;

import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.sylen.dao.mapping.FieldMapper;

import cn.sylen.common.util.CollectionUtils;
import cn.sylen.common.util.StringUtil;
import cn.sylen.dao.mapping.ObjectTableMapper;
import cn.sylen.dao.query.QueryObj;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 生成update语句的sql生成器
 * @author hongwm
 * @since 2013-8-25
 */
public class UpdateByQuerySqlBuilder extends QuerySqlBuilder implements SqlBuilder {

    private static Logger logger = LoggerFactory.getLogger(UpdateByQuerySqlBuilder.class);

    private ObjectTableMapper updateObjectTableMapper;

    public UpdateByQuerySqlBuilder(ObjectTableMapper objectTableMapper, QueryObj queryObj) {
        super(queryObj);
        this.updateObjectTableMapper = objectTableMapper;
    }

    @Override
    public void buildSql(Map<String, Object> parameterMap) {
        List<FieldMapper> mappedFields = updateObjectTableMapper.getMappedFields();
        for(FieldMapper field : mappedFields) {
            if(field.isDateUpdateColumn()) {
                // 是delete_update列,置为当前时间
                field.getObjectField().setValue(new Date());
            }

            if(!field.isNullObject()) {
                parameterMap.put("u_" + field.getObjectField().getUpdateFieldName(),
                        field.getObjectField().getValue());
            }
        }

        parameterMap.putAll(super.getQueryParameterMap());

        String queryParam = genQueryParams();
        StringBuffer sqlSB = new StringBuffer();
        sqlSB.append("update ")
        .append(updateObjectTableMapper.getTableSchema().getTableName())
        .append(" ").append(getTableNameMap(updateObjectTableMapper.getTableSchema().getTableName())).append(" ")
        .append(" set ")
        .append(getUpdateParams())
        .append(" where ")
        .append(queryParam);

        if(queryParam.length() < 10) {
            throw new RuntimeException(StringUtil.formatString("too short query params[%s]", queryParam));
        }

        //		parameterMap.put(queryField.getObjectField().getUpdateFieldName(), queryField.getObjectField().getValue());
        //		sqlSB.append(queryField.getColumnFieldName())
        //			.append(StringUtil.formatString(" = #{%s}",
        //					queryField.getObjectField().getUpdateFieldName()));
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

    public String getUpdateParams() {
        List<FieldMapper> mappedFields = updateObjectTableMapper.getMappedFields();
        if(CollectionUtils.isEmpty(mappedFields)) {
            return null;
        }

        StringBuffer sb = new StringBuffer();
        for(FieldMapper mappedField : mappedFields) {
            if(!mappedField.isLegalUpdateColumn()) {
                continue;
            }

            if(sb.length() > 0) {
                sb.append(", ");
            }

            String fieldName = mappedField.getColumnFieldNameWithSymbol();
            if(updateObjectTableMapper.getTableSchema().isPGSchema()) {
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
                sb.append(StringUtil.formatString("#{%s}", "u_" + mappedField.getObjectField().getUpdateFieldName()));
            }
        }
        return sb.toString();
    }

}
