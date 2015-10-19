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

/**
 *
 * @author s146728
 */
public class KMeans {

    //Number of Clusters. This metric should be related to the number of points
    private static int MAX_NUM_CLUSTERS;
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
        MAX_NUM_CLUSTERS = max_clusters; // max nubmer of clusters
        modelIndex = 5;
        //for (int i = 0; i < MAX_NUM_CLUSTERS - 1; i++) {
            clustersByK = new ArrayList<Cluster>(K_START_AT + modelIndex);
            init();
            calculate();
            computeBIC();
            modelIndex++;
        //}

    }

    public static void main(String[] args) {

    }

    private void computeBIC() {
        int k; // number of clusters
        int R = udata.getUserCount(), Rn;
        int dimensionCount = 2;
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
                clusterVariance += userDistance(centroid, pointFromCluster);
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
        for (int j = 0; j < NUMBER_KEYWORDS; j++) {
            distance += Math.pow((a.getKeyWord(j).getCount() - b.getKeyWord(j).getCount()), 2); /*distance function from Phillip*/

        }
           // System.out.println(u.getName());
        //System.out.println(u.getGender());

        //double distance= Math.sqrt(Math.pow((a.getKeyWord(0).getVSRscore()- b.getKeyWord(0).getVSRscore()), 2) + Math.pow((a.getKeyWord(0).getVSRscore() - b.getKeyWord(0).getVSRscore()), 2)); /*distance function from Phillip*/
        //System.out.println("Dist:" + distance);
        return Math.sqrt(distance);

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

    public void init() {
        //Create Points
        //Create Clusters
        //Set Random Centroids
        for (int i = 0; i < (K_START_AT + modelIndex); i++) {
            Cluster cluster = new Cluster(i);
            UserData.User centroid = cloneUser(udata.getUser(i), "centroid" + i);
            //udata.getUser(i); // there must be at n users to be able to do n-clustering
            cluster.setCentroid(centroid);
            clustersByK.add(cluster);
            /*Maybe this point is there twice???????????*/
        }
//        plotClusters();
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

            if (distance == 0) {
                finish = true;
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
        double max = Double.MAX_VALUE;
        double min = max;
        int clusterNumber = 0;
        double distance = 0.0;

        for (Object o : udata) {
            UserData.User user = (UserData.User) o;
            min = max;
            for (int i = 0; i < (K_START_AT + modelIndex); i++) {
                Cluster c = clustersByK.get(i);
                distance = userDistance(user, c.getCentroid());
                if (distance < min) {
                    min = distance;
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
