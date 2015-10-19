/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import static Main.TweetsExtractor.wordMap;
import Model.Word;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author s119503
 */
public class NameLookup {
    
    public static void main(String[] args){
//        NameLookup nl = new NameLookup();
        readStoreNames();
    }
    
    public static void readStoreNames() {
        BufferedReader br = null;
        Map<String, Double> nameMap = new HashMap<>();

        try {
            String sCurrentLine;
            br = new BufferedReader(new FileReader("src/Datafiles/dist.female.first.txt"));

            int i=0;
            while ((sCurrentLine = br.readLine()) != null) {
                Scanner s = new Scanner(sCurrentLine);
                String name = s.next();
                String percentageString = s.next();
                double percentage = Double.parseDouble(percentageString);
                System.out.println("arr[0] = " + name);
                System.out.println("arr[1] = " + percentageString);
                nameMap.put(name, percentage);
                i++;
            }   

        } catch (IOException e) {
           e.printStackTrace();
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    
    
    
    public String getGender(String name){
        String a = "male";
        return a;
    }
}
