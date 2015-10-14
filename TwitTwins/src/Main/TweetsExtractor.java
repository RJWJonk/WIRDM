package Main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import Model.Word;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TweetsExtractor {


    //Authorization
    private final static Twitter twitter = authenticate();

    public static Map<String, Word> wordMap; //hash map with extracted words from tweets
    public static TreeMap<String, Word> sorted_map; // hash map which is sorted by frequenty 
    public static Map<String, String> stopWords;
    public static String delims = ", !.)(\"?\'�_<>-|;";

    public static void main(String[] args) throws IOException {
        TweetsExtractor te = new TweetsExtractor();
        System.out.println(te.toString());
    }

    public TweetsExtractor() {
        
    }
    
    public TreeMap<String, Word> extractUser(String u) {
        //init values
        wordMap = new HashMap<>();
        stopWords = new HashMap<>();
        ValueComparator wvc = new ValueComparator(wordMap);
        sorted_map = new TreeMap<>(wvc);

        Queue<Tweet> tweets = queryUser(u);
        tokenizing(tweets);

        //read stopwords from file and store it in hashmap
        readStoreStopWords();

        //NOTICE only enable either DemoReadFromTxtFile or tokenizing, not both.
        // Read text from textfile and tokenize each line
        //DemoReadFromTxtFile();	     	
        //tokenizing on real tweets
        //tokenizing(readTweetsDemo());
        //remove stopWords
        removeStopWords();
        //apply stemming by provided API
        applyStemming();
        //Test- printing results
        //PrintTestResult();

//        int termAmount = 5;
//        String[] termArray = new String[termAmount];
//        int i = 0;
//        for (Object value : sorted_map.values()) {
//
//            Word word = (Word) value;
//            if (!isInteger(word.getWord())) {
//                termArray[i] = word.getWord();
//                if (i >= termAmount - 1) {
//                    break;
//                }
//                i++;
//            }
//
//        }
//                List<String> terms = new ArrayList<>();
//        for(String term: termArray)
//        {
//        	terms.add(term);
//        }
//        
//        tweets = query(terms);
//        File f = new File("tweets.txt");
//
//        try {
//            PrintStream writer = new PrintStream(f);
//            for (Tweet t : tweets) {
//                writer.println(t.toString());
//                //System.out.println(t.toString());
//            }
//            writer.close();
//        } catch (IOException ex) {
//
//        }
//        

        return sorted_map;
    }

    /**
     * Read from a txt file to test if tokenizer works
     *
     */
    public void DemoReadFromTxtFile() {
        try (BufferedReader br = new BufferedReader(new FileReader("bigtext.txt"))) {
            String sCurrentLine;
            //extract text, Just for testing on a large file with text
            while ((sCurrentLine = br.readLine()) != null) {
                tokenizing(sCurrentLine);
            }
            sorted_map.putAll(wordMap);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tokenizing from text
     *
     * @param text
     */
    public TreeMap<String, Word> tokenizing(String text) {
        StringTokenizer st = new StringTokenizer(text, delims);
        // extract each single words
        String concatWord = "";
        while (st.hasMoreTokens()) {
            String nextWord = st.nextToken(); //convert everything to lower case
            if(Character.isUpperCase(nextWord.charAt(0)))
            {
                concatWord = concatWord + nextWord + " ";
            }
            else
            {
                if(concatWord.isEmpty())
                {
                    putWordInWordMap(nextWord);
                }
                else
                {
                    putWordInWordMap(nextWord);
                    putWordInWordMap(concatWord);
                    concatWord = "";
                }

            }
           // putWordInWordMap(nextWord);
        }
        if(!concatWord.isEmpty())
            putWordInWordMap(concatWord);
        sorted_map.putAll(wordMap);
        return sorted_map;
    }

    
    public void putWordInWordMap(String word)
    {
        if (wordMap.get(word) == null) {
                identifyTypeOfWord(word);
            } else {
                //update the word
                Word tempWord = wordMap.get(word);
                tempWord.incrementFreq();
                wordMap.put(word, tempWord);
            }    
    }
    
    public TreeMap<String, Word> tokenizing(Queue<Tweet> input) {
        String concat = "";
        for (Tweet t : input) {
            tokenizing(t.getTweet()); 
        }
        return sorted_map;
    }


//    /**
//     * Using real tweets data to test what kind of results it gives Will be
//     * changed later on
//     *
//     * @return
//     */
//    public String readTweetsDemo() {
//        OAuthConsumer consumer = new DefaultOAuthConsumer(ConsumerKey, ConsumerSecret);
//        consumer.setTokenWithSecret(AccessToken, AccessSecret);
//        tweetsTxt = "";
//        // Prepare request
//        URL url;
//        try {
//            for (int j = 1; j <= 16; j++) {
//                System.out.println("Loading tweets: " + (float) j / 16 * 100 + "%.");
//                //REST API - request to return tweets from users as a JSON string 200 tweets max per page
//                url = new URL("https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=" + twitterUser + "&lang=en&count=200&page=+" + j);
//                HttpURLConnection request = (HttpURLConnection) url.openConnection();
//                consumer.sign(request);
//                request.connect();
//
//                BufferedReader in = new BufferedReader(
//                        new InputStreamReader(request.getInputStream()));
//                String inputLine;
//                StringBuffer response = new StringBuffer();
//
//                while ((inputLine = in.readLine()) != null) {
//                    response.append(inputLine);
//                }
//                in.close();
//
//                //parse json
//                String jsonString = "{\"tweets\":" + response.toString() + "}";
//                JSONObject obj = new JSONObject(jsonString);
//
//                JSONArray arr = obj.getJSONArray("tweets");
//                for (int i = 0; i < arr.length(); i++) {
//                    if (arr.getJSONObject(i).has("text")) {
//                        tweetsTxt += " " + arr.getJSONObject(i).getString("text");
//                    }
//                }
//            }
//        } catch (MalformedURLException e1) {
//            // TODO Auto-generated catch block
//            e1.printStackTrace();
//        } catch (OAuthMessageSignerException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (OAuthExpectationFailedException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (OAuthCommunicationException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        return tweetsTxt;
//    }

    /**
     * Using the stemming library (source file) provided by Porter. Stemming
     * will be used after tokenizing process and removal of stopwords 1. stemmed
     * word is already stemmed. 2. Stemmed word does not exist yet 3. Stemmed
     * word already exist
     */
    public void applyStemming() {
        for (Object value : sorted_map.values()) {
            Word word = (Word) value;
            Stemmer s = new Stemmer();
            char[] chars = word.getWord().toCharArray();
            s.add(chars, chars.length);
            s.stem();

            // only if word needs to be stemmed needs to be processed
            if (!s.toString().equals(word.getWord())) {
                int freq = 0;
                //if stemmed word already exist, sum the current frequency.
                if (wordMap.get(s.toString()) != null) {
                    //update frequency
                    freq = ((Word) wordMap.get(s.toString())).getFrequency();
                }
                Word newWord = new Word(s.toString(), word.getRealType());
                newWord.setFrequency(freq + word.getFrequency());
                wordMap.put(s.toString(), newWord);
                wordMap.remove(word.getWord());
            }
        }
        sorted_map.clear(); // must clear it first in order to sort it again
        sorted_map.putAll(wordMap);

    }

    /**
     * remove stopwords from hashmap, start with identifying the most frequent
     * word stored in hasmap. NOTICE: there could be multiple ways to remove
     * stopwords: 1. remove the 10-or more... most common stopwords 2. if 20
     * keywords (which are no stopwords) is passed, stop with removing
     * stopwords, it does not matter anymore.
     *
     * The Algorithm below will just remove all stopwords.
     */
    public void removeStopWords() {
        for (Object value : sorted_map.values()) {
            Word word = (Word) value;
            if (stopWords.get(word.getWord()) != null) {
                wordMap.remove(word.getWord());
            }
        }
        sorted_map.clear(); // must clear it first in order to sort it again
        sorted_map.putAll(wordMap);
    }

    /**
     * Read all stopwords from terrier-stop file, and store it in HashMap
     */
    public void readStoreStopWords() {
        // TODO Auto-generated method stub
        try (BufferedReader br = new BufferedReader(new FileReader("src\\Datafiles\\terrier-stop.txt"))) {
            String sCurrentLine;
            //extract text, Just for testing on a large file with text
            while ((sCurrentLine = br.readLine()) != null) {
                stopWords.put(sCurrentLine, sCurrentLine);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Identify the most frequent type of words of a Tweet, which is: - Normal
     * word - Hashtag - At sign - Weblink
     *
     * @param wordInput
     */
    public void identifyTypeOfWord(String wordInput) {
        if (wordInput.startsWith("www") || wordInput.startsWith("http://") || wordInput.startsWith("https://")) {
            wordMap.put(wordInput, new Word(wordInput, Word.WEBLINK));
        } else if (wordInput.startsWith("@")) {
            wordMap.put(wordInput, new Word(wordInput, Word.AT));
        } else if (wordInput.startsWith("#")) {
            wordMap.put(wordInput, new Word(wordInput, Word.HASHTAG));
        } else {
            wordMap.put(wordInput, new Word(wordInput, Word.WORD));
        }
    }

    public boolean isInteger(String s) {
        return isInteger(s, 10);
    }

    public boolean isInteger(String s, int radix) {
        if (s.isEmpty()) {
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            if (i == 0 && s.charAt(i) == '-') {
                if (s.length() == 1) {
                    return false;
                } else {
                    continue;
                }
            }
            if (Character.digit(s.charAt(i), radix) < 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Print test results with identified words
     *
     */
    public void PrintTestResult() {
        int i = 0;
        for (Object value : sorted_map.values()) {
            if (i < 100) {
                Word word = (Word) value;
                //if(word.getRealType() == Word.WORD)
                {
                    System.out.println("Word=" + (word.getWord() + " ||"
                            + "Type:" + word.getType() + "|| "
                            + "Frequency=" + word.getFrequency()));
                }
            } else {
                break;
            }
            i++;
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

    public Queue<Tweet> query(List<String> terms) {
        String query = "";
        for (String term : terms) {
            query += "(" + term + ")" + " OR ";
        }
        return query(query.substring(0, query.length() - 4));
    }

    public Queue<Tweet> query(String term) {
        Queue<Tweet> tweetQueue = new LinkedList<>();
        try {
            Query query = new Query(term);
            query.setLang("en");
            query.setCount(100);

            System.out.println(query.toString());
            QueryResult result;
            //    do {
            
            result = twitter.search(query);
            tweetQueue.clear();
            List<Status> tweets = result.getTweets();
            for (Status tweet : tweets) {
                Tweet t = new Tweet(tweet.getUser(), tweet.getText(), tweet.getLang());
                tweetQueue.add(t);


                tweetQueue.add(t);
                //System.out.println(tweet.getUser().getName());
                //     }
                //}
            } //while ((query = result.nextQuery()) != null);
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to search tweets: " + te.getMessage());
        }
        return tweetQueue;
    }

    public Queue<Tweet> queryUser(String user) {
        Queue<Tweet> tweetQueue = new LinkedList<>();
        try {
            int i = 1;
            Paging paging = new Paging(1, 200);
            List<Status> tweets = twitter.getUserTimeline(user, paging);
            while (!tweets.isEmpty()) {
                for (Status tweet : tweets) {
                    Tweet t = new Tweet(tweet.getUser(), tweet.getText(), tweet.getLang());
                    tweetQueue.add(t);
                }
                i += 1;
                paging = new Paging(i, 200);
                tweets = twitter.getUserTimeline(user, paging);
            }
            System.out.println("amount of tweets returned of " + user + ": " + tweetQueue.size());
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to search tweets: " + te.getMessage());
        }
        return tweetQueue;
    }

}
