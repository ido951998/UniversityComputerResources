package bgu.spl.tests;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import bgu.spl.mics.Future;

import java.util.concurrent.TimeUnit;

public class FutureTest {
    public static Future<Integer> future;

    @Before
    public void setUp() {
        future = new Future<>();
    }

    @Test
    public void testGetDefault() {
        future.resolve(0);
        assertEquals(future.get(), new Integer(0));
    }

    @Test
    public void testIsDone() {
        assertFalse(future.isDone());
        future.resolve(0);
        assertTrue(future.isDone());
    }

    @Test
    public void testResolve() {
        assertNull(future.get(0, TimeUnit.SECONDS));
        future.resolve(0);
        assertEquals(future.get(), new Integer(0));
        future.resolve(1);
        assertEquals(future.get(), new Integer(0));
    }

    @Test
    public void testGetTimeout() {
        assertNull(future.get(1000, TimeUnit.MILLISECONDS));
        future.resolve(0);
        assertEquals(future.get(100, TimeUnit.DAYS), new Integer(0));
    }
}
