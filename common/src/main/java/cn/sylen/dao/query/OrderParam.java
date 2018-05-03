package cn.sylen.dao.query;

import cn.sylen.common.util.StringUtil;

/**
 * 排序参数
 * @author hongwm
 * @since 2013-8-20
 */
public class OrderParam {
    public final static String ORDER_ASC = "ASC";
    public final static String ORDER_DESC = "DESC";
    private String orderName;
    private String orderType = ORDER_DESC;
    private boolean toGenOrderTableHead = true;	// 是否要加上表名的头部


    public OrderParam(String orderName) {
        this.orderName = orderName;
    }

    public OrderParam(String orderName, String orderType) {
        this.orderName = orderName;
        this.orderType = orderType;
    }

    public OrderParam(String orderName, String orderType, boolean toGenOrderTableHead) {
        this.orderName = orderName;
        this.orderType = orderType;
        this.toGenOrderTableHead = toGenOrderTableHead;
    }

    public String getOrderName() {
		return orderName;
	}

	public void setOrderName(String orderName) {
		this.orderName = orderName;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public boolean isValid() {
		if(StringUtil.isEmpty(orderName) || StringUtil.isEmpty(orderType)) {
			return false;
		}

		return true;
	}

	public boolean isToGenOrderTableHead() {
		return toGenOrderTableHead;
	}

	public void setToGenOrderTableHead(boolean toGenOrderTableHead) {
		this.toGenOrderTableHead = toGenOrderTableHead;
	}

	public String toString() {
        if(orderName == null) {
            return null;
        }

        StringBuffer sb = new StringBuffer();
        sb.append("order by ")
          .append(orderName)
          .append(" ").append(orderType);
        return sb.toString();
    }
}
