package cn.sylen.dao.query.join;

import java.util.ArrayList;
import java.util.List;

import cn.sylen.dao.query.QueryParam;


/**
 * 定义join参数
 * @author hongwm
 * @since 2013-8-19
 */
public class JoinParam {
	/**
	 * 要join表的class
	 */
	private Class<?> joinClass;
	/**
	 * join类型：INERJOIN、LEFTJOIN、RIGHTJOIN
	 */
	private JoinType joinType = JoinType.LEFT_JOIN;

	/**
	 * 用来和主表进行join的条件字段
	 */
	private List<JoinPair> joinPairs =
			new ArrayList<JoinPair>();
	/**
	 * 对该表的join是否为distinct结果
	 */
	private boolean distinct;

	/**
	 * 针对这个表的查询参数
	 */
	private QueryParam queryParam;

	public JoinParam(){}

	public JoinParam(Class<?> joinClass, JoinType joinType, JoinPair joinPair) {
		this(joinClass, joinType, joinPair, false);
	}

	public JoinParam(Class<?> joinClass, JoinType joinType, JoinPair joinPair, boolean distinct) {
		this.joinClass = joinClass;
		this.joinType = joinType;
		this.joinPairs.add(joinPair);
		this.distinct = distinct;
	}

	public Class<?> getJoinClass() {
		return joinClass;
	}
	public void setJoinClass(Class<?> joinClass) {
		this.joinClass = joinClass;
	}
	public JoinType getJoinType() {
		return joinType;
	}
	public void setJoinType(JoinType joinType) {
		this.joinType = joinType;
	}
	public List<JoinPair> getJoinPairs() {
		return joinPairs;
	}
	public void setJoinPairs(List<JoinPair> joinPairs) {
		this.joinPairs = joinPairs;
	}
	public QueryParam getQueryParam() {
		return queryParam;
	}
	public void setQueryParam(QueryParam queryParam) {
		this.queryParam = queryParam;
	}

	public boolean isDistinct() {
		return distinct;
	}

	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}

}
