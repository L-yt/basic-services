package cn.sylen.dao.query.join;

/**
 * 用来join的pair对
 * @author hongwm
 * @since 2013-8-20
 */
public class JoinPair {
	/**
	 * 主表的字段
	 */
	private String leftKey;
	/**
	 * 右表的字段
	 */
	private String rightKey;

	public JoinPair(){
	}

	public JoinPair(String leftKey, String rightKey) {
		this.leftKey = leftKey;
		this.rightKey = rightKey;
	}

	public String getLeftKey() {
		return leftKey;
	}
	public void setLeftKey(String leftKey) {
		this.leftKey = leftKey;
	}
	public String getRightKey() {
		return rightKey;
	}
	public void setRightKey(String rightKey) {
		this.rightKey = rightKey;
	}
}
