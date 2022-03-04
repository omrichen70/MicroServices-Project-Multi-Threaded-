package bgu.spl.mics;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class FutureTest {

    private Future<Integer> f;


    @Before
    public void setUp() throws Exception {
        f = new Future<Integer>();
    }

    @After
    public void tearDown() throws Exception {
        f= null;
    }

    @Test
    public void get() {
        Integer num = 49;
        f.resolve(num);
        Integer ans = f.get();
        assertEquals(ans, num);
    }

    @Test
    public void resolve() {
        Integer num = 49;
        f.resolve(num);
        Integer ans = f.get();
        assertEquals(ans, num);
        assertTrue(f.isDone());

    }

    @Test
    public void isDone() {
        assertFalse(f.isDone());
        f.resolve(49);
        assertTrue(f.isDone());
    }
}