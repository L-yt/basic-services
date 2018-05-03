package cn.sylen.dao.mapping;

import java.io.Serializable;

/**
 * 表的某一个列名
 * @author hongwm
 * @since 2013-8-21
 */
public class TableColumn implements Serializable {
    private static final long serialVersionUID = 7877567339122917860L;

    private String field;
    private String type;
    private String key;
    private String Default;
    private TableSchema tableSchema;
    public String getField() {
        return field;
    }
    public void setField(String field) {
        this.field = field;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }

    public String getDefault() {
        return Default;
    }
    public void setDefault(String default1) {
        Default = default1;
    }
    /**
     * 判断该列是否主键
     * @return
     */
    public boolean isPrimaryKey() {
        return "PRI".equals(key);
    }

    /**
     * 判断该列是否唯一索引
     * @return
     */
    public boolean isUniqKey() {
        return "UNI".equals(key);
    }
    public TableSchema getTableSchema() {
        return tableSchema;
    }
    public void setTableSchema(TableSchema tableSchema) {
        this.tableSchema = tableSchema;
    }
    @Override
    public String toString() {
        return "TableColumn [field=" + field + "]";
    }

}
