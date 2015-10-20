/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import static Main.TwitMain.NUMBER_KEYWORDS;
import java.util.ArrayList;
import java.util.List;
import java.lang.Math;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author s146728
 */
public class KMeans {

    //Number of Clusters. This metric should be related to the number of points
    //Number of Points
    private static int K_START_AT = 2;
    //Min and Max X and Y
    private static final int MIN_COORDINATE = 0;
    private static final int MAX_COORDINATE = 10;
 
    private UserData udata;
    private static int modelIndex = 0;
    private static ArrayList<Cluster> clustersByK;

    public KMeans(int max_clusters, UserData _udata) {
        //udata=_udata;UserData _udata
        udata = _udata;
        modelIndex = 0;
        for (int i = 0; i < max_clusters - 1; i++) {
            clustersByK = new ArrayList<Cluster>(K_START_AT + modelIndex);
            if (init()) {
                calculate();
                computeBIC();
            } else {
                System.out.println("There is not sufficient amount of users with useful information for the clustering. Clusters is stopped.");
                break;
            }
            modelIndex++;
        }

    }

    public static void main(String[] args) {

    }

    private void computeBIC() {
        int k; // number of clusters
        int R = udata.getUserCount(), Rn;
        int dimensionCount = TwitMain.NUMBER_KEYWORDS;
        double clusterVariance, sumCentroidCoord, modelVarinace, D, pj;
        k = modelIndex + K_START_AT;
        D = 0;
        sumCentroidCoord = 0;
        modelVarinace = 0;
        for (int j = 0; j < k; j++) { /*Iterate over cluster*/

            clusterVariance = 0;
            UserData.User centroid = clustersByK.get(j).getCentroid();

            //centroid.getKeyWord(0).
            sumCentroidCoord += getSumOfUserCoords(centroid);
            Rn = clustersByK.get(j).getUsers().size(); // number of points
            for (int l = 0; l < Rn; l++) { /*Itarate over points*/
                UserData.User pointFromCluster = clustersByK.get(j).users.get(l);
                clusterVariance += 1-userDistance(centroid, pointFromCluster); //CHECK THE cluster variance
            }
            clusterVariance = clusterVariance / (R - k);
            D += computeBICofCluster(Rn, dimensionCount, clusterVariance, R);
            modelVarinace += clusterVariance;
        }
        pj = modelVarinace + sumCentroidCoord;
        double BIC = D - pj / 2 * Math.log(R);
        System.out.println("BIC " + BIC);
    }

    private double userDistance(UserData.User a, UserData.User b) {
        /*double distance = 0;
         for (int i=0;i<TwitMain.NUMBER_KEYWORDS;i++){
         distance+=Math.pow(a.getKeyWord(i).getCount() - b.getKeyWord(i).getCount(),2);
         //return Math.sqrt(Math.pow((centroid.getY() - p.getY()), 2) + Math.pow((centroid.getX() - p.getX()), 2));
         }
         return Math.sqrt(distance);*/
        double distance = 0;
        Map keyWordsA = new HashMap();
        Map keyWordsB = new HashMap();
        for (int j = 0; j < NUMBER_KEYWORDS; j++) {
            keyWordsA.put(a.getKeyWord(j).getKeyWord(), a.getKeyWord(j).getCount());
            keyWordsB.put(b.getKeyWord(j).getKeyWord(), b.getKeyWord(j).getCount());
            //distance += Math.pow((a.getKeyWord(j).getCount() - b.getKeyWord(j).getCount()), 2); /*distance function from Phillip*/
        }
        
        VectorIR.cosine_similarity(keyWordsA, keyWordsB);
           // System.out.println(u.getName());
        //System.out.println(u.getGender());

        //double distance= Math.sqrt(Math.pow((a.getKeyWord(0).getVSRscore()- b.getKeyWord(0).getVSRscore()), 2) + Math.pow((a.getKeyWord(0).getVSRscore() - b.getKeyWord(0).getVSRscore()), 2)); /*distance function from Phillip*/
        //System.out.println("Dist:" + distance);
        return VectorIR.cosine_similarity(keyWordsA, keyWordsB);
        //return Math.sqrt(distance);

    }

    private int getSumOfUserCoords(UserData.User u) {
        int sum = 0;
        for (int i = 0; i < TwitMain.NUMBER_KEYWORDS; i++) {
            sum += u.getKeyWord(i).getCount();
        }
        return sum;
    }

    private double computeBICofCluster(int Rn, int dimensionCount, double clusterVariance, int R) {
        double Dn = -Rn / 2 * Math.log(2 * Math.PI) - (Rn * dimensionCount) / 2 * Math.log(clusterVariance) - ((Rn - 1) * dimensionCount) / 2 + (Rn * Math.log(Rn)) - Rn * Math.log(R);
        return Dn;
    }

    private UserData.User cloneUser(UserData.User user, String newName) {
        List<UserData.KeyWord> keywordList = new ArrayList<>(TwitMain.NUMBER_KEYWORDS);
        for (int j = 0; j < TwitMain.NUMBER_KEYWORDS; j++) {
            UserData.KeyWord keyword = new UserData(null).new KeyWord(user.getKeyWord(j).getKeyWord());
            keyword.setCount(user.getKeyWord(j).getCount());
            keywordList.add(keyword);//
        }
        return new UserData(null).new User(newName, keywordList);
    }

    //Initializes the process
    public boolean init() {
        //Create Points
        //Create Clusters
        //Set Random Centroids
        int centroidsAssigned = 0;
        int userIndex = 0;
        double sumKeywords;
        int i;
        for (i = 0; i < (K_START_AT + modelIndex); i++) {

            Cluster cluster = new Cluster(i);
            for (int l = userIndex; l < udata.getUserCount(); l++) {
                userIndex++;
                sumKeywords = 0;
                for (int j = 0; j < TwitMain.NUMBER_KEYWORDS; j++) {
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
            /*Maybe this point is there twice???????????*/
        }
        if (centroidsAssigned != i) {
            return false;
        }
        return true;
//        plotClusters();
    }
    private Boolean isUserLikeCentroid(UserData.User u, ArrayList<Cluster> clusters){
        for(int i=0;i<clusters.size();i++){
            if(userDistance(u, clusters.get(i).getCentroid())==1){
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
        //determine k

    }

    //The process to calculate the K Means, with iterating method.
    public ArrayList<UserData.User> calculate() {
        boolean finish = false;
        int iteration = 0;
        ArrayList<UserData.User> currentCentroids = new ArrayList();
        // Add in new data, one at a time, recalculating centroids with each new one. 
        while (!finish) {
            //Clear cluster state
            clearClusters();
            List<UserData.User> lastCentroids = getCentroids();

            //Assign points to the closer cluster
            assignCluster();

            //Calculate new centroids.
            calculateCentroids();

            iteration++;

            currentCentroids = getCentroids();

            //Calculates total distance between new and old Centroids
            double distance = 0;
            for (int i = 0; i < lastCentroids.size(); i++) {
                distance += userDistance(lastCentroids.get(i), currentCentroids.get(i));
            }
            System.out.println("#################");
            System.out.println("Iteration: " + iteration);
            System.out.println("Centroid distances: " + distance);
            plotClusters();

            if (distance == lastCentroids.size()) { // that means that every dimension is the same
                finish = true;
            }
            if(distance == Double.NaN){
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
            //(aux.getX(), aux.getY());
            centroids.add(centroid); /*PROBLEM: references*/

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
                cosineSimilarity = userDistance(user, c.getCentroid());
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
        double[] sumDim = new double[TwitMain.NUMBER_KEYWORDS];
        for (Cluster cluster : clustersByK) {

            List<UserData.User> list = cluster.getUsers();/*PROBLEM: referencing*/

            int numberUserInCluster = list.size();

            for (int i = 0; i < sumDim.length; i++) {
                sumDim[i] = 0;
            }
            for (UserData.User user : list) {
                for (int i = 0; i < sumDim.length; i++) {
                    sumDim[i] += user.getKeyWord(i).getCount();
                }
            }

            UserData.User centroid = cluster.getCentroid();
            System.out.println(centroid.getName());
            for (int i = 0; i < TwitMain.NUMBER_KEYWORDS; i++) {
                centroid.getKeyWord(i).setCount(sumDim[i] / numberUserInCluster);
                System.out.print(centroid.getKeyWord(i).getCount() + " ");
            }
            System.out.println("");

        }
    }
}
