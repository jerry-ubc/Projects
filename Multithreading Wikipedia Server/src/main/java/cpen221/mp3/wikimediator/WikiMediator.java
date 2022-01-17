package cpen221.mp3.wikimediator;

import cpen221.mp3.fsftbuffer.FSFTBuffer;
import org.fastily.jwiki.core.Wiki;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

public class WikiMediator {
    //requests and requestCount represent search() and getPage() requests and the amount of times they've been called
    private final List<String> requests = new ArrayList<>();
    private final List<Integer> requestCount = new ArrayList<>();
    //times keeps track of when requests have been made, information is useful for zeitgeist() and trending()
    private final List<Long> times = new ArrayList<>();
    //cache stores Website information, so we can store information to avoid searching on Wiki
    private final FSFTBuffer<Website> cache;
    //window represents activity of the WikiMediator, tracking when each function has been called
    private final List<Long> window = new ArrayList<>();

    public static final int SECONDS_TO_MILLISECONDS = 1000;

    /*
    Representation Invariant:
    requests, requestCount, times, cache, window != null
     */

    /*
    Abstraction Function:
    Represents an object to interact with JWiki
     */

    /**
     * Creates a WikiMediator Object with inputs to pass to FSFTBuffer.
     * @param capacity The maximum size of the buffer
     * @param stalenessInterval The time, in seconds, it takes for an element of the buffer to time out
     */
    public WikiMediator(int capacity, int stalenessInterval) {
        startup();
        cache = new FSFTBuffer<>(capacity, stalenessInterval);
    }

    /**
     * Overloaded constructor that calls overloaded constructor for FSFTBuffer with default values:
     * capacity = 32
     * timeout = 1000
     */
    public WikiMediator() {
        startup();
        cache = new FSFTBuffer<>();
    }

    /**
     * Finds page titles matching the query String through JWiki search() function.
     * @param query String to find titles for
     * @param limit Maximum page titles to return
     * @return List of Strings representing page titles related to query
     */
    public List<String> search(String query, int limit) {
        Wiki wiki = new Wiki.Builder().withDomain("en.wikipedia.org").build();
        long curTime = System.currentTimeMillis() / SECONDS_TO_MILLISECONDS;

        String garbageString = "NULL!NULL@NULL#";

        //Count if this String has been used before
        if(requests.contains(query)) {
            for(int i = 0; i < requests.size(); i++) {
                if(requests.get(i).equals(query)) {
                    requestCount.set(i, requestCount.get(i) + 1);
                    times.set(i, curTime);
                }
            }
        }
        else {
            //Add String to requests if it's a new String
            requests.add(query);
            requestCount.add(0);
            times.add(curTime);
        }
        window.add(curTime);
        return wiki.search(query, limit);
    }

    /**
     * Finds page text matching the page title given through JWiki getPageText() function.
     * Also stores Websites in cache to save time and avoid unnecessary searched with JWiki.
     * @param pageTitle String representing Wikipedia page to find associated text for
     * @return Wikipedia page text associated to pageTitle
     */
    public String getPage(String pageTitle) {
        Website website = new Website(pageTitle);
        long curTime = System.currentTimeMillis() / SECONDS_TO_MILLISECONDS;
        Wiki wiki = new Wiki.Builder().withDomain("en.wikipedia.org").build();
        String garbageString = "NULL!NULL@NULL#";
        //use touch to remove timed out websites
        cache.touch(garbageString);

        //Count if this String has been used before
        if(requests.contains(pageTitle)) {
            for(int i = 0; i < requests.size(); i++) {
                if(requests.get(i).equals(pageTitle)) {
                    requestCount.set(i, requestCount.get(i) + 1);
                    times.set(i, curTime);
                }
            }
        }
        else {
            //Add String to requests if it's a new String
            requests.add(pageTitle);
            requestCount.add(0);
            times.add(curTime);
        }
        window.add(curTime);
        cache.put(website);
        try {
            return cache.get(pageTitle).getText();
        }
        catch (NoSuchElementException nsee) {
            String text = wiki.getPageText(pageTitle);
            website.addPageText(text);
            cache.put(website);
            return text;
        }
    }

    /**
     * Finds most common Strings used as "query" and "pageTitle" for functions search() and getPage()
     * @param limit Maximum amount of Strings to return
     * @return List of Strings in non-increasing order of frequency
     */
    public List<String> zeitgeist(int limit) {
        long curTime = System.currentTimeMillis() / SECONDS_TO_MILLISECONDS;
        List<String> orders = new ArrayList<String>();
        String garbageString = "NULL!NULL@NULL#";

        List<String> workingRequests = new ArrayList<String>(requests);
        List<Integer> workingRequestCounts = new ArrayList<Integer>(requestCount);

        while(orders.size() < limit && orders.size() < requests.size()) {
            int maxCount = workingRequestCounts.get(0);
            int maxCountIndex = 0;
            for(int i = 0; i < workingRequestCounts.size(); i++) {
                if(workingRequestCounts.get(i) > maxCount && !workingRequests.get(i).equals(garbageString)) {
                    maxCountIndex = i;
                    maxCount = workingRequestCounts.get(i);
                }
            }
            orders.add(requests.get(maxCountIndex));
            //Give garbage values for that index to indicate it has been used
            workingRequests.set(maxCountIndex, garbageString);
            workingRequestCounts.set(maxCountIndex, -1);
        }
        window.add(curTime);
        //Maintain immutability
        return Collections.unmodifiableList(orders);
    }

    /**
     * Finds the most frequent query or pageTitle parameters in the last timeLimitInSeconds seconds.
     * Up to a maximum of maxItems items.
     * @param timeLimitInSeconds From this time to current time, find the most frequent String parameters
     * @param maxItems Maximum size of return List
     * @return List of Strings containing the most frequenty used String inputs from search() and getPage()
     */
    public List<String> trending(int timeLimitInSeconds, int maxItems) {
        String garbageString = "NULL!NULL@NULL#";
        long curTime = System.currentTimeMillis() / SECONDS_TO_MILLISECONDS;
        long windowLow = curTime - timeLimitInSeconds;

        List<String> workingRequests = new ArrayList<String>(requests);
        List<Integer> workingRequestCounts = new ArrayList<Integer>(requestCount);
        List<String> trends = new ArrayList<String>();

        finish:
        while(trends.size() < maxItems && trends.size() < requests.size()) {
            int maxFrequency = requestCount.get(0);
            int index = 0;
            int garbageValues = 0;
            for(int i = 0; i < workingRequestCounts.size(); i++) {
                if(requestCount.get(i) > maxFrequency && times.get(i) >= windowLow &&
                        times.get(i) <= curTime && !workingRequests.get(i).equals(garbageString)) {
                    index = i;
                    maxFrequency = requestCount.get(i);
                    trends.add(requests.get(index));
                }

                //Give garbage values for that index to indicate it has been used
                workingRequests.set(index, garbageString);
                workingRequestCounts.set(index, -1);
                garbageValues++;
                if(workingRequests.size() - garbageValues == trends.size())
                    break finish;
            }

        }
        window.add(curTime);
        //Maintain immutability
        return Collections.unmodifiableList(trends);
    }

    /**
     * Returns maximum number of function requests within a given time range. Does not count current function run in
     * calculations.
     * @param timeWindowInSeconds The time window to find most activity within
     * @return The maximum number of function calls within the function range
     */
    public int windowedPeakLoad(int timeWindowInSeconds) {
        int max = 0;
        int count;
        long curTime = System.currentTimeMillis() / SECONDS_TO_MILLISECONDS;
        for(int i = 0; i < window.size(); i++) {
            //Do in this order in case we run out of bits for startTimeLong, so divide to reduce bits used first
            long startTime = window.get(i);
            long endTime = startTime + timeWindowInSeconds;
            count = 0;

            for(int j = 0; j < window.size(); j++) {
                if(window.get(j) >= startTime && window.get(j) <= endTime) {
                    count++;
                }
            }
            if(count > max) {
                max = count;
            }
        }
        window.add(curTime);
        return max;
    }

    /**
     * Returns windowedPeakLoad for 30 seconds, overriding the previous windowedPeakLoad
     * @return windowedPeakLoad with timeWindowInSeconds = 30
     */
    public int windowedPeakLoad() {
        //Use 30 because it's the value given by the spec
        return windowedPeakLoad(30);
    }

    protected void startup() {
        //Create File objects for each List to store
        File requestsFile = new File("local/requests.txt");
        File requestCountFile = new File("local/requestCount.txt");
        File timesFile = new File("local/times.txt");
        File windowFile = new File("local/window.txt");

        try {
            //Create FileReader Objects for each File
            FileReader file = new FileReader(requestsFile);
            FileReader file2 = new FileReader(requestCountFile);
            FileReader file3 = new FileReader(timesFile);
            FileReader file4 = new FileReader(windowFile);

            //If corresponding text file exists, take the information stored in it
            if(requestsFile.canRead()) {
                BufferedReader reader = new BufferedReader(file);
                for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                    int startIndex = 0;
                    int endIndex = 0;
                    for (int i = 0; i < line.length(); i++) {
                        if (line.charAt(i) == ',' && i != line.length() - 1) {
                            startIndex = endIndex;
                            endIndex = i;
                            String curWord = line.substring(startIndex, endIndex);
                            requests.add(curWord);
                        }
                    }
                }
            }
            if (requestCountFile.canRead()) {
                BufferedReader reader2 = new BufferedReader(file2);
                for (String line = reader2.readLine(); line != null; line = reader2.readLine()) {
                    int startIndex = 0;
                    int endIndex = 0;
                    for (int i = 0; i < line.length(); i++) {
                        if (line.charAt(i) == ',' && i != line.length() - 1) {
                            startIndex = endIndex;
                            endIndex = i;
                            String curWord = line.substring(startIndex, endIndex);
                            requests.add(curWord);
                        }
                    }
                }
            }
            if (timesFile.canRead()) {
                BufferedReader reader3 = new BufferedReader(file3);
                for (String line = reader3.readLine(); line != null; line = reader3.readLine()) {
                    int startIndex = 0;
                    int endIndex = 0;
                    for (int i = 0; i < line.length(); i++) {
                        if (line.charAt(i) == ',' && i != line.length() - 1) {
                            startIndex = endIndex;
                            endIndex = i;
                            String curWord = line.substring(startIndex, endIndex);
                            requests.add(curWord);
                        }
                    }
                }
            }
            if (windowFile.canRead()) {
                BufferedReader reader4 = new BufferedReader(file4);
                for (String line = reader4.readLine(); line != null; line = reader4.readLine()) {
                    int startIndex = 0;
                    int endIndex = 0;
                    for (int i = 0; i < line.length(); i++) {
                        if (line.charAt(i) == ',' && i != line.length() - 1) {
                            startIndex = endIndex;
                            endIndex = i;
                            String curWord = line.substring(startIndex, endIndex);
                            requests.add(curWord);
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }


    }

    public void shutdown() {
        //Create File objects for each List to store
        File requestsFile = new File("local/requests.txt");
        File requestCountFile = new File("local/requestCount.txt");
        File timesFile = new File("local/times.txt");
        File windowFile = new File("local/window.txt");

        //If those files exist, delete them and create new ones
        if(requestsFile.canRead())
            requestsFile.delete();
        if(requestCountFile.canRead())
            requestCountFile.delete();
        if(timesFile.canRead())
            timesFile.delete();
        if(windowFile.canRead())
            windowFile.delete();

        try {
            //Create new files
            requestsFile.createNewFile();
            requestCountFile.createNewFile();
            timesFile.createNewFile();
            windowFile.createNewFile();

            FileWriter writer = new FileWriter(requestsFile);
            FileWriter writer2 = new FileWriter(requestCountFile);
            FileWriter writer3 = new FileWriter(timesFile);
            FileWriter writer4 = new FileWriter(windowFile);

            //Write the data to those files, for retrieval later
            for (String request : requests)
                writer.write(request + ",");
            for(int count : requestCount)
                writer2.write(count + ",");
            for(long time: times)
                writer3.write(time + ",");
            for(long time: window)
                writer4.write(time + ",");

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}