package cn.sylen.common.spring.mvc.interceptor;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.handler.MappedInterceptor;
import org.springframework.web.util.UrlPathHelper;

/**
 * 用户自定义的拦截器列表
 * @author hongwm
 * @since 2015-03-29
 */
public class RequestIntercepAggregator extends HandlerInterceptorAdapter implements InitializingBean {
    private Logger logger = LoggerFactory.getLogger(RequestIntercepAggregator.class);
    
    private UrlPathHelper urlPathHelper = new UrlPathHelper();
    private PathMatcher pathMatcher = new AntPathMatcher();
    private List<MappedInterceptor> userInterceptors = new ArrayList<MappedInterceptor>();
    private List<HandlerInterceptorAdapter> systemInterceptors = new ArrayList<HandlerInterceptorAdapter>();

    public RequestIntercepAggregator() {
        // 加入时间统计日志
        systemInterceptors.add(new RequestTimerInterceptor());
    }
    
    public void postHandle(HttpServletRequest request, 
            HttpServletResponse response, Object handler, 
            ModelAndView modelAndView) throws Exception{
        String lookupPath = this.urlPathHelper.getLookupPathForRequest(request);

        for(MappedInterceptor interceptor : userInterceptors) {
            if(interceptor.matches(lookupPath, pathMatcher)) {
            	/**
            	 * modified by wangrenjie 2016-03-23
            	 * 用户拦截器不处理异常
            	 */
            	interceptor.getInterceptor().postHandle(request, response, handler, modelAndView);
            }
        }
        
        for(HandlerInterceptorAdapter interceptor : systemInterceptors) {
            try {
                interceptor.postHandle(request, response, handler, modelAndView);
            } catch (Exception e) {
                logger.warn("", e);
            }
        }
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String lookupPath = this.urlPathHelper.getLookupPathForRequest(request);

        for(HandlerInterceptorAdapter interceptor : systemInterceptors) {
            try {
                if(!interceptor.preHandle(request, response, handler)) {
                    // 该拦截器处理返回false
                    return false;
                }
            } catch (Exception e) {
                logger.warn("", e);
            }
        }
        
        for(MappedInterceptor interceptor : userInterceptors) {
            if(interceptor.matches(lookupPath, pathMatcher)) {
            	/**
            	 * modified by wangrenjie 2016-03-23
            	 * 用户拦截器不处理异常
            	 */
            	if(!interceptor.getInterceptor().preHandle(request, response, handler)) {
                  // 该拦截器处理返回false
                  return false;
              }
            }
        }
        
        return true;
    }

    public void afterCompletion(
            HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
                    throws Exception {
        String lookupPath = this.urlPathHelper.getLookupPathForRequest(request);

        for(MappedInterceptor interceptor : userInterceptors) {
            if(interceptor.matches(lookupPath, pathMatcher)) {
                try {
                    interceptor.getInterceptor().afterCompletion(request, response, handler, ex);
                } catch (Exception e) {
                    logger.warn("", e);
                }
            }
        }

        for(HandlerInterceptorAdapter interceptor : systemInterceptors) {
            try {
                interceptor.afterCompletion(request, response, handler, ex);
            } catch (Exception e) {
                logger.warn("", e);
            }
        }

    }

    public void setInterceptorList(InterceptorList interceptorList) {
        System.out.println("start to set interceptorList: " + interceptorList);
        userInterceptors = new ArrayList<MappedInterceptor>();
        for(InterceptorWrapper inteceptorWrapper: interceptorList.getInterceptors()) {
            MappedInterceptor mappedInteceptor = new MappedInterceptor(
                    inteceptorWrapper.getIncludePatterns(), inteceptorWrapper.getExcludePatterns(), 
                    inteceptorWrapper.getInteceptor());

            userInterceptors.add(mappedInteceptor);
        }
    }
    
    // called after properties set
    public void afterPropertiesSet() {
    }
}
