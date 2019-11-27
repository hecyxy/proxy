package space.cosmos.one.binlog.handler.backend;

public interface BackendConnState {
    //未认证成功
    int BACKEND_NOT_AUTHED = 0;
    //连接成功
    int BACKEND_AUTHED = 1;

    //
    int RESULT_SET_FIELD_COUNT = 2;

    int RESULT_SET_FIELDS = 3;

    int RESULT_SET_EOF = 4;

    int RESULT_SET_ROW = 5;

    int RESULT_SET_LAST_EOF = 6;
}
