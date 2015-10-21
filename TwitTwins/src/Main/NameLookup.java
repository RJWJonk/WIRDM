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
    
    private static final Map<String, Double> nameMapFemale = new HashMap<>();
    private static final Map<String, Double> nameMapMale = new HashMap<>();
    
//    public static void main(String[] args){
//        readStoreNames(nameMapFemale, "src/Datafiles/dist.female.first.txt");
//        readStoreNames(nameMapMale, "src/Datafiles/dist.male.first.txt");
//        System.out.println(nameMapFemale.get("PATRICIA"));
//    }
    
    public NameLookup(){
        readStoreNames(nameMapFemale, "src/Datafiles/dist.female.first.txt");
        readStoreNames(nameMapMale, "src/Datafiles/dist.male.first.txt");
    }
    
    public static void readStoreNames(Map<String, Double> nameMap, String file) {
        BufferedReader br = null;

        try {
            String sCurrentLine;
            br = new BufferedReader(new FileReader(file));

            int i=0;
            while ((sCurrentLine = br.readLine()) != null) {
                Scanner s = new Scanner(sCurrentLine);
                String name = s.next();
                String percentageString = s.next();
                double percentage = Double.parseDouble(percentageString);
                String nameLowerCase = name.toLowerCase();
                nameMap.put(nameLowerCase, percentage);
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
        String a;
        double b;
        
        if(nameMapFemale.containsKey(name)&&nameMapMale.containsKey(name)){
            if(nameMapFemale.get(name)>=nameMapMale.get(name)){
                a="female";
//                b=nameMapFemale.get(name);
            }
            else{
                a="male";
//                b=nameMapMale.get(name);
            }
        }
        else if(nameMapFemale.containsKey(name)){
            a="female";
//            b=nameMapFemale.get(name);
        }
        else if (nameMapMale.containsKey(name)){
            a="male";
//            b=nameMapMale.get(name);
        }
        else {
            a="n.a.";
//            b=0;
        }
        
        return a;
    }
}
