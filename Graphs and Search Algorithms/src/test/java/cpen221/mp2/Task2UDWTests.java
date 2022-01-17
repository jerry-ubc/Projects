package cpen221.mp2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class Task2UDWTests {

    private static UDWInteractionGraph testGraphBase;

    @BeforeAll
    public static void setupTests() {
        testGraphBase = new UDWInteractionGraph("resources/Task1-2UDWTransactions.txt");
    }

    @Test
    public void testReportActivityInTimeWindow() {
        int[] result = testGraphBase.ReportActivityInTimeWindow(new int[]{0, 1});
        Assertions.assertEquals(3, result[0]);
        Assertions.assertEquals(2, result[1]);
    }

    @Test
    public void testReportOnUser() {
        int[] result = testGraphBase.ReportOnUser(0);
        Assertions.assertEquals(6, result[0]);
        Assertions.assertEquals(3, result[1]);
    }


    @Test
    public void testReportOnUser1() {
        List<Integer> userFilter = Arrays.asList(0, 1);
        UDWInteractionGraph t = new UDWInteractionGraph(testGraphBase, userFilter);
        int[] result = t.ReportOnUser(0);
        Assertions.assertEquals(6, result[0]);
        Assertions.assertEquals(3, result[1]);
    }

    @Test
    public void testReportOnUser2() {
        int[] result = testGraphBase.ReportOnUser(4);
        Assertions.assertEquals(0, result[0]);
        Assertions.assertEquals(0, result[1]);
    }

    @Test
    public void testNthActiveUser() {
        UDWInteractionGraph t = new UDWInteractionGraph(testGraphBase, new int[]{0, 2});
        Assertions.assertEquals(0, t.NthMostActiveUser(1));
    }

    @Test
    public void testNthActiveUser1() {
        UDWInteractionGraph t = new UDWInteractionGraph(testGraphBase, new int[]{0, 2});
        Assertions.assertEquals(1, t.NthMostActiveUser(2));
    }

    @Test
    public void testNthActiveUser2() {
        UDWInteractionGraph t = new UDWInteractionGraph(testGraphBase, new int[]{0, 2});
        Assertions.assertEquals(-1, t.NthMostActiveUser(5));
    }

    @Test
    public void testNthActiveUser3() {
        UDWInteractionGraph t = new UDWInteractionGraph(testGraphBase, new int[]{0, 2});
        Assertions.assertEquals(2, t.NthMostActiveUser(3));
    }

    @Test
    public void testNthActiveUser4() {
        UDWInteractionGraph t = new UDWInteractionGraph(testGraphBase, new int[]{0, 2});
        Assertions.assertEquals(3, t.NthMostActiveUser(4));
    }
}
