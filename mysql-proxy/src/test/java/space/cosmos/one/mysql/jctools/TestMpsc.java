package space.cosmos.one.mysql.jctools;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import org.jctools.queues.MpscArrayQueue;
import org.jctools.queues.MpscChunkedArrayQueue;

import java.nio.charset.Charset;

public class TestMpsc {
    public static void main(String[] args) {
        MpscArrayQueue<ByteBuf> queue = new MpscArrayQueue<>(1024 * 5);
        for (int i = 0; i < 10; i++) {
            queue.offer(ByteBufAllocator.DEFAULT.buffer().writeBytes((i + "aaa").getBytes()));
        }
        System.out.println(queue.size());
        Thread t = new Thread(() -> {
            ByteBuf buf = queue.poll();
            while (buf != null) {
                byte[] s = new byte[buf.readableBytes()];
                buf.readBytes(s);
                System.out.println("read...");
                System.out.println(new String(s,Charset.forName("utf-8")));
                buf = queue.poll();
                try {
                    Thread.sleep(3*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();

        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
