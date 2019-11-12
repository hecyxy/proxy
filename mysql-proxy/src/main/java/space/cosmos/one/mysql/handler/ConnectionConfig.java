package space.cosmos.one.mysql.handler;

import space.cosmos.one.mysql.codec.Parser;
import space.cosmos.one.mysql.gprc.remoting.Recorder;

import java.net.InetSocketAddress;

public class ConnectionConfig {
    private Long id;
    private InetSocketAddress frontend;
    private InetSocketAddress backend;
    private Recorder recorder;
    private Parser parser;

    public ConnectionConfig(long id, InetSocketAddress front, InetSocketAddress backend, Parser decoder) {
        this.id = id;
        this.frontend = front;
        this.backend = backend;
        this.parser = decoder;
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
