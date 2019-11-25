package space.cosmos.one.binlog.handler.backend.result.handler;

import space.cosmos.one.binlog.handler.factory.BackendConnection;

public class GtidModeResultHandler extends ResultSetHandler {

    public GtidModeResultHandler(BackendConnection source) {
        super(source);
    }

    @Override
    public void handleResultSet(ResultSet resultSet) {


    }
}
