package cn.sylen.common.util;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author wenming.hong
 * @since 2012-7-12
 */
public class UrlUtil {
    private final static Logger logger = LoggerFactory.getLogger(UrlUtil.class);
    private static DomainMap domainMap;
    static {
        domainMap = new DomainMap();
    }

    private static String regex_host = "(?<=http://|\\.)[^.]*?\\.(com|cn|net|org|biz|info|cc|tv)";                 //从URL中获取DOMAIN
    private static Pattern pattern_host = Pattern.compile(regex_host,Pattern.CASE_INSENSITIVE); //从URL中获取DOMAIN

    /**
     * 从URL中获取主机头
     * @param url
     * @return
     */
    public static String getDomain(String url) {
        if (StringUtil.isEmpty(url)) {
            return null;
        }

        Matcher matcher = pattern_host.matcher(url);
        if (!matcher.find()) {
            return null;
        }
        String domain = matcher.group();

        return domain;
    }

    /**
     * 去掉url中的路径，留下请求参数部分
     *
     * @param strURL url地址
     * @return url请求参数部分
     */
    private static String TruncateUrlPage(String strURL) {
        String strAllParam = null;
        String[] arrSplit = null;
        arrSplit = strURL.split("[?]");
        if (strURL.length() > 1) {
            if (arrSplit.length > 1) {
                if (arrSplit[1] != null) {
                    strAllParam = arrSplit[1];
                }
            }
        }
        return strAllParam;
    }
    
    public static String urlEncoding(String param){
        try {
            return URLEncoder.encode(param, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }
    
    /**
     * 从URL获取参数列表
     * @param URL
     * @return
     */
    public static Map<String, String> getParams(String URL) {
        Map<String, String> mapRequest = new HashMap<String, String>();
        String[] arrSplit = null;
        String strUrlParam = TruncateUrlPage(URL);
        if (strUrlParam == null) {
            return mapRequest;
        }
        //每个键值为一组
        arrSplit = strUrlParam.split("[&]");
        for (String strSplit : arrSplit) {
            String[] arrSplitEqual = null;
            arrSplitEqual = strSplit.split("[=]",2);
            //解析出键值
            if (arrSplitEqual.length > 1) {
                //正确解析
                mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);
            } else {
                if (arrSplitEqual[0] != "") {
                    //只有参数没有值，不加入
                    mapRequest.put(arrSplitEqual[0], "");
                }
            }
        }
        return mapRequest;
    }

    /**
     *  get domain names from a host
     *  @return domain
     */
    public static String getDomainFromUrl(String url){
        if(url == null) {
            return null;
        }

        int start = url.indexOf("//");
        if(start >= 0) {
            url = url.substring(start + 2);
        }

        int end = url.indexOf("/");
        if(end >= 0) {
            url = url.substring(0, end);
        }

        end = url.indexOf(":");
        if(end >= 0) {
            url = url.substring(0, end);
        }

        if(isIpAddr(url)) {
            return url;
        }

        int domainIndex = -1;
        int tailIndex = url.indexOf("."); 
        String domainTail = url.substring(tailIndex + 1);
        while(!domainMap.isDomainTail(domainTail)) {

            int nextTailIndex = url.indexOf(".", tailIndex + 1);
            if(nextTailIndex == -1) {
                break;
            } else {
                domainIndex = tailIndex;
                tailIndex = nextTailIndex;
            }

            domainTail = url.substring(tailIndex + 1);
        }

        return url.substring(domainIndex + 1);
    }

    public static boolean isIpAddr(String host) {
        if(host==null || host.length() == 0) {
            return false;
        }
        
        int dotNum = 0;
        for(int i=0; i<host.length(); i++) {
            char c = host.charAt(i);
            if(c == '.') {
                dotNum ++;
                continue;
            }
            if(dotNum > 3) {
                return false;
            }
        
            if(c > '9' || c < '0') {
                return false;
            }
        }
        return true;
    }

    public static boolean isLegalUrl(String url) {
        try {
            if(!url.startsWith("http")) {
                url = "http://" + url;
            }
            new URL(url).getHost();
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }
    
    public static String getUrlSuffix(String url) {
        if (url == null) {
            return null;
        }
        String path = url;
        try {
            URL uri = new URL(url);
            path = uri.getPath();
        } catch (Exception e) {
        }

        int lastDotIndex = path.lastIndexOf(".");
        if (lastDotIndex < 0) {
            return null;
        }
        return path.substring(lastDotIndex + 1);
    }

    public static String getUrlHead(String url) {
        if (url == null) {
            return null;
        }
        try {
            int lastDotIndex = url.lastIndexOf(".");
            if (lastDotIndex < 0) {
                return url;
            }
            return url.substring(0, lastDotIndex);
        } catch (Exception e) {
            logger.warn("", e);
        }
        return url;
    }
    
    /**
     * get absolute url
     * @param parentUrl
     * @param url
     * @return absolute url
     */
    public static String getAbsoluteUrl(URL parentUrl, String url){
        if(StringUtil.isEmpty(url)) {
            return url;
        }

        if(url.indexOf("//") > 0) {
            return url;
        }

        if(parentUrl == null) {
            return url;
        }

        String path;
        boolean modified;
        boolean absolute;
        int index;
        String base = null;
        URL u;

        if(parentUrl != null) {
            base = parentUrl.toExternalForm();
        }

        if (('?' == url.charAt (0)))
        {   // remove query part of base if any
            index = base.lastIndexOf ('?');
            if (-1 != index) {
                base = base.substring (0, index);
            }

            return base + url;
        }
        try {

            u = new URL (parentUrl, url);

            path = u.getFile ();
            modified = false;
            absolute = url.startsWith ("/");
            if (!absolute)
            {   // we prefer to fix incorrect relative links
                // this doesn't fix them all, just the ones at the start
                while (path.startsWith ("/."))
                {
                    if (path.startsWith ("/../"))
                    {
                        path = path.substring (3);
                        modified = true;
                    }
                    else if (path.startsWith ("/./") || path.startsWith("/."))
                    {
                        path = path.substring (2);
                        modified = true;
                    }
                    else {
                        break;
                    }
                }
            }
            // fix backslashes
            while (-1 != (index = path.indexOf ("/\\")))
            {
                path = path.substring (0, index + 1) + path.substring (index + 2);
                modified = true;
            }

            if (modified) {
                u = new URL (u, path);
            }

            return u.toExternalForm();
        }
        catch (MalformedURLException e) {
            return url;
        }
    }
    
    /**
     * 获取url的path
     * @param url
     * @return
     */
    public static String getUrlPath(String url) {
        if(StringUtil.isEmpty(url)) {
            return "";
        }
        
        if(!url.startsWith("http")) {
            return url;
        }
        
        try {
            URL u = new URL(url);
            return u.getPath();
        } catch (Exception e) {
            return url;
        }
    }

    public static String getHostname(String url) {
        if(url == null) {
            return null;
        }

        int start = url.indexOf("//");
        if(start >= 0) {
            url = url.substring(start + 2);
        }

        int end = url.indexOf("/");
        if(end >= 0) {
            url = url.substring(0, end);
        }

        end = url.indexOf(":");
        if(end >= 0) {
            url = url.substring(0, end);
        }
        
        return url;
    }
    
    public static String getIpAddr(HttpServletRequest request) {   
	     String ipAddress = null;   
	     //ipAddress = this.getRequest().getRemoteAddr();   
	     ipAddress = request.getHeader("x-forwarded-for");   
	     if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {   
	    	 ipAddress = request.getHeader("Proxy-Client-IP");   
	     }   
	     if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {   
	         ipAddress = request.getHeader("WL-Proxy-Client-IP");   
	     }   
	     if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {   
	    	 ipAddress = request.getRemoteAddr();   
	    	 if(ipAddress.equals("127.0.0.1")||ipAddress.equals("0:0:0:0:0:0:0:1")){   
	    		 //根据网卡取本机配置的IP   
	    		 InetAddress inet=null;   
	    		 try {   
	    			 inet = InetAddress.getLocalHost();   
	    		 } catch (UnknownHostException e) {   
	    			 e.printStackTrace();   
	    		 }   
	    		 ipAddress= inet.getHostAddress();   
	    	 }   
	     }   
	  
	     //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割   
	     if(ipAddress!=null && ipAddress.length()>15){ //"***.***.***.***".length() = 15   
	         if(ipAddress.indexOf(",")>0){   
	             ipAddress = ipAddress.substring(0,ipAddress.indexOf(","));   
	         }   
	     }   
	     return ipAddress;    
	}
}
