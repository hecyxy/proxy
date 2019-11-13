package space.cosmos.one.mysql.concurr;

import org.testng.annotations.Test;

import java.util.concurrent.CountDownLatch;

public class TestConcu {
    public static void main(String[] args){
//        new TestConcu().countdown();
        String OS_NAME = System.getProperty("os.name");
        System.out.println(OS_NAME);
    }

    @Test
    void countdown(){
        CountDownLatch latch = new CountDownLatch(1);
        System.out.println(latch.getCount());
        latch.countDown();
        System.out.println(latch.getCount());
    }
}
