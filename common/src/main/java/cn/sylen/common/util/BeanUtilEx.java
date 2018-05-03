package cn.sylen.common.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.support.AopUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 将CommonUtil中的部分反射方法移到这里,
 * 并且将ObjectUtil中的类型转换方法也移到了这里.
 * @author victorhan
 *
 */
public class BeanUtilEx extends BeanUtils {

	private final static Logger logger = LoggerFactory.getLogger(BeanUtilEx.class);

	private static Map<String,List<Annotation>> fieldMap = Maps.newConcurrentMap();
    private static List<String> nullList = Lists.newCopyOnWriteArrayList();

	private BeanUtilEx() {
	}

    /**
     * 这个方法clazz的类型与Object的类型一致时好用,
     * 如果不一样就不行了,例如Objct = "1", Clazz = int,
     * 会调用String.intValue()会报错转换不了
     * @param value
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
	public static<T> T convertValue(Object value,Class<T> clazz){

		if(value==null) return null;

		String param = value.toString().trim();
		if(param.trim().equals("")){
			return null;
		}

		if(clazz.isPrimitive()) {
			try {
				if(clazz == Integer.TYPE) {
					return (T)invokeMethod(value, "intValue");
				} if(clazz == Double.TYPE) {
					return (T)invokeMethod(value, "doubleValue");
				} else if(clazz == Long.TYPE) {
					return (T)invokeMethod(value, "longValue");
				} else if(clazz == Float.TYPE) {
					return (T)invokeMethod(value, "floatValue");
				} else if(clazz == Byte.TYPE) {
					return (T)invokeMethod(value, "intValue");
				} else if(clazz == Short.TYPE) {
					return (T)invokeMethod(value, "intValue");
				}
				return (T)value;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if(instanceOf(value, clazz)) {
			if(clazz==String.class) return (T)param;
			return (T) value;
		}

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		try {
			if(clazz==Integer.class){
				return (T)Integer.valueOf(param);
			} else if(clazz==Double.class){
				return (T)Double.valueOf(param);
			} else if(clazz==Float.class){
				return (T)Float.valueOf(param);
			} else if(clazz==Long.class){
				return (T)Long.valueOf(param);
			} else if(clazz==String.class){
				return (T)param;
			} else if(clazz==Boolean.class){
				return (T)Boolean.valueOf(value.toString());
			} else if(clazz==Date.class){
				return (T)dateFormat.parse(param);
			} else if(clazz==Timestamp.class){
				long time = 0;
				if(instanceOf(value, Date.class)) {
					time = ((Date)value).getTime();
				} else {
					time = dateFormat.parse(param).getTime();
				}
				return (T)new Timestamp(time);
			} else if(clazz==java.sql.Date.class){
				long time = 0;
				if(instanceOf(value, Date.class)) {
					time = ((Date)value).getTime();
				} else {
					time = dateFormat.parse(param).getTime();
				}

				return (T) new java.sql.Date(time);
			}
			return clazz.newInstance();

		} catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			return null;
		} catch (ParseException e) {
			return null;
		}
	}

	public static boolean instanceOf(Object value, Class<?> cls) {
		if(value == null)return false;
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

	static {
		// 注册sql.date的转换器，即允许BeanUtils.copyProperties时的源目标的sql类型的值允许为空
		ConvertUtils.register(new org.apache.commons.beanutils.converters.SqlDateConverter(null), java.sql.Date.class);
		ConvertUtils.register(new org.apache.commons.beanutils.converters.SqlDateConverter(null), Date.class);
		ConvertUtils.register(new org.apache.commons.beanutils.converters.SqlTimestampConverter(null), Timestamp.class);
		// 注册util.date的转换器，即允许BeanUtils.copyProperties时的源目标的util类型的值允许为空
	}

	public static void copyProperties(Object target, Object source)
			throws InvocationTargetException, IllegalAccessException {
		// 支持对日期copy
		org.apache.commons.beanutils.BeanUtils.copyProperties(target, source);
	}

	/**
     * invoke调用obj里面的一个函数
     */
    @SuppressWarnings("unchecked")
    public static Object invokeMethod(Object obj, String methodName,
            Object... params) {
        if (obj == null || methodName == null) {
            return null;
        }

        Class<?extends Object>[] parameterTypes = null;
        if (params != null && params.length > 0) {
            List<Class<?extends Object>> typeList = new ArrayList<Class<?extends Object>>();
            for (Object paramObj : params) {
            	if(paramObj==null){
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
                method = obj.getClass().getDeclaredMethod(methodName,
                        parameterTypes);
            } else {
                method = getMethod(obj, methodName, parameterTypes);
            }
            if (method == null) {
                return null;
            }
            method.setAccessible(true);
            return method.invoke(obj, params);
        } catch (Exception e) {
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static Method getMethod(Object obj, String methodName, Class<? extends Object>... parameterTypes) {
        try {
        	Class<? extends Object> cls = obj.getClass();
        	while(cls.getSuperclass() != null) {
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

    /**
     * 找到第一个匹配的方法
     * @param obj
     * @param methodName
     * @return
     */
    public static Method getFirstMatchMethod(@SuppressWarnings("rawtypes") Class cls,String methodName){
    	try {
        	while(cls.getSuperclass() != null) {
        		Method[] methods = cls.getMethods();

        		String internedName = methodName.intern();

                for (int i = 0; i < methods.length; i++) {
                    Method m = methods[i];

                    if (StringUtil.getLetterOrDigit(m.getName()).toLowerCase().equals(
                            StringUtil.getLetterOrDigit(internedName).toLowerCase())) {
                        return m;
                    }
                }
        		cls = cls.getSuperclass();
        	}
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 找到第一个匹配的方法
     * @param obj
     * @param methodName
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Method getFirstMatchMethod(Class cls,Class annoClass){
        try {
            while(cls.getSuperclass() != null) {
                Method[] methods = cls.getMethods();

                for (Method method: methods) {
                    if(method.getAnnotation(annoClass)!=null){
                        return method;
                    }
                }
                cls = cls.getSuperclass();
            }
        } catch (Exception e) {
        }
        return null;
    }

    public static boolean hasMethod(@SuppressWarnings("rawtypes") Class cls,List<String> methodNames){
    	try {
        	while(cls.getSuperclass() != null) {
        		Method[] methods = cls.getMethods();

        		for (int i = 0; i < methods.length; i++) {
        			Method m = methods[i];
        			if (methodNames.contains(m.getName())) {
        				return true;
        			}
        		}
        		cls = cls.getSuperclass();
        	}
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 对obj里面的Integer、Long、Float、Double类型字段中，为null值的成员，置为0
     */
    public static void invokeDefaultValue(Object obj) {
        if(obj == null) {
            return;
        }

        Class<? extends Object> cls = obj.getClass();
        Field[] fields = cls.getDeclaredFields();
        Class<? extends Object> superCls = cls.getSuperclass();
        Field[] superFields = superCls.getDeclaredFields();

        for(Field field : fields) {
            try {
                field.setAccessible(true);
                if(field.get(obj) != null) {
                    continue;
                }
                String setVariableName = getSetFunctionName(field.getName());
                if(field.getType() == Integer.class) {
                    invokeMethod(obj, setVariableName, 0);
                } else if(field.getType() == Long.class) {
                    invokeMethod(obj, setVariableName, 0L);
                } else if(field.getType() == Float.class) {
                    invokeMethod(obj, setVariableName, 0.0f);
                } else if(field.getType() == Double.class) {
                    invokeMethod(obj, setVariableName, 0.0d);
                }
            } catch (Exception e) {
                logger.warn("", e);
            }
        }
        for(Field field : superFields) {
            try {
                field.setAccessible(true);
                if(field.get(obj) != null) {
                    continue;
                }
                String setVariableName = getSetFunctionName(field.getName());
                if(field.getType() == Integer.class) {
                    invokeMethod(obj, setVariableName, 0);
                } else if(field.getType() == Long.class) {
                    invokeMethod(obj, setVariableName, 0L);
                } else if(field.getType() == Float.class) {
                    invokeMethod(obj, setVariableName, 0.0f);
                } else if(field.getType() == Double.class) {
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
    public static void updateObjectValue(Object toUpdateObj, Object obj2) {
        if(toUpdateObj == null || obj2 == null) {
            return;
        }

        Class<? extends Object> cls = toUpdateObj.getClass();
        while(cls.getSuperclass() != null) {
        	Method[] methods = cls.getDeclaredMethods();
        	for(Method method : methods) {
        		String methodName = method.getName();
        		if(methodName.startsWith("set")) {
        			String getFuncName = "g" + methodName.substring(1);
        			@SuppressWarnings("unchecked")
					Method getMethod = getMethod(obj2, getFuncName);
        			if(getMethod != null) {
        				try {
        					Object obj = getMethod.invoke(obj2);
        					if(obj != null) {
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

    public static String getSetFunctionName(String variablNname){
        String strHead = variablNname.substring(0, 1);
        String strTail = variablNname.substring(1, variablNname.length());

        String strRetval = "set" + strHead.toUpperCase() + strTail;

        return strRetval;
    }

    public static String getIsFunctionName(String variablNname){
        String strHead = variablNname.substring(0, 1);
        String strTail = variablNname.substring(1, variablNname.length());

        String strRetval = "is" + strHead.toUpperCase() + strTail;

        return strRetval;
    }

    public static String getGetFunctionName(String variablNname){
        String strHead = variablNname.substring(0, 1);
        String strTail = variablNname.substring(1, variablNname.length());

        String strRetval = "get" + strHead.toUpperCase() + strTail;

        return strRetval;
    }

    public static List<Field> getDeclaredFields(@SuppressWarnings("rawtypes") Class clazz){
        List<Field> list = Lists.newArrayList();
        while(clazz != null && clazz != Object.class){
            Field[] fields = clazz.getDeclaredFields();
            for(Field field : fields){
                if(field!=null){
                    list.add(field);
                }
            }
            clazz = clazz.getSuperclass();
        }
        return list;
    }

    /**
     * 获取 目标对象
     *
     * @param proxy
     *            代理对象
     * @return
     * @throws Exception
     */
    public static Object getTarget(Object proxy) {

        if (!AopUtils.isAopProxy(proxy)) {
            return proxy;// 不是代理对象
        }

        if (AopUtils.isJdkDynamicProxy(proxy)) {
            return getJdkDynamicProxyTargetObject(proxy);
        } else { // cglib
            return getCglibProxyTargetObject(proxy);
        }

    }

    private static Object getCglibProxyTargetObject(Object proxy) {
        try{
            Field h = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
            h.setAccessible(true);
            Object dynamicAdvisedInterceptor = h.get(proxy);
            Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised");
            advised.setAccessible(true);
            Object target = ((AdvisedSupport) advised.get(dynamicAdvisedInterceptor)).getTargetSource().getTarget();
            return target;
        } catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }


    private static Object getJdkDynamicProxyTargetObject(Object proxy) {
        try{
            Field h = proxy.getClass().getSuperclass().getDeclaredField("h");
            h.setAccessible(true);
            AopProxy aopProxy = (AopProxy) h.get(proxy);
            Field advised = aopProxy.getClass().getDeclaredField("advised");
            advised.setAccessible(true);
            Object target = ((AdvisedSupport) advised.get(aopProxy)).getTargetSource().getTarget();
            return target;
        } catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据一个类和字段名, 获取它的annotations
     * @param clazz
     * @param fieldName
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static List<Annotation> getAnnotationsByClassAndFieldName(Class clazz, String fieldName){
        String key = clazz.getCanonicalName()+"."+fieldName;
        if(nullList.contains(key)){
            return null;
        }
        if(fieldMap.containsKey(key)){
            return fieldMap.get(key);
        }
        List<Field> fields = BeanUtilEx.getDeclaredFields(clazz);
        List<Annotation> annoList = null;
        for(Field field : fields){
            if(fieldName.toString().equals(field.getName())){
                Annotation[] annotations = field.getAnnotations();
                annoList = Lists.newArrayList(annotations);
                break;
            }
        }
        if(annoList == null){
            nullList.add(key);
        } else {
            fieldMap.put(key, annoList);
        }
        return annoList;
    }

    @SuppressWarnings("rawtypes")
    public static Class getClassFromType(Type type) throws ClassNotFoundException{
        Class typeClazz = null;
        typeClazz = Class.forName(type.toString().substring(6));
        return typeClazz;
    }

    /**
     * 执行annotation的一个方法, 获取String结果
     * @param annotation
     * @param methodName
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T getValueFromAnnos(List<Annotation> annotations, String methodName){
        if(annotations == null || annotations.size()==0 || StringUtil.isEmpty(methodName)){
            return null;
        }
        for(Annotation annotation : annotations){
            if(annotation == null){
                continue;
            }
            Method method = BeanUtilEx.getFirstMatchMethod(annotation.getClass(), methodName);
            if(method != null){
                try {
                    return (T)method.invoke(annotation, new Object[0]);
                } catch (Exception e) {
                    return null;
                }
            }
            return null;
        }
        return null;
    }
}
