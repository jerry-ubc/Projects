package cpen221.mp1;

import cpen221.mp1.similarity.GroupingDocuments;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;


public class Task5PartitionTests {

    private static cpen221.mp1.Document testDocument1;
    private static cpen221.mp1.Document testDocument2;
    private static cpen221.mp1.Document testDocument3;
    private static cpen221.mp1.Document testDocument4;
    private static cpen221.mp1.Document testDocument5;
    private static cpen221.mp1.Document testDocument6;
    private static cpen221.mp1.Document testDocument7;
    private static cpen221.mp1.Document testDocument8;
    private static cpen221.mp1.Document testDocument9;
    private static cpen221.mp1.Document testDocument10;
    private static cpen221.mp1.Document testDocument11;
    private static cpen221.mp1.Document testDocument12;
    private static GroupingDocuments groupTest = new GroupingDocuments();
    Set<cpen221.mp1.Document> documents;

    @BeforeAll
    public static void setupTests(){
        groupTest = new GroupingDocuments();
        testDocument1 = new cpen221.mp1.Document("The Ant and The Cricket", "resources/antcrick.txt");
        testDocument2 = new cpen221.mp1.Document("antrick copy", "resources/antcrickcopy.txt");
        testDocument3 = new cpen221.mp1.Document("leon1", "resources/testDoc1.txt");
        testDocument4 = new cpen221.mp1.Document("leon2", "resources/testDoc2.txt");
        testDocument5 = new cpen221.mp1.Document("halfsimilar", "resources/halfsimilar.txt");
        testDocument6 = new cpen221.mp1.Document("antrickmod", "resources/antcrickmod.txt");
        testDocument7 = new cpen221.mp1.Document("1", "resources/1.txt");
        testDocument8 = new cpen221.mp1.Document("2", "resources/2.txt");
        testDocument9 = new cpen221.mp1.Document("3", "resources/3.txt");
        testDocument10 = new cpen221.mp1.Document("4", "resources/4.txt");
        testDocument11 = new cpen221.mp1.Document("5", "resources/5.txt");
        testDocument12 = new Document("The Ant and The Cricket", new URL("http://textfiles.com/stories/antcrick.txt"));
    }


    @Test
    public void testGroups(){
        Set<cpen221.mp1.Document> documents = new HashSet<>();
        documents.add(testDocument1);
        documents.add(testDocument2);
        documents.add(testDocument3);
        documents.add(testDocument4);
        documents.add(testDocument5);
        documents.add(testDocument6);
        documents.add(testDocument7);
        documents.add(testDocument8);
        documents.add(testDocument9);
        documents.add(testDocument10);
        documents.add(testDocument11);
        documents.add(testDocument12);

        Set<Set<cpen221.mp1.Document>> partitions = new HashSet<>();
        partitions = groupTest.groupBySimilarity(documents, 5);
    }

    @Test
    public void oneTestGroup(){
        Set<cpen221.mp1.Document> documents = new HashSet<>();
        documents.add(testDocument1);
        documents.add(testDocument2);
        documents.add(testDocument3);
        documents.add(testDocument4);
        documents.add(testDocument5);
        documents.add(testDocument6);
        documents.add(testDocument7);
        documents.add(testDocument8);
        documents.add(testDocument9);
        documents.add(testDocument10);
        documents.add(testDocument11);

        Set<Set<cpen221.mp1.Document>> partitions = new HashSet<>();
        Set<cpen221.mp1.Document> docSet = new HashSet<>();
        docSet.add(testDocument1);
        docSet.add(testDocument2);
        docSet.add(testDocument3);
        docSet.add(testDocument4);
        docSet.add(testDocument5);
        docSet.add(testDocument6);
        docSet.add(testDocument7);
        docSet.add(testDocument8);
        docSet.add(testDocument9);
        docSet.add(testDocument10);
        docSet.add(testDocument11);
        partitions.add(docSet);
        Assertions.assertEquals(partitions, groupTest.groupBySimilarity(documents, 1));
    }

    @Test
    public void nTestGroups(){
        Set<cpen221.mp1.Document> documents = new HashSet<>();
        documents.add(testDocument1);
        documents.add(testDocument2);
        documents.add(testDocument3);
        documents.add(testDocument4);
        documents.add(testDocument5);
        documents.add(testDocument6);
        documents.add(testDocument7);
        documents.add(testDocument8);
        documents.add(testDocument9);
        documents.add(testDocument10);
        documents.add(testDocument11);

        Set<Set<cpen221.mp1.Document>> partitions = new HashSet<>();
        Set<cpen221.mp1.Document> tempSet1 = new HashSet<>();
        Set<cpen221.mp1.Document> tempSet2 = new HashSet<>();
        Set<cpen221.mp1.Document> tempSet3 = new HashSet<>();
        Set<cpen221.mp1.Document> tempSet4 = new HashSet<>();
        Set<cpen221.mp1.Document> tempSet5 = new HashSet<>();
        Set<cpen221.mp1.Document> tempSet6 = new HashSet<>();
        Set<cpen221.mp1.Document> tempSet7 = new HashSet<>();
        Set<cpen221.mp1.Document> tempSet8 = new HashSet<>();
        Set<cpen221.mp1.Document> tempSet9 = new HashSet<>();
        Set<cpen221.mp1.Document> tempSet10 = new HashSet<>();
        Set<cpen221.mp1.Document> tempSet11 = new HashSet<>();
        tempSet1.add(testDocument1);
        tempSet2.add(testDocument2);
        tempSet3.add(testDocument3);
        tempSet4.add(testDocument4);
        tempSet5.add(testDocument5);
        tempSet6.add(testDocument6);
        tempSet7.add(testDocument7);
        tempSet8.add(testDocument8);
        tempSet9.add(testDocument9);
        tempSet10.add(testDocument10);
        tempSet11.add(testDocument11);
        partitions.add(tempSet1);
        partitions.add(tempSet2);
        partitions.add(tempSet3);
        partitions.add(tempSet4);
        partitions.add(tempSet5);
        partitions.add(tempSet6);
        partitions.add(tempSet7);
        partitions.add(tempSet8);
        partitions.add(tempSet9);
        partitions.add(tempSet10);
        partitions.add(tempSet11);


        Assertions.assertEquals(partitions, groupTest.groupBySimilarity(documents, documents.size()));
    }

}
