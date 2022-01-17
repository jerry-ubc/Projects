package cpen221.mp1;

import cpen221.mp1.similarity.DocumentSimilarity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import cpen221.mp1.Document;

import java.net.MalformedURLException;
import java.net.URL;

class Task4Tests {
    private static Document testDocument1;
    private static Document testDocument2;
    private static Document testDocument3;
    private static Document testDocument4;
    private static Document testDocument5;
    private static Document testDocument6;
    private static Document testDocument7;
    private static Document testDocument8;
    private static DocumentSimilarity testDoc;

    @BeforeAll
    public static void setupTests() throws MalformedURLException {
        testDoc = new DocumentSimilarity();
        testDocument1 = new Document("single long word", "resources/testDoc1.txt");
        testDocument2 = new Document("single short word", "resources/testDoc2.txt");
        testDocument3 = new Document("identicalDoc1", "resources/smallParagraph.txt");
        testDocument4 = new Document("identicalDoc2", "resources/smallParagraphCopy.txt");
        testDocument5 = new Document("empty file", "resources/emptyText.txt");
        testDocument6 = new Document("superBigDoc", "resources/superBigDoc.txt");
        testDocument7 = new Document("superBigDoc2", "resources/superBigDoc2.txt");
        testDocument8 = new Document("The Ant and The Cricket", new URL("http://textfiles.com/stories/antcrick.txt"));
    }

    @Test
    void singleWordDocuments() {

        System.out.println(testDoc.jsDivergence(testDocument1,testDocument2));
        Assertions.assertEquals(1.0, testDoc.jsDivergence(testDocument1,testDocument2), 0.005);
    }

    @Test
    void identicalDocuments() {

        System.out.println(testDoc.jsDivergence(testDocument3,testDocument4));
        Assertions.assertEquals(0.0, testDoc.jsDivergence(testDocument3,testDocument4), 0.005);
        Assertions.assertEquals(0.0, testDoc.documentDivergence(testDocument3,testDocument4), 0.005);
    }

    @Test
    void containsEmptyDocument() {

        System.out.println(testDoc.jsDivergence(testDocument5,testDocument6));
        System.out.println(testDoc.documentDivergence(testDocument5,testDocument6));
        Assertions.assertEquals(0.5, testDoc.jsDivergence(testDocument5,testDocument6), 0.005);
        Assertions.assertEquals(74.48869780574748, testDoc.documentDivergence(testDocument5,testDocument6), 0.005);
    }

    @Test
    void superLongDocumentComparison() {

        System.out.println(testDoc.jsDivergence(testDocument6,testDocument7));
        System.out.println(testDoc.documentDivergence(testDocument6,testDocument7));
        Assertions.assertEquals(0.3924134051032568, testDoc.jsDivergence(testDocument6,testDocument7), 0.005);
        Assertions.assertEquals(22.828192027563354, testDoc.documentDivergence(testDocument6,testDocument7), 0.005);
    }

    @Test
    void testURL() {

        System.out.println(testDoc.documentDivergence(testDocument7,testDocument8));
        Assertions.assertEquals(0.6665712002862428, testDoc.jsDivergence(testDocument7,testDocument8), 0.005);
        Assertions.assertEquals(45.768064496836665, testDoc.documentDivergence(testDocument7,testDocument8), 0.005);
    }
}
