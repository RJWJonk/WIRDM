/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Philip
 */
public class RocchioRFB {
    List<String> OldQuery = new ArrayList();
    List<String> NewQuery = new ArrayList();
    List<TwitTwinsGUI.RankingEntry> ranking = new ArrayList<>();
    List<TwitTwinsGUI.RankingEntry> relevant = new ArrayList<>();
    List<String> UserKeywords;
    List<Score> BetaScores = new ArrayList<Score>();
    List<Score> GammaScores = new ArrayList<Score>();
    double alpha, beta, gamma;
    
    public RocchioRFB(List<String> OldQuery, List<TwitTwinsGUI.RankingEntry> ranking, List<TwitTwinsGUI.RankingEntry> relevant, double alpha, double beta, double gamma) {
        this.OldQuery = OldQuery;
        this.alpha = alpha;
        this.beta = beta;
        this.gamma = gamma;   
    }
    
    public List<String> getUpdatedQuery() {
        NewQuery = OldQuery;
        NewQuery.add("Test1");
        NewQuery.add("Test2");
        
        
//        for (int i = 0; i < ranking.size(); i++) {
//           UserKeywords = ranking.get(i).getKeywords();
//           for (int j = 0; j < UserKeywords.size(); j++ ) {
//               if ( UserKeywords.get(j) in BetaScores.get() ) {
//                   
//               }
//               BetaScores.add(new Score( 1.0, UserKeywords.get(j) ));
//           }
//        }
//        
//        
//        for (Object o : ranking) {
//            TwitTwinsGUI.RankingEntry u = (TwitTwinsGUI.RankingEntry) o;
//            Iterator iter = u.iterator();
//            while( iter.hasNext() ) {
//                UserData.KeyWord keyW = (UserData.KeyWord)iter.next(); // Get next keyword of user
//                word = keyW.getKeyWord();
//                tf = (double) keyW.getCount();
//                KwTfdata.put(word, tf);
//                //System.out.println(word +"\t" + tf); //For testing
//            }
//        
//        
//        if (relevant.get) {
//            
//        }
        
        
        return NewQuery;
    }
    
}
