package space.cosmos.one.mysql.gprc;

/**
 * @Description build for grpc option
 */
public class InternalChannelOption {
    //地址
    private String address;
    //端口
    private int port = 1234;
    //是否使用明文
    private boolean usePlainText;
    //超时时间 秒
    private int timeout;
    //最大消息长度
    private int maxInboundMessageSize = 4 * 1024 * 1024;

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public boolean isUsePlainText() {
        return usePlainText;
    }

    public int getTimeout() {
        return timeout;
    }

    public int getMaxInboundMessageSize() {
        return maxInboundMessageSize;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setUsePlainText(boolean usePlainText) {
        this.usePlainText = usePlainText;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void setMaxInboundMessageSize(int maxInboundMessageSize) {
        this.maxInboundMessageSize = maxInboundMessageSize;
    }
}
