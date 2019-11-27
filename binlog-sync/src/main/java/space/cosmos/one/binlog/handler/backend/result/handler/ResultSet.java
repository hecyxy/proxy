package space.cosmos.one.binlog.handler.backend.result.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ResultSet {
    private static final Logger logger = LoggerFactory.getLogger(ResultSet.class);
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


    public void addField(String field) {
        fields.add(field);
    }

    public void addRow(String[] row) {
        rows.add(row);
    }

    public void clear() {
        logger.info("execute clear...");
        fields.clear();
        rows.clear();
    }

    private String joinToString(List<String> lis) {
        StringBuilder sb = new StringBuilder();
        lis.forEach(a -> sb.append(a).append(","));
        return sb.toString();
    }

    private String rowSjoinToString() {
        StringBuilder sb = new StringBuilder();
        rows.forEach(a -> {
            for (int i = 0; i < a.length; i++) {
                sb.append(a[i]).append(",");
            }
        });
        return sb.toString();
    }

    @Override
    public String toString() {
        return "ResultSet{" +
                "fieldCount=" + fieldCount +
                ", fields=" + joinToString(fields) +
                ", rows=" + rowSjoinToString() +
                '}';
    }
}
