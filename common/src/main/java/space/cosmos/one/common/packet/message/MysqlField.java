package space.cosmos.one.common.packet.message;

import java.util.Set;

public class MysqlField {
    private final int index;
    private final String catalogName;
    private final String schemaName;
    private final String tableLabel;
    private final String tableName;
    private final FieldType columnType;
    private final int columnLength;
    private final String columnLabel;
    private final String columnName;
    private Set<FieldFlag> flags;
    private int colDecimals;

    public MysqlField(int index, String catalogName, String schemaName, String tableLabel, String tableName, FieldType columnType, String columnLabel, String columnName, int columnLength, Set<FieldFlag> flags, int colDecimals) {
        this.index = index;
        this.catalogName = catalogName;
        this.schemaName = schemaName;
        this.tableLabel = tableLabel;
        this.tableName = tableName;
        this.columnType = columnType;
        this.columnLabel = columnLabel;
        this.columnName = columnName;
        this.columnLength = columnLength;
        this.flags = flags;
        this.colDecimals = colDecimals;
    }

    public int getIndex() {
        return index;
    }

    public String getCatalogName() {
        return catalogName;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public String getTableLabel() {
        return tableLabel;
    }

    public String getTableName() {
        return tableName;
    }

    public FieldType getColumnType() {
        return columnType;
    }

    public String getColumnLabel() {
        return columnLabel;
    }

    public String getColumnName() {
        return columnName;
    }

    public int getColumnLength() {
        return columnLength;
    }

    public Set<FieldFlag> getFlags() {
        return flags;
    }

    public int getColDecimals() {
        return colDecimals;
    }


    @Override
    public String toString() {
        return "index:" + index + " catalogName: " + catalogName + " schemaName:" + schemaName
                + " tableLabel:"+tableLabel+" tableName:"+tableName+" columnType:"+columnType
                +" tableLabel:"+columnLength+" columnLabel:"+columnLabel
                + "columnName:"+columnName+" flags:"+flags
                +" colDecimals: "+colDecimals;
    }

}