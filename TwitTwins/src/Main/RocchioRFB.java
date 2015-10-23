/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import Model.Word;
import java.util.*;

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
    Map<String, Score> totalScores = new HashMap<>();
    double alpha, beta, gamma;
    
    public RocchioRFB(List<String> OldQuery, Map<String, Word> rfbRMap, Map<String, Word> rfbNMap, List<TwitTwinsGUI.RankingEntry> ranking, List<TwitTwinsGUI.RankingEntry> relevant, double alpha, double beta, double gamma) {
        this.OldQuery = OldQuery;
        this.rfbRInputMap = rfbRMap;
        this.rfbNInputMap = rfbNMap;
        this.alpha = alpha;
        this.beta = beta;
        this.gamma = gamma;   
    }
    
    public List<String> getUpdatedQuery() {
        NewQuery = OldQuery;
        
        for(String w:OldQuery){
            
        }
        
        for(String key:rfbRInputMap.keySet()){
            Word w = rfbRInputMap.get(key);
            Score s = new Score(calculateScore(w,beta), w.getWord());
            betaScores.put(w.getWord(), s);
        }
        
        for(String key:rfbNInputMap.keySet()){
            Word w = rfbNInputMap.get(key);
            Score s = new Score(calculateScore(w,gamma), w.getWord());
            gammaScores.put(w.getWord(), s);
        }
        
        int betaScoreLength = betaScores.size();
        int gammaScoreLength = gammaScores.size();
        
        for(String key:betaScores.keySet()){
            double Score = betaScores.get(key).getScore()/betaScoreLength;
            Score s = new Score(Score, key);
            totalScores.put(key, s);
        }
        for(String key:gammaScores.keySet()){
            double Score;
            if(totalScores.containsKey(key)){
               Score a = totalScores.get(key);
               Score = a.getScore()-gammaScores.get(key).getScore()/gammaScoreLength;
               Score s = new Score(Score, key);
               totalScores.remove(key);
               totalScores.put(key, s);
            }
            else{
            Score = gammaScores.get(key).getScore()/gammaScoreLength;
            Score s = new Score(Score, key);
            totalScores.put(key, s);
            }
        }
        
        TreeMap<Double, String> sortedScores = new TreeMap<>();
        for(String key:totalScores.keySet()){
            sortedScores.put(totalScores.get(key).getScore(), key);
        }
        double rfbTreshold = 0.000001;
        while(sortedScores.lastKey()>rfbTreshold){
            NewQuery.add(sortedScores.pollLastEntry().getValue());
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
