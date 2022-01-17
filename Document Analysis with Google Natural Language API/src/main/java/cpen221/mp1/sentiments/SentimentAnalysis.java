package cpen221.mp1.sentiments;

import cpen221.mp1.exceptions.NoSuitableSentenceException;

public class SentimentAnalysis {

    public static String getMostPositiveSentence(cpen221.mp1.Document doc)
        throws NoSuitableSentenceException {
        if(doc.returnMostPositiveSentence().equals(null)) {
            throw new NoSuitableSentenceException();
        }
        return doc.returnMostPositiveSentence();
    }

    public static String getMostNegativeSentence(cpen221.mp1.Document doc)
        throws NoSuitableSentenceException {
        if(doc.returnMostNegativeSentence().equals(null)) {
            throw new NoSuitableSentenceException();
        }
        return doc.returnMostNegativeSentence();
    }
}
