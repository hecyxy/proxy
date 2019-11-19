package space.cosmos.one.common.packet.message;

public class ResultSetFieldMessage extends ServerMessage {
    private MysqlField mysqlField;

    public ResultSetFieldMessage(int packetLength, int packetNumber, MysqlField mysqlField) {
        super(packetLength, packetNumber);
        this.mysqlField = mysqlField;
    }

    public MysqlField getMysqlField() {
        return mysqlField;
    }

    @Override
    public String toString() {
        return String.format("field message :", mysqlField.toString());
    }
}
