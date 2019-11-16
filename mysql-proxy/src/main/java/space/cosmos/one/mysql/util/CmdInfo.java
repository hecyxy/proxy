package space.cosmos.one.mysql.util;

import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.cosmos.one.mysql.codec.CommandParser;
import space.cosmos.one.mysql.codec.ErrorParser;
import space.cosmos.one.mysql.codec.HandshakeParser;
import space.cosmos.one.mysql.codec.OkParser;
import space.cosmos.one.mysql.codec.message.Command;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static space.cosmos.one.mysql.codec.message.PacketHeader.*;
import static space.cosmos.one.mysql.util.CmdInfo.State.*;

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

    private LinkedList<Pair<ByteStream, ByteBuf>> producerQueue = new LinkedList<>();

    private String remoteAddress;

    public LinkedList<Pair<ByteStream, ByteBuf>> getProducerQueue() {
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


    public void parse(Pair<ByteStream, ByteBuf> pair) {
        ByteStream stream = pair.getObject1();
        ByteBuf buf = pair.getObject2();
        if (stream == ByteStream.REQUEST) {
            if (state != CONNECTION) {
                state = COMMAND;
            }
            switch (state) {
                case CONNECTION:
                    HandshakeParser handshakeParser = new HandshakeParser(buf);
                    if (!handshakeParser.decode()) {
                        return;
                    }
                    logger.info("parse result {}", handshakeParser.getBody().toString());
                    state = State.CLIENT_SENDAUTH;
                    break;
                case COMMAND:
                    CommandParser cp = new CommandParser(buf);
                    if (!cp.decode()) {
                        return;
                    }
                    logger.info("use execute sql {}", cp.getBody());
                    state = SERVER_RESPONSE;
                    break;
                default:
                    logger.info("request default...");
            }
        } else {
            if (state == COMMAND) {
                state = SERVER_RESPONSE;
            }
            switch (state) {
                case CONNECTION:
                    HandshakeParser handshakeParser = new HandshakeParser(buf);
                    if (!handshakeParser.decode()) {
                        return;
                    }
                    logger.info("parse result {}", handshakeParser.getBody().toString());
                    state = State.CLIENT_SENDAUTH;
                    break;
                case SERVER_AUTH:
                    logger.info("auth response {}", buf.readableBytes());
                    state = State.COMMAND;
                    break;
                case SERVER_RESPONSE:
//                    decodeResponse(buf);
                    logger.info("response ignore...");
                    state = State.COMMAND;
                    break;
                default:
                    logger.info("default....");
            }
        }
//        switch (state) {
//            case CONNECTION:
//                HandshakeParser handshakeParser = new HandshakeParser(buf);
//                if (!handshakeParser.decode()) {
//                    return;
//                }
//                logger.info("parse result {}", handshakeParser.getBody().toString());
//                state = State.CLIENT_SENDAUTH;
//                break;
//            case CLIENT_SENDAUTH:
//                logger.info("client send auth size {}", buf.readableBytes());
//                buf.clear();
//                state = State.SERVER_AUTH;
//                break;
//            case SERVER_AUTH:
//                logger.info("auth response {}", buf.readableBytes());
//                state = State.COMMAND;
//                break;
//            case COMMAND:
//                CommandParser cp = new CommandParser(buf);
//                if (!cp.decode()) {
//                    return;
//                }
//                logger.info("use execute sql {}", cp.getBody());
//                state = SERVER_RESPONSE;
//                break;
//            case SERVER_RESPONSE:
//                decodeResponse(buf);
//                state = State.COMMAND;
//                break;
//            default:
//                break;
//        }
    }

    private void decodeResponse(ByteBuf in) {
        short packetType = in.getUnsignedByte(in.readerIndex() + 4);
        switch (packetType) {
            case PACKET_OK:
                OkParser ok = new OkParser(in);
                if (ok.decode()) {
                    logger.info("ok msg {}", ok.getBody());
                }
                break;
            case PACKET_ERR:
                ErrorParser err = new ErrorParser(in);
                if (err.decode()) {
                    logger.info("ok msg {}", err.getBody());
                }
                break;
            default:
                decodeResultSet(in);
        }
    }

    private boolean decodeResultSet(ByteBuf in) {
        int packetLen = in.readUnsignedMediumLE();
        int packetNo = in.readUnsignedByte();

        int expectedFieldPackets = (int) BufferUtils.readEncodedLenInt(in);
        int remainingFieldPackets = expectedFieldPackets;
        state = State.FIELD;
        logger.info("result set {} {}", expectedFieldPackets, remainingFieldPackets);
        //out.add(new ResultSetResponse(packetLen, packetNo, this.expectedFieldPackets));
        return true;
    }

    @Override
    public void run() {
        while (true) {
            System.out.println("before buf size " + producerQueue.size());
            Pair<ByteStream, ByteBuf> buf = producerQueue.poll();
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
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.error("thread sleep error", e);
            }
        }

    }

    public void close() {
        //todo 清理部分资源
    }
}
