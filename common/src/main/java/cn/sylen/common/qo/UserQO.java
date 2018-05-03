package cn.sylen.common.qo;

import cn.sylen.common.vo.UserEntity;

/**
 * 用户查询对象
 * @author Sylen
 * 2018-4-11
 */
public class UserQO extends UserEntity {

	private Integer pageIndex;
    private Integer pageSize;
    
	public Integer getPageIndex() {
		return pageIndex;
	}
	public void setPageIndex(Integer pageIndex) {
		this.pageIndex = pageIndex;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
    
}
