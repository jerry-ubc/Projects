package cpen221.mp3;

import cpen221.mp3.fsftbuffer.FSFTBuffer;
import cpen221.mp3.fsftbuffer.BufferableInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Task1Tests {
    private static FSFTBuffer buffer;
    private static FSFTBuffer buffer1;
    private static FSFTBuffer buffer11;
    private static BufferableInteger integer1;
    private static BufferableInteger integer2;
    private static BufferableInteger integer3;
    private static BufferableInteger integer4;
    private static BufferableInteger integer5;
    private final CountDownLatch waiter = new CountDownLatch(1);

    @BeforeAll
    public static void setupTests() {
        buffer   = new FSFTBuffer();
        buffer1  = new FSFTBuffer(4, 5);
        buffer11 = new FSFTBuffer(1, 1);
        integer1 = new BufferableInteger(1);
        integer2 = new BufferableInteger(2);
        integer3 = new BufferableInteger(3);
        integer4 = new BufferableInteger(4);
        integer5 = new BufferableInteger(5);
    }

    @Test
    public void testPut() throws NoSuchElementException, InterruptedException {
        buffer11.put(integer1);
        waiter.await(2, TimeUnit.SECONDS);
        try {
            Assertions.assertEquals(integer1, buffer11.get("1"));
            fail();
        } catch (NoSuchElementException nse) {
            //passed
        }

        buffer11.put(integer2);
        Assertions.assertEquals(integer2, buffer11.get("2"));

        buffer1.put(integer1);
        buffer1.put(integer2);
        buffer1.put(integer3);
        buffer1.put(integer4);
        buffer1.put(integer5);
        Assertions.assertEquals(integer2, buffer1.get("2"));
        try {
            Assertions.assertEquals(integer1, buffer1.get("1"));
            fail();
        } catch (NoSuchElementException nse) {
            //passed
        }

        buffer.put(integer5);
        Assertions.assertEquals(integer5, buffer.get("5"));
    }

    @Test
    public void testTouch() throws NoSuchElementException, InterruptedException {
        buffer1.put(integer1);
        waiter.await(3, TimeUnit.SECONDS);
        buffer1.touch("1");
        waiter.await(3, TimeUnit.SECONDS);
        Assertions.assertEquals(integer1, buffer1.get("1"));

        buffer1.put(integer2);
        waiter.await(6, TimeUnit.SECONDS);
        buffer1.touch("2");
        try {
            Assertions.assertEquals(integer2, buffer1.get("2"));
            fail();
        } catch (NoSuchElementException nse) {
            //passed
        }

        buffer1.put(integer1);
        waiter.await(1, TimeUnit.SECONDS);
        buffer1.put(integer2);
        waiter.await(1, TimeUnit.SECONDS);
        buffer1.put(integer3);
        waiter.await(1, TimeUnit.SECONDS);
        buffer1.put(integer4);
        waiter.await(1, TimeUnit.SECONDS);
        buffer1.touch("1");
        buffer1.put(integer5);
        Assertions.assertEquals(integer1, buffer1.get("1"));
    }

    @Test
    public void testUpdate() throws NoSuchElementException, InterruptedException {
        System.out.println(integer3);
        buffer1.put(integer3);
        waiter.await(3, TimeUnit.SECONDS);
        buffer1.update(integer3);
        waiter.await(3, TimeUnit.SECONDS);
        Assertions.assertEquals(integer3, buffer1.get("3"));

        buffer1.put(integer4);
        waiter.await(6, TimeUnit.SECONDS);
        buffer1.update(integer4);
        try {
            Assertions.assertEquals(integer4, buffer1.get("4"));
            fail();
        } catch (NoSuchElementException nse) {
            //passed
        }
    }
}
