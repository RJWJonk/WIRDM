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
        TreeMap<String, Word> data = te.extractUser("Ben_Rutten"); //tferris
        int i = NUMBER_KEYWORDS;
        int keywordSearchedUserCount = 0;
        
        ArrayList<Integer> searchedUserKeywordFrequency = new ArrayList();
        ArrayList<String> keywords = new ArrayList();
        for (Word w : data.values()) {
            if (i == 0) break; else i--;
            keywords.add(w.getWord());
            searchedUserKeywordFrequency.add(w.getFrequency());
            keywordSearchedUserCount+=w.getFrequency();
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
            for(Map.Entry<String,Word> entry : user.entrySet()) {
                Word value = entry.getValue();
                userWordLenght+= value.getFrequency();
              }
            udata.addUser(name, 0, gender, userWordLenght, user);
            collectionWordLenght+=userWordLenght;
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
        KMeans kClustering = new KMeans(7, udata);
        
        // Do not delte

        /*Clustering*/
       // KMeans kClustering = new KMeans(3, udata);
        
        // Do not delte
        ProbabRetrieval pr = new ProbabRetrieval(); //Probabilist Retrieval
        udata = pr.rank(udata, searchedUserKeywordFrequency, keywordSearchedUserCount, collectionWordLenght,0.8);

        // Rank users to query using VSR method
            //String[] q = { "co","to","and","http","t" }; // Enter query keywords here
            //ArrayList<String> query = new ArrayList<>();
            //query.addAll( Arrays.asList(q) );
        Ranking(udata, keywords);

        //testing and print some scores
        
    }
    /*private UserData createTestingData(List<String> kws){
        String[] keywords = {"ICT", "girls", "technology", "testing", "school"};
        UserData newUdata = new UserData(kws);
        TreeMap<String, Word> user = new TreeMap();
        Word w = new Word(firstKW, 1);
        w.setFrequency(15);
        user.put(firstKW,w);
        newUdata.addUser(firstKW, NUMBER_KEYWORDS, firstKW, NUMBER_KEYWORDS, user);
        
        
    }*/
    public static void Ranking(UserData udata, ArrayList<String> query ) {
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
            while( iter.hasNext() ) {
                KeyWord keyW = (KeyWord)iter.next(); // Get next keyword of user
                word = keyW.getKeyWord();
                tf = (double) keyW.getCount();
                KwTfdata.put(word, tf);
                //System.out.println(word +"\t" + tf); //For testing
            }
            KwTfdataList.add(KwTfdata);
        // Calculate cosine similarity of every user with the query and add to scores list.
        scores.add(new Score( VectorIR.cosine_similarity(QueryData,KwTfdata ), u.getName() )); // Generate a new Score class containing (Score,Username)
        
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
        
//        // Test users for testing cosine similarity scoring
//        Map d1 = new HashMap();
//        Map d2 = new HashMap();
//        d1.put("Fred", 0.0);
//        d1.put("Poep", 0.0);
//        d1.put("CD",0.0);
//        d1.put("Draak", 0.0);
//        
//        d2.put("Girls", 52.0);
//        d2.put("Pink", 52.0);
//        d2.put("Barbie",52.0);
//        d2.put("hoi",52.0);       
//        System.out.println("Cosine similarity: " + VectorIR.cosine_similarity(QueryData,KwTfdata) );
        
    }       

    private static void printScores(UserData udata) {
       
         for (Object o : udata) {
            UserData.User u = (UserData.User) o;
            String userInfo = u.getName() + " - Keywords:";
            UserData.KeyWord keyword = u.getFirstKeyWord();
            Iterator iter = u.iterator();
            while(iter.hasNext()) {
                UserData.KeyWord keyW = (UserData.KeyWord)iter.next();
                userInfo = userInfo + "|"+keyW.getKeyWord()+ ":"+keyW.getCount()+"|";
            }

            System.out.println(userInfo);
        }
    }
    
    
}

