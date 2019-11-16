package space.cosmos.one.mysql.codec.message;

public class ResultSetFieldMessage extends ServerMessage {
    private MysqlField mysqlField;

    public ResultSetFieldMessage(int packetLength, int packetNumber, MysqlField mysqlField) {
        super(packetLength, packetNumber);
        this.mysqlField = mysqlField;
    }

    public MysqlField getMysqlField() {
        return mysqlField;
    }
}
