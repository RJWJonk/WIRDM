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
 * @author s119503
 */
public class RocchioRFB {
    static int maxNewTerms = 10;
    static int maxTerms = 15;
    List<String> OldQuery = new ArrayList();
    List<String> newQuery = new ArrayList();
    Map<String, Word> rfbRInputMap = new HashMap<>();
    Map<String, Word> rfbNInputMap = new HashMap<>();
    List<TwitTwinsGUI.RankingEntry> ranking = new ArrayList<>();
    List<TwitTwinsGUI.RankingEntry> relevant = new ArrayList<>();
    Map<String, Score> alphaScores = new HashMap<>();
    Map<String, Score> betaScores = new HashMap<>();
    Map<String, Score> gammaScores = new HashMap<>();
    Map<String, Score> totalScores = new HashMap<>();
    double alpha, beta, gamma;
    
    public RocchioRFB(List<String> OldQuery, Map<String, Word> rfbRMap, Map<String, Word> rfbNMap, List<TwitTwinsGUI.RankingEntry> ranking, List<TwitTwinsGUI.RankingEntry> relevant, double alpha, double beta, double gamma) {
        this.OldQuery = OldQuery;
        this.rfbRInputMap = rfbRMap;
        this.rfbNInputMap = rfbNMap;
        this.ranking = ranking;
        this.relevant = relevant;
        this.alpha = alpha;
        this.beta = beta;
        this.gamma = gamma;   
    }
    
    public List<String> getUpdatedQuery() {
        
        for(String s:OldQuery){
            Word w = new Word(s,1);
            Score x = new Score(calculateScore(w,alpha), s);
            alphaScores.put(s, x);
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
        
        int relevantLength = relevant.size();
        int irrelevantLength = ranking.size()-relevantLength;
        
        putInTotalScores(alphaScores, 1, 1);
        putInTotalScores(betaScores, 1, relevantLength);
        putInTotalScores(gammaScores, -1, irrelevantLength);
        
        TreeMap<Double, String> sortedScores = new TreeMap<>();
        for(String key:totalScores.keySet()){
            sortedScores.put(totalScores.get(key).getScore(), key);
        }
        double rfbTreshold = 0.1;
        int i=0;
        while(sortedScores.lastKey()>rfbTreshold&&i<maxNewTerms&&newQuery.size()<maxTerms){
            newQuery.add(sortedScores.pollLastEntry().getValue());
            i++;
        }
        
        return newQuery;
    }
    
    private double calculateScore(Word w, double d){
        double s;
        s=w.getFrequency()*d;
        return s;
    }
    
    private void putInTotalScores(Map<String, Score> scoreMap, int plusmin, int numberOfDocs){
        for(String key:scoreMap.keySet()){
            double Score;
            if(totalScores.containsKey(key)){
               Score a = totalScores.get(key);
               Score = a.getScore()+plusmin*scoreMap.get(key).getScore()/numberOfDocs;
               Score s = new Score(Score, key);
               totalScores.remove(key);
               totalScores.put(key, s);
            }
            else{
            Score = plusmin*scoreMap.get(key).getScore()/numberOfDocs;
            Score s = new Score(Score, key);
            totalScores.put(key, s);
            }
        }
    }
    
}
