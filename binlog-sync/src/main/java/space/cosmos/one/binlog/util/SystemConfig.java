package space.cosmos.one.binlog.util;

public class SystemConfig {
    public static final int backendInitialSize = 10;
    public static final int backendMaxSize = 20;
    public static final int backendInitialWaitTime = 60;
    public static final String mysqlHost = "127.0.0.1";//"172.16.52.81";//
    public static final int sqlPort = 3306;//8806;
    public static final String userName = "root";//"dev";
    public static final String pwd = "123456";//"haolie123";
    public static final long serverId = 1; //1

    public static final String dataBase = "test";
    public static final int idleCheckInterval = 5000;
    public static final int backendConnectRetryTimes = 3;

    public static String defaultCharset = "utf8";
    public static int DEFAULT_TX_ISOLATION = Isolations.REPEATED_READ;
}
