package space.cosmos.one.binlog;

import org.testng.annotations.Test;
import sun.nio.ch.DirectBuffer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class TestClean {

    @Test
    public void get0(){
        ByteBuffer b = ByteBuffer.allocateDirect(10);
        b.putInt(10);
        System.out.println(b.getInt());
    }
    /**
     * 先执行cleaner 再执行clean即可回收
     *
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @Test
    public void clean() throws InvocationTargetException, IllegalAccessException {
        ByteBuffer b = ByteBuffer.allocateDirect(20);
        b.putInt(100);
        invoke(b, "cleaner");
//        invoke(b);
//        ((DirectBuffer) b).cleaner().clean();
        System.gc();
        b.position(0);
        System.out.println(b.getInt());
    }

    public void invoke(final Object object) throws InvocationTargetException, IllegalAccessException {
        Method[] s = object.getClass().getMethods();
        for (Method m : s) {
            if (m.getName().equals("cleaner")) {
                System.out.println("find name cleaner");
                m.setAccessible(true);
                m.invoke(object);
            }
        }
    }

    public static Object invoke(final Object target, final String methodName, final Class<?>... args) {
        return AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
            Method method = null;
            try {
                method = method(target, methodName, args);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            method.setAccessible(true);
            try {
                return method.invoke(target);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        });
    }

    private static Method method(Object target, String methodName, Class<?>[] args)
            throws NoSuchMethodException {
        try {
            return target.getClass().getMethod(methodName, args);
        } catch (NoSuchMethodException e) {
            return target.getClass().getDeclaredMethod(methodName, args);
        }
    }

    @Test
    public Void getStr() {
        return null;
    }



    public static void clean(final ByteBuffer buffer) {
        if (buffer == null || !buffer.isDirect() || buffer.capacity() == 0)
            return;
        invoke(invoke(viewed(buffer), "cleaner"), "clean");
    }

    private static Object invoke1(final Object target, final String methodName, final Class<?>... args) {
        return AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
            try {
                Method method = method1(target, methodName, args);
                method.setAccessible(true);
                return method.invoke(target);
            } catch (Exception e) {
//                    log.error(String.format("invoke method excetpion{%s}", e.getMessage()));
                throw new IllegalStateException(e);
            }
        });
    }

    private static Method method1(Object target, String methodName, Class<?>[] args) throws NoSuchMethodException {
        try {
            return target.getClass().getMethod(methodName, args);
        } catch (NoSuchMethodException e) {
            return target.getClass().getDeclaredMethod(methodName, args);
        }
    }

    private static ByteBuffer viewed(ByteBuffer buffer) {
        String methodName = "viewedBuffer";

        // JDK7中将DirectByteBuffer类中的viewedBuffer方法换成了attachment方法
        Method[] methods = buffer.getClass().getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equals("attachment")) {
                methodName = "attachment";
                break;
            }
        }

        ByteBuffer viewedBuffer = (ByteBuffer) invoke1(buffer, methodName);
        if (viewedBuffer == null)
            return buffer;
        else
            return viewed(viewedBuffer);
    }
}
