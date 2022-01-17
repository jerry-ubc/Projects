package cpen221.mp1;

import com.google.cloud.language.v1.AnalyzeSentimentResponse;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import cpen221.mp1.exceptions.NoSuitableSentenceException;
import cpen221.mp1.sentiments.SentimentAnalysis;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.BreakIterator;
import java.util.*;
import java.util.List;

public class Document {

    private boolean analysisFilled = false;
    private final String id;
    private URL url;
    private String fileName;
    private final boolean hasURL;
    private Scanner urlScanner;
    BufferedReader reader;
    private ArrayList<String> sentences = new ArrayList<>();
    private ArrayList<String> words = new ArrayList<>();
    private List<String> uniqueWords = new ArrayList<>();
    private String mostPositiveSentence = null;
    private String mostNegativeSentence = null;

    /* ------- Task 0 - Jerry ------ */

    /**
     * Create a new document using a URL
     * @param docId the document identifier
     * @param docURL the URL with the contents of the document
     */
    public Document(String docId, URL docURL) {
        id = docId;
        url = docURL;
        hasURL = true;
        try {
            urlScanner = new Scanner(new URL(docURL.toString()).openStream());
        }
        catch (MalformedURLException mue) {
            System.out.println("Problem opening URL");
        }
        catch (IOException ioe) {
            System.out.println("Problem opening URL");
        }
        fillSentenceAndWordsList();
    }

    /**
     *
     * @param docId the document identifier
     * @param fileName the name of the file with the contents of
     *                 the document
     */
    public Document(String docId, String fileName) {
        id = docId;
        this.fileName = fileName;
        hasURL = false;
        try {
            reader = new BufferedReader(new FileReader(fileName));
        }
        catch (FileNotFoundException nfe) {
            System.out.println("Problem reading file");
        }
        fillSentenceAndWordsList();
    }

    /**
     * Obtain the identifier for this document
     * @return the identifier for this document
     */
    public String getDocId() {
        return id;
    }

    /* ------- Task 1 - Trevor and Jerry and Leon ------- */

    public double averageWordLength() {
        int numChars = 0;
        double avgWrdLen;
        for(int i = 0; i < words.size(); i++){
            for(int j = 0; j < words.get(i).length(); j++){
                numChars++;
            }
        }

        avgWrdLen = (double)numChars / numWords();

        return avgWrdLen;
    }


    public double uniqueWordRatio() {
        double uniqueSize = 0;

        for (int i = 0; i < words.size(); i++){
            String currWord = words.get(i);
            String lowerCaseWord = currWord.toLowerCase();

            if (!(uniqueWords.contains(lowerCaseWord))){
                uniqueWords.add(lowerCaseWord);
            }

            uniqueSize = uniqueWords.size();
        }

        return uniqueSize / words.size();
    }

    public double hapaxLegomanaRatio() {
        int hapaxWordCounter = 0;
        HashMap<String, Integer> wordComp = new HashMap<>();

        for (int i = 0; i < words.size(); i++){
            String currWord = words.get(i);
            String lowerCaseWord = currWord.toLowerCase();

            if (wordComp.containsKey(lowerCaseWord)){
                wordComp.replace(lowerCaseWord, wordComp.get(lowerCaseWord) + 1);
            }
            else {
                wordComp.put(lowerCaseWord, 1);
            }
        }

        double uniqueSize = 0;

        for (int i = 0; i < words.size(); i++){
            String currWord = words.get(i);
            String lowerCaseWord = currWord.toLowerCase();

            if (!(uniqueWords.contains(lowerCaseWord))){
                uniqueWords.add(lowerCaseWord);
            }

            uniqueSize = uniqueWords.size();
        }

        for (int i = 0; i < uniqueSize; i++){
            if (wordComp.get(uniqueWords.get(i)) == 1){
                hapaxWordCounter++;
            }
        }

        return (double) hapaxWordCounter / words.size();
    }

    /* ------- Task 2 - Jerry ------- */

    private void fillSentenceAndWordsList() {
        BreakIterator sentenceIterator = BreakIterator.getSentenceInstance(Locale.CANADA);
        BreakIterator wordIterator = BreakIterator.getWordInstance(Locale.CANADA);
        String curSentence = "";
        int ELLIPSES_LENGTH = 3;
        if(hasURL)
        {
            while (urlScanner.hasNext()) {
                String originalLine = urlScanner.nextLine();
                String nextLine = originalLine.replaceAll("`","'");

                sentenceIterator.setText(nextLine);
                wordIterator.setText(nextLine);
                int sentenceEnd = sentenceIterator.first();
                int sentenceStart;
                int wordEnd = wordIterator.first();
                int wordStart;

                while(wordEnd != BreakIterator.DONE) {
                    wordStart = wordEnd;
                    wordEnd = wordIterator.next();
                    String curWord;

                    if(wordEnd > 0 && wordEnd <= nextLine.length())
                    {
                        curWord = nextLine.substring(wordStart,wordEnd);
                        if(nextLine.length() > 1 && Character.isLetterOrDigit(curWord.charAt(0))) {
                            words.add(curWord);
                        }
                    }
                }

                while(sentenceEnd != BreakIterator.DONE) {
                    sentenceStart = sentenceEnd;
                    sentenceEnd = sentenceIterator.next();

                    if(nextLine.length() > ELLIPSES_LENGTH && sentenceEnd != BreakIterator.DONE) {
                        curSentence += nextLine.substring(sentenceStart, sentenceEnd);
                        String lastThree = nextLine.substring(sentenceEnd-ELLIPSES_LENGTH,sentenceEnd);
                        if((lastThree.contains("!") || lastThree.contains(".") || lastThree.contains("?")) && !lastThree.contains("... ")) {
                            if(curSentence.charAt(curSentence.length() - 1) == ' ')
                                curSentence = curSentence.substring(0, curSentence.length()-1);
                            sentences.add(curSentence);
                            curSentence = "";
                        }
                        else
                        {
                            curSentence += " ";
                        }
                    }
                }
            }
        }
        else {
            try {
                for (String originalLine = reader.readLine(); originalLine != null; originalLine = reader.readLine()) {
                    String nextLine = originalLine.replaceAll("`","'");

                    sentenceIterator.setText(nextLine);
                    wordIterator.setText(nextLine);
                    int sentenceEnd = sentenceIterator.first();
                    int sentenceStart;
                    int wordEnd = wordIterator.first();
                    int wordStart;

                    while(wordEnd != BreakIterator.DONE) {
                        wordStart = wordEnd;
                        wordEnd = wordIterator.next();
                        String curWord;

                        if (wordEnd > 0 && wordEnd <= nextLine.length()) {
                            curWord = nextLine.substring(wordStart, wordEnd);
                            if (nextLine.length() > 1 && Character.isLetterOrDigit(curWord.charAt(0))) {
                                words.add(curWord);
                            }
                        }
                    }

                    while(sentenceEnd != BreakIterator.DONE) {
                        sentenceStart = sentenceEnd;
                        sentenceEnd = sentenceIterator.next();
                        if(nextLine.length() > ELLIPSES_LENGTH && sentenceEnd != BreakIterator.DONE) {
                            curSentence += nextLine.substring(sentenceStart, sentenceEnd);
                            String lastThree = nextLine.substring(sentenceEnd-ELLIPSES_LENGTH,sentenceEnd);
                            if((lastThree.contains("!") || lastThree.contains(".") || lastThree.contains("?")) && !lastThree.contains("... ")) {
                                if(curSentence.charAt(curSentence.length() - 1) == ' ')
                                    curSentence = curSentence.substring(0, curSentence.length()-1);
                                sentences.add(curSentence);
                                curSentence = "";
                            }
                            else
                            {
                                curSentence += " ";
                            }
                        }
                    }
                }
                reader.close();
            }
            catch (IOException ioe) {
                System.out.println("Problem reading file");
            }
        }
    }

    /**
     * Obtain the number of sentences in the document
     * @return the number of sentences in the document
     */
    public int numSentences() {
        return sentences.size();
    }
    /**
     * Obtain a specific sentence from the document.
     * Sentences are numbered starting from 1.
     *
     * @param sentence_number the position of the sentence to retrieve,
     * {@code 1 <= sentence_number <= this.getSentenceCount()}
     * @return the sentence indexed by {@code sentence_number}
     */
    public String getSentence(int sentence_number) {
        //Subtract 1 as index = sentence_number - 1
        return sentences.get(sentence_number-1);
    }

    public double averageSentenceLength() {
        return (double) words.size() / numSentences();
    }

    private int numWords()
    {
        return words.size();
    }

    public double averageSentenceComplexity() {
        int phraseCount = 0;
        double averagePhrasesPerSentence;
        for(int i = 0; i < sentences.size(); i++) {
            for(int j = 0; j < sentences.get(i).length(); j++) {
                char curChar = sentences.get(i).charAt(j);
                if(curChar == ';' || curChar == ':' || curChar == ',')
                    phraseCount++;
                if(j == sentences.get(i).length() - 1)
                    phraseCount++;
            }
        }
        averagePhrasesPerSentence = (double) phraseCount / numSentences();
        return averagePhrasesPerSentence;
    }

    /* ------- Task 3 - Jerry ------ */

    /**
     * Obtain the sentence with the most positive sentiment in the document
     * @return the sentence with the most positive sentiment in the
     * document; when multiple sentences share the same sentiment value
     * returns the sentence that appears later in the document
     * @throws NoSuitableSentenceException if there is no sentence that
     * expresses a positive sentiment
     */
    public String getMostPositiveSentence() throws NoSuitableSentenceException {
        if(!analysisFilled) {
            fillSentencesAnalysis();
        }
        return SentimentAnalysis.getMostPositiveSentence(this);

    }

    /**
     * Obtain the sentence with the most negative sentiment in the document
     * @return the sentence with the most negative sentiment in the document;
     * when multiple sentences share the same sentiment value, returns the
     * sentence that appears later in the document
     * @throws NoSuitableSentenceException if there is no sentence that
     * expresses a negative sentiment
     */
    public String getMostNegativeSentence() throws NoSuitableSentenceException {
        return SentimentAnalysis.getMostNegativeSentence(this);
    }

    private void fillSentencesAnalysis() {
        float mostPositiveSentiment = 0;
        float mostNegativeSentiment = 0;
        try (LanguageServiceClient language = LanguageServiceClient.create()) {
            for(int i = 1; i < numSentences(); i++)
            {
                String currentSentence = sentences.get(i);
                com.google.cloud.language.v1.Document doc = com.google.cloud.language.v1.Document.newBuilder().setContent(currentSentence).setType(com.google.cloud.language.v1.Document.Type.PLAIN_TEXT).build();
                AnalyzeSentimentResponse response = language.analyzeSentiment(doc);
                Sentiment sentiment = response.getDocumentSentiment();
                //0.3 is the threshold determined by the problem definition for a positive sentence
                if(sentiment.getScore() >= 0.3) {
                    if(sentiment.getScore() >= mostPositiveSentiment) {
                        mostPositiveSentiment = sentiment.getScore();
                        mostPositiveSentence = currentSentence;
                    }
                }
                //-0.3 is the threshold determined by the problem definition for a negative sentence
                else if(sentiment.getScore() <= -0.3) {
                    if(sentiment.getScore() <= mostNegativeSentiment) {
                        mostNegativeSentiment = sentiment.getScore();
                        mostNegativeSentence = currentSentence;
                    }
                }
            }
        }
        catch (IOException ioe) {
            System.out.println(ioe);
            throw new RuntimeException("Unable to communicate with Sentiment Analyzer!");
        }
    }

    public String returnMostPositiveSentence() {
        if(!analysisFilled)
            fillSentencesAnalysis();
        analysisFilled = true;
        return mostPositiveSentence;
    }

    public String returnMostNegativeSentence() {
        if(!analysisFilled)
            fillSentencesAnalysis();
        analysisFilled = true;
        return mostNegativeSentence;
    }

}
