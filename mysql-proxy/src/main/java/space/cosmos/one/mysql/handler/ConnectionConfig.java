package space.cosmos.one.mysql.handler;

import io.netty.channel.Channel;
import org.jctools.queues.MpscChunkedArrayQueue;
import space.cosmos.one.mysql.gprc.remoting.Recorder;
import space.cosmos.one.mysql.util.CmdInfo;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionConfig {
    private Long id;
    private InetSocketAddress frontend;

    public ArrayList<ConcurrentHashMap<Channel, CmdInfo>> getList() {
        return list;
    }

    private InetSocketAddress backend;
    private Recorder recorder;
    private MpscChunkedArrayQueue<CmdInfo> mpsc;

    private CmdInfo cmdInfo;

    private ArrayList<ConcurrentHashMap<Channel, CmdInfo>> list;

    public void setMpsc(MpscChunkedArrayQueue<CmdInfo> mpsc) {
        this.mpsc = mpsc;
    }

    public MpscChunkedArrayQueue<CmdInfo> getMpsc() {
        return mpsc;
    }

    public ConcurrentHashMap<Channel, CmdInfo> getMap() {
        return map;
    }

    public void setMap(ConcurrentHashMap<Channel, CmdInfo> map) {
        this.map = map;
    }

    private ConcurrentHashMap<Channel,CmdInfo> map;
    public ConnectionConfig(long id, InetSocketAddress front, InetSocketAddress backend, ArrayList<ConcurrentHashMap<Channel, CmdInfo>> list) {
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
