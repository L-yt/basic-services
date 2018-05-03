package cn.sylen.common.spring.mvc.interceptor;

import java.util.List;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.google.common.collect.Lists;

/**
 * 定义用户的拦截器
 * @author hongwm
 * @since 2015-03-29
 */
public class InterceptorWrapper {
    /**
     * 拦截的处理器
     */
    private HandlerInterceptorAdapter inteceptor;
    /**
     * 匹配的pattern
     */
    private String includePattern;
    /**
     * 例外的pattern
     */
    private String excludePattern;
    
    /**
     * 匹配的patternList, 因为Pattern太多的话,
     * 单个String输入可能不方便,所以提供以List方式的传入方式
     */
    private List<String> excludePatternList = Lists.newArrayList();
    
    /**
     * 例外的patternList, 因为Pattern太多的话,
     * 单个String输入可能不方便,所以提供以List方式的传入方式
     */
    private List<String> includePatternList = Lists.newArrayList();
     
    public HandlerInterceptorAdapter getInteceptor() {
        return inteceptor;
    }
    public void setInteceptor(HandlerInterceptorAdapter inteceptor) {
        this.inteceptor = inteceptor;
    }
    public String getIncludePattern() {
        return includePattern;
    }
    public void setIncludePattern(String includePattern) {
        this.includePattern = includePattern;
    }
    public String getExcludePattern() {
        return excludePattern;
    }
    public void setExcludePattern(String excludePattern) {
        this.excludePattern = excludePattern;
    }
    
    public List<String> getExcludePatternList() {
        return excludePatternList;
    }
    public void setExcludePatternList(List<String> excludePatternList) {
        this.excludePatternList = excludePatternList;
    }
    public List<String> getIncludePatternList() {
        return includePatternList;
    }
    public void setIncludePatternList(List<String> includePatternList) {
        this.includePatternList = includePatternList;
    }
    
    public String[] getIncludePatterns() {
        if(includePattern == null && includePatternList.isEmpty()) {
            return null;
        }
        if(includePattern != null){
            includePattern = includePattern.replace(" ", "");
            String[] patterns = includePattern.split(";");
            includePatternList.addAll(Lists.newArrayList(patterns));
        }
        if(includePatternList.size() > 0){
            String[] array = new String[includePatternList.size()];
            for(int i=0;i<includePatternList.size();i++){
                String metaPattern = includePatternList.get(i);
                array[i] = metaPattern.replace(" ", "");
            }
            return array;
        } else {
            return null;
        }
    }
    
    public String[] getExcludePatterns() {
        if(excludePattern == null && excludePatternList.isEmpty()) {
            return null;
        }
        if(excludePattern != null){
            excludePattern = excludePattern.replace(" ", "");
            String[] patterns = excludePattern.split(";");
            excludePatternList.addAll(Lists.newArrayList(patterns));
        }
        
        if(excludePatternList.size() > 0){
            String[] array = new String[excludePatternList.size()];
            for(int i=0;i<excludePatternList.size();i++){
                String metaPattern = excludePatternList.get(i);
                array[i] = metaPattern.replace(" ", "");
            }
            return array;
        } else {
            return null;
        }
    }
    
    
}
