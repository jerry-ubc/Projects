package cpen221.mp2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * UDWInteractionGraph represents an immutable interaction graph
 * contains email interactions between users that send emails and users that receive emails
 * with time in seconds to represent when a user has sent and received an email. User activity
 * is captured together (ex. users that are sending and users that are receiving are not differentiated apart).
 */

public class UDWInteractionGraph {

    /* ------- Task 1 ------- */
    /* Building the Constructors */
    private String fileName;
    private int numPeople;
    private int numTransactions;
    private int countNotAdded;
    private int[][] emails;
    private int[][] mirroredEmails;
    private int[][] transactionCopy;
    private List<Integer> DFSList = new ArrayList<>();

    //Representation Invariant (RI):
    //emails contain no null values are not null. Requires that userIDs are whole numbers that are greater than or equal to 0.

    //Abstraction Function (AF);
    //AF(r) = stores email interactions between numbered users that satisfy given conditions (e.g. emails sent within a time window,
    //emails sent by certain users, or no condition)

    private void UDWCheckRep() {
        for(int i = 0; i < numPeople; i++){
            for(int j = 0; j < numPeople; j++){
                if(emails[i][j] < 0)
                    throw new RuntimeException ("userID needs to be a whole number, greater than or equal to 0");
            }
        }
    }

    /**
     * Creates a new UDWInteractionGraph using an email interaction file.
     * The email interaction file will be in the resources directory.
     *
     * @param fileName the name of the file in the resources
     *                 directory containing email interactions
     */
    public UDWInteractionGraph(String fileName) {
        this.fileName = fileName;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            int max = 0;
            numTransactions = 0;
            int transColIndex;
            int transRowIndex = 0;

            for (String nextLine = reader.readLine(); nextLine != null; nextLine = reader.readLine()) {
                numTransactions++;
            }
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
                while (curIndex < nextLine.length()) {
                    if (nextLine.charAt(curIndex) == ' ' && firstNumIndex == 0) {
                        firstNum = Integer.parseInt(nextLine.substring(firstNumIndex, curIndex));
                        firstNumIndex = curIndex + 1;
                        numSpaces = 1;
                        transactionCopy[transRowIndex][transColIndex] = firstNum;
                        transColIndex++;
                    } else if (nextLine.charAt(curIndex) == ' ' && firstNumIndex != 0) {
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
                if (firstNum > max)
                    max = firstNum;
                if (secondNum > max)
                    max = secondNum;
                transRowIndex++;
            }
            numPeople = max+1;
            emails = new int[numPeople][numPeople];
            for(int[] row: emails)
                Arrays.fill(row, 0);

            reader = new BufferedReader(new FileReader(fileName));
            int firstNum = 0;
            int secondNum = 0;
            int smallerNum;
            int largerNum;
            for (String nextLine = reader.readLine(); nextLine != null; nextLine = reader.readLine()) {
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
                if(firstNum < secondNum) {
                    smallerNum = firstNum;
                    largerNum = secondNum;
                }
                else if(secondNum < firstNum) {
                    smallerNum = secondNum;
                    largerNum = firstNum;
                }
                else {
                    smallerNum = firstNum;
                    largerNum = firstNum;
                }
                emails[smallerNum][largerNum]++;
            }
            UDWCheckRep();
        }
        catch (IOException ioe) {
            System.out.println("Problem reading file");
        }
    }

    /**
     * Creates a new UDWInteractionGraph using an email interaction file
     * and considering a time window filter.
     *
     * @param fileName the name of the file in the resources
     *                 directory containing email interactions
     * @param timeFilter an integer array of length 2: [t0, t1]
     *                   where t0 <= t1. The created UDWInteractionGraph
     *                   should only include those emails in the input
     *                   UDWInteractionGraph with send time t in the
     *                   t0 <= t <= t1 range.
     */
    public UDWInteractionGraph(String fileName, int[] timeFilter) {
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
                while (curIndex < nextLine.length()) {
                    if (nextLine.charAt(curIndex) == ' ' && firstNumIndex == 0) {
                        firstNum = Integer.parseInt(nextLine.substring(firstNumIndex, curIndex));
                        firstNumIndex = curIndex + 1;
                        numSpaces = 1;
                        transactionCopy[transRowIndex][transColIndex] = firstNum;
                        transColIndex++;
                    } else if (nextLine.charAt(curIndex) == ' ' && firstNumIndex != 0) {
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
                if (firstNum > max)
                    max = firstNum;
                if (secondNum > max)
                    max = secondNum;
                transRowIndex++;
            }
            numPeople = max+1;
            emails = new int[numPeople][numPeople];
            for(int[] row: emails)
                Arrays.fill(row, 0);

            reader = new BufferedReader(new FileReader(fileName));
            int firstNum = 0;
            int secondNum = 0;
            int smallerNum;
            int largerNum;
            int time = -1;
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
                if(firstNum < secondNum) {
                    smallerNum = firstNum;
                    largerNum = secondNum;
                }
                else if(secondNum < firstNum) {
                    smallerNum = secondNum;
                    largerNum = firstNum;
                }
                else {
                    smallerNum = firstNum;
                    largerNum = firstNum;
                }
                if(time >= timeFilter[0] && time <= timeFilter[1])
                    emails[smallerNum][largerNum]++;
            }
            UDWCheckRep();
        } catch (IOException ioe) {
            System.out.println("Problem reading file");
        }
    }

    /**
     * Creates a new UDWInteractionGraph from a UDWInteractionGraph object
     * and considering a time window filter.
     *
     * @param inputUDWIG a UDWInteractionGraph object
     * @param timeFilter an integer array of length 2: [t0, t1]
     *                   where t0 <= t1. The created UDWInteractionGraph
     *                   should only include those emails in the input
     *                   UDWInteractionGraph with send time t in the
     *                   t0 <= t <= t1 range.
     */
    public UDWInteractionGraph(UDWInteractionGraph inputUDWIG, int[] timeFilter) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputUDWIG.fileName));
            numTransactions = 0;
            int transColIndex;
            int transRowIndex = 0;

            for (String nextLine = reader.readLine(); nextLine != null; nextLine = reader.readLine())
                numTransactions++;

            transactionCopy = new int[numTransactions][3];
            for (int[] row: transactionCopy)
                Arrays.fill(row, -1);

            numPeople = inputUDWIG.numPeople;
            emails = new int[numPeople][numPeople];
            for(int[] row: emails)
                Arrays.fill(row, 0);

            reader = new BufferedReader(new FileReader(inputUDWIG.fileName));
            int firstNum = 0;
            int secondNum = 0;
            int largerNum;
            int smallerNum;
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
                if (numSpaces == 2) {
                    transactionTime = Integer.parseInt(nextLine.substring(secondNumIndex, curIndex));
                    if (timeFilter[0] <= transactionTime && transactionTime <= timeFilter[1]){
                        transactionCopy[transRowIndex][transColIndex-2] = firstNum;
                        transactionCopy[transRowIndex][transColIndex-1] = secondNum;
                        transactionCopy[transRowIndex][transColIndex] = transactionTime;
                    }
                }
                transRowIndex++;
                if(firstNum < secondNum) {
                    smallerNum = firstNum;
                    largerNum = secondNum;
                }
                else if(secondNum < firstNum) {
                    smallerNum = secondNum;
                    largerNum = firstNum;
                }
                else {
                    smallerNum = firstNum;
                    largerNum = firstNum;
                }
                if(time >= timeFilter[0] && time <= timeFilter[1])
                    emails[smallerNum][largerNum]++;
            }
            UDWCheckRep();
        }
        catch (IOException ioe) {
            System.out.println("Problem reading file");
        }
    }

    /**
     * Creates a new UDWInteractionGraph from a UDWInteractionGraph object
     * and considering a list of User IDs.
     *
     * @param inputUDWIG a UDWInteractionGraph object
     * @param userFilter a List of User IDs. The created UDWInteractionGraph
     *                   should exclude those emails in the input
     *                   UDWInteractionGraph for which neither the sender
     *                   nor the receiver exist in userFilter.
     */
    public UDWInteractionGraph(UDWInteractionGraph inputUDWIG, List<Integer> userFilter) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputUDWIG.fileName));
            numTransactions = 0;
            int transColIndex;
            int transRowIndex = 0;
            for (String nextLine = reader.readLine(); nextLine != null; nextLine = reader.readLine())
                numTransactions++;

            transactionCopy = new int[numTransactions][3];
            for (int[] row: transactionCopy)
                Arrays.fill(row, -1);

            numPeople = inputUDWIG.numPeople;
            emails = new int[numPeople][numPeople];
            for(int[] row: emails)
                Arrays.fill(row, 0);

            reader = new BufferedReader(new FileReader(inputUDWIG.fileName));
            int firstNum = 0;
            int secondNum = 0;
            int largerNum;
            int smallerNum;
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
                        secondNumIndex = curIndex + 1;
                        numSpaces = 2;
                        transColIndex++;
                    }
                    curIndex++;
                }
                if (numSpaces == 2) {
                    if(userFilter.contains(firstNum) || userFilter.contains(secondNum)){
                        transactionCopy[transRowIndex][transColIndex-2] = firstNum;
                        transactionCopy[transRowIndex][transColIndex-1] = secondNum;
                        transactionCopy[transRowIndex][transColIndex] = Integer.parseInt(nextLine.substring(secondNumIndex, curIndex));
                    }
                }
                transRowIndex++;

                if(firstNum < secondNum) {
                    smallerNum = firstNum;
                    largerNum = secondNum;
                }
                else if(secondNum < firstNum) {
                    smallerNum = secondNum;
                    largerNum = firstNum;
                }
                else {
                    smallerNum = firstNum;
                    largerNum = firstNum;
                }
                if(userFilter.contains(firstNum) || userFilter.contains(secondNum))
                    emails[smallerNum][largerNum]++;
            }
            UDWCheckRep();
        } catch (IOException ioe) {
            System.out.println("Problem reading file");
        }
    }

    /**
     * Creates a new UDWInteractionGraph from a DWInteractionGraph object.
     *
     * @param inputDWIG a DWInteractionGraph object
     */
    public UDWInteractionGraph(DWInteractionGraph inputDWIG) {
        Set<Integer> users = inputDWIG.getUserIDs();
        Integer[] userArray = new Integer[users.size()];
        users.toArray(userArray);
        Arrays.sort(userArray);
        int max = -1;
        for (Integer integer : userArray) {
            if (integer > max)
                max = integer;
        }
        numPeople = max + 1;
        emails = new int[numPeople][numPeople];
        for(int[] row: emails)
            Arrays.fill(row, 0);

        int largerNum;
        int smallerNum;
        for(int i = 0; i < numPeople; i++) {
            for(int j = 0; j < numPeople; j++) {
                if(i > j) {
                    largerNum = i;
                    smallerNum = j;
                }
                else if(j > i) {
                    largerNum = j;
                    smallerNum = i;
                }
                else {
                    largerNum = i;
                    smallerNum = i;
                }
                emails[smallerNum][largerNum] += inputDWIG.getEmailCount(i, j);
            }
        }
        UDWCheckRep();
    }

    /**
     * @return a Set of Integers, where every element in the set is a User ID
     * in this UDWInteractionGraph.
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
     * receiver in this DWInteractionGraph.
     */
    public int getEmailCount(int sender, int receiver) {
        int smallerNum;
        int largerNum;
        if(sender > receiver) {
            largerNum = sender;
            smallerNum = receiver;
        }
        else if(receiver > sender) {
            largerNum = receiver;
            smallerNum = sender;
        }
        else {
            largerNum = sender;
            smallerNum = sender;
        }

        return emails[smallerNum][largerNum];
    }

    /* ------- Task 2 ------- */

    /**
     * @param timeWindow is an int array of size 2 [t0, t1]
     *                   where t0<=t1
     * @return an int array of length 2, with the following structure:
     *  [NumberOfUsers, NumberOfEmailTransactions]
     */
    public int[] ReportActivityInTimeWindow(int[] timeWindow) {
        int numSenders = 0;
        int numReceivers = 0;
        int numEmails = 0;
        int [] activityInTimeWindow = new int[2];
        List<Integer> senders = new ArrayList<>();
        List<Integer> receivers = new ArrayList<>();

        //go through transactions row by row
        for (int rowIndex = 0; rowIndex < numTransactions; rowIndex++) {
            //only care about transactions in timeframe
            if (timeWindow[0] <= transactionCopy[rowIndex][2] && transactionCopy[rowIndex][2] <= timeWindow[1]) {
                //first count unique senders
                if (!senders.contains(transactionCopy[rowIndex][0])) {
                    senders.add(transactionCopy[rowIndex][0]);
                    numSenders++;
                }
                //then count unique receivers
                if (!receivers.contains(transactionCopy[rowIndex][1])) {
                    receivers.add(transactionCopy[rowIndex][1]);
                    numReceivers++;
                }
                //then count emails
                numEmails++;
            }
        }
        activityInTimeWindow[0] = numSenders + numReceivers;
        activityInTimeWindow[1] = numEmails;
        return activityInTimeWindow;
    }

    /**
     * @param userID the User ID of the user for which
     *               the report will be created
     * @return an int array of length 2 with the following structure:
     *  [NumberOfEmails, UniqueUsersInteractedWith]
     * If the specified User ID does not exist in this instance of a graph,
     * returns [0, 0].
     */
    public int[] ReportOnUser(int userID) {
        int numSent = 0;
        int numReceived = 0;
        int numInteracted;
        List<Integer> uniqueUsers = new ArrayList<>();
        int [] userReport = new int[3];

        //go through each row
        for (int rowIndex = 0; rowIndex < numTransactions; rowIndex++) {
            //count sent emails
            if (transactionCopy[rowIndex][0] == userID) {
                numSent++;
                if (!uniqueUsers.contains(transactionCopy[rowIndex][1]))
                    uniqueUsers.add(transactionCopy[rowIndex][1]);
            }
            //count received emails
            if (transactionCopy[rowIndex][1] == userID) {
                numReceived++;
                if (!uniqueUsers.contains(transactionCopy[rowIndex][0]))
                    uniqueUsers.add(transactionCopy[rowIndex][0]);
            }
        }
        numInteracted = uniqueUsers.size();
        userReport[0] = numSent + numReceived;
        userReport[1] = numInteracted;
        return userReport;
    }

    /**
     * @param N a positive number representing rank. N=1 means the most active.
     * @return the User ID for the Nth most active user
     */
    public int NthMostActiveUser(int N) {
        HashMap<Integer,Integer> userActivityMap = new HashMap<>();
        List<Integer> sendingUsers = new ArrayList<>();
        List<Integer> receivingUsers = new ArrayList<>();
        List<Integer> userIDs = new ArrayList<>();
        List<Integer> rank = new ArrayList<>();
        int scanIDs;
        int userID;
        int activity;
        int highestActivity = -1;
        int tempUser = 0;

        for(int copySend = 0; copySend < numTransactions; copySend++) {
            sendingUsers.add(transactionCopy[copySend][0]);
            receivingUsers.add(transactionCopy[copySend][1]);
        }

        sendingUsers.removeAll(Collections.singleton(-1));
        receivingUsers.removeAll(Collections.singleton(-1));

        for (Integer sendingUser : sendingUsers) {
            if (!(userIDs.contains(sendingUser)))
                userIDs.add(sendingUser);
        }
        for (Integer receivingUser : receivingUsers) {
            if (!(userIDs.contains(receivingUser))) {
                userIDs.add(receivingUser);
            }
        }
        Collections.sort(userIDs);

        for(scanIDs = 0; scanIDs < userIDs.size(); scanIDs++) {
            int iterAdd;
            userID = userIDs.get(scanIDs);
            for(iterAdd = 0; iterAdd < userIDs.size(); iterAdd++) {
                if(!(userActivityMap.containsKey(userID)))
                    userActivityMap.put(userID, 0);
            }
        }
        for (Integer sendingUser : sendingUsers) {
            userID = sendingUser;
            if (userActivityMap.containsKey(userID))
                userActivityMap.put(userID, userActivityMap.get(userID) + 1);
        }
        for (Integer receivingUser : receivingUsers) {
            userID = receivingUser;
            if (userActivityMap.containsKey(userID))
                userActivityMap.put(userID, userActivityMap.get(userID) + 1);
        }

        for(int iterLoop = 0; iterLoop < userIDs.size(); iterLoop++) {
            activity = userActivityMap.get(userIDs.get(iterLoop));
            if(rank.size() != userIDs.size()) {
                if(activity > highestActivity) {
                    highestActivity = activity;
                    tempUser = userIDs.get(iterLoop);
                }
                if(iterLoop == userIDs.size() - 1) {
                    rank.add(tempUser);
                    userActivityMap.put(tempUser, -1);
                    iterLoop = -1;
                    highestActivity = -5;
                }
            }
        }
        if(N-1 >= rank.size())
            return -1;
        return rank.get(N-1);
    }

    /* ------- Task 3 ------- */

    /**
     * @return the number of completely disjoint graph
     *    components in the UDWInteractionGraph object.
     */
    public int NumberOfComponents() {
        HashMap<Integer, int[]> components = new HashMap<>();
        boolean contains;
        boolean searchAgain = true;
        boolean exist;
        boolean remove = true;
        int dfsSize = -1;
        int hashIndex = 0;

        for (int i = 0; i < numPeople; i++) {
            //check if node is already in a tree, only if it is not the first tree
            while (components.size() != 0 && searchAgain) {
                for (int l = 0; l < components.size(); l++) {
                    //if node is already in a tree, move on
                    contains = false;
                    int[] arrayCopy = components.get(l);

                    for (int m = 0; m < dfsSize; m++) {
                        if (arrayCopy[m] == i)
                            contains = true;
                    }
                    if (contains) {
                        i++;
                        break;
                    }
                    if (l == (components.size() - 1))
                        searchAgain = false;
                }
            }
            searchAgain = true;
            if (i >= numPeople)
                break;

            //create a tree that stems from node i
            DFSList = new ArrayList<>();
            for (int j = 0; j < numPeople; j++) {
                exist = PathExists(i, j);

                if (exist)
                    remove = false;
            }
            if (remove) {
                DFSList.remove(0);
            }
            //make a copy of the list if its not empty
            if (DFSList.size() != 0) {
                int[] dfsCopy = new int[DFSList.size()];
                dfsSize = DFSList.size();
                for(int k = 0; k < DFSList.size(); k++) {
                    dfsCopy[k] = DFSList.get(k);
                }
                //put tree in hashmap
                components.put(hashIndex, dfsCopy);
                hashIndex++;
            }
        }
        int numComponents = components.size();
        return numComponents;
    }

    /**
     * @param userID1 the user ID for the first user
     * @param userID2 the user ID for the second user
     * @return whether a path exists between the two users
     */
    public boolean PathExists(int userID1, int userID2) {
        DFSList = new ArrayList<>();
        mirroredEmails = emails;
        boolean dummy;
        boolean exists;

        //special case: user 1 sends an email to themselves
        if (userID1 == userID2 && emails[userID1][userID1] > 0) {
            DFSList.add(userID1);
            return true;
        }

        for (int i = 0; i < numPeople; i++){
            for (int j = 0; j < numPeople; j++){
                if (mirroredEmails[i][j] == 1)
                    mirroredEmails[j][i] = 1;
            }
        }
        DFSList.add(userID1);
        dummy = dfSearch(userID1, userID2);
        exists = DFSList.contains(userID2);

        if ((userID1 == userID2) && !dummy)
            exists = false;
        return exists;
    }

    /**
     * performs depth first search on the UDWInteractionGraph object
     * to check path between user with userID1 and user with userID2.
     * @param user1 the user ID for the first user
     * @param user2 the user ID for the second user
     * @return if a path exists, returns true.
     *         if no path exists, returns false.
     */
    private boolean dfSearch(int user1, int user2) {
        boolean added = false;
        //look for the people user1 sent emails to
        for (int j = 0; j < numPeople; j++) {
            //recursively search the next unexplored node
            if (((mirroredEmails[user1][j] > 0) || (mirroredEmails[j][user1] > 0)) && (!DFSList.contains(j))) {
                DFSList.add(j);
                added = true;
                countNotAdded = 0;

                //return if path to user2 has been found
                if (DFSList.contains(user2))
                    return true;

                dfSearch(j, user2);
            }
        }
        if (!added) {
            //dead end reached. retrace steps
            countNotAdded++;
            if (countNotAdded > DFSList.size())
                return false;
            dfSearch(DFSList.get(DFSList.size() - countNotAdded), user2);
        }
        return false;
    }
}
