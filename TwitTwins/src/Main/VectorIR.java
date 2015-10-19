/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import java.util.HashMap;
import Model.Word;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import com.google.common.collect.Sets;


/**
 *
 * @author Philip
 */
public class VectorIR {
    
    private final Map<String, Double> u1;
    private final Map<String, Double> u2;
    
    public VectorIR(Map<String, Double> v1, Map<String, Double> v2) {
        this.u1 = v1;
        this.u2 = v2;
    }

    
    
    static double cosine_similarity(Map<String, Double> v1, Map<String, Double> v2) {
            Set<String> both = Sets.newHashSet(v1.keySet());
            double score;
            both.retainAll(v2.keySet());
            double sclar = 0, norm1 = 0, norm2 = 0;
            for (String k : both) sclar += v1.get(k) * v2.get(k);
            for (String k : v1.keySet()) norm1 += v1.get(k) * v1.get(k);
            for (String k : v2.keySet()) norm2 += v2.get(k) * v2.get(k);
            score = sclar / Math.sqrt(norm1 * norm2);
            
            if ( Double.isNaN(score) ) {
                return 0.0;
            }
            else return score;
    }
    
    public void CompareVectors () {
        System.out.println( cosine_similarity(u1,u2) );
    }
  
}
