/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import static Main.TwitMain.NUMBER_KEYWORDS;
import java.util.ArrayList;

/**
 *
 * @author s146728
 */
public class ProbabRetrieval {
    
        
        public UserData rank(UserData udata,  ArrayList<Integer> searchedUserKeywordFrequency, int keywordSearchedUserCount, int collectionWordLenght, Double alpha){
        Double userScore;
        Double keywordWeight;
        int i;
        //SortedList
        for(Object o: udata){
             UserData.User u = (UserData.User) o;
             //System.out.println("Name:"+u.getName());
             userScore = (double)0;
             for (i = 0; i< NUMBER_KEYWORDS; i++)
             {
                UserData.KeyWord k = u.getKeyWord(i); 
                keywordWeight = (double) searchedUserKeywordFrequency.get(i) / keywordSearchedUserCount;
                userScore+= (alpha *  k.getCount() / u.getWordTweetCount()) + ((1-alpha)*k.getCount() / collectionWordLenght);
               // System.out.println("-word:" + k.getKeyWord() + "(" + k.getCount()+ ")");
             }
             u.setScore(userScore);
             //System.out.println("--score:"+u.getScore());
        }
       // udata.sortByScore();
        for(Object o: udata){
             UserData.User u = (UserData.User) o;
             System.out.println("Name:"+u.getName());
             for (i = 0; i< NUMBER_KEYWORDS; i++)
             {
                UserData.KeyWord k = u.getKeyWord(i); 
               
                //System.out.println("-word:" + k.getKeyWord() + "(" + k.getCount()+ ")");
             }
             System.out.println("--score:"+u.getScore());
        }
        return udata;
        }
}
