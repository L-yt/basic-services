package cn.sylen.dao.builder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import cn.sylen.dao.mapping.FieldMapper;

import cn.sylen.common.util.CollectionUtils;
import cn.sylen.common.util.ThreadLocalUtil;
import cn.sylen.dao.mapping.ObjectTableMapper;

/**
 * 生成query语句的sql生成基类
 * @author hongwm
 * @since 2013-8-2９
 */
public abstract class QuerySqlBuilderBase implements SqlBuilder{
	private final static String[] chars = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t"};
	/**
	 * 表名关联map
	 */
	private Map<String, String> tableNameMap = new HashMap<String, String>();

	/**
	 * 根据映射关系,获取表的查询字段
	 * @param objectTableMapper
	 * @return
	 */
	public String getQueryColumnsByTableMapper (
			ObjectTableMapper objectTableMapper,
			String tableName,
			Set<String> existedFieldSet) {
		List<FieldMapper> mappedFields = objectTableMapper.getAllMappedFields();
		if(CollectionUtils.isEmpty(mappedFields)) {
			return "";
		}

		String tableMapName = getTableNameMap(tableName);
		StringBuffer sb = new StringBuffer();
		for(FieldMapper mappedField : mappedFields) {
			if(existedFieldSet.contains(mappedField.getObjectField().getFieldName())) {
				// 该字段已经存在了,忽略该字段
				continue;
			}

			if(!tableName.equals(mappedField.getColumn().getTableSchema().getTableName())) {
				continue;
			}

			if(sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(tableMapName).append(".")
			  .append(mappedField.getColumnFieldName()).append(" as ")
			  .append(mappedField.getObjectField().getFieldName());
			existedFieldSet.add(mappedField.getObjectField().getFieldName());
		}

		return sb.toString();
	}

	protected void setupThreadLocal(Map<String, Object> parameterMap) {
	    String sql = (String) parameterMap.get("sql");
	    Map<String, Object> cloneMap = Maps.newHashMap();
	    cloneMap.putAll(parameterMap);

	    ThreadLocalUtil.addSql(sql);

	    cloneMap.remove("sql");
	    ThreadLocalUtil.setSqlQueryMap(cloneMap);
	}

	/**
	 * 获取表名的映射字母
	 * @param tableName
	 * @return
	 */
	public String getTableNameMap(String tableName) {
		if(tableName == null) {
			return null;
		}
		if(tableName.length() <= 1) {
			return tableName;
		}

		String mapName = tableNameMap.get(tableName);
		if(mapName == null) {
			mapName = chars[tableNameMap.size()];
			tableNameMap.put(tableName, mapName);
		}
		return mapName;
	}

	public Map<String, String> getTableNameMap() {
		return tableNameMap;
	}
}
