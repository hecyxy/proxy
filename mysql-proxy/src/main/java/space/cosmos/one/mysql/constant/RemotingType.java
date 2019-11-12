package space.cosmos.one.mysql.constant;

public enum RemotingType {
    USER_TO_PROXY("用户连接到proxy"),
    PROXY_TO_SERVER("proxy连接到server");

    private String desp;

    RemotingType(String desp) {
        this.desp = desp;
    }

    public String getDesp() {
        return this.desp;
    }
}
