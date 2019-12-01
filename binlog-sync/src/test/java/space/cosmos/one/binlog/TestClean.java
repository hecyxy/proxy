package space.cosmos.one.binlog;

import org.testng.annotations.Test;
import sun.nio.ch.DirectBuffer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class TestClean {


    /**
     * 先执行cleaner 再执行clean即可回收
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
}
