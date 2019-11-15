package space.cosmos.one.mysql.util;

import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.cosmos.one.mysql.codec.CommandParser;
import space.cosmos.one.mysql.codec.HandshakeParser;
import space.cosmos.one.mysql.codec.message.Command;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

import static space.cosmos.one.mysql.util.CmdInfo.State.SERVER_RESPONSE;

public class CmdInfo implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(CmdInfo.class);

    private AtomicBoolean started = new AtomicBoolean(false);

    public boolean getStarted() {
        return started.get();
    }

    public void setStarted(boolean flag) {
        started.set(flag);
    }

    enum State {
        CONNECTION,
        CLIENT_SENDAUTH,
        SERVER_AUTH,
        COMMAND,
        RESPONSE,
        FIELD,
        FIELD_EOF,
        ROW,
        SERVER_RESPONSE
    }

    private State state = State.CONNECTION;
    //每个请求用唯一ID标识
    private long id;
    //用户请求
    private ByteBuf request;
    //给用户的回复
    private ByteBuf response;

    private LinkedList<ByteBuf> producerQueue = new LinkedList<>();

    private String remoteAddress;

    public LinkedList<ByteBuf> getProducerQueue() {
        return producerQueue;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ByteBuf getRequest() {
        return request;
    }

    public void setRequest(ByteBuf request) {
        this.request = request;
    }

    public ByteBuf getResponse() {
        return response;
    }

    public void setResponse(ByteBuf response) {
        this.response = response;
    }

    public void parseRequest(ByteBuf buf) {
        switch (state) {
            case CONNECTION:
                HandshakeParser handshakeParser = new HandshakeParser(buf);
                logger.info("parser request: {}", handshakeParser.getBody().toString());
                break;
            case RESPONSE:
                break;
            case FIELD:
                break;
            case FIELD_EOF:
                break;
            case ROW:
                break;
            default:
                break;
        }
    }

    public void parseResponse(ByteBuf buf) {
    }


    public void parse(ByteBuf buf) {
        switch (state) {
            case CONNECTION:
                HandshakeParser handshakeParser = new HandshakeParser(buf);
                if (!handshakeParser.decode()) {
                    return;
                }
                logger.info("parse result {}", handshakeParser.getBody().toString());
                state = State.CLIENT_SENDAUTH;
                break;
            case CLIENT_SENDAUTH:
                logger.info("client send auth");
                buf.clear();
                state = State.SERVER_AUTH;
                break;
            case SERVER_AUTH:
                buf.clear();
                state = State.COMMAND;
                break;
            case COMMAND:
                CommandParser cp = new CommandParser(buf);
                if (!cp.decode()) {
                    return;
                }
                logger.info("use execute sql {}",cp.getBody());
                state = SERVER_RESPONSE;
                break;
            case SERVER_RESPONSE:
                logger.info("ignore the server response");
                state = State.COMMAND;
                break;
            default:
                break;
        }
    }

    @Override
    public void run() {
        System.out.println("before buf size " + producerQueue.size());
        ByteBuf buf = producerQueue.poll();
        while (buf != null) {
            try {
                System.out.println("buf size " + producerQueue.size());
                parse(buf);
            } catch (Exception e) {
                logger.error("exception ", e);
            } finally {
                buf = producerQueue.poll();
            }
        }
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            logger.error("thread sleep error", e);
        }

    }

    public void close() {
        //todo 清理部分资源
    }
}
