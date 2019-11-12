package space.cosmos.one.mysql.exception;

public class NpeException extends RuntimeException {
    private static final long serialVersionUID = -3752985849571697432L;

    public NpeException() {
        super();
    }

    public NpeException(String msg) {
        super(msg);
    }

    public NpeException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
