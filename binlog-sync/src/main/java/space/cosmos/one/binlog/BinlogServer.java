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
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
//                        ch.pipeline().addLast()
                    }
                });
    }
}
