package space.cosmos.one.common.packet.message;

public class ErrorMessage extends ServerMessage{

    private final int errCode;

    private final String sqlState;

    private final String errMsg;

    public ErrorMessage(int packetLength, int packetNumber, int errCode, String sqlState, String errMsg) {
        super(packetLength, packetNumber);
        this.errCode = errCode;
        this.sqlState = sqlState;
        this.errMsg = errMsg;
    }


    public int getErrCode() {
        return errCode;
    }

    public String getSqlState() {
        return sqlState;
    }

    public String getErrMsg() {
        return errMsg;
    }
}
