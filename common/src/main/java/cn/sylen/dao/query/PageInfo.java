package cn.sylen.dao.query;

import java.io.Serializable;

/**
 * 主要用于给ibatis中startPage,和pageSize传
 * @author mingming
 * @version 1.0
 **/

public class PageInfo implements Serializable {
    private static final long serialVersionUID = -3125985325628759677L;
    /**
	 * 当前页
	 */
	private int page = 1;
	/**
	 * 分页单位初始化为1000
	 */
	private int pageSize = 1000;

	/**
	 * 查询分页时的偏移量
	 */
	private int offset;

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int curPage) {
		this.page = curPage;
	}

	public int getStartPos(){
		if(this.getPage()>0 && getPageSize()>0){
			return (this.getPage()-1)*getPageSize()+offset;
		}else{
			return 0;
		}
	}

	public int getPageSize() {
		return pageSize>0?pageSize:20;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public void plusCurPage() {
	    this.page = this.page + 1;
	}

	public String toString() {
	    return page + "-" + pageSize + "-" + offset;
	}

	public int hashCode(){
		return Integer.valueOf(pageSize).hashCode()+Integer.valueOf(page);
	}
}
