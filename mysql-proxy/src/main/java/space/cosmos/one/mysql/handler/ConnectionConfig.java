package space.cosmos.one.mysql.handler;

import io.netty.buffer.ByteBuf;
import org.jctools.queues.MpscChunkedArrayQueue;
import space.cosmos.one.mysql.codec.Parser;
import space.cosmos.one.mysql.gprc.remoting.Recorder;
import space.cosmos.one.mysql.util.CmdInfo;

import java.net.InetSocketAddress;

public class ConnectionConfig {
    private Long id;
    private InetSocketAddress frontend;
    private InetSocketAddress backend;
    private Recorder recorder;
    private MpscChunkedArrayQueue<CmdInfo> mpsc;

    private CmdInfo cmdInfo;

    public void setMpsc(MpscChunkedArrayQueue<CmdInfo> mpsc) {
        this.mpsc = mpsc;
    }

    public MpscChunkedArrayQueue<CmdInfo> getMpsc() {
        return mpsc;
    }

    public ConnectionConfig(long id, InetSocketAddress front, InetSocketAddress backend, MpscChunkedArrayQueue queue) {
        this.id = id;
        this.frontend = front;
        this.backend = backend;
        this.mpsc = queue;
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
