package cn.sylen.common.util;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

public class CommonUtil {
    private static Random rand = new Random();
    private final static Logger logger = LoggerFactory.getLogger(CommonUtil.class);
    private static char CHARACTERS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C',
                                        'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
                                        'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

    /**
     * 对obj里面的Integer、Long、Float、Double类型字段中，为null值的成员，置为0
     */
    public static void invokeDefaultValue(Object obj) {
        ObjectUtil.invokeDefaultValue(obj);
    }

    /**
     * 把obj2里面不为null的字段，更新到toUpdateObj中
     */
    public static void updateObjectValue(Object toUpdateObj, Object obj2) {
        ObjectUtil.updateObjectValue(toUpdateObj, obj2);
    }

    public static Object getObjectFieldValue(Object obj, String field) {
        return ObjectUtil.getObjectFieldValue(obj, field);
    }

//    public static List<Entry<String, Object>> getValidFieldValues(Object obj) {
//        return ObjectUtil.getValidFieldValues(obj);
//    }

    /**
     * 生成object里面所有有效参数的string串
     */
    public static String ObjectToString(Object obj) {
        return ObjectUtil.ObjectToString(obj);
    }

    /**
     * invoke调用obj里面的一个函数
     */
    public static Object invokeMethod(Object obj, String methodName, Object... params) {
        return ObjectUtil.invokeMethod(obj, methodName, params);
    }

    /**
     * 生成n以内的随机数
     */
    public static int randomNum(int n) {
        return Math.abs(rand.nextInt(n) % n);
    }


    public static <K, V> String toJsonString(Map<K, V> obj) {
        return JSON.toJSONString(obj);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getJsonMap(String jsonString) {
        if (jsonString == null) {
            return null;
        }

        return JsonUtils.fromJson(jsonString, Map.class);
    }

    /**
     * 得到int值
     */
    public static int getIntValue(Object obj) {
        try {
            if (obj == null) {
                return 0;
            }

            if (obj instanceof Integer) {
                return (Integer) obj;
            }

            if (obj instanceof Long) {
                return ((Long) obj).intValue();
            }

            if (obj instanceof String) {
                if (StringUtil.isEmpty((String) obj)) {
                    return 0;
                }
                return Integer.parseInt(StringUtil.trim((String) obj));
            }
        } catch (Exception e) {
            // logger.warn("", e);
        }
        return 0;
    }

    public static long getLongValue(Object obj) {
        try {
            if (obj == null) {
                return 0;
            }

            if (obj instanceof Integer) {
                return ((Integer) obj).longValue();
            }

            if (obj instanceof Long) {
                return ((Long) obj).longValue();
            }

            if (obj instanceof String) {
                return Long.parseLong((String) obj);
            }
        } catch (Exception e) {
            logger.warn("", e);
        }
        return 0;
    }

    public static float getFloatValue(Object obj) {
        try {
            if (obj == null) {
                return 0;
            }

            if (obj instanceof Integer) {
                return ((Integer) obj).floatValue();
            }

            if (obj instanceof Long) {
                return ((Long) obj).floatValue();
            }

            if (obj instanceof String) {
                return Float.parseFloat(((String) obj).replaceAll("\\s+", ""));
            }
            if (obj instanceof Float) {
                return ((Float) obj).floatValue();
            }
            if (obj instanceof Double) {
                return ((Double) obj).floatValue();
            }
        } catch (Exception e) {
            logger.warn("", e);
        }
        return 0;
    }

    /**
     * 生成长度为length的随机数
     */
    public static String genRandomString(int length) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int randomNum = randomNum(CHARACTERS.length);
            sb.append(CHARACTERS[randomNum]);
        }

        return sb.toString();
    }


    public static InputStream openResourceInputStream(String file) throws IOException {
        if (new File(file).exists()) {
            return new FileInputStream(file);
        }

        URL u = CommonUtil.class.getResource(file);
        if (u == null) {
            u = CommonUtil.class.getResource("/" + file);
            if (u == null) {
                return null;
            }
        }
        return u.openStream();
    }

    public static String getAbsoluteFilePath(String file) {
        if (new File(file).exists()) {
            return file;
        }

        URL u = CommonUtil.class.getResource(file);
        if (u == null) {
            u = CommonUtil.class.getResource("/" + file);
            if (u == null) {
                return file;
            }
        }
        return u.getPath();
    }

    public static String getAbsoluteFilePathStream(String file) {
        if (new File(file).exists()) {
            return file;
        }

        URL u = CommonUtil.class.getResource(file);
        if (u == null) {
            u = CommonUtil.class.getResource("/" + file);
            if (u == null) {
                return file;
            }
        }
        return u.getPath();
    }

    /**
     * 在字符串头部填充字符
     *
     * @param str
     *            要填充的字符串
     * @param num
     *            长度
     * @param c
     *            要填充的字符
     * @return
     */
    public static String fillStringHead(String str, int num, char c) {
        if (str == null) {
            return null;
        }

        if (str.length() >= num) {
            return str;
        }

        StringBuffer sb = new StringBuffer();
        for (int i = str.length(); i < num; i++) {
            sb.append(c);
        }
        sb.append(str);
        return sb.toString();
    }
    
    public static String getRequestUrl(HttpServletRequest request, boolean fullParam) {
        StringBuffer sb = new StringBuffer();
        sb.append(request.getRequestURI());
        
        Map<String, String> paramMap = Maps.newTreeMap();
        if(fullParam) {
            for(Object key : request.getParameterMap().keySet()) {
                String k = key.toString();
                String value = request.getParameter((String) key);
                paramMap.put(k, value);
            }
        } else {
            String query = request.getQueryString();
            if (StringUtil.isNotEmpty(query)) {
                String[] kvs = query.split("&");
                for(String kv : kvs) {
                    String[] arr = kv.split("=", 2);
                    if(arr.length != 2) {
                        continue;
                    }
                    paramMap.put(arr[0], arr[1]);
                }
            }
        }
        
        int n = 0;
        for (Object key : request.getParameterMap().keySet()) {
            if (n++ == 0) {
                sb.append("?");
            } else {
                sb.append("&");
            }
            String value = request.getParameter((String) key);
            // 加上脱敏信息
            sb.append(key).append("=").append(value);
        }

        return sb.toString();
    }
    
    public static String getRemoteIpAddress(HttpServletRequest request) {
        String realIP = request.getHeader("X-Real-IP");
        if (StringUtil.isNotEmpty(realIP)) {
            return realIP;
        }
        return request.getRemoteAddr();
    }


}
