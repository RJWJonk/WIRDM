/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import Model.Word;
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

    //list of keywords to be stored in the data
    private List<String> keywords;

    public UserData(List<String> keywords) {
        this.keywords = keywords;
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

        for (String s : keywords) {
            KeyWord k = new KeyWord(s);
            if (data == null) {
                System.out.println("RETARD");
            }
            Word w = data.get(s);
            if (w != null) {
                k.setCount(data.get(s).getFrequency());
            } else {
                k.setCount(0);
            }
            u.setKeyWordAtEnd(k);
        }

        u.setTweetWordCount(calcWordTweetCount(data));
        u.setCount(tweetCount);
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

        public User(String name, int age, String gender) {
            this.name = name;
            this.age = age;
            this.gender = gender;
            tweetCount = 0;
            tweetWordCount = 0;
            firstKW = null;
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
                returnKW = firstKW.next;
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
        private int wordCount;
        private KeyWord next;

        public KeyWord(String word) {
            this.word = word;
            wordCount = 0;
            next = null;
        }

        public void setCount(int c) {
            wordCount = c;
        }

        public void setNext(KeyWord next) {
            this.next = next;
        }

        public String getKeyWord() {
            return word;
        }

        public int getCount() {
            return wordCount;
        }

        public KeyWord getNext() {
            return next;
        }

    }
}
