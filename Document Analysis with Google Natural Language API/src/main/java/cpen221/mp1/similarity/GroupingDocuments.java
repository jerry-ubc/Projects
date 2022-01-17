package cpen221.mp1.similarity;

import cpen221.mp1.Document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;



public class GroupingDocuments {

    /* ------- Task 5 - Jerry, Leon, Trevor ------ */

    /**
     * Group documents by similarity
     * @param allDocuments the set of all documents to be considered,
     *                     is not null
     * @param numberOfGroups the number of document groups to be generated
     * @return groups of documents, where each group (set) contains similar
     * documents following this rule: if D_i is in P_x, and P_x contains at
     * least one other document, then P_x contains some other D_j such that
     * the divergence between D_i and D_j is smaller than (or at most equal
     * to) the divergence between D_i and any document that is not in P_x.
     */
    public static Set<Set<cpen221.mp1.Document>> groupBySimilarity(Set<cpen221.mp1.Document> allDocuments, int numberOfGroups) {
        Set<Set<Document>> partitions = new HashSet<>();
        final int PAIR_SIZE = 2;
        final int DECIMAL_SIZE = 10;
        DocumentSimilarity docSimilarity = new DocumentSimilarity();
        cpen221.mp1.Document[] docArray = new cpen221.mp1.Document[allDocuments.size()];
        allDocuments.toArray(docArray);
        int secondDocID;
        int firstDocID;

        if(numberOfGroups == allDocuments.size()) {
            for(int i = 0; i < docArray.length; i++) {
                HashSet<cpen221.mp1.Document> setSizeOne = new HashSet<>();
                setSizeOne.add(docArray[i]);
                partitions.add(setSizeOne);
            }
            return partitions;
        }
        else if (numberOfGroups == 1) {
            HashSet<cpen221.mp1.Document> allDocs = new HashSet<>();
            for(int i = 0; i < docArray.length; i++) {
                allDocs.add(docArray[i]);
            }
            partitions.add(allDocs);
            return partitions;
        }

        int combinationsSize = factorial(allDocuments.size())/factorial(allDocuments.size()-PAIR_SIZE)/PAIR_SIZE;

        String[] combinationNames = new String[combinationsSize];
        double[] divergences = new double[combinationsSize];
        String[] combinationSorted = new String[combinationsSize];
        double[] divergenceSorted = new double[combinationsSize];
        int index = 0;
        for(int i = 0; i < allDocuments.size()-1; i++) {
            for(int j = i; j < allDocuments.size(); j++) {
                if(i != j) {
                    combinationNames[index++] = i + "" + j;
                }
            }
        }
        for(int i = 0; i < combinationsSize; i++) {
            int curNum = Integer.parseInt(combinationNames[i]);
            int tempSize = allDocuments.size();
            int counter = 0;
            int counter2 = 1;
            while(curNum/DECIMAL_SIZE != 0) {
                curNum /= DECIMAL_SIZE;
                counter++;
            }
            while(tempSize/DECIMAL_SIZE != 0) {
                tempSize /= DECIMAL_SIZE;
                counter2++;
            }
            int cap = (int) Math.pow(DECIMAL_SIZE, counter2);
            int denominator = (int) Math.pow(DECIMAL_SIZE, counter);
            if(denominator >= cap) {
                denominator = cap;
            }
            int rememberDenom = denominator;
            if(denominator <= DECIMAL_SIZE)
                //Add one to make the case of 10/10 = 0, we revert denominator later
                denominator = DECIMAL_SIZE + 1;
            firstDocID = Integer.parseInt(combinationNames[i]) / denominator;
            //Revert denominator value
            if (denominator == DECIMAL_SIZE + 1) {
                denominator = rememberDenom;
            }
            secondDocID = Integer.parseInt(combinationNames[i]) % denominator;

            if(secondDocID == 0) {
                secondDocID = Integer.parseInt(combinationNames[i]) - firstDocID * denominator;
            }
            if(Integer.parseInt(combinationNames[i]) == DECIMAL_SIZE) {
                secondDocID = DECIMAL_SIZE;
            }

            double divergence = docSimilarity.documentDivergence(docArray[firstDocID], docArray[secondDocID]);
            divergences[i] = divergence;
        }
        double currentMin = divergences[0];
        int minIndex = 0;
        for(int i = 0; i < combinationsSize; i++) {
            for(int j = 0; j < combinationsSize; j++) {
                if(divergences[j] < currentMin) {
                    currentMin = divergences[j];
                    minIndex = j;
                }
            }
            divergenceSorted[i] = currentMin;
            combinationSorted[i] = combinationNames[minIndex];
            divergences[minIndex] = Integer.MAX_VALUE;
            currentMin = Integer.MAX_VALUE;
        }

        int numPartitions = allDocuments.size();
        int curIndex = 0;
        boolean firstRun = true;
        ArrayList<Set<cpen221.mp1.Document>> partitionList = new ArrayList<>();
        while(numPartitions > numberOfGroups) {
            if(firstRun || allDocuments.size() == numberOfGroups) {
                for(int i = 0; i < numPartitions; i++) {
                    Set<Document> curSet = new HashSet<>();
                    curSet.add(docArray[i]);
                    partitionList.add(curSet);
                    firstRun = false;
                }
            }
            else {
                int curNum = Integer.parseInt(combinationSorted[curIndex]);
                int tempSize = allDocuments.size();
                int counter = 0;
                int counter2 = 1;
                while(curNum/DECIMAL_SIZE != 0) {
                    curNum /= DECIMAL_SIZE;
                    counter++;
                }
                while(tempSize/DECIMAL_SIZE != 0) {
                    tempSize /= DECIMAL_SIZE;
                    counter2++;
                }
                int cap = (int) Math.pow(DECIMAL_SIZE, counter2);
                int denominator = (int) Math.pow(DECIMAL_SIZE, counter);
                if(denominator >= cap) {
                    denominator = cap;
                }
                firstDocID = Integer.parseInt(combinationSorted[curIndex]) / denominator;
                secondDocID = Integer.parseInt(combinationSorted[curIndex]) % denominator;
                int index1 = 0;
                int index2 = 0;
                int size2 = 0;

                for(int i = 0; i < partitionList.size(); i++) {
                    if(partitionList.get(i).contains(docArray[firstDocID]) && !partitionList.get(i).contains(null)) {
                        index1 = i;
                    }
                    if(partitionList.get(i).contains(docArray[secondDocID]) && !partitionList.get(i).contains(null) && i != index1) {
                        index2 = i;
                        size2 = partitionList.get(i).size();
                    }
                }

                cpen221.mp1.Document[] setTwoToArray = new cpen221.mp1.Document[size2];
                for(int i = 0; i < partitionList.size(); i++) {
                    if(partitionList.get(i).contains(docArray[secondDocID]))
                        partitionList.get(i).toArray(setTwoToArray);
                }
                boolean added = false;
                for(int i = 0; i < setTwoToArray.length; i++) {
                    partitionList.get(index1).add(setTwoToArray[i]);
                    added = true;
                }
                if(added) {
                    partitionList.get(index2).add(null);
                    numPartitions--;
                }
                curIndex++;
            }
        }
        for(int k = 0; k < partitionList.size(); k++) {
            int curSize = partitionList.get(k).size();
            cpen221.mp1.Document[] array = new cpen221.mp1.Document[curSize];
            partitionList.get(k).toArray(array);
        }
        for(int j = 0; j < partitionList.size(); j++) {
            if(!partitionList.get(j).contains(null)) {
                partitions.add(partitionList.get(j));
                int curSize = partitionList.get(j).size();
                cpen221.mp1.Document[] array = new cpen221.mp1.Document[curSize];
                partitionList.get(j).toArray(array);
            }
        }
        return partitions;
    }

    private static int factorial(int val) {
        if(val == 1)
            return 1;
        return val * factorial(val-1);
    }
}
