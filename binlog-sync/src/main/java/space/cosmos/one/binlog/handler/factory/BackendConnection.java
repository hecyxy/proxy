package space.cosmos.one.binlog.handler.factory;

import io.netty.channel.ChannelHandlerContext;
import space.cosmos.one.binlog.handler.backend.result.BinlogContext;
import space.cosmos.one.binlog.handler.backend.result.handler.ResultSetHandler;

public class BackendConnection {
    private int charsetIndex;
    private String charset;
    private long threadId;

    public boolean isSelecting() {
        return selecting;
    }

    public void setSelecting(boolean selecting) {
        this.selecting = selecting;
    }

    private volatile boolean selecting;

    private ChannelHandlerContext ctx;

    private BinlogContext binlogContext;

    private ResultSetHandler resultSetHandler;

    public ResultSetHandler getResultSetHandler() {
        return resultSetHandler;
    }

    public void setResultSetHandler(ResultSetHandler resultSetHandler) {
        this.resultSetHandler = resultSetHandler;
    }

    public BackendConnection() {
        this.binlogContext = new BinlogContext();
    }

    public int getCharsetIndex() {
        return charsetIndex;
    }

    public void setCharsetIndex(int charsetIndex) {
        this.charsetIndex = charsetIndex;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public BinlogContext getBinlogContext() {
        return binlogContext;
    }

    public void setBinlogContext(BinlogContext binlogContext) {
        this.binlogContext = binlogContext;
    }
}
