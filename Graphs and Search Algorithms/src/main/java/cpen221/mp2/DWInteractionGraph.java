package cpen221.mp2;

import java.io.*;
import java.util.*;

/**
 * DWInteractionGraph represents an immutable interaction graph
 * contains email interactions between users that send emails and users that receive emails
 * with time in seconds to represent when a user has sent and received an email. User activity
 * is captured separately (ex. users that are sending and users that are receiving are differentiated apart).
 */

public class DWInteractionGraph {

    /* ------- Task 1 ------- */
    /* Building the Constructors */

    private String fileName;
    private int numPeople;
    private int numTransactions;
    private int countNotAdded;
    private int[][] emails;
    private int[][] transactionCopy;
    private final List<Integer> BFSList = new ArrayList<>();
    private final List<Integer> DFSList = new ArrayList<>();

    //Representation Invariant (RI):
    //emails is not null and contain no null values. Requires that userIDs are whole numbers greater than or equal to 0.
    //input file has no values less than 0

    //Abstraction Function (AF):
    //AF(r) = stores email interactions between numbered users that satisfy given conditions (e.g. emails sent within a time window,
    //emails sent by certain users, or no condition)

    private void DWCheckRep() {
        for(int i = 0; i < numPeople; i++) {
            for(int j = 0; j < numPeople; j++) {
                if(emails[i][j] < 0)
                    throw new RuntimeException ("userID needs to be a whole number, greater than or equal to 0");
            }
        }
    }

    /**
     * Creates a new DWInteractionGraph using an email interaction file.
     * The email interaction file will be in the resources directory.
     *
     * @param fileName the name of the file in the resources
     *                 directory containing email interactions
     */
    public DWInteractionGraph(String fileName) {
        this.fileName = fileName;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            int max = 0;
            numTransactions = 0;
            int transColIndex;
            int transRowIndex = 0;

            for (String nextLine = reader.readLine(); nextLine != null; nextLine = reader.readLine())
                numTransactions++;

            transactionCopy = new int[numTransactions][3];
            for (int[] row: transactionCopy)
                Arrays.fill(row, -1);

            reader = new BufferedReader(new FileReader(fileName));

            for (String nextLine = reader.readLine(); nextLine != null; nextLine = reader.readLine()) {
                int firstNum = 0;
                int secondNum = 0;
                int curIndex = 0;
                int firstNumIndex = 0;
                int secondNumIndex = 0;
                int numSpaces = 0;
                transColIndex = 0;

                while(curIndex < nextLine.length()) {
                    if(nextLine.charAt(curIndex) == ' ' && firstNumIndex == 0) {
                        firstNum = Integer.parseInt(nextLine.substring(firstNumIndex, curIndex));
                        firstNumIndex = curIndex + 1;
                        numSpaces = 1;

                        transactionCopy[transRowIndex][transColIndex] = firstNum;
                        transColIndex++;
                    }
                    else if(nextLine.charAt(curIndex) == ' ' && firstNumIndex != 0) {
                        secondNum = Integer.parseInt(nextLine.substring(firstNumIndex, curIndex));
                        secondNumIndex = curIndex + 1;
                        numSpaces = 2;

                        transactionCopy[transRowIndex][transColIndex] = secondNum;
                        transColIndex++;
                    }
                    curIndex++;
                }
                if (numSpaces == 2)
                    transactionCopy[transRowIndex][transColIndex] = Integer.parseInt(nextLine.substring(secondNumIndex, curIndex));
                if(firstNum > max)
                    max = firstNum;
                else if(secondNum > max)
                    max = secondNum;
                transRowIndex++;
            }
            numPeople = max+1;
            emails = new int[numPeople][numPeople];
            for(int[] row: emails)
                Arrays.fill(row, 0);
            reader = new BufferedReader(new FileReader(fileName));

            for (String nextLine = reader.readLine(); nextLine != null; nextLine = reader.readLine()) {
                int firstNum = 0;
                int secondNum = 0;
                int curIndex = 0;
                int firstNumIndex = 0;
                while(curIndex < nextLine.length()) {
                    if(nextLine.charAt(curIndex) == ' ' && firstNumIndex == 0) {
                        firstNum = Integer.parseInt(nextLine.substring(firstNumIndex, curIndex));
                        firstNumIndex = curIndex + 1;
                    }
                    else if(nextLine.charAt(curIndex) == ' ' && firstNumIndex != 0)
                        secondNum = Integer.parseInt(nextLine.substring(firstNumIndex, curIndex));
                    curIndex++;
                }
                emails[firstNum][secondNum]++;
            }
            DWCheckRep();
        } catch (IOException ioe) {
            System.out.println("Problem reading file");
        }
    }

    /**
     * Creates a new DWInteractionGraph using an email interaction file
     * and considering a time window filter.
     *
     * @param fileName the name of the file in the resources
     *                 directory containing email interactions
     * @param timeFilter an integer array of length 2: [t0, t1]
     *                   where t0 <= t1. The created DWInteractionGraph
     *                   should only include those emails in the input
     *                   DWInteractionGraph with send time t in the
     *                   t0 <= t <= t1 range.
     */
    public DWInteractionGraph(String fileName, int[] timeFilter) {
        this.fileName = fileName;
        try {
            numTransactions = 0;
            int transColIndex;
            int transRowIndex = 0;
            int time = -1;
            int transactionTime;
            int max = 0;
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            for(String nextLine = reader.readLine(); nextLine != null; nextLine = reader.readLine())
                numTransactions++;

            transactionCopy = new int[numTransactions][3];
            for(int[] row: transactionCopy)
                Arrays.fill(row, -1);
            reader = new BufferedReader(new FileReader(fileName));
            for (String nextLine = reader.readLine(); nextLine != null; nextLine = reader.readLine()) {
                int firstNum = 0;
                int secondNum = 0;
                int curIndex = 0;
                int firstNumIndex = 0;
                int secondNumIndex = 0;
                int numSpaces = 0;
                transColIndex = 0;

                while(curIndex < nextLine.length()) {
                    if(nextLine.charAt(curIndex) == ' ' && firstNumIndex == 0) {
                        firstNum = Integer.parseInt(nextLine.substring(firstNumIndex, curIndex));
                        firstNumIndex = curIndex + 1;
                        numSpaces = 1;
                        transColIndex++;
                    }
                    else if(nextLine.charAt(curIndex) == ' ' && firstNumIndex != 0) {
                        secondNum = Integer.parseInt(nextLine.substring(firstNumIndex, curIndex));
                        //Increment by 1 and 2 to find the number right after
                        time = Integer.parseInt(nextLine.substring(curIndex+1));
                        secondNumIndex = curIndex + 1;
                        numSpaces = 2;
                        transColIndex++;
                    }
                    curIndex++;
                }
                if (numSpaces == 2){
                    transactionTime = Integer.parseInt(nextLine.substring(secondNumIndex, curIndex));
                    if (timeFilter[0] <= transactionTime && transactionTime <= timeFilter[1]){
                        transactionCopy[transRowIndex][transColIndex-2] = firstNum;
                        transactionCopy[transRowIndex][transColIndex-1] = secondNum;
                        transactionCopy[transRowIndex][transColIndex] = transactionTime;
                    }
                }
                transRowIndex++;

                if(firstNum > max)
                    max = firstNum;
                else if(secondNum > max)
                    max = secondNum;
            }
            numPeople = max+1;
            emails = new int[numPeople][numPeople];
            for(int[] row: emails)
                Arrays.fill(row, 0);

            reader = new BufferedReader(new FileReader(fileName));
            int firstNum = 0;
            int secondNum = 0;
            for (String nextLine = reader.readLine(); nextLine != null; nextLine = reader.readLine()) {
                int curIndex = 0;
                int firstNumIndex = 0;
                while(curIndex < nextLine.length()) {
                    if(nextLine.charAt(curIndex) == ' ' && firstNumIndex == 0) {
                        firstNum = Integer.parseInt(nextLine.substring(firstNumIndex, curIndex));
                        firstNumIndex = curIndex + 1;
                    }
                    else if(nextLine.charAt(curIndex) == ' ' && firstNumIndex != 0) {
                        secondNum = Integer.parseInt(nextLine.substring(firstNumIndex, curIndex));
                        time = Integer.parseInt(nextLine.substring(curIndex+1));
                    }
                    curIndex++;
                }
                if(time >= timeFilter[0] && time <= timeFilter[1])
                    emails[firstNum][secondNum]++;
            }
            DWCheckRep();
        } catch (IOException ioe) {
            System.out.println("Problem reading file");
        }
    }

    /**
     * Creates a new DWInteractionGraph from a DWInteractionGraph object
     * and considering a time window filter.
     *
     * @param inputDWIG a DWInteractionGraph object
     * @param timeFilter an integer array of length 2: [t0, t1]
     *                   where t0 <= t1. The created DWInteractionGraph
     *                   should only include those emails in the input
     *                   DWInteractionGraph with send time t in the
     *                   t0 <= t <= t1 range.
     */
    public DWInteractionGraph(DWInteractionGraph inputDWIG, int[] timeFilter) {
        try {
            numTransactions = 0;
            int transColIndex;
            int transRowIndex = 0;

            BufferedReader reader = new BufferedReader(new FileReader(inputDWIG.fileName));

            for (String nextLine = reader.readLine(); nextLine != null; nextLine = reader.readLine()){
                numTransactions++;
            }
            transactionCopy = new int[numTransactions][3];
            for (int[] row: transactionCopy)
                Arrays.fill(row, -1);

            numPeople = inputDWIG.numPeople;
            emails = new int[numPeople][numPeople];
            for(int[] row: emails)
                Arrays.fill(row, 0);

            reader = new BufferedReader(new FileReader(inputDWIG.fileName));
            int firstNum = 0;
            int secondNum = 0;
            int time = -1;
            int transactionTime;

            for (String nextLine = reader.readLine(); nextLine != null; nextLine = reader.readLine()) {
                int curIndex = 0;
                int firstNumIndex = 0;
                int secondNumIndex = 0;
                int numSpaces = 0;
                transColIndex = 0;

                while(curIndex < nextLine.length()) {
                    if(nextLine.charAt(curIndex) == ' ' && firstNumIndex == 0) {
                        firstNum = Integer.parseInt(nextLine.substring(firstNumIndex, curIndex));
                        firstNumIndex = curIndex + 1;
                        numSpaces = 1;
                        transColIndex++;
                    }
                    else if(nextLine.charAt(curIndex) == ' ' && firstNumIndex != 0) {
                        secondNum = Integer.parseInt(nextLine.substring(firstNumIndex, curIndex));
                        //Increment by 1 and 2 to find the number right after
                        time = Integer.parseInt(nextLine.substring(curIndex+1));
                        secondNumIndex = curIndex + 1;
                        numSpaces = 2;
                        transColIndex++;
                    }
                    curIndex++;
                }
                if (numSpaces == 2){
                    transactionTime = Integer.parseInt(nextLine.substring(secondNumIndex, curIndex));
                    if (timeFilter[0] <= transactionTime && transactionTime <= timeFilter[1]){
                        transactionCopy[transRowIndex][transColIndex-2] = firstNum;
                        transactionCopy[transRowIndex][transColIndex-1] = secondNum;
                        transactionCopy[transRowIndex][transColIndex] = transactionTime;
                    }
                }
                transRowIndex++;
                if(time >= timeFilter[0] && time <= timeFilter[1])
                    emails[firstNum][secondNum]++;
            }
            DWCheckRep();
        } catch (IOException ioe) {
            System.out.println("Problem reading file");
        }
    }

    /**
     * Creates a new DWInteractionGraph from a DWInteractionGraph object
     * and considering a list of User IDs.
     *
     * @param inputDWIG a DWInteractionGraph object
     * @param userFilter a List of User IDs. The created DWInteractionGraph
     *                   should exclude those emails in the input
     *                   DWInteractionGraph for which neither the sender
     *                   nor the receiver exist in userFilter.
     */
    public DWInteractionGraph(DWInteractionGraph inputDWIG, List<Integer> userFilter) {
        try {
            numTransactions = 0;
            int transColIndex;
            int transRowIndex = 0;
            BufferedReader reader = new BufferedReader(new FileReader(inputDWIG.fileName));

            for (String nextLine = reader.readLine(); nextLine != null; nextLine = reader.readLine()){
                numTransactions++;
            }
            transactionCopy = new int[numTransactions][3];
            for (int[] row: transactionCopy)
                Arrays.fill(row, -1);

            numPeople = inputDWIG.numPeople;
            emails = new int[numPeople][numPeople];
            for(int i = 0; i < numPeople; i++) {
                for(int j = 0; j < numPeople; j++) {
                    emails[i][j] = 0;
                }
            }
            reader = new BufferedReader(new FileReader(inputDWIG.fileName));
            int firstNum = 0;
            int secondNum = 0;
            for (String nextLine = reader.readLine(); nextLine != null; nextLine = reader.readLine()) {
                int curIndex = 0;
                int firstNumIndex = 0;
                int secondNumIndex = 0;
                int numSpaces = 0;
                transColIndex = 0;

                while(curIndex < nextLine.length()) {
                    if(nextLine.charAt(curIndex) == ' ' && firstNumIndex == 0) {
                        firstNum = Integer.parseInt(nextLine.substring(firstNumIndex, curIndex));
                        firstNumIndex = curIndex + 1;
                        numSpaces = 1;
                        transColIndex++;
                    }
                    else if(nextLine.charAt(curIndex) == ' ' && firstNumIndex != 0){
                        secondNum = Integer.parseInt(nextLine.substring(firstNumIndex, curIndex));
                        secondNumIndex = curIndex + 1;
                        numSpaces = 2;
                        transColIndex++;
                    }
                    curIndex++;
                }
                if (numSpaces == 2){
                    if(userFilter.contains(firstNum) || userFilter.contains(secondNum)){
                        transactionCopy[transRowIndex][transColIndex-2] = firstNum;
                        transactionCopy[transRowIndex][transColIndex-1] = secondNum;
                        transactionCopy[transRowIndex][transColIndex] = Integer.parseInt(nextLine.substring(secondNumIndex, curIndex));
                    }
                }
                transRowIndex++;
                if(userFilter.contains(firstNum) || userFilter.contains(secondNum))
                    emails[firstNum][secondNum]++;
            }
            DWCheckRep();
        } catch (IOException ioe) {
            System.out.println("Problem reading file");
        }
    }

    /**
     * @return a Set of Integers, where every element in the set is a User ID
     * in this DWInteractionGraph.
     */
    public Set<Integer> getUserIDs() {
        Set<Integer> users = new HashSet<>();
        for(int i = 0; i < numPeople; i++) {
            for(int j = 0; j < numPeople; j++) {
                if(emails[i][j] >= 1) {
                    users.add(i);
                    users.add(j);
                }
            }
        }
        return users;
    }

    /**
     * @param sender the User ID of the sender in the email transaction.
     * @param receiver the User ID of the receiver in the email transaction.
     * @return the number of emails sent from the specified sender to the specified
     *         receiver in this DWInteractionGraph.
     */
    public int getEmailCount(int sender, int receiver) {
        int copy = emails[sender][receiver];
        return copy;
    }

    /* ------- Task 2 ------- */

    /**
     * Given an int array, [t0, t1], reports email transaction details.
     * Suppose an email in this graph is sent at time t, then all emails
     * sent where t0 <= t <= t1 are included in this report.
     *
     * @param timeWindow is an int array of size 2 [t0, t1] where t0<=t1.
     * @return an int array of length 3, with the following structure:
     *         [NumberOfSenders, NumberOfReceivers, NumberOfEmailTransactions]
     */
    public int[] ReportActivityInTimeWindow(int[] timeWindow) {
        int numSenders = 0;
        int numReceivers = 0;
        int numEmails = 0;
        int [] activityInTimeWindow = new int[3];
        List<Integer> senders = new ArrayList<>();
        List<Integer> receivers = new ArrayList<>();

        //go through transactions row by row
        for (int rowIndex = 0; rowIndex < numTransactions; rowIndex++){
            //only care about transactions in timeframe
            if (timeWindow[0] <= transactionCopy[rowIndex][2] && transactionCopy[rowIndex][2] <= timeWindow[1]){
                //first count unique with senders
                if (!senders.contains(transactionCopy[rowIndex][0])){
                    senders.add(transactionCopy[rowIndex][0]);
                    numSenders++;
                }
                //then count unique receivers
                if (!receivers.contains(transactionCopy[rowIndex][1])){
                    receivers.add(transactionCopy[rowIndex][1]);
                    numReceivers++;
                }
                //then count emails
                numEmails++;
            }
        }
        activityInTimeWindow[0] = numSenders;
        activityInTimeWindow[1] = numReceivers;
        activityInTimeWindow[2] = numEmails;
        return activityInTimeWindow;
    }

    /**
     * Given a User ID, reports the specified User's email transaction history.
     *
     * @param userID the User ID of the user for which the report will be
     *               created.
     * @return an int array of length 3 with the following structure:
     *         [NumberOfEmailsSent, NumberOfEmailsReceived, UniqueUsersInteractedWith]
     *         If the specified User ID does not exist in this instance of a graph,
     *         returns [0, 0, 0].
     */
    public int[] ReportOnUser(int userID) {
        int numSent = 0;
        int numReceived = 0;
        int numInteracted;
        List<Integer> uniqueUsers = new ArrayList<>();
        int [] userReport = new int[3];

        //go through each row
        for (int rowIndex = 0; rowIndex < numTransactions; rowIndex++){
            //count sent emails
            if (transactionCopy[rowIndex][0] == userID){
                numSent++;
                if (!uniqueUsers.contains(transactionCopy[rowIndex][1])){
                    uniqueUsers.add(transactionCopy[rowIndex][1]);
                }
            }
            //count received emails
            if (transactionCopy[rowIndex][1] == userID){
                numReceived++;
                if (!uniqueUsers.contains(transactionCopy[rowIndex][0])){
                    uniqueUsers.add(transactionCopy[rowIndex][0]);
                }
            }
        }
        numInteracted = uniqueUsers.size();

        userReport[0] = numSent;
        userReport[1] = numReceived;
        userReport[2] = numInteracted;
        return userReport;
    }

    /**
     * @param N a positive number representing rank. N=1 means the most active.
     * @param interactionType Represent the type of interaction to calculate the rank for
     *                        Can be SendOrReceive.Send or SendOrReceive.RECEIVE
     * @return the User ID for the Nth most active user in specified interaction type.
     *         Sorts User IDs by their number of sent or received emails first. In the case of a
     *         tie, secondarily sorts the tied User IDs in ascending order.
     */
    public int NthMostActiveUser(int N, SendOrReceive interactionType) {
        Map<Integer,Integer> mapSend = new HashMap<>();
        Map<Integer,Integer> mapReceive = new HashMap<>();
        List<Integer> outgoing = new ArrayList<>();
        List<Integer> sendNoRepeats = new ArrayList<>();
        List<Integer> incoming = new ArrayList<>();
        List<Integer> receiveNoRepeats = new ArrayList<>();
        List<Integer> userIDs = new ArrayList<>();
        List<Integer> sendRank = new ArrayList<>();
        List<Integer> receiveRank = new ArrayList<>();

        for(int copyOut = 0; copyOut < numTransactions; copyOut++) {
            outgoing.add(transactionCopy[copyOut][0]);
            incoming.add(transactionCopy[copyOut][1]);
        }
        outgoing.removeAll(Collections.singleton(-1));
        incoming.removeAll(Collections.singleton(-1));

        for (Integer integer : outgoing) {
            if (!(sendNoRepeats.contains(integer)))
                sendNoRepeats.add(integer);
            if (!(userIDs.contains(integer)))
                userIDs.add(integer);
        }
        for (Integer integer : incoming) {
            if (!(receiveNoRepeats.contains(integer)))
                receiveNoRepeats.add(integer);
        }
        Collections.sort(sendNoRepeats);
        Collections.sort(receiveNoRepeats);
        Collections.sort(userIDs);

        if(interactionType == SendOrReceive.SEND) {
            typeSendHelper(sendNoRepeats, outgoing, mapSend, sendRank);
        }

        if(interactionType == SendOrReceive.RECEIVE) {
            typeReceiveHelper(receiveNoRepeats, incoming, mapReceive, receiveRank);
        }

        if(interactionType == SendOrReceive.SEND) {
            if(N-1 >= sendRank.size())
                return -1;
            return sendRank.get(N-1);
        }

        if(interactionType == SendOrReceive.RECEIVE) {
            if(N-1 >= receiveRank.size())
                return -1;
            return receiveRank.get(N-1);
        }
        return -1;
    }

    /**
     * If the interactionType is SEND, helps order the userIDs sending activity from highest activity to lowest activity
     *
     * @param sendNoRepeats an ArrayList that contains all userIDs that are sending emails but has no repeated userIDs
     * @param outgoing an ArrayList that contains all userIDs that are sending emails (can contain repeated userIDs)
     * @param mapSend a HashMap that contains userIDs and how many emails they have sent
     * @param sendRank an ArrayList that ranks users' based on their email sending activity (ranks from highest activity to lowest activity)
     */
    private void typeSendHelper(List<Integer> sendNoRepeats, List<Integer> outgoing, Map<Integer,Integer> mapSend, List<Integer> sendRank){
        int userID;
        int scanIDs;
        int activity;
        int highestActivity = -1;
        int tempUser = -1;

        for(scanIDs = 0; scanIDs < sendNoRepeats.size(); scanIDs++) {
            userID = sendNoRepeats.get(scanIDs);
            for(int iterSend = 0; iterSend < outgoing.size(); iterSend++) {
                if(!(mapSend.containsKey(userID)))
                    mapSend.put(userID, 0);
            }
        }
        for (Integer ids : outgoing) {
            if (mapSend.containsKey(ids))
                mapSend.put(ids, mapSend.get(ids) + 1);
        }

        for(int iterSendLoop = 0; iterSendLoop < sendNoRepeats.size(); iterSendLoop++) {
            activity = mapSend.get(sendNoRepeats.get(iterSendLoop));

            if(sendRank.size() != sendNoRepeats.size()) {
                if(activity > highestActivity) {
                    highestActivity = activity;
                    tempUser = sendNoRepeats.get(iterSendLoop);
                }
                if(iterSendLoop == sendNoRepeats.size() - 1) {
                    sendRank.add(tempUser);
                    mapSend.put(tempUser, -1);
                    iterSendLoop = -1;
                    highestActivity = -5;
                }
            }
        }
    }

    /**
     * If the interactionType is RECEIVE, helps order the userIDs receiving activity from highest activity to lowest activity
     *
     * @param receiveNoRepeats an ArrayList that contains all userIDs that are receiving emails but has no repeated userIDs
     * @param incoming an ArrayList that contains all userIDs that are receiving emails (can contain repeated userIDs)
     * @param mapReceive a HashMap that contains userIDs and how many emails they have received
     * @param receiveRank an ArrayList that ranks users' based on their email receiving activity (ranks from highest activity to lowest activity)
     */
    private void typeReceiveHelper(List<Integer> receiveNoRepeats, List<Integer> incoming, Map<Integer,Integer> mapReceive, List<Integer> receiveRank){
        int scanIDs;
        int userID;
        int activity;
        int highestActivity = -1;
        int tempUser = -1;

        for(scanIDs = 0; scanIDs < receiveNoRepeats.size(); scanIDs++) {
            userID = receiveNoRepeats.get(scanIDs);
            for(int iterSend = 0; iterSend < incoming.size(); iterSend++) {
                if(!(mapReceive.containsKey(userID)))
                    mapReceive.put(userID, 0);
            }
        }
        for (Integer integer : incoming) {
            userID = integer;
            if (mapReceive.containsKey(userID))
                mapReceive.put(userID, mapReceive.get(userID) + 1);
        }
        for(int iterReceiveLoop = 0; iterReceiveLoop < receiveNoRepeats.size(); iterReceiveLoop++) {
            activity = mapReceive.get(receiveNoRepeats.get(iterReceiveLoop));

            if(receiveRank.size() != receiveNoRepeats.size()) {
                if(activity > highestActivity){
                    highestActivity = activity;
                    tempUser = receiveNoRepeats.get(iterReceiveLoop);
                }

                if(iterReceiveLoop == receiveNoRepeats.size() - 1) {
                    receiveRank.add(tempUser);
                    mapReceive.put(tempUser, -1);
                    iterReceiveLoop = -1;
                    highestActivity = -5;
                }
            }
        }
    }

    /* ------- Task 3 ------- */

    /**
     * performs breadth first search on the DWInteractionGraph object
     * to check path between user with userID1 and user with userID2.
     *
     * @param userID1 the user ID for the first user
     * @param userID2 the user ID for the second user
     * @return if a path exists, returns aa list of user IDs
     *         in the order encountered in the search.
     *         if no path exists, should return null.
     */
    public List<Integer> BFS(int userID1, int userID2) {
        int size = BFSList.size(); //first time: size = 0
        ArrayList<Integer> returnList = new ArrayList<>();
        List<Integer> dummy = new ArrayList<>();
        boolean found = false;
        BFSList.add(userID1);

        dummy = bfSearch(userID1, userID2, size);

        //create a copy of BFSList
        for (Integer integer : BFSList) {
            if (!found)
                returnList.add(integer);
            if (integer == userID2)
                found = true;
        }
        if (!BFSList.contains(userID2))
            return null;
        return (ArrayList<Integer>) returnList.clone();
    }

    /**
     * performs breadth first search on the DWInteractionGraph object
     * to check path between user with userID1 and user with userID2.
     *
     * @param user1 the user ID for the first user
     * @param user2 the user ID for the second user
     * @return if a path exists, returns aa list of user IDs
     *         in the order encountered in the search.
     *         if no path exists, returns null.
     */
    private List<Integer> bfSearch(int user1, int user2, int start) {
        List<Integer> returnList;
        //look for people that user1 sent emails to
        for (int j = 0; j < numPeople; j++) {
            //add node if not explored yet
            if ((emails[user1][j] > 0) && (!BFSList.contains(j))) {
                BFSList.add(j);

                //return if path to user2 has been found
                if (BFSList.contains(user2)) {
                    returnList = BFSList;
                    return (returnList);
                }
            }
        }
        if (start < BFSList.size())
            bfSearch(BFSList.get(start), user2, start + 1);
        return null;
    }

    /**
     * performs depth first search on the DWInteractionGraph object
     * to check path between user with userID1 and user with userID2.
     *
     * @param userID1 the user ID for the first user
     * @param userID2 the user ID for the second user
     * @return if a path exists, returns a list of user IDs
     *         in the order encountered in the search.
     *         if no path exists, should return null.
     */
    public List<Integer> DFS(int userID1, int userID2) {
        boolean found = false;
        List<Integer> returnList = new ArrayList<>();
        List<Integer> dummy = new ArrayList<>();
        DFSList.add(userID1);

        dummy = dfSearch(userID1, userID2);

        for (Integer integer : DFSList) {
            if (!found)
                returnList.add(integer);
            if (integer == userID2)
                found = true;
        }

        if (!DFSList.contains(userID2))
            return null;

        return returnList;
    }

    /**
     * performs depth first search on the DWInteractionGraph object
     * to check path between user with userID1 and user with userID2.
     *
     * @param user1 the user ID for the first user
     * @param user2 the user ID for the second user
     * @return if a path exists, returns a list of user IDs
     *         in the order encountered in the search.
     *         if no path exists, returns null.
     */
    private List<Integer> dfSearch(int user1, int user2) {
        List<Integer> returnList;
        boolean added = false;

        //record who user1 sent emails to
        for (int j = 0; j < numPeople; j++) {
            //recursively search the next unexplored node
            if ((emails[user1][j] > 0) && (!DFSList.contains(j))) {
                DFSList.add(j);
                added = true;
                countNotAdded = 0;
                //return if path to user2 has been found
                if (DFSList.contains(user2)) {
                    returnList = DFSList;
                    return returnList;
                }
                dfSearch(j, user2);
            }
        }
        if (!added) {
            //dead end reached. retrace steps
            countNotAdded++;
            if (countNotAdded > DFSList.size())
                return null;
            dfSearch(DFSList.get(DFSList.size() - countNotAdded), user2);
        }

        return null;
    }
    /* ------- Task 4 ------- */

    /**
     * Returns maximum number of infected people if one person in the file is infected with a virus first, and infects
     * others by sending them emails
     *
     * @param hours the number of hours until the virus is activated
     * @return the maximum number of users that can be polluted in N hours
     */
    public int MaxBreachedUserCount(int hours) {
        if(hours == 0)
            return 1;
        //3600 seconds in an hour
        int timeInSec = hours * 3600;
        int receiver;
        int sender;
        int maxRow = transactionCopy.length - 1;
        int max = 0;
        Set<Integer> relevantUsers = getUserIDs();
        Integer[] users = new Integer[relevantUsers.size()];
        relevantUsers.toArray(users);

        for(int i = 0; i < transactionCopy.length; i++) {
            if(transactionCopy[i][2] > timeInSec) {
                maxRow = i - 1;
                break;
            }
        }
        //Loop through all users who have sent or received emails, ignoring the rest
        for (Integer user : users) {
            Set<Integer> path = new HashSet<>();
            //Find # of infected people for each sender
            sender = user;
            for (int j = 0; j <= maxRow; j++) {
                if (transactionCopy[j][0] == sender) {
                    int startRow = j;
                    receiver = transactionCopy[j][1];
                    path.add(sender);
                    path.add(receiver);
                    while (startRow > 0 && transactionCopy[startRow - 1][2] == transactionCopy[startRow][2]) {
                        startRow--;
                    }
                    findPath(path, startRow, maxRow, receiver);
                }
            }
            //Path contains all IDs affected by virus, so path.size() represents # of infected people
            if (path.size() > max)
                max = path.size();
        }
        return max;
    }

    /**
     * Adds all newly infected people to path
     *
     * @param path Set of all infected people
     * @param startRow the row to start looping from, as checking previous rows would waste time
     * @param maxRow the row to stop looping at, as they are beyond the time scope given
     * @param receiver the person who received the infected email
     */
    private void findPath(Set<Integer> path, int startRow, int maxRow, int receiver) {
        for(int i = startRow; i <= maxRow; i++) {
            if(transactionCopy[i][0] == receiver) {
                //Recursively add each recipient of virus to set
                path.add(transactionCopy[i][1]);
                findPath(path, i, maxRow, transactionCopy[i][1]);
            }
        }
    }
}
