package space.cosmos.one.binlog.handler.request;

import io.netty.buffer.ByteBuf;

public class AuthRequest extends ClientRequest {
    private final ByteBuf body;

    public AuthRequest(int sequenceNo, ByteBuf body) {
        super(sequenceNo);
        this.body = body;
    }

    public ByteBuf getBody() {
        return body;
    }
}
