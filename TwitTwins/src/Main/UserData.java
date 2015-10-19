/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import Model.Word;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 *
 * @author s080440
 */
public class UserData implements Iterable, BiConsumer<String, Word> {

    private User firstU;
    private User last;
    private static int userCount = 0;
    //list of keywords to be stored in the data
    private List<String> keywords;

    public void sortByScore() { // fix this
        /*
         User currentU = firstU;
         int i =0;
         while(currentU != last && currentU.next != null)  {
         User nextU = currentU.next;
         while(nextU != null) {
         if (currentU.score > nextU.score){
         User tempU = currentU;
         currentU = nextU;                       
         nextU = tempU;
         }
         nextU=nextU.next;
         }
         //last = nextU;
         if(i==0) // after the first iteration, the first record is the highest 
         firstU = currentU;
         currentU = currentU.next;
         }*/
    }

    public UserData(List<String> keywords) {
        this.keywords = keywords;
    }

    public int getUserCount() {
        return userCount;
    }

    public User getUser(int index) {
        User returnU = firstU;
        while (index > 0) {
            returnU = returnU.next;
            index--;
        }
        return returnU;
    }

    public User getUser(String name) {
        User returnU = firstU;
        do {
            //System.out.println("Comparing -" + returnU.getName() + "- and =" + name + "=!");
            if (returnU.getName().equals(name)) {
                return returnU;
            } else {
                returnU = returnU.next;
            }
        } while (returnU != null);
        return null;
    }

    public void addUser(String user, int age, String gender, int tweetCount, Map<String, Word> data) {
        User u = new User(user, age, gender);
        if (firstU == null) {
            firstU = u;
        }
        if (last != null) {
            last.next = u;
        }
        last = u;
        Map<String, Word> userKeywords = new HashMap<String, Word>(data);
        Word w = null;
        for (String s : keywords) {
            KeyWord k = new KeyWord(s);
            if (userKeywords == null) {
                System.out.println("RETARD");
            }
            w = userKeywords.get(s);
            if (w != null) {
                k.setCount(userKeywords.get(s).getFrequency());
            } else {
                k.setCount(0);
            }
            u.setKeyWordAtEnd(k);
        }

        u.setTweetWordCount(calcWordTweetCount(data));
        u.setCount(tweetCount);
        userCount++;
    }

    @Override
    public Iterator iterator() {
        return new UDIterator(firstU);
    }

    //helper variable
    private int tempCount;

    private int calcWordTweetCount(Map<String, Word> data) {
        tempCount = 0;
        data.forEach(this);
        return tempCount;
    }

    @Override
    public void accept(String t, Word w) {
        tempCount += w.getFrequency();
    }

    private class UDIterator implements Iterator<User> {

        User next;

        private UDIterator(User firstU) {
            next = firstU;
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public User next() {
            User returnU = next;
            next = next.next;
            return returnU;
        }

    }

    public class User implements Iterable {

        private final String name;
        private final int age;
        private final String gender;
        private int tweetCount;
        private int tweetWordCount;
        private KeyWord firstKW;
        private User next;
        private double score;
        private int cluster_number;

        public User(String name, int age, String gender) {
            this.name = name;
            this.age = age;
            this.gender = gender;
            tweetCount = 0;
            tweetWordCount = 0;
            firstKW = null;
        }

        public User(String name, List<KeyWord> keywordList) {
            this.name = name;
            firstKW = null;
            age = 0;
            tweetCount = 0;
            gender = null;
            tweetWordCount = 0;
            for (int i = 0; i < keywordList.size(); i++) {
                setKeyWordAtEnd(keywordList.get(i));
            }
        }

        public void setCluster(int n) {
            this.cluster_number = n;
        }

        public int getCluster() {
            return this.cluster_number;
        }

        public void setScore(double sc) {
            score = sc;
        }

        public double getScore() {
            return score;
        }

        public void setCount(int c) {
            tweetCount = c;
        }

        public void setTweetWordCount(int c) {
            tweetWordCount = c;
        }

        public void setFirstKeyWord(KeyWord kw) {
            firstKW = kw;
        }

        public KeyWord getFirstKeyWord() {
            return firstKW;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        public String getGender() {
            return gender;
        }

        public int getTweetCount() {
            return tweetCount;
        }

        public int getWordTweetCount() {
            return tweetWordCount;
        }

        private void setKeyWordAtEnd(KeyWord kw) {
            if (firstKW == null) {
                firstKW = kw;
            } else {
                KeyWord k = firstKW;
                while (k.next != null) {
                    k = k.next;
                }
                k.next = kw;
            }
        }

        public KeyWord getKeyWord(int index) {
            KeyWord returnKW = firstKW;
            while (index > 0) {
                returnKW = returnKW.next;
                index--;
            }
            return returnKW;
        }

        @Override
        public Iterator iterator() {
            return new KWIterator(firstKW);
        }

    }

    private class KWIterator implements Iterator<KeyWord> {

        KeyWord next;

        private KWIterator(KeyWord firstKW) {
            next = firstKW;
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public KeyWord next() {
            KeyWord returnKW = next;
            next = next.next;
            return returnKW;
        }

    }

    public class KeyWord {

        private final String word;
        private double wordCount;
        private KeyWord next;

        public KeyWord(String word) {
            this.word = word;
            wordCount = 0;
            next = null;
        }

        public void setCount(double c) {
            wordCount = c;
        }

        public void setNext(KeyWord next) {
            this.next = next;
        }

        public String getKeyWord() {
            return word;
        }

        public double getCount() {
            return wordCount;
        }

        public KeyWord getNext() {
            return next;
        }

    }
}
