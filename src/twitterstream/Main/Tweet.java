package Main;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import twitter4j.User;

/**
 *
 * @author Ruben
 */
public class Tweet {

    private final User user;
    private final String tweet;
    private final String language;

    public Tweet(User user, String text, String lang) {
        this.user = user;
        tweet = text;
        language = lang;
    }

    public User getUser() {
        return user;
    }

    public String getTweet() {
        return tweet;
    }

    public String getLanguage() {
        return language;
    }

    
    @Override
    public String toString() {
        return user.getName() + " - " + tweet;
    }
}
