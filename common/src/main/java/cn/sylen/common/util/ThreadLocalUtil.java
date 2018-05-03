package cn.sylen.common.util;

import java.util.Map;

/**
 * 存放thread local的变量
 * @author hongwm
 * @since 2013-8-13
 */
public class ThreadLocalUtil {
	/**
	 * 登录上下文信息的thread local
	 */
	private static ThreadLocal<Object> loginContexts = new ThreadLocal<Object>();
	/**
	 * 记录时间性能time profile的thread local
	 */
	private static ThreadLocal<TimeProfile> profileContexts = new ThreadLocal<TimeProfile>();

	/**
	 * http servlet request的thread local
	 */
	private static ThreadLocal<Object> httpRequest = new ThreadLocal<Object>();

	/**
	 * http response的thread local
	 */
	private static ThreadLocal<Object> httpResponse = new ThreadLocal<Object>();

	private static ThreadLocal<String> sqlLocal = new ThreadLocal<String>();

	private static ThreadLocal<Map<String, Object>> sqlQueryMapLocal = new ThreadLocal<Map<String, Object>>();

	private static ThreadLocal<Object> sqlSessionLocal = new ThreadLocal<Object>();

	public static void setLoginContext(Object loginContext) {
		loginContexts.set(loginContext);
	}

	public static void removeLoginContext() {
		loginContexts.remove();
	}

	public static Object getLoginContext() {
		return loginContexts.get();
	}

	public static void setTimeProfileContext(TimeProfile timeProfile) {
		profileContexts.set(timeProfile);
	}

	public static void removeTimeProfileContext() {
		profileContexts.remove();
	}

	public static void setupTimeProfile() {
	    if(profileContexts.get() == null) {
	        TimeProfile profile = new TimeProfile();
	        setTimeProfileContext(profile);
	    }
	}

	public static TimeProfile getTimeProfileContext() {
	    TimeProfile profile = profileContexts.get();
	    if(profile == null) {
	        profile = new TimeProfile();
	        profileContexts.set(profile);
	    }
		return profile;
	}

	public static void setHttpRequest(Object request) {
		httpRequest.set(request);
	}

	public static void removeHttpRequest() {
		httpRequest.remove();
	}

	public static Object getHttpRequest() {
		return httpRequest.get();
	}

	public static void setHttpResponse(Object response) {
		httpResponse.set(response);
	}

	public static void removeHttpResponse() {
		httpResponse.remove();
	}

	public static Object getHttpResponse() {
		return httpResponse.get();
	}

	public static String getSql() {
		return sqlLocal.get();
	}

	public static void addSql(String sql) {
		sqlLocal.set(sql);
	}
	public static void removeSql() {
		sqlLocal.remove();
	}

	public static Object getSqlSession() {
        return sqlSessionLocal.get();
    }

	public static void setSqlSession(Object sqlSession) {
	    sqlSessionLocal.set(sqlSession);
	}
	public static void removeSqlSession() {
	    sqlSessionLocal.remove();
	}

	public static void setSqlQueryMap(Map<String, Object> queryMap) {
	    sqlQueryMapLocal.set(queryMap);
	}

	public static void removeSqlQueryMap() {
	    sqlQueryMapLocal.remove();
	}

	public static Map<String, Object> getSqlQueryMap() {
	    return sqlQueryMapLocal.get();
	}

	public static void removeAllThreadLocalContext() {
		loginContexts.remove();
		profileContexts.remove();
		httpRequest.remove();
		httpResponse.remove();
		sqlLocal.remove();
		sqlSessionLocal.remove();
		sqlQueryMapLocal.remove();
	}
}
