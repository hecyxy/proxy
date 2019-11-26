package space.cosmos.one.binlog.handler.backend.result.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.cosmos.one.binlog.handler.backend.netty.BinlogEventHandler;
import space.cosmos.one.binlog.handler.factory.BackendConnection;
import space.cosmos.one.binlog.handler.request.DumpBinaryLogCommand;
import space.cosmos.one.binlog.handler.request.DumpBinaryLogGtidCommand;
import space.cosmos.one.binlog.util.SystemConfig;
import space.cosmos.one.common.packet.message.OkMessage;

public class SetCheckSumHandler extends ResultSetHandler {

    private static final Logger logger = LoggerFactory.getLogger(SetCheckSumHandler.class);


    public SetCheckSumHandler(BackendConnection source) {
        super(source);
    }

    @Override
    public void doOkay(OkMessage okPacket) {
        setBinlogEventHandler();
        // 发送dump命令
        if (source.getBinlogContext().getGtidSet() == null) {
            sendDumpBinaryLog();
        } else {
            sendDumpBinaryLogGtid();
        }

    }

    private void sendDumpBinaryLog() {
        DumpBinaryLogCommand command =
                new DumpBinaryLogCommand(SystemConfig.serverId, source.getBinlogContext().getBinlogFileName()
                        , source.getBinlogContext().getBinlogPosition());
//        source.getCtx().writeAndFlush(command.getByteBuf(source.getCtx()));
        logger.debug("send dump command");
    }

    private void sendDumpBinaryLogGtid() {
        DumpBinaryLogGtidCommand command =
                new DumpBinaryLogGtidCommand(SystemConfig.serverId, "", 4L, source.getBinlogContext().getGtidSet());
        source.getCtx().writeAndFlush(command.getByteBuf(source.getCtx()));
        logger.debug("send dump gtid command");
    }

    // begin to parse binlog event
    public void setBinlogEventHandler() {
        source.getCtx().pipeline()
                .replace("BackendCommandHandler"
                        , "BinlogEventHandler"
                        , new BinlogEventHandler(source));
    }
}
