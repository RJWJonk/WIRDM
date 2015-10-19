/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

/**
 *
 * @author s119503
 */
public class Score implements Comparable<Score>{
    double score;
        String name;

        public Score(double score, String name) {
        this.score = score;
        this.name = name;
        }

        @Override
        public int compareTo(Score o) {
            return score < o.score ? -1 : score > o.score ? 1 : 0;
        }
            
        public double getScore() {
            return score;
        }
            
        public String getName() {
            return name;
        }
}
