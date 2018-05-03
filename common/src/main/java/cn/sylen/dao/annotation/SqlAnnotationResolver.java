package cn.sylen.dao.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.ibatis.reflection.MetaClass;
import org.apache.ibatis.reflection.invoker.Invoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import cn.sylen.common.util.BeanUtilEx;
import cn.sylen.common.util.CollectionUtils;
import cn.sylen.common.util.ObjectUtil;
import cn.sylen.common.util.StringUtil;
import cn.sylen.dao.mapping.TableMappingFactory;

/**
 * sql映射的annotation解释器
 * @author hongwm
 * @since 2013-8-22
 */
public class SqlAnnotationResolver {
	private final static Logger logger = LoggerFactory.getLogger(SqlAnnotationResolver.class);
	/**
	 * 获取object里面声明的SqlTable表名
	 * @param object
	 * @return
	 */
	public static String getAnnotationTableName(Object object) {
		return getAnnotationTableName(object.getClass());
	}

	/**
	 * 获取cls类里面声明的SqlTable表名
	 * @param cls
	 * @return
	 */
	public static String getAnnotationTableName(Class<?> cls) {
		String className = cls.toString();
		while(cls != null) {
			SqlTable tableDef = cls.getAnnotation(SqlTable.class);
			if(tableDef != null) {
				TableMappingFactory.setTableNameMap(className, tableDef.value());
				return tableDef.value();
			}

			cls = cls.getSuperclass();
		}
		return null;
	}

	/**
	 * 获取一个object的所有field字段
	 * @param obj
	 * @return
	 */
	public static List<ObjectField> getObjectFieds(Object obj) {
		if(obj == null) {
			return null;
		}

		Class<? extends Object> cls = obj.getClass();
		List<ObjectField> objectFields = new ArrayList<ObjectField>();

		Map<String, ObjectField> nameSet = new HashMap<String, ObjectField>();

		while(cls.getSuperclass() != null) {
			Method[] methods = cls.getDeclaredMethods();
			Field[] fields = cls.getDeclaredFields();
			for(Method method : methods) {
				String methodName = method.getName();
				if(methodName.startsWith("get") || methodName.startsWith("is")) {
					String fieldName = methodName.replaceAll("^get|^is", "");
					if(StringUtil.isEmpty(fieldName)) {
						continue;
					}
					if(method.getParameterTypes().length != 0) {
						continue;
					}
					fieldName = Character.toLowerCase(fieldName.charAt(0)) + fieldName.substring(1);
					SqlField sqlField = method.getAnnotation(SqlField.class);
					ObjectField objectField = nameSet.get(fieldName.toUpperCase());

					SqlPrimaryKey primaryKey = method.getAnnotation(SqlPrimaryKey.class);
					SqlUniqueKey uniqueKey = method.getAnnotation(SqlUniqueKey.class);
					if(objectField != null
							&& sqlField == null
							&& primaryKey == null
							&& uniqueKey == null) {
						continue;
					}

					if(objectField == null) {
						objectField = new ObjectField();
						objectField.setCls(method.getReturnType());
						objectField.setGetMethod(method);
						objectField.setParentObj(obj);
						objectField.setFieldName(fieldName);
						objectFields.add(objectField);
					}

					if(sqlField != null) {
						objectField.setDefaultValue(sqlField.defaultValue());
						objectField.setTableField(sqlField.value());
						objectField.setQueryKey(sqlField.queryKey());
						objectField.setUniqKey(sqlField.uniqKey());
						String t = getAnnotationTableName(sqlField.clazz());
						if(t != null) {
							objectField.setTable(getAnnotationTableName(sqlField.clazz()));
						}
						objectField.setUpdateValue(sqlField.updateValue());
					}

					if(primaryKey != null) {
						objectField.setPrimaryKey(true);
					}
					if(uniqueKey != null) {
						objectField.setUniqKey(true);
					}

					if(fieldName.equalsIgnoreCase("dateCreate")) {
						objectField.setDefaultValue("now()");
					} else if(fieldName.equalsIgnoreCase("dateUpdate")) {
						objectField.setDefaultValue("now()");
						objectField.setUpdateValue("now()");
					}

					nameSet.put(fieldName.toUpperCase(), objectField);
				}
			}

			for(Field field : fields) {
				if(Modifier.isStatic(field.getModifiers())) {
					continue;
				}

				String fieldName = field.getName();
				SqlField sqlField = field.getAnnotation(SqlField.class);
				SqlPrimaryKey primaryKey = field.getAnnotation(SqlPrimaryKey.class);
				SqlUniqueKey uniqueKey = field.getAnnotation(SqlUniqueKey.class);
				if(sqlField == null
						&& primaryKey == null
						&& uniqueKey == null) {
					continue;
				}

				ObjectField objectField = nameSet.get(fieldName.toUpperCase());
				if(objectField == null) {
					field.setAccessible(true);
					objectField = new ObjectField();
					objectField.setCls(field.getDeclaringClass());
					Object value = null;
					try {
						value = field.get(obj);
					} catch (Exception e) {
						logger.warn("", e);
						e.printStackTrace();
					}
					objectField.setValue(value);
					objectFields.add(objectField);
					objectField.setFieldName(fieldName);
				}
				if(sqlField != null) {
					objectField.setDefaultValue(sqlField.defaultValue());
					String t = getAnnotationTableName(sqlField.clazz());
					if(t != null) {
						objectField.setTable(t);
					}

					objectField.setTableField(sqlField.value());
					objectField.setQueryKey(sqlField.queryKey());
					objectField.setUniqKey(sqlField.uniqKey());
					objectField.setUpdateValue(sqlField.updateValue());
				}
				if(primaryKey != null) {
					objectField.setPrimaryKey(true);
				}
				if(uniqueKey != null) {
					objectField.setUniqKey(true);
				}
				if(fieldName.equalsIgnoreCase("dateCreate")) {
					objectField.setDefaultValue("now()");
				} else if(fieldName.equalsIgnoreCase("dateUpdate")) {
					objectField.setDefaultValue("now()");
					objectField.setUpdateValue("now()");
				}

				nameSet.put(fieldName.toUpperCase(), objectField);
			}

			cls = cls.getSuperclass();
		}

		return objectFields;
	}

	/**
	 * 根据字段名,把map里面的记录转换成object
	 * @param params
	 * @return
	 */
	public static <T> T convertToObject(Map<String, Object> params, Class<? extends T> cls) {
		if(CollectionUtils.isEmpty(params)) {
			return null;
		}

		// 内部子参数的参数列表
		Map<String, Map<String, Object>> subParams = new HashMap<String, Map<String, Object>>();

		MetaClass metaClass = MetaClass.forClass(cls);
		String[] setProperties = metaClass.getSetterNames();
		Map<String, String> properiesMap = new HashMap<String, String>();
		for(String t : setProperties) {
			properiesMap.put(t.toUpperCase(), t);
		}

		T obj = null;
		try {
			obj = cls.newInstance();
		} catch (Exception e1) {
			logger.error("can not create object for class {}", cls.toString());
			return null;
		}

		for(Entry<String, Object> e : params.entrySet()) {
			String key = e.getKey();
			if(e.getKey().startsWith("__")) {
				// 是内部参数
				key = key.substring(2);
				String[] ks = key.split("_", 2);
				if(ks.length == 2) {
					Map<String, Object> subParam = subParams.get(ks[0]);
					if(subParam == null) {
						subParam = new HashMap<String, Object>();
						subParams.put(ks[0], subParam);
					}
					subParam.put(ks[1], e.getValue());
					continue;
				}
			}

			String property = properiesMap.get(key.toUpperCase());
			if(property == null) {
				continue;
			}

			Invoker invoker = metaClass.getSetInvoker(property);
			Class<?> setterCls = metaClass.getSetterType(property);

			if(invoker == null || setterCls == null) {
				// 找不到相应的set method
				continue;
			}
			Object value = ObjectUtil.convertValue(e.getValue(), setterCls);

			try {
				invoker.invoke(obj, new Object[]{value});
			} catch (Exception e1) {
				if(value != null) {
					logger.warn(value + ":" + value.getClass().getName() + ":" + setterCls.getName());
				}
				logger.warn("invoke method error", e1);
			}
		}

		// 对子参数进行处理
		for(Entry<String, Map<String, Object>> subParam : subParams.entrySet()) {
			String subProperty = subParam.getKey();
			if(!properiesMap.containsKey(subProperty.toUpperCase())) {
				continue;
			}

			subProperty = properiesMap.get(subProperty.toUpperCase());
			Class<?> setterCls = metaClass.getSetterType(subProperty);
			if(setterCls != null) {
				Object value = convertToObject(subParam.getValue(), setterCls);
				try {
				metaClass.getSetInvoker(subProperty).invoke(obj, new Object[]{value});
				} catch (Exception e1) {
					logger.warn("invoke method error", e1);
				}
			}
		}
		return obj;
	}
}
