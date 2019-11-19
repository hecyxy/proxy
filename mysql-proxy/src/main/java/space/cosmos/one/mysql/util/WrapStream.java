package space.cosmos.one.mysql.util;

import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.cosmos.one.common.packet.*;
import space.cosmos.one.common.packet.message.EofMessage;
import space.cosmos.one.common.util.BufferUtils;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

import static space.cosmos.one.common.packet.message.PacketHeader.PACKET_ERR;
import static space.cosmos.one.common.packet.message.PacketHeader.PACKET_OK;
import static space.cosmos.one.mysql.util.WrapStream.State.*;

public class WrapStream implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(WrapStream.class);

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

    private int expectedFieldPackets = 0, remainingFieldPackets = 0;

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
                    logger.info("response ignore..., buf size {}", buf.readableBytes());
//                    state = State.COMMAND;
                    if (!decodeResponse(buf)) {
                        return;
                    }
                    break;
                case FIELD:
                    FieldParser packet = new FieldParser(buf, 0);
                    packet.decode();
                    logger.info("field info {}", packet.getBody());

                    this.remainingFieldPackets--;
                    if (this.remainingFieldPackets == 0) {
                        this.state = State.FIELD_EOF;
                    }
                    return;
                case FIELD_EOF:
                    EofParser fieldPacket = new EofParser(buf, EofMessage.Type.FIELD);
                    if (!fieldPacket.decode()) {
                        return;
                    }
                    logger.info("field eof {}", fieldPacket.getBody());
                    this.state = State.ROW;
                    return;
                case ROW:
                    if (BufferUtils.isEOFPacket(buf)) {
                        EofParser rowPacket = new EofParser(buf, EofMessage.Type.ROW);
                        if (!rowPacket.decode()) {
                            return;
                        }
                        this.state = State.RESPONSE;
                        this.expectedFieldPackets = 0;
                        return;
                    }
                    RowParser rowParser = new RowParser(buf, this.expectedFieldPackets);
                    if (!rowParser.decode()) {
                        return;
                    }
                    logger.info("row info {}", rowParser.getBody());
                    return;
                default:
                    logger.info("default....");
            }
        }
    }

    private boolean decodeResponse(ByteBuf in) {
        int start = in.readerIndex() + 4;
        short packetType = in.getUnsignedByte(start);
        logger.info("start: {},packet type: {}", start, packetType);
        switch (packetType) {
            case PACKET_OK:
                OkParser ok = new OkParser(in);
                if (ok.decode()) {
                    logger.info("ok msg {}", ok.getBody());
                    return true;
                }
                break;
            case PACKET_ERR:
                ErrorParser err = new ErrorParser(in);
                if (err.decode()) {
                    logger.info("ok msg {}", err.getBody());
                    return true;
                }
                break;
            default:
                return decodeResultSet(in);
        }
        return false;
    }

    private boolean decodeResultSet(ByteBuf in) {
        int packetLen = in.readUnsignedMediumLE();
        int packetNo = in.readUnsignedByte();

        this.expectedFieldPackets = (int) BufferUtils.readEncodedLenInt(in);
        this.remainingFieldPackets = this.expectedFieldPackets;
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
                    System.out.println("queue size " + producerQueue.size());
                    parse(buf);
                    if (ReferenceCountUtil.refCnt(buf) > 0) {
                        ReferenceCountUtil.release(buf);
                        logger.info("release buf success...");
                    }
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
}
