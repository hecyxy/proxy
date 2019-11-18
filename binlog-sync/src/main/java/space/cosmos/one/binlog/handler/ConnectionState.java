package space.cosmos.one.binlog.handler;

public enum ConnectionState {
    NOT_AUTH,
    AUTH_SUC,

    RS_FIELD_COUNT,
    RS_FIELD,
    RS_EOF,
    RS_ROW
}
