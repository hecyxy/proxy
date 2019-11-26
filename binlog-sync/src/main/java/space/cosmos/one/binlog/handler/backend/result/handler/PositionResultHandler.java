package space.cosmos.one.binlog.handler.backend.result.handler;

import space.cosmos.one.binlog.handler.factory.BackendConnection;

public class PositionResultHandler extends ResultSetHandler{

    public PositionResultHandler(BackendConnection connection){
        super(connection);
    }

    public void doHandleResultSet(ResultSet resultSet) {
        source.getBinlogContext().handlePositionResult(resultSet);
        // the next handler
        source.setResultSetHandler(new GitModeResultHandler(source));
        // then post a new command to decide gtid use todo solve the proble
//        source.write(new CommandPacket("show global variables like 'gtid_mode'"));
    }
}
