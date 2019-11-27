package space.cosmos.one.binlog;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.cosmos.one.binlog.handler.backend.netty.ClientAuthHandler;
import space.cosmos.one.binlog.handler.backend.netty.MysqlPacketDecoder;
import space.cosmos.one.binlog.handler.backend.netty.MysqlPacketEncoder;
import space.cosmos.one.binlog.handler.factory.BackendConnection;
import space.cosmos.one.binlog.util.SystemConfig;

public class BinlogServer {
    private static final Logger logger = LoggerFactory.getLogger(BinlogServer.class);


    public static void main(String[] args) {
        BinlogServer server = new BinlogServer();
        server.start();

    }

    private void start() {
        Bootstrap strap = new Bootstrap();
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        strap.group(bossGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, false)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast("logging", new LoggingHandler());
                        ch.pipeline().addLast(new MysqlPacketDecoder());
                        ch.pipeline().addLast(new MysqlPacketEncoder());
                        ch.pipeline().addLast(new ClientAuthHandler(new BackendConnection()));
                    }
                });
        try {
            strap.connect(SystemConfig.mysqlHost, SystemConfig.sqlPort).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
