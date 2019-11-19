package space.cosmos.one.binlog.plugin;

public interface AuthenticationPlugin {
    String getPluginName();

    byte[] process(byte[] password, byte[] seedAsBytes);
}
