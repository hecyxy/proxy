package space.cosmos.one.binlog.handler.backend.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.cosmos.one.binlog.handler.backend.BackendConnState;
import space.cosmos.one.binlog.handler.backend.result.handler.ResultSet;
import space.cosmos.one.binlog.handler.factory.BackendConnection;
import space.cosmos.one.common.packet.EofParser;
import space.cosmos.one.common.packet.ErrorParser;
import space.cosmos.one.common.packet.FieldParser;
import space.cosmos.one.common.packet.RowParser;
import space.cosmos.one.common.packet.message.EofMessage;
import space.cosmos.one.common.packet.message.ServerStatus;

public class BackendCommandHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(BackendCommandHandler.class);

    private BackendConnection source;

    private ResultSet resultSet;

    private int nowFieldCount;

    private volatile int selectState;

    public BackendCommandHandler(BackendConnection source) {
        this.source = source;
        selectState = BackendConnState.RESULT_SET_FIELD_COUNT;
        resultSet = new ResultSet();
        nowFieldCount = 0;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        handleResponse((ByteBuf) msg);
    }

    private boolean handleResponse(ByteBuf bin) {
        System.out.println(source.isSelecting()+"  "+bin.readableBytes());
        if (source.isSelecting()) {
            return handleResultSet(bin);
        } else {
            return handleNormalResult(bin);
        }
    }

    private boolean handleResultSet(ByteBuf buf) {
        boolean result = false;
        short packType = buf.getUnsignedByte(buf.readerIndex() + 4);
        System.out.println(String.format("packet type %s", packType));
        switch (packType) {
            case 0xff: //error packet.field_count error packet
                // 重置状态,且告诉上层当前select已经处理完毕
                logger.info("field count 1...");
                resetSelect();
                resultSet.clear();
                logger.info("error");
                result = true;
                ErrorParser errorParser = new ErrorParser(buf);
                if (!errorParser.decode()) {
                    return false;
                }
                logger.error("handleResultSet errorMessage:" + errorParser.getBody());
                break;
            case 0xfe: //eof field_count
                logger.info("field count 2...");
                EofParser eofParser = new EofParser(buf, EofMessage.Type.FIELD);
                if (!eofParser.decode()) {
                    return false;
                }
                System.out.println("select state"+selectState);
                if (selectState == BackendConnState.RESULT_SET_FIELDS) {
                    resultSet.setFieldCount(nowFieldCount);
                    // 推进状态 需要步进两次状态,先到field_eof,再到row
                    selectStateStep();
                    selectStateStep();
                } else {
                    if (eofParser.getBody().getServerStatus().contains(ServerStatus.MORE_RESULTS)) {
                        // 重置为select的初始状态,但是还是处在select mode下
                        selectState = BackendConnState.RESULT_SET_FIELD_COUNT;
                    } else {
                        // 重置,且告诉上层当前select已经处理完毕
                        resetSelect();
                        // 顺序不可变
                        source.getResultSetHandler().handleResultSet(resultSet);
                        result = true;
                    }
                }
                break;
            default:
                switch (selectState) {
                    case BackendConnState.RESULT_SET_FIELD_COUNT:
                        selectStateStep();
                        logger.info("default...1");
                        break;
                    case BackendConnState.RESULT_SET_FIELDS:
                        FieldParser fieldParser = new FieldParser(buf, 0);
                        logger.info("default...2");
                        if (!fieldParser.decode()) {
                            return false;
                        }
                        resultSet.addField(fieldParser.getBody().getMysqlField().getColumnName());
                        // 累积的FieldCount
                        nowFieldCount++;
                        break;
                    case BackendConnState.RESULT_SET_ROW:
                        logger.info("default...3");
                        RowParser rowParser = new RowParser(buf, nowFieldCount);
                        if (!rowParser.decode()) {
                            return false;
                        }
                        resultSet.addRow(rowParser.getBody().getRow());
                        break;
                }
        }
        return result;
    }

    private void resetSelect() {
        source.setSelecting(false);
        selectState = BackendConnState.RESULT_SET_FIELD_COUNT;
        nowFieldCount = 0;
        // resultSet的清理下放到ResultSetHandler执行
    }

    // select状态的推进
    private void selectStateStep() {
        selectState++;
        // last_eof和field_count合并为同一状态
        if (selectState == 6) {
            selectState = 2;
        }
    }

    private boolean handleNormalResult(ByteBuf bin) {
        source.getResultSetHandler().handler(bin);
        return true;
    }
}
