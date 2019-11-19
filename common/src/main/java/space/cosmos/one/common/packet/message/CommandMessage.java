package space.cosmos.one.common.packet.message;

public class CommandMessage {
    private String typeName;
    private String cmdName;

    public CommandMessage(String typeName, String command) {
        this.typeName = typeName;
        this.cmdName = command;
    }

    @Override
    public String toString() {
        return String.format("command type is[%s],command is [%s]", this.typeName, this.cmdName);
    }
}
