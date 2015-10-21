/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import Model.Word;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Philip
 */
public class RocchioRFB {
    List<String> OldQuery = new ArrayList();
    List<String> NewQuery = new ArrayList();
    Map<String, Word> rfbRInputMap = new HashMap<>();
    Map<String, Word> rfbNInputMap = new HashMap<>();
    List<TwitTwinsGUI.RankingEntry> ranking = new ArrayList<>();
    List<TwitTwinsGUI.RankingEntry> relevant = new ArrayList<>();
    List<String> UserKeywords;
    Map<String, Score> betaScores = new HashMap<>();
    Map<String, Score> gammaScores = new HashMap<>();
    double alpha, beta, gamma;
    
    public RocchioRFB(List<String> OldQuery, Map<String, Word> rfbRMap, Map<String, Word> rfbNMap, List<TwitTwinsGUI.RankingEntry> ranking, List<TwitTwinsGUI.RankingEntry> relevant, double alpha, double beta, double gamma) {
        this.OldQuery = OldQuery;
        this.rfbRInputMap = rfbRMap;
        this.alpha = alpha;
        this.beta = beta;
        this.gamma = gamma;   
    }
    
    public List<String> getUpdatedQuery() {
        NewQuery = OldQuery;
        NewQuery.add("Test1");
        NewQuery.add("Test2");
        
        for(String key:rfbRInputMap.keySet()){
            Word w = rfbRInputMap.get(key);
            Score s = new Score(calculateScore(w,beta), w.getWord());
            betaScores.put(w.getWord(), s);
        }
        
        
        
        
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
    
    private double calculateScore(Word w, double a){
        double s;
        s=w.getFrequency()*a;
        return s;
    }
    
}
