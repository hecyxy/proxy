package space.cosmos.one.mysql.concurr;

import org.testng.annotations.Test;

import java.util.concurrent.CountDownLatch;

public class TestConcu {
    public static void main(String[] args){
        new TestConcu().countdown();
    }

    @Test
    void countdown(){
        CountDownLatch latch = new CountDownLatch(1);
        System.out.println(latch.getCount());
        latch.countDown();
        System.out.println(latch.getCount());
    }
}
