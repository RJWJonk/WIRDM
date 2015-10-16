/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import java.util.ArrayList;
import java.util.List;
import java.lang.Math;

/**
 *
 * @author s146728
 */
public class KMeans {

    //Number of Clusters. This metric should be related to the number of points
    private static int NUM_CLUSTERS = 3;
    //Number of Points
    private static int kStartAt = 2;
    //Min and Max X and Y
    private static final int MIN_COORDINATE = 0;
    private static final int MAX_COORDINATE = 10;

    private UserData udata;
    private static int clusterPosition;
    static ArrayList<ArrayList<Cluster>> clustersByK;

    public KMeans() {
        //udata=_udata;UserData _udata
    }

    public static void main(String[] args) {

        clustersByK = new ArrayList<ArrayList<Cluster>>(NUM_CLUSTERS);
        KMeans kmeans = new KMeans();
        /*points = Point.createRandomPoints(MIN_COORDINATE, MAX_COORDINATE, NUM_POINTS);*/
        for (int i = 0; i < NUM_CLUSTERS - 1; i++) {
            kmeans.init();
            kmeans.calculate();
            clusterPosition++;
        }
        kmeans.computeBIC();

    }

    private void computeBIC() {
        int k; // number of clusters
        int R = udata.getUserCount(), Rn;
        int dimensionCount = 2;
        double clusterVariance, sumCentroidCoord, modelVarinace, D, pj;
        double[] BIC = new double[NUM_CLUSTERS - 1];
        for (int i = 0; i < NUM_CLUSTERS - 1; i++) { /*Iterate over models*/

            System.out.println("Cluster " + i);
            k = clustersByK.get(i).size();
            D = 0;
            sumCentroidCoord = 0;
            modelVarinace = 0;
            for (int j = 0; j < k; j++) { /*Iterate over cluster*/

                clusterVariance = 0;
                UserData.User centroid = clustersByK.get(i).get(j).getCentroid();

                //centroid.getKeyWord(0).
                sumCentroidCoord += getSumOfUserCoords(centroid);
                Rn = clustersByK.get(i).get(j).getUsers().size(); // number of points
                for (int l = 0; l < Rn; l++) { /*Itarate over points*/

                    UserData.User pointFromCluster = clustersByK.get(i).get(j).users.get(l);
                    clusterVariance += userDistance(centroid, pointFromCluster);
                }
                clusterVariance = clusterVariance / (R - k);
                D += computeBICofCluster(Rn, dimensionCount, clusterVariance, R);
                modelVarinace += clusterVariance;
            }
            pj = modelVarinace + sumCentroidCoord;
            BIC[i] = D - pj / 2 * Math.log(R);
            System.out.println("BIC " + BIC[i]);
        }
    }

    private double userDistance(UserData.User a, UserData.User b) {
        /*double distance = 0;
         for (int i=0;i<TwitMain.NUMBER_KEYWORDS;i++){
         distance+=Math.pow(a.getKeyWord(i).getCount() - b.getKeyWord(i).getCount(),2);
         //return Math.sqrt(Math.pow((centroid.getY() - p.getY()), 2) + Math.pow((centroid.getX() - p.getX()), 2));
         }
         return Math.sqrt(distance);*/
        return 0; /*distance function from Phillip*/

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

    //Initializes the process
    public void init() {
    	//Create Points
        //Create Clusters
        //Set Random Centroids
        ArrayList<Cluster> ll = new ArrayList<>();
        clustersByK.add(ll);
        for (int i = 0; i < (NUM_CLUSTERS - 1 + clusterPosition); i++) {
            Cluster cluster = new Cluster(i);
            UserData.User centroid = udata.getUser(0);
            cluster.setCentroid(centroid);
            clustersByK.get(clusterPosition).add(cluster);
            /*Maybe this point is there twice???????????*/
        }
//        plotClusters();
    }

    private void plotClusters() {
        for (int i = 0; i < (NUM_CLUSTERS - 1 + clusterPosition); i++) {
            Cluster c = clustersByK.get(clusterPosition).get(i);
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
        for (Cluster cluster : clustersByK.get(clusterPosition)) {
            cluster.clear();
        }
    }

    private ArrayList getCentroids() {
        ArrayList centroids = new ArrayList((NUM_CLUSTERS - 1 + clusterPosition));
        for (Cluster cluster : clustersByK.get(clusterPosition)) {
            UserData.User aux = cluster.getCentroid();
           // UserData.User user = new UserData.User()
            //(aux.getX(), aux.getY());
            centroids.add(aux); /*PROBLEM: references*/

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
            for (int i = 0; i < (NUM_CLUSTERS - 1 + clusterPosition); i++) {
                Cluster c = clustersByK.get(clusterPosition).get(i);
                distance = userDistance(user, c.getCentroid());
                if (distance < min) {
                    min = distance;
                    clusterNumber = i;
                }
            }
            user.setCluster(clusterNumber);
            clustersByK.get(clusterPosition).get(clusterNumber).addPoint(user);
        }
    }

    private void calculateCentroids() {
        for (Cluster cluster : clustersByK.get(clusterPosition)) {
            double sumX = 0;
            double sumY = 0;
            double[] sumDim = new double[TwitMain.NUMBER_KEYWORDS];
            List<UserData.User> list = cluster.getUsers();/*PROBLEM: referencing*/
            // int n_points = list.size();

            for (UserData.User user : list) {
                for (int i = 0; i < TwitMain.NUMBER_KEYWORDS; i++) {
                    sumDim[i] += user.getKeyWord(i).getVSRscore();
                }
            }

            UserData.User centroid = cluster.getCentroid();
            if (TwitMain.NUMBER_KEYWORDS > 0) {
                for (UserData.User user : list) {
                    for (int i = 0; i < TwitMain.NUMBER_KEYWORDS; i++) {
                        user.getKeyWord(i).setVSRscore(sumDim[i] / ((double)TwitMain.NUMBER_KEYWORDS));
                    }
                }
            }
        }
    }
}
