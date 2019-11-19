package space.cosmos.one.binlog.util;

import space.cosmos.one.binlog.plugin.AuthenticationPlugin;
import space.cosmos.one.binlog.plugin.MysqlNativePasswordPlugin;

import java.util.HashMap;
import java.util.Map;

public class AuthUtil {
    private static Map<String, AuthenticationPlugin> pluginsMap = new HashMap<>();

    static {
        AuthenticationPlugin nativePasswordPlugin = new MysqlNativePasswordPlugin();
        pluginsMap.put(nativePasswordPlugin.getPluginName(), nativePasswordPlugin);
    }

    public static AuthenticationPlugin getPlugin(String name) {
        return pluginsMap.get(name);
    }
}
