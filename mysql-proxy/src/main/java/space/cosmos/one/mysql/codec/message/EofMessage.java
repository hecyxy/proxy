package space.cosmos.one.mysql.codec.message;

import space.cosmos.one.mysql.util.ServerStatus;

import java.util.Set;

public class EofMessage extends ServerMessage {
    public enum Type {
        FIELD, ROW, PS_PARAMETER, PS_COLUMN,
    }

    private final int warnings;
    private final Set<ServerStatus> serverStatus;
    private final Type type;

    public EofMessage(int packetLength, int packetNumber, int warnings, Set<ServerStatus> serverStatus, Type type) {
        super(packetLength, packetNumber);
        this.warnings = warnings;
        this.serverStatus = serverStatus;
        this.type = type;
    }

    public int getWarnings() {
        return warnings;
    }

    public Set<ServerStatus> getServerStatus() {
        return serverStatus;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return String.format("[warnings: %s ],[server status: %s],[type: %s]", warnings, serverStatus, type);
    }
}
