/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import static Main.TwitMain.NUMBER_KEYWORDS;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author s146728
 */
public class ProbabRetrieval {

    public List<Score> rank(UserData udata, List<Score> searchedUserKeywordFrequency, Double alpha) {
        double userScore;
        double keywordWeight;
        List<Score> scores = new ArrayList<Score>();
        int i;
        //SortedList

        int collectionWordLenght = 0;
        for (Object o : udata) {
            UserData.User u = (UserData.User) o;
            collectionWordLenght+=u.getTotalWordCount();
        }
        double totalSearchUserLenght = 0;
        for (Object o : searchedUserKeywordFrequency)
        {
            Score s = (Score) o;
            totalSearchUserLenght+=s.getScore();
        }
        
        
        for (Object o : udata) {
            UserData.User u = (UserData.User) o;
            userScore = 0;
            for (i = 0; i < NUMBER_KEYWORDS; i++) {
                UserData.KeyWord k = u.getKeyWord(i);
                keywordWeight = (double) searchedUserKeywordFrequency.get(i).getScore() / totalSearchUserLenght;
                userScore += keywordWeight*(alpha * k.getCount() / u.getTotalWordCount()) + ((1 - alpha) * k.getCount() / collectionWordLenght);
            }
            if(Double.isNaN(userScore))
                userScore = 0;
            scores.add(new Score(userScore, u.getName()));

        }
        
        System.out.println("-------- PRP Ranking results --------");
        Collections.sort(scores);
        Collections.reverse(scores); // Changes the list to an ascending order.
        int rank = 0;
        for (Object o : scores) {
            Score s = (Score) o;
            rank++;
            //System.out.println("Ranked: " + s.getName()+ "with score: " +"\t"+ s.getScore()  ); 
            System.out.format("#%d: \t %-20s \t (PRPscore: %f)%n", rank, s.getName(), s.getScore());
        }

        return scores;
    }
}
