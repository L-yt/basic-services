package cn.sylen.common.spring.mvc.interceptor;

import java.util.List;

public class InterceptorList {
    private List<InterceptorWrapper> interceptors;

    public List<InterceptorWrapper> getInterceptors() {
        return interceptors;
    }

    public void setInterceptors(List<InterceptorWrapper> interceptors) {
        this.interceptors = interceptors;
    }
}
