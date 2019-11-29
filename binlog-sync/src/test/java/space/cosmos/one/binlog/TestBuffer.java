package space.cosmos.one.binlog;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
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

//    @Test
    public void test() {
        int a = 3;
        sys(3);
        System.out.println(a);
        String b = "hcy";
        sys(b);
        System.out.println(b);
        Assert.assertTrue(b == "ff");
    }

    void sys(int b) {
        b = 5;
    }

    void sys(String str) {
        str = "aaa";
    }

    @Benchmark
    @BenchmarkMode(Mode.All)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public String concat() {
        String a = "a";
        String b = "b";
        String s = a + b;
        return s;
    }

//    @Test
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include(TestBuffer.class.getSimpleName())
                .measurementIterations(5).build();
        new Runner(opt).run();
    }

}
