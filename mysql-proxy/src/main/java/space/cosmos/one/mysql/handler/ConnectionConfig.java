package space.cosmos.one.mysql.handler;

import io.netty.channel.Channel;
import org.jctools.queues.MpscChunkedArrayQueue;
import space.cosmos.one.mysql.gprc.remoting.Recorder;
import space.cosmos.one.mysql.util.WrapStream;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionConfig {
    private Long id;
    private InetSocketAddress frontend;

    public ArrayList<ConcurrentHashMap<Channel, WrapStream>> getList() {
        return list;
    }

    private InetSocketAddress backend;
    private Recorder recorder;
    private MpscChunkedArrayQueue<WrapStream> mpsc;

    private WrapStream cmdInfo;

    private ArrayList<ConcurrentHashMap<Channel, WrapStream>> list;

    public void setMpsc(MpscChunkedArrayQueue<WrapStream> mpsc) {
        this.mpsc = mpsc;
    }

    public MpscChunkedArrayQueue<WrapStream> getMpsc() {
        return mpsc;
    }

    public ConcurrentHashMap<Channel, WrapStream> getMap() {
        return map;
    }

    public void setMap(ConcurrentHashMap<Channel, WrapStream> map) {
        this.map = map;
    }

    private ConcurrentHashMap<Channel, WrapStream> map;
    public ConnectionConfig(long id, InetSocketAddress front, InetSocketAddress backend, ArrayList<ConcurrentHashMap<Channel, WrapStream>> list) {
        this.id = id;
        this.frontend = front;
        this.backend = backend;
        this.list = list;
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
