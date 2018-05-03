package cn.sylen.dao.query;

import cn.sylen.common.util.StringUtil;

/**
 * 支持针对具体字段的去重
 * @author ming
 * 2014-4-3
 */
public final class DistinctParam {

    private String distinctName;
    private boolean toGenDistinctTableHead = true;	// 是否要加上表名的头部


    public DistinctParam(String distinctName) {
        this.distinctName = distinctName;
    }

    public DistinctParam(String distinctName, boolean toGenDistinctTableHead) {
        this.distinctName = distinctName;
        this.toGenDistinctTableHead = toGenDistinctTableHead;
    }

    public String getDistinctName() {
		return distinctName;
	}

	public void setDistinctName(String distinctName) {
		this.distinctName = distinctName;
	}

	public boolean isValid() {
		if(StringUtil.isEmpty(distinctName)) {
			return false;
		}

		return true;
	}

	public boolean isToGenDistinctTableHead() {
		return toGenDistinctTableHead;
	}

	public void setToGenDistinctTableHead(boolean toGenDistinctTableHead) {
		this.toGenDistinctTableHead = toGenDistinctTableHead;
	}

	public String toString() {
        if(distinctName == null) {
            return null;
        }

        StringBuffer sb = new StringBuffer();
        sb.append("group by ")
          .append(distinctName);
        return sb.toString();
    }

}
