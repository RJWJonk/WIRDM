/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import Model.Word;
import java.util.ArrayList;
import java.util.Queue;
import java.util.TreeMap;

/**
 *
 * @author s080440
 */
public class TwitMain {
   
    
    
    public static void main(String[] args) {
        TweetsExtractor te = new TweetsExtractor();
        TreeMap<String, Word> data = te.extractUser("rekkleslol");
        int i = 5;
        ArrayList<String> keywords = new ArrayList();
        for (Word w : data.values()) {
            if (i == 0) break; else i--;
            keywords.add(w.getWord());
        }
        
        UserData udata = new UserData(keywords);
        
        Queue<Tweet> names = te.query(keywords);
        
        int n = 12;
        while (n > 0 && !names.isEmpty()) {
            n--;
            
            Tweet t = names.poll();
            String name = t.getUser().getScreenName();
            TreeMap<String, Word> user = te.extractUser(name);
            udata.addUser(name, 0, null, 5, user);
        }
        
        for (Object o : udata) {
            UserData.User u = (UserData.User) o;
            System.out.println(u.getName());
        }
        
    }
}
