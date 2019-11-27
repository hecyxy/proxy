package space.cosmos.one.binlog.handler.backend.result.handler;

import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.cosmos.one.binlog.handler.factory.BackendConnection;
import space.cosmos.one.common.packet.ErrorParser;
import space.cosmos.one.common.packet.OkParser;
import space.cosmos.one.common.packet.message.ErrorMessage;
import space.cosmos.one.common.packet.message.OkMessage;

import static space.cosmos.one.common.packet.message.PacketHeader.PACKET_ERR;
import static space.cosmos.one.common.packet.message.PacketHeader.PACKET_OK;


public abstract class ResultSetHandler {

    private static final Logger logger = LoggerFactory.getLogger(ResultSetHandler.class);
    protected BackendConnection source;

    public ResultSetHandler(BackendConnection source) {
        this.source = source;
    }

    /**
     * 抽象方法调用具体的实现
     */
    public void handleResultSet(ResultSet resultSet) {
        doHandleResultSet(resultSet);
        System.out.println("is on on on...");
        resultSet.clear();
    }

    public void handler(ByteBuf in) {
        int start = in.readerIndex() + 4;
        short packetType = in.getUnsignedByte(start);
        logger.info("start: {},packet type: {}", start, packetType);
        switch (packetType) {
            case PACKET_OK:
                OkParser ok = new OkParser(in);
                if (!ok.decode()) {
                    logger.info("ok msg {}", ok.getBody());
                }
                doOkay(ok.getBody());
                logger.info("ok msg {}", ok.getBody());
                break;
            case PACKET_ERR:
                ErrorParser err = new ErrorParser(in);
                if (!err.decode()) {
                    logger.info("ok msg {}", err.getBody());
                }
                logger.info("error msg {}", err.getBody());
                break;
            default:
                logger.warn("unknown message {}", packetType);
        }
    }

    public void doOkay(OkMessage okMsg) {

    }

    public void doErr(ErrorMessage err) {
        throw new RuntimeException(err.getErrMsg());
    }

    public void doHandleResultSet(ResultSet resultSet) {

    }

}
