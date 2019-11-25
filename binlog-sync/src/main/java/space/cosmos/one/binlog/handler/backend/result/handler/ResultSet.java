package space.cosmos.one.binlog.handler.backend.result.handler;

import java.util.ArrayList;
import java.util.List;

public class ResultSet {
    private int fieldCount;
    private List<String> fields;
    private List<String[]> rows;

    public ResultSet() {
        fieldCount = 0;
        fields = new ArrayList<>();
        rows = new ArrayList<>();
    }

    public int getFieldCount() {
        return fieldCount;
    }

    public void setFieldCount(int fieldCount) {
        this.fieldCount = fieldCount;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public List<String[]> getRows() {
        return rows;
    }

    public void setRows(List<String[]> rows) {
        this.rows = rows;
    }

    public void clear() {
        fields.clear();
        rows.clear();
    }

    @Override
    public String toString() {
        return "ResultSet{" +
                "fieldCount=" + fieldCount +
                ", fields=" + fields +
                ", rows=" + rows +
                '}';
    }
}
