package space.cosmos.one.common.packet.message;

public class ResultSetRowMessage extends ServerMessage {
    private final String[] row;

    public ResultSetRowMessage(int packetLength, int packetNumber, String[] row) {
        super(packetLength, packetNumber);
        this.row = row;
    }

    public String[] getRow() {
        return row;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (String r : row) {
            builder.append(r + ",");
        }
        return builder.toString();
    }
}
