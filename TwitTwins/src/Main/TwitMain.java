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
        int n = 10;
        while (n > 0 && !names.isEmpty()) {
            n--;
            
            Tweet t = names.poll();
            String name = t.getUser().getScreenName();
           /* String ProfilePicURL = t.getUser().getOriginalProfileImageURL();
            ProfilePredict pp = new ProfilePredict();
            String gender = pp.getGender(ProfilePicURL);*/
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
            for(int j = 0; j<NUMBER_KEYWORDS; j++){
                u.getKeyWord(j).setVSRscore(co);
                co++;
            }
            System.out.println(u.getName());
            System.out.println(u.getGender());
        }
        
        /*Clustering*/
       // KMeans kClustering = new KMeans(3, udata);
        
        /* Do not delte
        ProbabRetrieval pr = new ProbabRetrieval(); //Probabilist Retrieval
        udata = pr.rank(udata, searchedUserKeywordFrequency, keywordSearchedUserCount, collectionWordLenght,0.8);    
        */
    }
    
}
