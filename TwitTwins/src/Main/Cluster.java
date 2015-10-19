/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author s146728
 */
public class Cluster {

    public List<UserData.User> users;
    public UserData.User centroid;
    public int id;

    //Creates a new Cluster
    public Cluster(int id) {
        this.id = id;
        this.users = new ArrayList();
        this.centroid = null;
    }

    public List getUsers() {
        return users;
    }

    public void addPoint(UserData.User u) {
        users.add(u);
    }

    public void setPoints(List users) {
        this.users = users;
    }

    public UserData.User getCentroid() {
        return centroid;
    }

    public void setCentroid(UserData.User centroid) {
        this.centroid = centroid;
    }

    public int getId() {
        return id;
    }

    public void clear() {
        users.clear();
    }

    public void plotCluster() {
        System.out.println("[Cluster: " + id + "]");
        System.out.println("[Centroid: " + centroid.getName() + "]");
        System.out.println("[Points: \n");
        double sumDim;
        for (UserData.User p : users) {

            /*sumDim = 0;
            for (int i = 0; i < TwitMain.NUMBER_KEYWORDS; i++) {
                sumDim+= p.getKeyWord(i).getCount();
            }*/
            System.out.print(p.getName()+", ");
        }
        System.out.println("]");
    }

}
