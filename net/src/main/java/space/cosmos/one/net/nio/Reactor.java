package space.cosmos.one.net.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Reactor implements Runnable {
    final Selector selector;
    final ServerSocketChannel serverSocket;

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                selector.select();//阻塞知道有事件就绪
                Set<SelectionKey> selected = selector.selectedKeys();
                Iterator<SelectionKey> it = selected.iterator();
                while (it.hasNext()) {
                    SelectionKey skTmp = it.next();
                    dispatch(skTmp);//分发
                }
                selected.clear();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void dispatch(SelectionKey k) {
        Runnable r = (Runnable) k.attachment();// 获取key关联的处理器
        if (r != null)
            r.run();
    }

    public Reactor(int port) throws IOException {
        selector = Selector.open();
        serverSocket = ServerSocketChannel.open();
        serverSocket.socket().bind(new InetSocketAddress(port));
        serverSocket.configureBlocking(false);//非阻塞
        //注册并关注一个IO事件
        SelectionKey sk = serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        sk.attach(new Acceptor());
    }

    public static void main(String[] args) {
        try {
            Thread t = new Thread(new Reactor(10241));
            t.setName("reactor");
            t.start();
            t.join();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    class Acceptor implements Runnable {

        @Override
        public void run() {
            try {
                SocketChannel sc = serverSocket.accept();
                if (sc != null) {
                    sc.write(ByteBuffer.wrap("reactor design".getBytes()));
                    System.out.println("acceptor and handler -" + sc.socket().getLocalSocketAddress());
                    //todo
                    new BasicHandler(selector, sc); // 单线程处理连接
//                    new MultithreadHandler(selector,sc);//多线程处理
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
