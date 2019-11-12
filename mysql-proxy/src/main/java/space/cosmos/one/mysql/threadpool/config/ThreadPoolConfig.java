package space.cosmos.one.mysql.threadpool.config;

public class ThreadPoolConfig {
    private String prefixName;
    private int coreSize;
    private int maxSize;
    private int queues;
    int alive;

    public String getPrefixName() {
        return prefixName;
    }

    public int getCoreSize() {
        return coreSize;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public int getQueues() {
        return queues;
    }

    public int getAlive() {
        return alive;
    }

    public void setPrefixName(String prefixName) {
        this.prefixName = prefixName;
    }

    public void setCoreSize(int coreSize) {
        this.coreSize = coreSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public void setQueues(int queues) {
        this.queues = queues;
    }

    public void setAlive(int alive) {
        this.alive = alive;
    }
}
