/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

/**
 *
 * @author Ruben
 */
public class TwitterStream {

    private final Queue<Tweet> tweetQueue = new LinkedList<>();
    private final static Twitter twitter = authenticate();
    User u;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        TwitterStream stream = new TwitterStream(null);
        stream.toString();
    }

    public TwitterStream(String[]termArray) {
        List<String> terms = new ArrayList<>();
//        for(String term: termArray)
//        {
//        	terms.add(term);
//        }
        terms.add("weert");
        terms.add("landgraaf");
        terms.add("amsterdam");
        query(terms);
        System.out.println("Retrieved " + tweetQueue.size() + " tweets.");
        File f = new File("tweets.txt");

        try {
            PrintStream writer = new PrintStream(f);
            for (Tweet t : tweetQueue) {
                writer.println(t.toString());
            }
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(TwitterStream.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static Twitter authenticate() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("b0DV73vfaiAinFwkc0BsaGWRi")
                .setOAuthConsumerSecret("lM35xfcnaJQDaUuoZsB749bNv1GbA8dbItDH9VlglmbAefUZn5")
                .setOAuthAccessToken("2387531042-QRMVloVxBoNYntQMvKs7dZHN8ybe3ciwS34JzBz")
                .setOAuthAccessTokenSecret("t3RtJdrcYaf9EfDRxVgD9vO4FXYh8vIv0XVfC1D4ojkF8");
        TwitterFactory tf = new TwitterFactory(cb.build());
        return tf.getInstance();
    }

    public void query(List<String> terms) {
        String query = "";
        for (String term : terms) {
            query += "(" + term + ")" + " OR ";
        }
        query(query.substring(0, query.length() - 4));
    }

    public void query(String term) {
        try {
            Query query = new Query(term);
            query.setLang("en");
            query.setCount(100);
            
            System.out.println(query.toString());
            QueryResult result;
               do {
            result = twitter.search(query);
            List<Status> tweets = result.getTweets();
            for (Status tweet : tweets) {
                Tweet t = new Tweet(tweet.getUser(), tweet.getText(), tweet.getLang());
                this.tweetQueue.add(t);
                System.out.println(t.toString());
                //     }
            }
                } while ((query = result.nextQuery()) != null);
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to search tweets: " + te.getMessage());
        }
    }
}

