package space.cosmos.one.mysql.codec.message;

public class CsGreeting {
    private final String pluginName;
    private final byte[] seed;
    private int serverCapabilities;

    public CsGreeting(int serverCapabilities, String pluginName, byte[] seed) {
        this.serverCapabilities = serverCapabilities;
        this.pluginName = pluginName;
        this.seed = seed;
    }

    public String getPluginName() {
        return pluginName;
    }

    public byte[] getSeed() {
        return seed;
    }

    public int getServerCapabilities() {
        return serverCapabilities;
    }

    @Override
    public String toString() {
        return String.format("capb: %s,plugin name: %s", serverCapabilities, pluginName);
    }
}
