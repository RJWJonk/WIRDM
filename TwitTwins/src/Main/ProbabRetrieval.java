/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import static Main.TwitMain.NUMBER_KEYWORDS;
import static Main.TwitMain.printScores;
import Model.Word;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author s146728
 */
public class ProbabRetrieval {

//    public static void main(String[] args) {
//
//        List<String> keywords = new ArrayList<String>();
//        keywords.add("ICT");
//        keywords.add("school");
//        keywords.add("girls");
//        keywords.add("technology");
//        keywords.add("testing");
//
//        List<Score> searchedUser = createSearchUserScore(keywords);
//
//        UserData testingUdata = createTestingData(keywords);
//        printScores(testingUdata);
//        //rank(testingUdata, searchedUser, 1.0);
//    }

        List<Score> searchedUser = createSearchUserScore(keywords);

        UserData testingUdata = createTestingData(keywords);
        printScores(testingUdata);
    }
    private  int getCollectionLenght(UserData udata){
        int collectionWordLenght = 0;
        for (Object o : udata) {
            UserData.User u = (UserData.User) o;
            collectionWordLenght += u.getWordTweetCount();
        }
        return collectionWordLenght;
    }
    private  double getTotalKeywordCount(List<Score> searchedUserKeywordFrequency){
        double totalSearchUserLenght = 0;
        for (Object o : searchedUserKeywordFrequency) {
            Score s = (Score) o;
            totalSearchUserLenght += s.getScore();
        }
        return totalSearchUserLenght;
    }
    public  List<Score> rank(UserData udata, List<Score> searchedUserKeywordFrequency, ArrayList<Cluster> clustersByK, Boolean includeCLustering, double alpha, double beta, double gama) {
        double userScore;
        double keywordWeight;
        List<Score> resultScores = new ArrayList<Score>();

        int collectionWordLenght = getCollectionLenght(udata);
        double searchedUserTotalKeywordFreq = getTotalKeywordCount(searchedUserKeywordFrequency);
        int keywordCount = searchedUserKeywordFrequency.size();
        
        int clusterKeywordLenght = 0;
        double documentWeight, clusterWeight = 0, collectionWeight;
        for (Object o : udata) {
            UserData.User u = (UserData.User) o;
            userScore = 1; // 1 because it is a product
            for (int i = 0; i < keywordCount; i++) {

                UserData.KeyWord k = u.getKeyWord(i);
                if (includeCLustering) {
                    clusterKeywordLenght = 0;
                    for (UserData.User userFromCluster : clustersByK.get(u.getCluster()).users) {
                        clusterKeywordLenght += userFromCluster.getKeyWord(i).getCount();
                    }
                }
                keywordWeight = (double) searchedUserKeywordFrequency.get(i).getScore() / searchedUserTotalKeywordFreq;
                documentWeight = (alpha * k.getCount() / u.getWordTweetCount());
                if (includeCLustering) {
                    clusterWeight = (beta * clusterKeywordLenght / clustersByK.get(u.getCluster()).getTotalLenght());
                }
                collectionWeight = (gama * k.getCount() / collectionWordLenght);
                double currentUserScore = keywordWeight * (documentWeight + clusterWeight + collectionWeight);
                if(currentUserScore == 0 || Double.isNaN(currentUserScore)){
                    currentUserScore = 1f/collectionWordLenght;
                }
                userScore *= 1f+currentUserScore;
            }
            resultScores.add(new Score(userScore, u.getName()));

        }

        System.out.println("-------- PRP Ranking results --------");
        Collections.sort(resultScores);
        Collections.reverse(resultScores); // Changes the list to an ascending order.
        int rank = 0;
        for (Object o : resultScores) {
            Score s = (Score) o;
            rank++;
            //System.out.println("Ranked: " + s.getName()+ "with score: " +"\t"+ s.getScore()  ); 
            System.out.format("#%d: \t %-20s \t (PRPscore: %f)%n", rank, s.getName(), s.getScore());
        }

        return resultScores;
    }

    public static List<Score> createSearchUserScore(List<String> keywords) {
        List<Score> searchUserScores = new ArrayList();
        int[] searchUserKeywordFrequncy = {15, 10, 6, 0, 8};
        System.out.print("SU:\t");
        for (int i = 0; i < keywords.size(); i++) {

            searchUserScores.add(new Score(searchUserKeywordFrequncy[i], keywords.get(i)));
            System.out.format("%s: %2.0f \t", keywords.get(i), (double) searchUserKeywordFrequncy[i]);
        }
        System.out.println("----------------------------\n");
        return searchUserScores;
    }

    public static UserData createTestingData(List<String> keywords) {
        UserData newUdata = new UserData(keywords);
        String[] names = {"John", "Adam", "Ben", "Luke", "Phillip", "Ruben", "Chung"};
        int[][] keywordFreqency = new int[][]{
            {5, 4, 0, 1, 2},
            {0, 18, 0, 4, 5},
            {10, 0, 10, 0, 0},
            {0, 3, 0, 4, 0},
            {6, 0, 7, 0, 1},
            {0, 4, 12, 4, 1},
            {2, 8, 1, 1, 4}};
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
            newUdata.addUser(names[i], -1, "Male", NUMBER_KEYWORDS, userKeywords);
        }
        return newUdata;
    }
}
