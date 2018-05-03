package cn.sylen.dao.query;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cn.sylen.common.util.CollectionUtils;
import cn.sylen.dao.builder.SqlUtil;

/**
 * Advanced Query Parameters, can be nested unlimited. Example:
 *
 * <pre>
 * QueryParam queryParam1 = new QueryParam(&quot;name&quot;, &quot;like&quot;, &quot;admin%&quot;);
 * QueryParam queryParam2 = new QueryParam(&quot;id&quot;, &quot;&gt;&quot;, new Integer(100));
 * QueryParam queryParam3 = new QueryParam(&quot;age&quot;, &quot;&lt;&quot;, new Integer(30));
 * QueryParam queryParam4 = new QueryParam(&quot;creationDate &gt; '2004-10-24'&quot;);
 * QueryParam queryParam = new QueryParam();
 * queryParam.andParameter(queryParam1);
 * queryParam.andParameter(queryParam2);
 * queryParam.notParameter(queryParam3);
 * queryParam.notParameter(queryParam4);
 * </pre>
 *
 * the above code will generate the sql: where (name like admin% and id > 100) and not age < 30 and not creationDate > '2004-10-24')
 *
 * Note: A QueryParam can not be nested when his model is BASIC.
 */
public final class QueryParam {
    private String sql;

    private List<QueryParam> andParams;
    private List<QueryParam> orParams;
    private List<QueryParam> notParams;

    private Map<String, Object> parameterMap =
    		new HashMap<String, Object>();

    public QueryParam(String sql, Object ... objs) {
        this.sql = sql;
        normalizeSqlParams(sql, objs);
    }

    public QueryParam() {
    }

    public QueryParam andParameter(String sql, Object ... objs) {
    	if(sql == null) return this;
        return this.andParameter(new QueryParam(sql, objs));
    }

    public QueryParam andParameter(QueryParam queryParam) {
        if (andParams == null) {
            andParams = new LinkedList<QueryParam>();
        }
        andParams.add(queryParam);
        return this;
    }

    public QueryParam orParameter(String sql, Object ... objs) {
    	if(sql == null) return this;
        return this.orParameter(new QueryParam(sql, objs));
    }

    public QueryParam orParameter(QueryParam queryParam) {
        if (orParams == null) {
            orParams = new LinkedList<QueryParam>();
        }
        orParams.add(queryParam);
        return this;
    }

    public QueryParam notParameter(String sql, Object ... objs) {
    	if(sql == null) return this;
        return this.notParameter(new QueryParam(sql));
    }

    public QueryParam notParameter(QueryParam queryParam) {
        if (notParams == null) {
            notParams = new LinkedList<QueryParam>();
        }
        notParams.add(queryParam);
        return this;
    }

    public void normalizeSqlParams(String sql, Object ...objects) {
    	List<String> sqlParams = SqlUtil.getSqlParamNames(sql);
    	this.sql = sqlParams.get(0);
    	for(int i=1; i<sqlParams.size(); i++) {
    		if(i <= objects.length) {
    			putParam(sqlParams.get(i), objects[i-1]);
    		}
    	}
    }

    public void putParam(String key, Object value) {
    	if(!key.startsWith("_")) {
    		key = "_" + key;
    	}
    	parameterMap.put(key, value);
    }

    public String getSql() {
        return sql;
    }

    public String toString() {
    	if(sql == null
    			&& CollectionUtils.isEmpty(andParams)
    			&& CollectionUtils.isEmpty(orParams)
    			&& CollectionUtils.isEmpty(notParams)) {
    		return null;
    	}

        StringBuffer sb = new StringBuffer();
        //sb.append('(');
        if (sql != null) {
        	sb.append(sql);
        	if(hasSubCondition()) {
        		sb.insert(0, '(');
        		sb.append(')');
        	}
        }

        if (andParams != null && andParams.size() > 0) {
        	if (sb.length() > 2) {
        		sb.append(" and ");
        	}
        	boolean firstFlag = true;
        	for (Iterator<QueryParam> iterator = andParams.iterator(); iterator.hasNext(); ) {
        		QueryParam q = (QueryParam) iterator.next();

	        	if(q.toString() != null){

	        		if (firstFlag) {
	        			firstFlag = false;
	        		} else {
	        			sb.append(" and ");
	        		}

	        		sb.append(q.toString());
        		}
        	}
        }
        if (orParams != null && orParams.size() > 0) {
        	if (sb.length() > 2) {
        		sb.append(" or ");
        	}
        	boolean firstFlag = true;
        	for (Iterator<QueryParam> iterator = orParams.iterator(); iterator.hasNext(); ) {
        		QueryParam q = (QueryParam) iterator.next();

        		if(q.toString() != null){
	        		if (firstFlag) {
	        			firstFlag = false;
	        		} else {
	        			sb.append(" or ");
	        		}
	        		sb.append(q.toString());
        		}
        	}
        }
        if (notParams != null && notParams.size() > 0) {
        	if (sb.length() > 2) {
        		sb.append(" and ");
        	}
        	boolean firstFlag = true;
        	for (Iterator<QueryParam> iterator = notParams.iterator(); iterator.hasNext(); ) {
        		QueryParam q = (QueryParam) iterator.next();
        		if(q.toString() != null){
	        		if (firstFlag) {
	        			firstFlag = false;
	        		} else {
	        			sb.append(" and ");
	        		}
	        		sb.append("not ");
	        		sb.append(q.toString());
        		}
        	}
        }

        if(!sb.toString().equals("")){
        	sb.insert(0, "(");
        	sb.append(')');
        }

        return sb.toString();
    }

    protected boolean hasSubCondition() {

    	return !CollectionUtils.isEmpty(orParams) || !CollectionUtils.isEmpty(andParams) || !CollectionUtils.isEmpty(notParams);
    }

    public Map<String, Object> getParameterMap() {
    	if(orParams != null) {
    		for(QueryParam p : orParams) {
    			parameterMap.putAll(p.getParameterMap());
    		}
    	}

    	if(andParams != null) {
    		for(QueryParam p : andParams) {
    			parameterMap.putAll(p.getParameterMap());
    		}
    	}
    	if(notParams != null) {
    		for(QueryParam p : notParams) {
    			parameterMap.putAll(p.getParameterMap());
    		}
    	}

    	return parameterMap;
    }
}
