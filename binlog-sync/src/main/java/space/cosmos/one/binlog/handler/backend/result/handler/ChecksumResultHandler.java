package space.cosmos.one.binlog.handler.backend.result.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.cosmos.one.binlog.handler.backend.netty.BinlogEventHandler;
import space.cosmos.one.binlog.handler.factory.BackendConnection;
import space.cosmos.one.binlog.handler.request.CommandRequest;
import space.cosmos.one.binlog.handler.request.DumpBinaryLogCommand;
import space.cosmos.one.binlog.handler.request.DumpBinaryLogGtidCommand;
import space.cosmos.one.binlog.util.SystemConfig;
import space.cosmos.one.common.packet.binlog.ChecksumType;
import space.cosmos.one.common.packet.binlog.decoder.DecoderConfig;
import space.cosmos.one.common.packet.message.Command;
import space.cosmos.one.common.util.BufferUtils;

public class ChecksumResultHandler extends ResultSetHandler {

    private static final Logger logger = LoggerFactory.getLogger(ChecksumResultHandler.class);

    public ChecksumResultHandler(BackendConnection source) {
        super(source);
    }


    @Override
    public void handleResultSet(ResultSet resultSet) {
        logger.info("result set {}", resultSet.toString());
        String checksum;
        if (resultSet.getRows().isEmpty()) {
            checksum = "NONE";
        } else {
            checksum = resultSet.getRows().get(1)[1];
        }
        // 校验checksum
        if (ChecksumType.valueOf(checksum.toUpperCase()) == ChecksumType.NONE) {
            DecoderConfig.checksumType = ChecksumType.NONE;
            setBinlogEventHandler();
            // 发送dump命令
            if (source.getBinlogContext().getGtidSet() == null) {
                sendDumpBinaryLog();
            } else {
                sendDumpBinaryLogGtid();
            }
        } else if (ChecksumType.valueOf(checksum.toUpperCase()) == ChecksumType.CRC32) {
            DecoderConfig.checksumType = ChecksumType.CRC32;
            source.setResultSetHandler(new SetCheckSumHandler(source));
            // set binlog_checksum todo write no select是什么意思
            source.setSelecting(false);
            source.getCtx().writeAndFlush(new CommandRequest(Command.QUERY, BufferUtils.wrapString("set @master_binlog_checksum= @@global.binlog_checksum")));
//            source.writeNoSelect();
        } else {
            throw new RuntimeException("Unknown checksum type:" + ChecksumType.valueOf(checksum.toUpperCase()));
        }
    }

    // begin to parse binlog event
    public void setBinlogEventHandler() {
        source.getCtx().pipeline()
                .replace("BackendCommandHandler", "BinlogEventHandler", new BinlogEventHandler(source));
    }

    private void sendDumpBinaryLog() {
        DumpBinaryLogCommand command =
                new DumpBinaryLogCommand(SystemConfig.serverId, source.getBinlogContext().getBinlogFileName()
                        , source.getBinlogContext().getBinlogPosition());
        //todo
        source.getCtx().writeAndFlush(command);
        logger.debug("send dump command");
    }

    private void sendDumpBinaryLogGtid() {
        DumpBinaryLogGtidCommand command =
                new DumpBinaryLogGtidCommand(SystemConfig.serverId, "", 4L, source.getBinlogContext().getGtidSet());
        source.getCtx().writeAndFlush(command.getByteBuf(source.getCtx()));
        logger.debug("send dump gtid command");
    }


}
