package space.cosmos.one.binlog.handler.backend.result.handler;

import space.cosmos.one.binlog.handler.factory.BackendConnection;
import space.cosmos.one.common.packet.message.ErrorMessage;
import space.cosmos.one.common.packet.message.OkMessage;

import java.sql.ResultSet;

public abstract class ResultSetHandler {

    protected BackendConnection source;

    public ResultSetHandler(BackendConnection source) {
        this.source = source;
    }



}
