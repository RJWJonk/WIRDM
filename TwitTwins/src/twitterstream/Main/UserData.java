/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twitterstream.Main;

import java.util.Iterator;

/**
 *
 * @author s080440
 */
public class UserData implements Iterable {

    @Override
    public Iterator iterator() {
        return new UDIterator();
    }

    private class UDIterator implements Iterator<User> {

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public User next() {
            return null;
        }

    }

    private class User {

        public User(String name, int age, String location) {
            this.name = name;
            this.age = age;
            tweetCount = 0;
            tweetWordCount = 0;
            firstKW = null;
        }

        public void incCount() {
            tweetCount++;
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
        
        
        private final String name;
        private final int age;
        private int tweetCount;
        private int tweetWordCount;
        private KeyWord firstKW;

    }

    private class KeyWord {

        private final String word;
        private int wordCount;
        private KeyWord next;

        public KeyWord(String word) {
            this.word = word;
            wordCount = 0;
            next = null;
        }

        public void incCount() {
            wordCount++;
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
