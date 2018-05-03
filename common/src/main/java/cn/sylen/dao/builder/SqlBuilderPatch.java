package cn.sylen.dao.builder;

import java.util.Map;


/*
 * sqlbuilder接口补丁，支持优化后的count取总生成parameterMap
 */
public interface SqlBuilderPatch {
	/**
	 *
	 * @param parameterMap
	 */
	public void buildShortCountSql(Map<String, Object> parameterMap);
}
