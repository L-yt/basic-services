package cn.sylen.common.spring.mvc.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import cn.sylen.common.util.CollectionUtils;
import cn.sylen.common.util.CommonUtil;
import cn.sylen.common.util.Entry;
import cn.sylen.common.util.ThreadLocalUtil;
import cn.sylen.common.util.TimeProfile;

/**
 * 统计时间请求的拦截器
 */
public class RequestTimerInterceptor extends HandlerInterceptorAdapter {
    private Logger logger = LoggerFactory.getLogger(RequestTimerInterceptor.class);

	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) {
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        ThreadLocalUtil.getTimeProfileContext();
        // 禁用打印出本地日志
        String userAgent = request.getHeader("user-agent");
        String refer = request.getHeader("referer");
        String host = request.getHeader("host");
        userAgent = (userAgent == null ? "" : userAgent);

        StringBuffer sb = new StringBuffer();
        sb.append("request url, ")
          .append("url: ").append(host + CommonUtil.getRequestUrl(request, true))
          .append(", refer: ").append(refer)
          .append(", ip: ").append(CommonUtil.getRemoteIpAddress(request));

        logger.info(sb.toString());
        return true;
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex){

        // 禁用应用端的日志打印
        String requestUrl = CommonUtil.getRequestUrl(request, false);
        TimeProfile timeProfile = (TimeProfile) ThreadLocalUtil.getTimeProfileContext();
        if (timeProfile != null) {
            StringBuffer sb = new StringBuffer();
            sb.append("request ").append(requestUrl).append(" costs ").append(timeProfile.getEclapseTime()).append("ms");

            List<Entry<String, Long>> markTimes = timeProfile.getMarkEclapseTimes();
            if (CollectionUtils.isNotEmpty(markTimes)) {
                sb.append(", mark eclapses: [");
                for (int i = 0; i < markTimes.size(); i++) {
                    if (i > 0) {
                        sb.append(", ");
                    }
                    Entry<String, Long> e = markTimes.get(i);
                    sb.append(e.getKey()).append(":").append(e.getValue()).append("ms");
                }
                sb.append("]");
            }
            logger.info(sb.toString());
            ThreadLocalUtil.removeAllThreadLocalContext();
        }
    }

}
