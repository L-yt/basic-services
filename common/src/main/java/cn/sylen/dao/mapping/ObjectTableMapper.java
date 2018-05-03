package cn.sylen.dao.mapping;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.sylen.common.util.CollectionUtils;
import cn.sylen.common.util.StringUtil;
import cn.sylen.dao.annotation.ObjectField;
import cn.sylen.dao.annotation.SqlAnnotationResolver;

import org.apache.ibatis.reflection.MetaClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 定义vo与表结构的映射关系
 * @author hongwm
 * @since 2013-8-22
 */
public class ObjectTableMapper implements Serializable {
	private static final long serialVersionUID = 222209056554684681L;
	private static final Logger logger = LoggerFactory.getLogger(ObjectTableMapper.class);
	/**
	 * 要操作的vo实体
	 */
	private Object object;
	/**
	 * 映射的表结构
	 */
	private TableSchema tableSchema;

	private MetaClass metaClass;

	private List<ObjectTableMapper> subObjects = new ArrayList<ObjectTableMapper>();

	/**
	 * vo与表结构相互映射到的字段列表
	 */
	private List<FieldMapper> mappedFields = new ArrayList<FieldMapper>();

	private ObjectTableMapper(){}

	/**
	 * @param cls
	 * @return
	 */
	public static ObjectTableMapper buildTableMapper(Class<?> cls) {
		try {
			return buildTableMapper(cls.newInstance(), cls);
		} catch (Exception e) {
			logger.error("can not create new instance for class {}", cls.toString());
			return null;
		}
	}

	/**
	 * 建立object内部的表映射关系
	 */
	public static ObjectTableMapper buildTableMapper(Object obj) {
		return buildTableMapper(obj, null);
	}

	/**
	 * 建立object与cls包含表名的映射关系
	 */
	public static ObjectTableMapper buildTableMapper(Object obj, Class<?> cls) {
		return buildTableMapper(obj, cls, null);
	}

	/**
	 * 建立表名与类名的映射关系
	 */
	public static ObjectTableMapper buildTableMapper(Class<?> cls, String tableName) {
		return buildTableMapper(null, cls, tableName);
	}

	public static ObjectTableMapper buildTableMapper(Object obj, Class<?> cls, String tableName) {
		if(obj == null && cls == null) {
			return null;
		} else if(cls == null) {
			cls = obj.getClass();
		} else if(obj == null){
			try {
				obj = cls.newInstance();
			} catch (Exception e) {
			}
		}

		if(tableName == null) {
			tableName = SqlAnnotationResolver.getAnnotationTableName(cls);
		}

		ObjectTableMapper tableMapper = new ObjectTableMapper();
		tableMapper.setMetaClass(MetaClass.forClass(cls));
		tableMapper.setObject(obj);

		// 对object里面的变量,建立表映射
		tableMapper.buildSubTableMaps(cls);


		TableSchema tableSchema = TableMappingFactory.getTableSchema(tableName);

		if(CollectionUtils.isEmpty(tableMapper.getSubObjects()) && tableSchema == null) {
			// 没有定义表声明
			logger.error("table {} schema not exist", tableName);
			return null;
		}
		List<FieldMapper> mappedFields = new ArrayList<FieldMapper>();

		tableMapper.setTableSchema(tableSchema);

		// 内部对象的field
		List<ObjectField> objectFields = SqlAnnotationResolver.getObjectFieds(obj);
		for(ObjectField objectField : objectFields) {
			String tableField = objectField.getTableField();
			if(StringUtil.isEmpty(tableField)) {
				tableField = objectField.getFieldName();
			}

			String table = objectField.getTable();
			TableSchema fieldTableSchema = tableSchema;
			if(StringUtil.isNotEmpty(table)) {
				fieldTableSchema = TableMappingFactory.getTableSchema(table);
			}

			if(fieldTableSchema == null) {
				fieldTableSchema = tableSchema;
			}

			if(fieldTableSchema == null) {
				continue;
			}

			TableColumn column = fieldTableSchema.getMatchColumn(tableField);
			if(column != null) {
				FieldMapper mappedField = new FieldMapper(column, objectField);
				mappedFields.add(mappedField);
			}

		}

		tableMapper.setMappedFields(mappedFields);
		return tableMapper;
	}

	/**
	 * 建立变量类的object sql map
	 * @param cls
	 */
	public void buildSubTableMaps(Class<?> cls) {
		if(metaClass == null) {
			metaClass = MetaClass.forClass(cls);
		}

		String[] getProperties = metaClass.getGetterNames();
		for(String property : getProperties) {
			Class<?> getterCls = metaClass.getGetterType(property);
			if(getterCls == null || getterCls.getName().equals(cls.getName())) {
				continue;
			}

			String table = SqlAnnotationResolver.getAnnotationTableName(getterCls);
			if(table == null) {
				continue;
			}
			Object subValue = null;
			try {
				subValue = metaClass.getGetInvoker(property).invoke(object, new Object[]{});
			} catch (Exception e) {
			}
			ObjectTableMapper subObject = ObjectTableMapper.buildTableMapper(subValue, getterCls);
			if(subObject != null) {
				for(FieldMapper mapper : subObject.getAllMappedFields()) {
					// 设置子属性
					mapper.getObjectField().setParentField(property);
				}
				subObjects.add(subObject);
			}
		}

	}

	public Object getObject() {
		return object;
	}
	public void setObject(Object voObject) {
		this.object = voObject;
	}
	public TableSchema getTableSchema() {
		return tableSchema;
	}
	public void setTableSchema(TableSchema tableSchema) {
		this.tableSchema = tableSchema;
	}
	public List<FieldMapper> getMappedFields() {
		return mappedFields;
	}

	public List<FieldMapper> getUniqkeyFields() {
		List<FieldMapper> uniqFields = new ArrayList<FieldMapper>();
		for(FieldMapper field : mappedFields) {
			if(field.isUniqueKey()) {
				uniqFields.add(field);
			}
		}

		return uniqFields;
	}

	/**
	 * 获取主键映射关系
	 */
	public FieldMapper getPrimarykeyField() {
		for(FieldMapper field : mappedFields) {
			if(field.isPrimaryKey()) {
				return field;
			}
		}

		return null;
	}

	public List<FieldMapper> getAllMappedFields() {
		List<FieldMapper> fields = new ArrayList<FieldMapper>();
		fields.addAll(getMappedFields());
		for(ObjectTableMapper subMapper : subObjects) {
			fields.addAll(subMapper.getMappedFields());
		}
		return fields;
	}

	public void setMappedFields(List<FieldMapper> mappedFields) {
		this.mappedFields = mappedFields;
	}

	public MetaClass getMetaClass() {
		return metaClass;
	}
	public void setMetaClass(MetaClass metaClass) {
		this.metaClass = metaClass;
	}
	public List<ObjectTableMapper> getSubObjects() {
		return subObjects;
	}
	public void setSubObjects(List<ObjectTableMapper> subObjects) {
		this.subObjects = subObjects;
	}

}
