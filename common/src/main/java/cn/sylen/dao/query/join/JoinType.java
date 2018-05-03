
package cn.sylen.dao.query.join;

/**
 * 定义数据库对象间的join类型
 * @author hongwm
 * @since 2013-8-19
 */
public enum JoinType {
	INNER_JOIN, LEFT_JOIN, RIGHT_JOIN;

	public String toString() {
		switch(this) {
			case INNER_JOIN:
				return "INNER JOIN";
			case LEFT_JOIN:
				return "LEFT JOIN";
			case RIGHT_JOIN:
				return "RIGHT JOIN";
			default:
				return "LEFT JOIN";
		}
	}
}
