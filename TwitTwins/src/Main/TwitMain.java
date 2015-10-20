/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import Model.Word;
import com.facepp.error.FaceppParseException;
import java.util.ArrayList;
import java.util.Queue;
import java.util.TreeMap;
import java.util.Map;
import java.util.Comparator;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Collections;
import java.util.Iterator;
import Main.UserData.KeyWord;
import java.util.Arrays;
import java.util.HashMap;
import com.google.common.collect.Sets;
import java.util.Set;

/**
 *
 * @author s080440
 */
public class TwitMain {

    static int NUMBER_KEYWORDS = 5;

    public static void main(String[] args) throws FaceppParseException {
        TweetsExtractor te = new TweetsExtractor();
        TreeMap<String, Word> data = te.extractUser("tferriss"); //tferris
        int i = NUMBER_KEYWORDS;
        int keywordSearchedUserCount = 0;

        ArrayList<Integer> searchedUserKeywordFrequency = new ArrayList();
        ArrayList<String> keywords = new ArrayList();
        for (Word w : data.values()) {
            if (i == 0) {
                break;
            } else {
                i--;
            }
            keywords.add(w.getWord());
            searchedUserKeywordFrequency.add(w.getFrequency());
            keywordSearchedUserCount += w.getFrequency();
        }

        UserData udata = new UserData(keywords);

        Queue<Tweet> names = te.query(keywords);

        int collectionWordLenght = 0;
        int userWordLenght;
        int n = 20;
        while (n > 0 && !names.isEmpty()) {
            n--;

            Tweet t = names.poll();
            //t.g
            String name = t.getUser().getScreenName();
            /* String ProfilePicURL = t.getUser().getOriginalProfileImageURL();
             ProfilePredict pp = new ProfilePredict();
             String gender = pp.getGender(ProfilePicURL);
             int age = pp.getAge(ProfilePicURL);*/
            String gender = "male";
            TreeMap<String, Word> user = te.extractUser(name);

            userWordLenght = 0;
            for (Map.Entry<String, Word> entry : user.entrySet()) {
                Word value = entry.getValue();
                userWordLenght += value.getFrequency();
            }
            udata.addUser(name, 0, gender, userWordLenght, user);
            collectionWordLenght += userWordLenght;
//            collectionLenght+=TweetCount;

        }
        int co = 0;
        for (Object o : udata) {
            UserData.User u = (UserData.User) o;
            System.out.println(u.getName());
            System.out.println(u.getGender());
        }

        printScores(udata);
        //Clustering
        //KMeans kClustering = new KMeans(7, udata);

        // Do not delte
        //ProbabRetrieval pr = new ProbabRetrieval(); //Probabilist Retrieval
        //pr.rank(udata, searchedUserKeywordFrequency, keywordSearchedUserCount, collectionWordLenght,0.8);
        // Rank users to query using VSR method
        Ranking(udata, keywords);

        //testing and print some scores
    }

    private UserData createTestingData(List<String> kws) {
        ArrayList<String> keywords = new ArrayList<String>();
        keywords.add("ICT");
        keywords.add("school");
        keywords.add("girls");
        keywords.add("technology");
        keywords.add("testing");
        List<Score> searchUserScores = new ArrayList();
        int[] searchUserKeywordFrequncy = {15, 10, 6, 0, 8};
        for (int i = 0; i < keywords.size(); i++) {
            searchUserScores.add(new Score(searchUserKeywordFrequncy[i], keywords.get(i)));
        }

        UserData newUdata = new UserData(keywords);
        String[] names = {"John", "Adam", "Ben", "Luke", "Phillip", "Ruben", "Chung-Kit"};
        int[][] keywordFreqency = new int[][]{{5, 4, 0, 1, 2},
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

                Word w = new Word(keywords.get(i), 1);
                w.setFrequency(keywordFreqency[i][j]);
                userKeywords.put(keywords.get(i), w);
            }
            newUdata.addUser(names[i], -1, "Male", NUMBER_KEYWORDS, userKeywords);
        }
        return newUdata;
    }

    public static void Ranking(UserData udata, ArrayList<String> query) {
        String word;
        Double tf;
        Map KwTfdata = new HashMap(); //KeyWord + TermFrequency data of single user
        Map QueryData = new HashMap(); // Store the query in a Map (for processing in VectorIR class)
        ArrayList<Map> KwTfdataList = new ArrayList<>(); //KeyWord + TermFrequency data of all users
        List<Score> scores = new ArrayList<Score>(); // Stores the cosine similarity score between query and all users

        for (int i = 0; i < query.size(); i++) {
            QueryData.put(query.get(i), 1.0);
        }
        // Retrieve all keywords (including their term frequency) from every user and put it in a map
        for (Object o : udata) {
            UserData.User u = (UserData.User) o;
            KwTfdata.clear();
            Iterator iter = u.iterator();
            while (iter.hasNext()) {
                KeyWord keyW = (KeyWord) iter.next(); // Get next keyword of user
                word = keyW.getKeyWord();
                tf = (double) keyW.getCount();
                KwTfdata.put(word, tf);
                //System.out.println(word +"\t" + tf); //For testing
            }
            KwTfdataList.add(KwTfdata);
            // Calculate cosine similarity of every user with the query and add to scores list.
            scores.add(new Score(VectorIR.cosine_similarity(QueryData, KwTfdata), u.getName())); // Generate a new Score class containing (Score,Username)

        }

        // Sort the scores list in ascending order of scores (and their corresponding users)
        System.out.println("-------- VSR Ranking results --------");
        System.out.println("Query: " + query);
        Collections.sort(scores);
        Collections.reverse(scores); // Changes the list to an ascending order.
        int rank = 0;
        for (Object o : scores) {
            Score s = (Score) o;
            rank++;
            //System.out.println("Ranked: " + s.getName()+ "with score: " +"\t"+ s.getScore()  );
            System.out.format("#%d: \t %-20s \t (CosineScore: %f)%n", rank, s.getName(), s.getScore());
        }

    }

    public static void printScores(UserData udata) {

        for (Object o : udata) {
            UserData.User u = (UserData.User) o;
            String userInfo = u.getName() + " - Keywords:";
            UserData.KeyWord keyword = u.getFirstKeyWord();
            Iterator iter = u.iterator();
            while (iter.hasNext()) {
                UserData.KeyWord keyW = (UserData.KeyWord) iter.next();
                userInfo = userInfo + "|" + keyW.getKeyWord() + ":" + keyW.getCount() + "|";
            }

            System.out.println(userInfo);
        }
    }

}
