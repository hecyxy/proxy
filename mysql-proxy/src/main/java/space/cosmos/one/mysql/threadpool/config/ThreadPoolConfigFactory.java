package space.cosmos.one.mysql.threadpool.config;

public class ThreadPoolConfigFactory {
    public static ThreadPoolConfig defaultConfig(String name) {
        ThreadPoolConfig config = new ThreadPoolConfig();
        config.setPrefixName(name);
        config.setAlive(60);
        config.setCoreSize(8);
        config.setMaxSize(16);
        config.setQueues(128);
        return config;
    }
}
