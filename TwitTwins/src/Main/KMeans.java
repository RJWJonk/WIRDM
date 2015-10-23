/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import Model.Word;
import java.util.ArrayList;
import java.util.List;
import java.lang.Math;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author s146728
 */
public class KMeans {

    private static final int K_START_AT = 2;
    private final int NUMBER_KEYWORDS;
    private UserData udata;
    private int modelIndex = 0;
    private ArrayList<Cluster> clustersByK;

    public KMeans(int keywordCount, UserData _udata) {
        NUMBER_KEYWORDS = keywordCount;
        udata = _udata;

    }

    public static void main(String[] args) {
         List<String> keywords = new ArrayList<String>();
        keywords.add("ICT");
        keywords.add("school");
//        keywords.add("girls");
//        keywords.add("technology");
//        keywords.add("testing");

        KMeans km = new KMeans(2, KMeans.createTestingData(keywords));
        boolean clusteringOK = km.calculateClusteringSetK();
    }

    private static UserData createTestingData(List<String> keywords) {
        UserData newUdata = new UserData(keywords);
        String[] names = {"John", "Adam", "Ben", "Luke", "Phillip", "Ruben", "Chung"};

        int[][] keywordFreqency = new int[][]{
 /*0*/      {7, 2},
            {8, 3},
            {10, 2},
            {15, 3},
            {20, 4},
 /*4*/      {5, 1},
            {11, 3},
            {2, 7},
            {3, 8},
            {2, 10},
 /*9*/      {3, 15},
            {4, 20},
            {1, 5},
            {3, 11},
            {7, 6},
 /*14*/     {10, 8},
            {7, 13},
            {3, 5},
            {4, 6},
 /*19*/     {17, 15}
                
        };

        //int[] ages = {}
        //String [] genders = {}
        for (int i = 0; i < keywordFreqency.length; i++) {
            TreeMap<String, Word> user = new TreeMap();
            Map<String, Word> userKeywords = new HashMap<String, Word>();
            for (int j = 0; j < keywordFreqency[i].length; j++) {

                Word w = new Word(keywords.get(j), 1);
                w.setFrequency(keywordFreqency[i][j]);
                userKeywords.put(keywords.get(j), w);
            }
            newUdata.addUser(Integer.toString(i), -1, "Male", 2, userKeywords);
        }
        return newUdata;
    }

    public Boolean calculateClusteringByBic() {
        modelIndex = 0;
        Boolean kmeansOK = true;
        double[] BIC = new double[NUMBER_KEYWORDS - 1];
        for (int i = 0; i < NUMBER_KEYWORDS - 1; i++) { // there is NUMBER_KEYWORDS-1 clusters(not counting the k=1 number of clusters to be made
            clustersByK = new ArrayList<Cluster>(K_START_AT + modelIndex);
            if (init()) {
                calculate();
                BIC[i] = computeBIC();
            } else {
                System.out.println("There is either too many users with keyword freqeuncy 0 or there the amount of unique users is lower than the number of cluster. Clusters stopped.");
                kmeansOK = false;
                break;
            }
            modelIndex++;
        }
        if (kmeansOK) {
            modelIndex = findMaxIndex(BIC);
            clustersByK = new ArrayList<Cluster>(K_START_AT + modelIndex);
            init();
            calculate(); // asign correct labels to the data
        }
        return kmeansOK;
    }

    public Boolean calculateClusteringSetK() {
        modelIndex = (int) Math.sqrt(udata.getUserCount() / 2D) - K_START_AT; // substracting K_START_AT is not nice, but this is done to persist the current architecture
        Boolean kmeansOK = true;
        clustersByK = new ArrayList<Cluster>(K_START_AT + modelIndex);
        if (init()) {
            calculate();
        } else {
            System.out.println("There is either too many users with keyword freqeuncy 0 or there the amount of unique users is lower than the number of cluster. Clusters stopped.");
            kmeansOK = false;
        }
        return kmeansOK;
    }

    public ArrayList<Cluster> getClusterByK() {
        return clustersByK;
    }

    private int findMaxIndex(double[] BIC) {
        double max = Double.NEGATIVE_INFINITY;
        int index = -1;
        for (int i = 0; i < BIC.length; i++) {
            if (BIC[i] > max) {
                max = BIC[i];
                index = i;
            }
        }
        return index;
    }

    private double computeBIC() {
        int k; // number of clusters
        int R = udata.getUserCount();
        int Rn;
        double clusterVariance, sumCentroidCoord, modelVarinace = 0, D = 0, pj;
        k = modelIndex + K_START_AT; // we are doing k-mean clustering ==> k clusters
        sumCentroidCoord = 0;
        for (int j = 0; j < k; j++) { /*Iterate over cluster*/

            clusterVariance = 0;
            UserData.User centroid = clustersByK.get(j).getCentroid();
            sumCentroidCoord += getSumOfUserCoords(centroid);
            Rn = clustersByK.get(j).getUsers().size(); // number of users in a cluster
            for (int l = 0; l < Rn; l++) { /*Itarate over points in the cluster*/

                UserData.User pointFromCluster = clustersByK.get(j).users.get(l);
                clusterVariance += userDistanceByEuclide(centroid, pointFromCluster); //CHECK THE cluster variance
            }
            clusterVariance = clusterVariance / (R - k);
            if (clusterVariance == 0) {
                D = -Double.MAX_VALUE;
                break;
            }
            D += computeBICofCluster(Rn, NUMBER_KEYWORDS, clusterVariance, R);
            modelVarinace += clusterVariance;
        }
        pj = modelVarinace + sumCentroidCoord;
        double BIC = D - pj / 2 * Math.log(R);
        System.out.println("BIC: " + BIC);
        return BIC;
    }

    private double userDistanceByEuclide(UserData.User a, UserData.User b) {
        double distance = 0;
        for (int i = 0; i < NUMBER_KEYWORDS; i++) {
            distance += Math.pow(a.getKeyWord(i).getCount() - b.getKeyWord(i).getCount(), 2);
        }
        return Math.sqrt(distance);
    }

    private double userDistanceByCosine(UserData.User a, UserData.User b) {
        Map keyWordsA = new HashMap();
        Map keyWordsB = new HashMap();
        for (int j = 0; j < NUMBER_KEYWORDS; j++) {
            keyWordsA.put(a.getKeyWord(j).getKeyWord(), a.getKeyWord(j).getCount());
            keyWordsB.put(b.getKeyWord(j).getKeyWord(), b.getKeyWord(j).getCount());
        }
        return VectorIR.cosine_similarity(keyWordsA, keyWordsB);

    }

    private int getSumOfUserCoords(UserData.User u) {
        int sum = 0;
        for (int i = 0; i < NUMBER_KEYWORDS; i++) {
            sum += u.getKeyWord(i).getCount();
        }
        return sum;
    }

    private double computeBICofCluster(int Rn, int dimensionCount, double clusterVariance, int R) {
        double Dn = -Rn / 2 * Math.log(2 * Math.PI) - (Rn * dimensionCount) / 2 * Math.log(clusterVariance) - ((Rn - 1) * dimensionCount) / 2 + (Rn * Math.log(Rn)) - Rn * Math.log(R); // originilly It was Math.log(clusterVariance), but for n<0,1>, log is negative, giving therefore wrong results
        return Dn;
    }

    private UserData.User cloneUser(UserData.User user, String newName) {
        List<UserData.KeyWord> keywordList = new ArrayList<>(NUMBER_KEYWORDS);
        for (int j = 0; j < NUMBER_KEYWORDS; j++) {
            UserData.KeyWord keyword = new UserData(null).new KeyWord(user.getKeyWord(j).getKeyWord());
            keyword.setCount(user.getKeyWord(j).getCount());
            keywordList.add(keyword);//
        }
        return new UserData(null).new User(newName, keywordList);
    }

    //Initializes the process
    public boolean init() {
        int centroidsAssigned = 0;
        double sumKeywords;
        int i;
        for (i = 0; i < (K_START_AT + modelIndex); i++) {

            Cluster cluster = new Cluster(i);
            for (int l = i; l < udata.getUserCount(); l++) { // at least i-users have been considered to be a centorid, we skip them
                sumKeywords = 0;
                for (int j = 0; j < NUMBER_KEYWORDS; j++) {
                    sumKeywords += udata.getUser(l).getKeyWord(j).getCount();
                }
                if (sumKeywords > 0 && !isUserLikeCentroid(udata.getUser(l), clustersByK)) {
                    UserData.User centroid = cloneUser(udata.getUser(l), "centroid" + l);
                    cluster.setCentroid(centroid);
                    clustersByK.add(cluster);
                    centroidsAssigned++;
                    break;
                }

            }
        }
        if (centroidsAssigned != i) {
            return false;
        }
        return true;
    }

    private Boolean isUserLikeCentroid(UserData.User u, ArrayList<Cluster> clusters) {
        for (int i = 0; i < clusters.size(); i++) {
            if (userDistanceByCosine(u, clusters.get(i).getCentroid()) == 1) {
                return true;
            }
        }
        return false;
    }

    private void plotClusters() {
        for (int i = 0; i < (K_START_AT + modelIndex); i++) {
            Cluster c = clustersByK.get(i);
            c.plotCluster();
        }
    }

    //The process to calculate the K Means, with iterating method.
    public ArrayList<UserData.User> calculate() {
        int iteration = 0;
        ArrayList<UserData.User> currentCentroids = new ArrayList();
        // Add in new data, one at a time, recalculating centroids with each new one. 
        while (true) {
            System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
            //Clear cluster state
            clearClusters();
            List<UserData.User> lastCentroids = getCentroids();

            //Assign points to the closer cluster
            assignCluster();
            plotClusters();
            //Calculate new centroids.
            calculateCentroids();

            iteration++;

            currentCentroids = getCentroids();

            //Calculates total distance between new and old Centroids
            double distance = 0;
            for (int i = 0; i < lastCentroids.size(); i++) {
                distance += userDistanceByCosine(lastCentroids.get(i), currentCentroids.get(i));
            }
            System.out.println("Iteration: " + iteration);
            System.out.println("Centroid distances: " + distance);
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

            if (distance == lastCentroids.size()) { // that means that every dimension is the same
                break;
            }
            if (distance == Double.NaN) {
                System.out.println("<<<<<<<<<<<<<<<Clustering-distance is non a number, sth is wrong.>>>>>>>>>>>>>");
                break;
            }
        }
        return currentCentroids;
    }

    private void clearClusters() {
        for (Cluster cluster : clustersByK) {
            cluster.clear();
        }
    }

    private ArrayList getCentroids() {
        ArrayList centroids = new ArrayList((K_START_AT + modelIndex));
        for (Cluster cluster : clustersByK) {
            UserData.User auxCentroid = cluster.getCentroid();
            UserData.User centroid = cloneUser(auxCentroid, auxCentroid.getName());
            centroids.add(centroid);
        }
        return centroids;
    }

    private void assignCluster() {
        double min;
        int clusterNumber = 0;
        double cosineSimilarity = 0.0;

        for (Object o : udata) {
            UserData.User user = (UserData.User) o;
            min = 0;
            for (int i = 0; i < (K_START_AT + modelIndex); i++) {
                Cluster c = clustersByK.get(i);
                cosineSimilarity = userDistanceByCosine(user, c.getCentroid());
                if (cosineSimilarity > min) {
                    min = cosineSimilarity;
                    clusterNumber = i;
                }
            }
            user.setCluster(clusterNumber);
            clustersByK.get(clusterNumber).addPoint(user);
        }
    }

    private void calculateCentroids() {
        double[] sumDim = new double[NUMBER_KEYWORDS];
        for (Cluster cluster : clustersByK) {

            List<UserData.User> list = cluster.getUsers();/*PROBLEM: referencing*/

            int numberUserInCluster = list.size();
            for (int i = 0; i < NUMBER_KEYWORDS; i++) { // nessesary to do it every it!!!!!!
                sumDim[i] = 0;
            }
            for (UserData.User user : list) {
                for (int i = 0; i < NUMBER_KEYWORDS; i++) {
                    sumDim[i] += user.getKeyWord(i).getCount();
                }
            }
            UserData.User centroid = cluster.getCentroid();
            System.out.println(centroid.getName());
            for (int i = 0; i < NUMBER_KEYWORDS; i++) {
                centroid.getKeyWord(i).setCount(sumDim[i] / numberUserInCluster);
                System.out.print(centroid.getKeyWord(i).getCount() + " ");
            }
            System.out.println("");
        }
    }
}
