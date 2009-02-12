package unitTests.utils;

import junit.framework.Assert;

import org.junit.Test;
import org.objectweb.proactive.core.util.Sleeper;

import unitTests.UnitTests;


public class TestSleeper extends UnitTests {
    static final long SLEEP_TIME = 1000;

    @Test
    public void test() {
        Sleeper sleeper = new Sleeper(SLEEP_TIME);

        T t = new T(Thread.currentThread());
        Thread thread = new Thread(t);
        thread.setDaemon(true);
        thread.start();

        long before = System.currentTimeMillis();
        sleeper.sleep();
        long after = System.currentTimeMillis();

        Assert.assertTrue(after - before >= SLEEP_TIME);
    }

    private class T implements Runnable {
        private Thread sleeper;

        public T(Thread sleeper) {
            this.sleeper = sleeper;
        }

        public void run() {
            while (true) {
                this.sleeper.interrupt();
            }
        }

    }
}