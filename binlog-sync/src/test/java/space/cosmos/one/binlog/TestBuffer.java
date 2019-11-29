package space.cosmos.one.binlog;

import org.testng.annotations.Test;

public class TestBuffer {

    private static int c;
    private int d;

    class Builder {
        public int a = 3;

        void get() {
            c = 3;
            c++;
            d++;
        }
    }

    @Test
    public void test() {
        int a = 3;
        sys(3);
        System.out.println(a);
        String b = "hcy";
        sys(b);
        System.out.println(b);
    }

    void sys(int b) {
        b = 5;
    }

    void sys(String str) {
        str = "aaa";
    }
}
