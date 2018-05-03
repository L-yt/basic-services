package cn.sylen.dao.mapping;

public class PgTableSchema  extends TableColumn {

    private static final long serialVersionUID = 11932136023826787L;

    private String column;


    public void setColumn(String column) {
        this.column = column;
        super.setField(column);
    }

    public String getColumn() {
        return column;
    }
}
