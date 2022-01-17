package cpen221.mp3;

import cpen221.mp3.fsftbuffer.FSFTBuffer;
import cpen221.mp3.wikimediator.WikiMediator;

import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Task3Tests {
    private static WikiMediator mediator;
    private static WikiMediator mediator1;
    private static WikiMediator mediator2;
    private final CountDownLatch waiter = new CountDownLatch(1);

    @BeforeAll
    public static void setupTests() {
        mediator  = new WikiMediator();
        mediator1 = new WikiMediator(4, 5);
        mediator2 = new WikiMediator(1, 1);
    }

    //how to predict output?
    @Test
    public void testSearch() {
        System.out.println(mediator.search("concurrency", 5));
        System.out.println(mediator.search("computer vision", 10));
        System.out.println(mediator.search("concurrency", 2));
    }

    //returns null everytime
    @Test
    public void testGetPage() {
        System.out.println(mediator2.getPage("Java"));
        System.out.println(mediator.getPage("underground railroad"));
    }

    @Test
    public void testZeitgeist() {
        List<String> expected = Arrays.asList("three", "two");
        mediator1.search("one", 5);
        mediator1.search("two", 5);
        mediator1.search("two", 5);
        mediator1.search("three", 5);
        mediator1.search("three", 5);
        mediator1.search("three", 5);
        Assertions.assertEquals(expected, mediator1.zeitgeist(2));
    }

    //bug
    @Test
    public void testTrending() throws InterruptedException {
        List<String> expected = Arrays.asList("three");
        mediator1.search("three", 1);
        Assertions.assertEquals(expected, mediator1.trending(2, 3));

        mediator.search("one", 5);
        mediator.search("two", 5);
        mediator.search("two", 5);
        waiter.await(3, TimeUnit.SECONDS);
        mediator.search("three", 5);
        mediator.search("three", 5);
        mediator.search("three", 5);
        waiter.await(1, TimeUnit.SECONDS);
//        System.out.println(mediator.trending(20, 3));
        Assertions.assertEquals(expected, mediator.trending(2, 3));
    }

    //bug
    @Test
    public void testPeakLoad() throws InterruptedException {
        mediator.search("github", 1);
        mediator.search("github", 1);
        mediator.search("github", 1);
        mediator.zeitgeist(2);
        mediator.zeitgeist(2);
        mediator.getPage("github");
        mediator.getPage("wikipedia");
        waiter.await(3, TimeUnit.SECONDS);
        System.out.println(mediator.windowedPeakLoad(4));
        System.out.println(mediator.windowedPeakLoad());
    }
}
