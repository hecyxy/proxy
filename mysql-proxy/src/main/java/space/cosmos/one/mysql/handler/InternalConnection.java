package space.cosmos.one.mysql.handler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.cosmos.one.mysql.util.OsUtil;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class InternalConnection {
    private static final Logger logger = LoggerFactory.getLogger(InternalConnection.class);
    private ServerBootstrap serverBootstrap;
    private final EventLoopGroup eventLoopGroupBoss;
    private final EventLoopGroup eventLoopGroupSelector;
    private AtomicBoolean started = new AtomicBoolean(false);
    private ChannelFuture channelFuture;
    private ConnectionConfig config;

    InternalConnection(ConnectionConfig config) {
        if (useEpoll()) {
            this.eventLoopGroupBoss = new EpollEventLoopGroup(1, new ThreadFactory() {
                private AtomicInteger threadIndex = new AtomicInteger(0);

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, String.format("boostap_epoll_%d", this.threadIndex.incrementAndGet()));
                }
            });

            this.eventLoopGroupSelector = new EpollEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2 - 1, new ThreadFactory() {
                private AtomicInteger threadIndex = new AtomicInteger(0);
                private int threadTotal = Runtime.getRuntime().availableProcessors() * 2 - 1;

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, String.format("epoll_selector_%d_%d", threadTotal, this.threadIndex.incrementAndGet()));
                }
            });
        } else {
            this.eventLoopGroupBoss = new NioEventLoopGroup(1, new ThreadFactory() {
                private AtomicInteger threadIndex = new AtomicInteger(0);

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, String.format("boss_group_%d", this.threadIndex.incrementAndGet()));
                }
            });

            this.eventLoopGroupSelector = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2 - 1, new ThreadFactory() {
                private AtomicInteger threadIndex = new AtomicInteger(0);
                private int threadTotal = Runtime.getRuntime().availableProcessors() * 2 - 1;

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, String.format("worker_group_%d_%d", threadTotal, this.threadIndex.incrementAndGet()));
                }
            });
        }

        serverBootstrap = new ServerBootstrap();
        this.config = config;
    }

    private boolean useEpoll() {
        return OsUtil.isLinuxPlatform()
                && Epoll.isAvailable();
    }

    public void start() {
        synchronized (this) {
            if (!started.get()) {
                serverBootstrap.group(eventLoopGroupBoss, eventLoopGroupSelector)
                        .channel(useEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                        .option(ChannelOption.SO_REUSEADDR, true)
                        .option(ChannelOption.SO_BACKLOG, 1024 * 3)
                        .handler(new LoggingHandler(LogLevel.DEBUG))
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            public void initChannel(SocketChannel ch) {
//                                ch.pipeline().addLast("logging", new LoggingHandler());
                                ch.pipeline().addLast(new MysqlDecoder());
                                ch.pipeline().addLast("front handler",new FrontedHandler(config));
                            }
                        });

                try {
                    channelFuture = serverBootstrap.bind(config.getFrontend().getPort()).sync();
                    logger.info(String.format("%s success bind on port: %s for backend", config.getFrontend().getPort(),config.getBackend().getHostName()));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                started.set(true);
            }
        }
    }

    public boolean isStart() {
        return started.get();
    }

    public void shutdown() {
        synchronized (this) {
            channelFuture.channel().close();
        }
    }
}
