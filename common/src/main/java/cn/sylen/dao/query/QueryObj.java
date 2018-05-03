package cn.sylen.dao.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.sylen.dao.query.join.JoinParam;

/**
 * sql查询obj
 * @author hongwm
 * @since 2013-8-19
 */
public class QueryObj implements Serializable {
	private static final long serialVersionUID = 2604198323788189603L;
	/**
	 * 查询的query do
	 */
	private Object querydo;
	/**
	 * 额外的查询参数
	 */
	private QueryParam queryParam;
	/**
	 * join参数
	 */
	private List<JoinParam> joinParams =
			new ArrayList<JoinParam>();


	/**
	 * 按照某些字段去重
	 */
	private List<DistinctParam> distinctParams =
			new ArrayList<DistinctParam>();

	/**
	 * 排序参数
	 */
	private List<OrderParam> orderParams =
			new ArrayList<OrderParam>();
	/**
	 * 页码信息
	 */
	private PageInfo pageInfo = new PageInfo();

	public QueryObj() {
	}

	public QueryObj(Object sample) {
	    this.querydo = sample;
	}

	public Object getQuerydo() {
		return querydo;
	}
	public void setQuerydo(Object querydo) {
		this.querydo = querydo;
	}
	public QueryParam getQueryParam() {
		return queryParam;
	}
	public void setQueryParam(QueryParam queryParam) {
		this.queryParam = queryParam;
	}
	public List<JoinParam> getJoinParams() {
		return joinParams;
	}
	public void setJoinParams(List<JoinParam> joinParams) {
		this.joinParams = joinParams;
	}
	public List<OrderParam> getOrderParams() {
		return orderParams;
	}
	public void setOrderParams(List<OrderParam> orderParams) {
		this.orderParams = orderParams;
	}

	public void addOrderParam(String key, String orderType) {
		this.orderParams.add(new OrderParam(key, orderType));
	}

	public void addDistinctParam(String key) {
		this.distinctParams.add(new DistinctParam(key));
	}


	public PageInfo getPageInfo() {
		return pageInfo;
	}
	public void setPageInfo(PageInfo pageInfo) {
		this.pageInfo = pageInfo;
	}

	public void addQuery(String sql, Object ... params) {
	    addQueryParam(new QueryParam(sql, params));
	}

    public void addQueryParam(QueryParam queryParam) {
        if(this.queryParam == null) {
            this.queryParam = new QueryParam();
        }
        this.queryParam.andParameter(queryParam);
    }
    public void orQueryParam(QueryParam queryParam) {
        if(this.queryParam == null) {
            this.queryParam = new QueryParam();
        }
        this.queryParam.orParameter(queryParam);
    }
    public void notQueryParam(QueryParam queryParam) {
        if(this.queryParam == null) {
            this.queryParam = new QueryParam();
        }
        this.queryParam.notParameter(queryParam);
    }
    public void addJoinParam(JoinParam joinParam) {
    	this.joinParams.add(joinParam);
    }

	public Map<String, Object> getParamMap() {
		if(queryParam == null) {
			return new HashMap<String, Object>();
		}
		return queryParam.getParameterMap();
	}



	public List<DistinctParam> getDistinctParams() {
		return distinctParams;
	}
	public void setDistinctParams(List<DistinctParam> distinctParams) {
		this.distinctParams = distinctParams;
	}
	public void setPage(int page) {
		pageInfo.setPage(page);
	}

	public void setPageSize(int pageSize) {
		pageInfo.setPageSize(pageSize);
	}
	public void plusCurPage() {
		pageInfo.plusCurPage();
	}
}
