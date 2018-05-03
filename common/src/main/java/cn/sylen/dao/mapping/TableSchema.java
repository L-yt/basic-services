package cn.sylen.dao.mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.sylen.common.util.CollectionUtils;
import cn.sylen.common.util.StringUtil;


/**
 * 定义一个表的结构，从mysql中导出
 * @author hongwm
 * @since 2013-8-21
 */
public class TableSchema {
	/**
	 * 表名
	 */
	private String tableName;

	/**
	 * 表结构字段
	 */
	private List<TableColumn> columns =
			new ArrayList<TableColumn>();

	/**
	 * 表的主键
	 */
	private TableColumn primaryColumn;
	/**
	 * uniq键列表，包括主键
	 */
	private List<TableColumn> uniqColumns =
			new ArrayList<TableColumn>();

	private boolean isPGSchema = false;

	/**
	 * 外部vo属性名和数据库字段名的映射关系map
	 */
	private Map<String, TableColumn> columnMap =
			new ConcurrentHashMap<String, TableColumn>();
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public List<TableColumn> getColumns() {
		return columns;
	}
	public void setColumns(List<TableColumn> columns) {
		this.columns = columns;
		if(CollectionUtils.isNotEmpty(columns)) {
			// 加到唯一键的列表中去
			for(TableColumn column : columns) {
				column.setTableSchema(this);
				if(column.isPrimaryKey()) {
					primaryColumn = column;
					uniqColumns.add(column);
				} else if(column.isUniqKey()) {
					uniqColumns.add(column);
				}
			}
		}
	}

	/**
	 * 获取主键
	 * @return
	 */
	public TableColumn getPrimaryColumn() {
		return primaryColumn;
	}

	/**
	 * 获取唯一键列表
	 * @return
	 */
	public List<TableColumn> getUniqColumns() {
		return uniqColumns;
	}

	/**
	 * 获取数据库表中匹配的列
	 * @param fieldName
	 * @return
	 */
	public TableColumn getMatchColumn(String fieldName) {
		TableColumn column = columnMap.get(fieldName);
		if(column == null) {
			String trimedFieldName = StringUtil.getLetterOrDigit(fieldName).toUpperCase();
			for(TableColumn c : columns) {
				String trimedColumnField = StringUtil.getLetterOrDigit(c.getField()).toUpperCase();
				if(trimedFieldName.equals(trimedColumnField)) {
					column = c;
					break;
				}

			}

			if(column == null) {
				column = new TableColumn();
			}
			column.setTableSchema(this);
			columnMap.put(fieldName, column);
		}

		if(column.getField() == null) {
			return null;
		}

		return column;
	}

	/**
	 * 根据输入属性名，获取匹配到的数据库字段名
	 * @param name
	 * @return
	 */
	public String getMatchColumnName(String fieldName) {
		if(fieldName == null) {
			return null;
		}

		TableColumn column = getMatchColumn(fieldName);

		if(column == null) {
			throw new RuntimeException("can not match column " + fieldName + " from table " + tableName);
		}

		return column.getField();
	}

	public boolean isPGSchema() {
        return isPGSchema;
    }

	public void setPGSchema(boolean isPGSchema) {
        this.isPGSchema = isPGSchema;
    }

    @Override
    public String toString() {
        return "TableSchema [tableName=" + tableName + ", columns=" + columns
                + ", columnMap=" + columnMap + "]";
    }


}
