package space.cosmos.one.binlog.handler.backend.netty;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.cosmos.one.binlog.util.CharsetUtil;

public class BackendConnection {
    private static final Logger logger = LoggerFactory.getLogger(BackendConnection.class);

    public int charsetIndex;
    public String charset;

    private long threadId;

    private ChannelHandlerContext ctx;

    public boolean setCharset(String charset) {
        int ci = CharsetUtil.getIndex(charset);
        if (ci > 0) {
            this.charset = charset;
            this.charsetIndex = ci;
            return true;
        } else {
            return false;
        }
    }

    public boolean isAlive() {
        return ctx.channel().isActive();
    }

}
