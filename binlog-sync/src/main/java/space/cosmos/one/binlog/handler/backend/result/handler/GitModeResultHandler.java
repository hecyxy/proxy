package space.cosmos.one.binlog.handler.backend.result.handler;

import space.cosmos.one.binlog.handler.factory.BackendConnection;

public class GitModeResultHandler extends ResultSetHandler {
    public GitModeResultHandler(BackendConnection source) {
        super(source);
    }

    @Override
    public void handleResultSet(ResultSet resultSet) {
        if (!resultSet.getRows().isEmpty()) {
            String[] row = resultSet.getRows().get(0);
            if ("ON".equals(row[0])) {
                source.getBinlogContext().useGtidSet();
            }
        }
        source.setResultSetHandler(new ChecksumResultHandler(source));
        //todo
    }
}
