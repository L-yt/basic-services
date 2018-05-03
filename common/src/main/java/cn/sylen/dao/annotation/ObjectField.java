package cn.sylen.dao.annotation;

import java.io.Serializable;
import java.lang.reflect.Method;


/**
 * 定义vo对象一个field相关的属性
 * @author hongwm
 * @since 2013-8-22
 */
public class ObjectField implements Serializable {
	private static final long serialVersionUID = -7805480019688029746L;
	/**
	 * 字段名
	 */
	private String fieldName;
	/**
	 * 映射到数据库中的字段名
	 */
	private String tableField;
	/**
	 * 插入时的默认值
	 */
	private String defaultValue;
	/**
	 * 更新时的默认值，一般用来置updateTime
	 */
	private String updateValue;
	/**
	 * field的值
	 */
	private Object value;

	/**
	 * 配置项是否做为查询条件
	 */
	private boolean queryKey;

	/**
	 * 配置项里面是否唯一键值
	 */
	private boolean uniqKey = false;

	/**
	 * 是否主键
	 */
	private boolean primaryKey = false;
	/**
	 * 定义该字段映射的表名
	 */
	private String table;

	/**
	 * 父object的变量
	 */
	private String parentField;

	/**
	 * 该字段对应的class
	 */
	private Class<?> cls;

	private Method getMethod;
	private Object parentObj;

	public String getFieldName() {
		if(parentField == null) {
			return fieldName;
		} else {
			return "__" + parentField + "_" + fieldName;
		}
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getUpdateValue() {
		return updateValue;
	}

	public void setUpdateValue(String updateValue) {
		this.updateValue = updateValue;
	}

	public Object getValue() {
		if(value == null && getMethod != null && parentObj != null) {
			// 调用get方法
			try {
				value = getMethod.invoke(parentObj);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(value == null){
		    return null;
		}
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Class<?> getCls() {
		return cls;
	}

	public void setCls(Class<?> cls) {
		this.cls = cls;
	}

	public boolean isQueryKey() {
		return queryKey;
	}

	public void setQueryKey(boolean queryKey) {
		this.queryKey = queryKey;
	}

	public boolean isUniqKey() {
		return uniqKey;
	}
	public void setUniqKey(boolean uniqKey) {
		this.uniqKey = uniqKey;
	}

	public String getTableField() {
		return tableField;
	}
	public void setTableField(String tableField) {
		this.tableField = tableField;
	}

	public boolean isPrimaryKey() {
		return primaryKey;
	}
	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}
	public String getTable() {
		return table;
	}
	public void setTable(String table) {
		this.table = table;
	}
	public String getUpdateFieldName() {
		if("value".equals(fieldName)) {
			// insert/update时，字段名不允许出现value字段，否则会报错
			return "value1";
		}

		return getFieldName();
	}

	public String getParentField() {
		return parentField;
	}

	public void setParentField(String parentField) {
		this.parentField = parentField;
	}

	public boolean isDefaultPrimitiveValue() {
		if(getValue() == null) {
			return false;
		}
		if(!cls.isPrimitive()) {
			return false;
		}

		String className = cls.toString();
		if(className.equals("int")) {
			return value.equals(0);
		} else if(className.equals("long")) {
			return value.equals((long)0);
		} else if(className.equals("double")) {
			return value.equals((double)0);
		} else if(className.equals("float")) {
			return value.equals((float)0);
		} else if(className.equals("boolean")) {
			return value.equals(false);
		}

		return false;

	}

	public Method getGetMethod() {
		return getMethod;
	}
	public void setGetMethod(Method getMethod) {
		this.getMethod = getMethod;
	}
	public Object getParentObj() {
		return parentObj;
	}
	public void setParentObj(Object parentObj) {
		this.parentObj = parentObj;
	}
}
