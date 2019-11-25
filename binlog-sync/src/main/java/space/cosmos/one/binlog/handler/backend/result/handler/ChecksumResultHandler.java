package space.cosmos.one.binlog.handler.backend.result.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.cosmos.one.binlog.handler.factory.BackendConnection;

public class ChecksumResultHandler extends ResultSetHandler {

    private static final Logger logger = LoggerFactory.getLogger(ChecksumResultHandler.class);

    public ChecksumResultHandler(BackendConnection source) {
        super(source);
    }


    @Override
    public void handleResultSet(ResultSet resultSet) {

    }


}
