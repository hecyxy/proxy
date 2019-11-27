package space.cosmos.one.binlog.handler.backend.result.handler;

import space.cosmos.one.binlog.handler.factory.BackendConnection;
import space.cosmos.one.binlog.handler.request.CommandRequest;
import space.cosmos.one.common.packet.message.Command;
import space.cosmos.one.common.util.BufferUtils;

public class PositionResultHandler extends ResultSetHandler {

    public PositionResultHandler(BackendConnection connection) {
        super(connection);
    }

    public void doHandleResultSet(ResultSet resultSet) {
        source.getBinlogContext().handlePositionResult(resultSet);
        // the next handler
        source.setResultSetHandler(new GtidModeResultHandler(source));
        // then post a new command to decide gtid use todo solve the proble
        source.setSelecting(true);
        source.getCtx().writeAndFlush(new CommandRequest(Command.QUERY, BufferUtils.wrapString("show global variables like 'gtid_mode'")));

//        source.write(new CommandPacket("show global variables like 'gtid_mode'"));
    }
}
