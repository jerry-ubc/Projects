package cpen221.mp1.similarity;

import cpen221.mp1.Document;
import java.util.*;

public class DocumentSimilarity {

    /* DO NOT CHANGE THESE WEIGHTS */
    private final int WT_AVG_WORD_LENGTH      = 5;
    private final int WT_UNIQUE_WORD_RATIO    = 15;
    private final int WT_HAPAX_LEGOMANA_RATIO = 25;
    private final int WT_AVG_SENTENCE_LENGTH  = 1;
    private final int WT_AVG_SENTENCE_CPLXTY  = 4;
    private final int WT_JS_DIVERGENCE        = 50;
    /* ---- END OF WEIGHTS ------ */

    /* ------- Task 4 ------- */

    /**
     * Compute the Jensen-Shannon Divergence between the given documents
     * @param doc1 the first document, is not null
     * @param doc2 the second document, is not null
     * @return the Jensen-Shannon Divergence between the given documents
     */
    public double jsDivergence(Document doc1, Document doc2) {
        double sum = 0;
        double termOne = -1;
        double termTwo = -1;
        HashMap<String, Integer> docOneWordsMap = new HashMap<>();
        HashMap<String, Integer> docTwoWordsMap = new HashMap<>();
        ArrayList<String> docOneWordsList = new ArrayList<>();
        ArrayList<String> docTwoWordsList = new ArrayList<>();
        ArrayList<String> jsUniqueWordsList = new ArrayList<>();

        for (int i = 1; i <= doc1.numSentences(); i++){
            String sentenceString = doc1.getSentence(i);
            String[] sentenceArr = sentenceString.split(" ");

            for (int j = 0; j < sentenceArr.length; j++){
                docOneWordsList.add(sentenceArr[j]);
            }
        }

        for (int i = 1; i <= doc2.numSentences(); i++){
            String sentenceString = doc2.getSentence(i);
            String[] sentenceArr = sentenceString.split(" ");

            for (int j = 0; j < sentenceArr.length; j++){
                docTwoWordsList.add(sentenceArr[j]);
            }
        }

        for (int i = 0; i < docOneWordsList.size(); i++){
            String currWord = docOneWordsList.get(i);
            String lowerCaseWord = currWord.toLowerCase();

            placeWordInMap(docOneWordsMap, lowerCaseWord);

            if (!jsUniqueWordsList.contains(lowerCaseWord)){
                jsUniqueWordsList.add(lowerCaseWord);
            }
        }

        for (int i = 0; i < docTwoWordsList.size(); i++){
            String currWord = docTwoWordsList.get(i);
            String lowerCaseWord = currWord.toLowerCase();

            placeWordInMap(docTwoWordsMap, lowerCaseWord);

            if (!jsUniqueWordsList.contains(lowerCaseWord)){
                jsUniqueWordsList.add(lowerCaseWord);
            }
        }

        for (int i = 0; i < jsUniqueWordsList.size(); i++){
            double p = 0;
            double q = 0;

            if (docOneWordsMap.containsKey(jsUniqueWordsList.get(i))){
                p = (double) docOneWordsMap.get(jsUniqueWordsList.get(i)) / docOneWordsList.size();
            }

            if (docTwoWordsMap.containsKey(jsUniqueWordsList.get(i))){
                q = (double) docTwoWordsMap.get(jsUniqueWordsList.get(i)) / docTwoWordsList.size();
            }

            double m = (p + q) / 2;

            if (p == 0){
                termOne = 0;
            }
            else{
                termOne = p*logBaseTwo(p/m);
            }

            if (q == 0){
                termTwo = 0;
            }
            else{
                termTwo = q*logBaseTwo(q/m);
            }

            sum += termOne + termTwo;
        }
        return 0.5 * sum;
    }

    /**
     * Compute the Document Divergence between the given documents
     * @param doc1 the first document, is not null
     * @param doc2 the second document, is not null
     * @return the Document Divergence between the given documents
     */
    public double documentDivergence(Document doc1, Document doc2) {
        double avgSentenceLength = Math.abs(doc1.averageSentenceLength() - doc2.averageSentenceLength()) * WT_AVG_SENTENCE_LENGTH;
        double sentenceComplexity = Math.abs(doc1.averageSentenceComplexity() - doc2.averageSentenceComplexity()) * WT_AVG_SENTENCE_CPLXTY;
        double avgWordLength = (Math.abs(doc1.averageWordLength() - doc2.averageWordLength())) * WT_AVG_WORD_LENGTH;
        double uniqueWordRatio = Math.abs(doc1.uniqueWordRatio() - doc2.uniqueWordRatio()) * WT_UNIQUE_WORD_RATIO ;
        double hapaxRatio = Math.abs(doc1.hapaxLegomanaRatio() - doc2.hapaxLegomanaRatio()) * WT_HAPAX_LEGOMANA_RATIO;
        double jsDiv = jsDivergence(doc1, doc2) * WT_JS_DIVERGENCE;

        double sum = avgSentenceLength + sentenceComplexity + avgWordLength + uniqueWordRatio + hapaxRatio + jsDiv;

        return sum;
    }

    /**
     * Computes the binary logarithm of a given number
     * @param num the number that will have its logarithm taken. Must be greater than 0
     * @return the binary logarithm of the given number
     */
    private double logBaseTwo (double num){
        return Math.log(num) / Math.log(2);
    }

    /**
     * Stores a given word and the number of its copies that are stored in a HashMap
     * @param wordsMap the HashMap the will contain the word
     * @param word the word to be stored
     */
    private void placeWordInMap (HashMap<String,Integer> wordsMap, String word){
        if (wordsMap.containsKey(word)){
            wordsMap.replace(word, wordsMap.get(word) + 1);
        }
        else {
            wordsMap.put(word, 1);
        }
    }
}
