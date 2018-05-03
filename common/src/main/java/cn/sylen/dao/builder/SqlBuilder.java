package cn.sylen.dao.builder;

import java.util.Map;

/**
 * sql builder
 * @author hongwm
 * @since 2013-8-25
 */
public interface SqlBuilder {
	/**
	 * 把sql语句放入到parameterMap中去
	 */
	public void buildSql(Map<String, Object> parameterMap);

}
