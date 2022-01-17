package cpen221.mp1;

import cpen221.mp1.exceptions.NoSuitableSentenceException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.cloud.language.v1.AnalyzeSentimentResponse;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Document.Type;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;

public class Task3SentimentTests {

    private static cpen221.mp1.Document testDocument1;
    private static cpen221.mp1.Document testDocument2;
    private static cpen221.mp1.Document testDocument3;
    private static cpen221.mp1.Document testDocument4;

    @BeforeAll
    public static void setupTests() throws MalformedURLException {
        testDocument1 = new cpen221.mp1.Document("The Ant and The Cricket", "resources/antcrick.txt");
        testDocument2 = new cpen221.mp1.Document("The Ant and The Cricket", new URL("http://textfiles.com/stories/antcrick.txt"));
        testDocument3 = new cpen221.mp1.Document("Dash testing", "resources/testdashwords.txt");
        testDocument4 = new cpen221.mp1.Document("New line testing", "resources/test2.txt");
    }


    @Test
    public void testSentiments() throws NoSuitableSentenceException {
        Assertions.assertEquals("\"Well, try dancing now!\"", testDocument1.getMostPositiveSentence());
        Assertions.assertEquals("\"Well, try dancing now!\"", testDocument2.getMostPositiveSentence());
        Assertions.assertEquals("Then the snow fell and she could find nothing at all to eat.", testDocument1.getMostNegativeSentence());
        Assertions.assertEquals("Then the snow fell and she could find nothing at all to eat.", testDocument2.getMostNegativeSentence());
    }

}