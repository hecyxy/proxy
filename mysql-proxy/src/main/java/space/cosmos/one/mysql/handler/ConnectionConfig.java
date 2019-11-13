package space.cosmos.one.mysql.handler;

import io.netty.buffer.ByteBuf;
import org.jctools.queues.MpscChunkedArrayQueue;
import space.cosmos.one.mysql.codec.Parser;
import space.cosmos.one.mysql.gprc.remoting.Recorder;

import java.net.InetSocketAddress;

public class ConnectionConfig {
    private Long id;
    private InetSocketAddress frontend;
    private InetSocketAddress backend;
    private Recorder recorder;
    private Parser parser;
    private MpscChunkedArrayQueue<ByteBuf> mpsc;

    public void setMpsc(MpscChunkedArrayQueue<ByteBuf> mpsc) {
        this.mpsc = mpsc;
    }

    public MpscChunkedArrayQueue<ByteBuf> getMpsc() {
        return mpsc;
    }

    public ConnectionConfig(long id, InetSocketAddress front, InetSocketAddress backend, MpscChunkedArrayQueue queue) {
        this.id = id;
        this.frontend = front;
        this.backend = backend;
        this.mpsc = queue;
    }

    public Parser getParser() {
        return parser;
    }

    public Long getId() {
        return id;
    }

    public InetSocketAddress getFrontend() {
        return frontend;
    }

    public InetSocketAddress getBackend() {
        return backend;
    }

    public Recorder getRecorder() {
        return recorder;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFrontend(InetSocketAddress frontend) {
        this.frontend = frontend;
    }

    public void setBackend(InetSocketAddress backend) {
        this.backend = backend;
    }

    public void setRecorder(Recorder recorder) {
        this.recorder = recorder;
    }
}
