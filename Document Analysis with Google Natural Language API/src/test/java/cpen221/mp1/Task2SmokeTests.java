package cpen221.mp1;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;

public class Task2SmokeTests {

    private static Document testDocument1;
    private static Document testDocument2;

    @BeforeAll
    public static void setupTests() throws MalformedURLException {
        testDocument1 = new Document("The Ant and The Cricket", "resources/antcrick.txt");
        testDocument2 = new Document("The Ant and The Cricket", new URL("http://textfiles.com/stories/antcrick.txt"));
    }

    @Test
    public void testAvgWordLength() {
        Assertions.assertEquals(4.08, testDocument1.averageWordLength(), 0.005);
    }

    @Test
    public void testUniqueWordRatio() {
        Assertions.assertEquals(0.524, testDocument2.uniqueWordRatio(), 0.005);
    }


    @Test
    public void testHapaxLegomanaRatio() {
        Assertions.assertEquals(0.355, testDocument1.hapaxLegomanaRatio(), 0.005);
    }

}
