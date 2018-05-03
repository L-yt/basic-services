package cn.sylen.common.util;

import com.alibaba.fastjson.JSON;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ObjectUtil {
    private final static Logger logger = LoggerFactory.getLogger(ObjectUtil.class);
    /**
     * 获取某种类型的值
     * @param value
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
    public static<T> T convertValue(Object value,Class<T> clazz){

        if(value==null) return null;

        if(instanceOf(value, clazz)) {
            return (T) value;
        }

        String param = value.toString().trim();
        if(param.trim().equals("")){
            return null;
        }

        try {
            if(clazz==Integer.class || clazz == Integer.TYPE){
                return (T)Integer.valueOf(param);
            } else if(clazz==Double.class || clazz == Double.TYPE){
                return (T)Double.valueOf(param);
            } else if(clazz==Float.class || clazz == Float.TYPE){
                return (T)Float.valueOf(param);
            } else if(clazz==Long.class || clazz == Long.TYPE){
                return (T)Long.valueOf(param);
            } else if(clazz==Short.class || clazz == Short.TYPE){
                return (T)Short.valueOf(param);
            } else if(clazz==Byte.class || clazz == Byte.TYPE){
                return (T)Byte.valueOf(param);
            } else if(clazz==String.class){
                return (T)param;
            } else if(clazz==Boolean.class  || clazz == Boolean.TYPE){
                return (T)Boolean.valueOf(value.toString());
            } else if(clazz == Date.class || clazz == Timestamp.class || clazz == java.sql.Date.class) {
                long time = 0;
                if(StringUtil.isNumeric(param)) {
                    time = Long.valueOf(param);
                    if(time < Integer.MAX_VALUE) {
                        time *= 1000;
                    }
                } else {
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    if(param.length() > 11) {
                        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    }
                    time = dateFormat.parse(param).getTime();
                }
                Date t = null;
                if(clazz == Date.class) {
                    t = new Date(time);
                } else if(clazz == Timestamp.class) {
                    t = new Timestamp(time);
                } else if(clazz ==java.sql.Date.class) {
                    t = new java.sql.Date(time);
                }
                t.setTime(time);
                return (T)t;
            }

            return JSON.parseObject(param, clazz);
        } catch (Exception e) {
            logger.debug("", e);
            return null;
        }
    }

    public static boolean instanceOf(Object value, Class<?> cls) {
        if(cls.isInstance(value)) {
            return true;
        }

        Class<? extends Object> c = value.getClass();
        while(c != null) {
            if(c.equals(cls)) {
                return true;
            }
            c = c.getSuperclass();
        }

        return false;
    }


    /**
     * 对obj里面的Integer、Long、Float、Double类型字段中，为null值的成员，置为0
     */
    public static void invokeDefaultValue(Object obj) {
        if (obj == null) {
            return;
        }

        Class<? extends Object> cls = obj.getClass();
        Field[] fields = cls.getDeclaredFields();
        Class<? extends Object> superCls = cls.getSuperclass();
        Field[] superFields = superCls.getDeclaredFields();

        for (Field field : fields) {
            try {
                field.setAccessible(true);
                if (field.get(obj) != null) {
                    continue;
                }
                String setVariableName = getSetFunctionName(field.getName());
                if (field.getType() == Integer.class) {
                    invokeMethod(obj, setVariableName, 0);
                } else if (field.getType() == Long.class) {
                    invokeMethod(obj, setVariableName, 0L);
                } else if (field.getType() == Float.class) {
                    invokeMethod(obj, setVariableName, 0.0f);
                } else if (field.getType() == Double.class) {
                    invokeMethod(obj, setVariableName, 0.0d);
                }
            } catch (Exception e) {
                logger.warn("", e);
            }
        }
        for (Field field : superFields) {
            try {
                field.setAccessible(true);
                if (field.get(obj) != null) {
                    continue;
                }
                String setVariableName = getSetFunctionName(field.getName());
                if (field.getType() == Integer.class) {
                    invokeMethod(obj, setVariableName, 0);
                } else if (field.getType() == Long.class) {
                    invokeMethod(obj, setVariableName, 0L);
                } else if (field.getType() == Float.class) {
                    invokeMethod(obj, setVariableName, 0.0f);
                } else if (field.getType() == Double.class) {
                    invokeMethod(obj, setVariableName, 0.0d);
                }
            } catch (Exception e) {
                logger.warn("", e);
            }
        }
    }

    /**
     * 把obj2里面不为null的字段，更新到toUpdateObj中
     */
    @SuppressWarnings("unchecked")
    public static void updateObjectValue(Object toUpdateObj, Object obj2) {
        if (toUpdateObj == null || obj2 == null) {
            return;
        }

        Class<? extends Object> cls = toUpdateObj.getClass();
        while (cls.getSuperclass() != null) {
            Method[] methods = cls.getDeclaredMethods();
            for (Method method : methods) {
                String methodName = method.getName();
                if (methodName.startsWith("set")) {
                    String getFuncName = "g" + methodName.substring(1);
                    Method getMethod = getMethod(obj2, getFuncName);
                    if (getMethod != null) {
                        try {
                            Object obj = getMethod.invoke(obj2);
                            if (obj != null) {
                                invokeMethod(toUpdateObj, methodName, obj);
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            }
            cls = cls.getSuperclass();
        }
    }

    /**
     * 判断cls中是否包含fieldName对应的字段(只映射字母和数字)
     * @param cls 要检查的class类
     * @param fieldName 字段名
     * @return
     */
    public static boolean containsFieldName(Class<?> cls, String fieldName) {
        Map<String, String> fieldNames = getObjectFieldsNameMap(cls);

        return fieldNames.containsKey(StringUtil.getLetterOrDigit(fieldName).toLowerCase());
    }

    /**
     * 设置field的值
     * @param obj
     * @param field
     * @param value
     */
    @SuppressWarnings("unchecked")
    public static void setFieldValue(Object obj, String field, Object value) {
        if(obj == null) {
            return;
        }

        if(obj instanceof Map) {
            ((Map<String, Object>) obj).put(field, value);
            return;
        }

        try {
            Class<?> cls = null;
            if(value != null) {
                cls = value.getClass();
            }

            Map<String, String> fieldNames = getObjectFieldsNameMap(obj.getClass());
            String fieldName = fieldNames.get(StringUtil.getLetterOrDigit(field).toLowerCase());

            String setMethodName = getSetFunctionName(fieldName);
            Method method = getMethod(obj, setMethodName, cls);
            if(method != null) {
                Object v = ObjectUtil.convertValue(value, method.getParameterTypes()[0]);
                method.setAccessible(true);
                method.invoke(obj, v);
            }
        } catch (Exception e) {
            logger.warn("", e);
        }
    }

    private static Map<String, Map<String, String>> fieldsNameMap =
            new ConcurrentHashMap<String, Map<String,String>>();
    private static Map<String, String> getObjectFieldsNameMap(Class<?> cls) {
        Map<String, String> fieldNames = fieldsNameMap.get(cls.getName());
        if(fieldNames == null) {
            fieldNames = new HashMap<String, String>();
            List<String> fields = getObjectFields(cls);
            for(String field : fields) {
                fieldNames.put(StringUtil.getLetterOrDigit(field).toLowerCase(), field);
            }
            fieldsNameMap.put(cls.getName(), fieldNames);
        }
        return fieldNames;

    }

    private static Map<String, List<String>> fieldsMap =
            new ConcurrentHashMap<String, List<String>>();
    /**
     * 获取object内所有的fields属性
     * @param cls
     * @return
     */
    public static List<String> getObjectFields(Class<?> cls) {
        List<String> fields = fieldsMap.get(cls.getName());
        if(fields != null) {
            return fields;
        }
        fields = new ArrayList<String>();
        while (cls.getSuperclass() != null) {
            Method[] methods = cls.getDeclaredMethods();
            Field[] fs = cls.getDeclaredFields();
            for(Field f : fs) {
                if(!f.getName().startsWith("this$") && !fields.contains(f.getName())) {
                    fields.add(f.getName());
                }
            }

            for (Method method : methods) {
                String methodName = method.getName();
                if (methodName.startsWith("get")) {
                    String fieldName = methodName.replace("get", "");
                    if (StringUtil.isEmpty(fieldName)) {
                        continue;
                    }

                    fieldName = Character.toLowerCase(fieldName.charAt(0)) + fieldName.substring(1);
                    if(!fields.contains(fieldName)) {
                        fields.add(fieldName);
                    }
                }
            }

            cls = cls.getSuperclass();
        }

        fieldsMap.put(cls.getName(), fields);
        return fields;
    }

    public static Object getObjectFieldValue(Object obj, String field) {
        if (obj == null || field == null) {
            return null;
        }

        field = StringUtil.getLetterOrDigit(field);
        try {
            Class<? extends Object> cls = obj.getClass();
            while (cls != null && cls != Object.class) {
                Field[] fields = cls.getDeclaredFields();
                for (Field f : fields) {
                    f.setAccessible(true);
                    if (StringUtil.getLetterOrDigit(f.getName()).equalsIgnoreCase(field)) {
                        return f.get(obj);
                    }
                }
                cls = cls.getSuperclass();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    /**
     * 获取obj内部所有不为null的kv对列表
     * @param obj
     * @return
     */
//    public static List<Entry<String, Object>> getValidFieldValues(Object obj) {
//        List<Entry<String, Object>> validValues = new ArrayList<Entry<String, Object>>();
//        if (obj == null) {
//            return validValues;
//        }
//
//        if(obj instanceof Map) {
//            @SuppressWarnings("unchecked")
//            Map<String, Object> m = (Map<String, Object>) obj;
//            for(Map.Entry<String, Object> e : m.entrySet()) {
//                validValues.add(new Entry<String, Object>(e.getKey(), e.getValue()));
//            }
//            return validValues;
//        }
//
//        Set<String> nameSet = new HashSet<String>();
//
//        Class<? extends Object> cls = obj.getClass();
//        while (cls.getSuperclass() != null) {
//            Method[] methods = cls.getDeclaredMethods();
//            for (Method method : methods) {
//                String methodName = method.getName();
//                if (methodName.startsWith("get")) {
//                    try {
//                        Object value = method.invoke(obj);
//                        String fieldName = methodName.replace("get", "");
//                        if (StringUtil.isEmpty(fieldName)) {
//                            continue;
//                        }
//
//                        fieldName = Character.toLowerCase(fieldName.charAt(0)) + fieldName.substring(1);
//
//                        if (value != null) {
//                            if (nameSet.contains(fieldName.toUpperCase())) {
//                                continue;
//                            }
//
//                            nameSet.add(fieldName.toUpperCase());
//
//                            validValues.add(new Entry<String, Object>(fieldName, value));
//
//                        }
//                    } catch (Exception e) {
//                    }
//                }
//            }
//
//            cls = cls.getSuperclass();
//        }
//
//        return validValues;
//    }

    /**
     * 生成object里面所有有效参数的string串
     */
    public static String ObjectToString(Object obj) {
        if (obj == null) {
            return null;
        }

        Set<String> nameSet = new HashSet<String>();

        StringBuffer querySB = new StringBuffer();
        Class<? extends Object> cls = obj.getClass();
        while (cls.getSuperclass() != null) {
            Field[] fields = cls.getDeclaredFields();
            int count = 0;
            Method[] methods = cls.getDeclaredMethods();
            for (Method method : methods) {
                String methodName = method.getName();
                if (methodName.startsWith("get")) {
                    try {
                        Object value = method.invoke(obj);
                        String fieldName = methodName.replace("get", "");
                        if (StringUtil.isEmpty(fieldName)) {
                            continue;
                        }

                        fieldName = Character.toLowerCase(fieldName.charAt(0)) + fieldName.substring(1);

                        if (value != null) {
                            if (nameSet.contains(fieldName.toUpperCase())) {
                                continue;
                            }

                            nameSet.add(fieldName.toUpperCase());

                            if (count++ > 0) {
                                querySB.append("    ");
                            }

                            querySB.append(fieldName).append(": ").append(String.valueOf(value));
                        }
                    } catch (Exception e) {
                    }
                }
            }

            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    if (Modifier.isStatic(field.getModifiers())) {
                        continue;
                    }

                    if (nameSet.contains(field.getName().toUpperCase())) {
                        continue;
                    }

                    nameSet.add(field.getName().toUpperCase());

                    if (count++ > 0) {
                        querySB.append("    ");
                    }

                    querySB.append(field.getName()).append(": ").append(String.valueOf(field.get(obj)));
                } catch (Exception e) {
                    logger.warn("", e);
                }
            }

            cls = cls.getSuperclass();
        }

        return querySB.toString();
    }

    /**
     * invoke调用obj里面的一个函数
     */
    @SuppressWarnings("unchecked")
    public static Object invokeMethod(Object obj, String methodName, Object... params) {
        if (obj == null || methodName == null) {
            return null;
        }

        Class<? extends Object>[] parameterTypes = null;
        if (params != null && params.length > 0) {
            List<Class<? extends Object>> typeList = new ArrayList<Class<? extends Object>>();
            for (Object paramObj : params) {
                if (paramObj == null) {
                    continue;
                }
                Class<? extends Object> c = paramObj.getClass();
                typeList.add(c);
            }

            parameterTypes = new Class[typeList.size()];
            typeList.toArray(parameterTypes);
        }

        try {
            Method method = null;
            if (parameterTypes == null) {
                method = getMethod(obj, methodName);
            } else {
                method = getMethod(obj, methodName, parameterTypes);
            }
            if (method == null) {
                return null;
            }
            method.setAccessible(true);

            if(method.getParameterTypes().length != params.length) {
                return null;
            }
            Object[] p = new Object[method.getParameterTypes().length];
            for(int i=0; i<method.getParameterTypes().length; i++) {
                Class<?> cls = method.getParameterTypes()[i];
                Object o = convertValue(params[i], cls);
                p[i] = o;
            }

            return method.invoke(obj, p);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static Method getMethod(Object obj, String methodName,
                                    Class<? extends Object>... parameterTypes) {
        try {
            Class<? extends Object> cls = obj.getClass();
            while (cls.getSuperclass() != null) {
                Method[] methods = cls.getDeclaredMethods();

                String internedName = methodName.intern();

                for (int i = 0; i < methods.length; i++) {
                    Method m = methods[i];
                    if (!m.getName().equals(internedName)) {
                        continue;
                    }
                    if (arrayContentsEq(parameterTypes, m.getParameterTypes())) {
                        return m;
                    }
                }
                cls = cls.getSuperclass();
            }
        } catch (Exception e) {
        }
        return null;
    }

    private static boolean arrayContentsEq(Object[] a1, Object[] a2) {
        if (a1 == null || a2 == null) {
            return false;
        }
        if (a1.length != a2.length) {
            return false;
        }

        return true;
    }

    protected static String getSetFunctionName(String variablNname) {
        String strHead = variablNname.substring(0, 1);
        String strTail = variablNname.substring(1, variablNname.length());

        String strRetval = "set" + strHead.toUpperCase() + strTail;

        return strRetval;
    }

    protected static String getGetFunctionName(String variablNname) {
        String strHead = variablNname.substring(0, 1);
        String strTail = variablNname.substring(1, variablNname.length());

        String strRetval = "get" + strHead.toUpperCase() + strTail;

        return strRetval;
    }

}
