package space.cosmos.one.binlog.handler.request;

import io.netty.buffer.ByteBuf;
import io.netty.util.concurrent.Promise;
import space.cosmos.one.common.packet.message.Command;

import java.util.HashMap;
import java.util.Map;

public class CommandRequest extends ClientRequest {
    private final Command cmd;

    private final ByteBuf data;

    private Map<String, Object> extraData = new HashMap<>();

    public CommandRequest(Command cmd, ByteBuf data) {
        this(cmd, data, 0x00);
    }

    public CommandRequest(Command cmd, ByteBuf data, int sequenceNo) {
        super(sequenceNo);
        this.cmd = cmd;
        this.data = data;
    }

    public Command getCmd() {
        return cmd;
    }

    public ByteBuf getData() {
        return data;
    }

    public void addExtraData(String key, Object value) {
        extraData.put(key, value);
    }

    public Object getExtraData(String key) {
        return extraData.get(key);
    }
}
