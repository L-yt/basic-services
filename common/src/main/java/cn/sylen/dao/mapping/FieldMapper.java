package cn.sylen.dao.mapping;

import java.io.Serializable;

import cn.sylen.common.util.StringUtil;
import cn.sylen.dao.annotation.ObjectField;

/**
 * object字段与数据库表中某一列的字段映射
 * @author hongwm
 * @since 2013-8-22
 */
public class FieldMapper implements Serializable {
	private static final long serialVersionUID = -4657592498249856528L;
	/**
	 * 对应的数据库列信息
	 */
	private TableColumn column;
	/**
	 * 对应的object field信息
	 */
	private ObjectField objectField;

	public FieldMapper(TableColumn column, ObjectField objectField) {
		this.column = column;
		this.objectField = objectField;
	}

	public String getColumnFieldNameWithSymbol() {
	    return "`" + column.getField() + "`";
	}

    public String getColumnFieldNameWithQuote() {
        return "\"" + column.getField() + "\"";
    }

	public String getColumnFieldName() {
		return column.getField();
	}

	/**
	 * 判断该映射字段的值是否为空
	 */
	public boolean isNullObject() {
		return objectField.getValue() == null;
	}

	/**
	 * 判断该字段对应的值是否合法的插入字段
	 */
	public boolean isLegalInsertColumn() {
		if(objectField.getValue() != null) {
			return true;
		}
		if(StringUtil.isNotEmpty(objectField.getDefaultValue())) {
			return true;
		}

		return false;
	}

	/**
	 * 判断该字段对应的值是否合法的更新字段
	 */
	public boolean isLegalUpdateColumn() {
		if(objectField.isDefaultPrimitiveValue()) {
			// 该字段为弱变量,且为默认的值,不更新
			return false;
		}
		if(objectField.getValue() != null) {
			return true;
		}
		if(StringUtil.isNotEmpty(objectField.getUpdateValue())) {
			return true;
		}

		return false;
	}

	/**
	 * 判断该字段是否date_update字段
	 */
	public boolean isDateUpdateColumn() {
		return getColumnFieldName().equals("date_update");
	}

	/**
	 * 判断该字段是否date_update字段
	 */
	public boolean isDateCreateColumn() {
		return getColumnFieldName().equals("date_create");
	}

	/**
	 * 判断该字段是否date_delete字段
	 */
	public boolean isDateDeleteColumn() {
		return getColumnFieldName().equals("date_delete");
	}

	/**
	 * field是否匹配上数据库表中的字段
	 */
	public boolean fieldMatched() {
		if(column == null || column.getField() == null) {
			return false;
		}
		return true;
	}

	public boolean isPrimaryKey() {
		if(objectField.isPrimaryKey()) {
			return true;
		}

		return column.isPrimaryKey();
	}

	public boolean isUniqueKey() {
		if(objectField.isUniqKey()) {
			return true;
		}

		return column.isUniqKey();
	}

	public boolean matchName(String key) {
		String k = StringUtil.getLetterOrDigit(key).toUpperCase();
		if(k.equalsIgnoreCase(StringUtil.getLetterOrDigit(column.getField()))) {
			return true;
		}

		if(k.equalsIgnoreCase(StringUtil.getLetterOrDigit(objectField.getFieldName()))) {
			return true;
		}

		return false;
	}
	public TableColumn getColumn() {
		return column;
	}
	public void setColumn(TableColumn column) {
		this.column = column;
	}
	public ObjectField getObjectField() {
		return objectField;
	}
	public void setObjectField(ObjectField objectField) {
		this.objectField = objectField;
	}

}
