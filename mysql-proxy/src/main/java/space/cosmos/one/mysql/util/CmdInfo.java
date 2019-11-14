package space.cosmos.one.mysql.util;

import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.cosmos.one.mysql.codec.HandshakeParser;

import java.util.LinkedList;

public class CmdInfo {
    private static final Logger logger = LoggerFactory.getLogger(CmdInfo.class);

    enum State {
        CONNECTION,
        RESPONSE,
        FIELD,
        FIELD_EOF,
        ROW
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
                logger.info("parser request: {}", handshakeParser.decode().toString());
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

    public void parseResponse(ByteBuf buf){}


    public void parse(){
        if(state == State.CONNECTION){
            HandshakeParser parser = new HandshakeParser(producerQueue.poll());
            parser.decode();
        }else{
            parseRequest(producerQueue.poll());
        }
    }
}
