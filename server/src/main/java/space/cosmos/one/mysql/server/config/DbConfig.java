package space.cosmos.one.mysql.server.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;


public class DbConfig {
    static Config config;

    /**
     * config content is as this
     * mysql {
     *     server1: "",
     *     server2: ""
     * }
     */
    static {
        config = ConfigFactory.load("db.conf");
    }


    public static Config getConfig() {
        return config;
    }
}
