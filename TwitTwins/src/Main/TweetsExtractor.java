package Main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import Model.Word;
import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

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
    public static int collectionFrequncy = 0;
    public static Map<String, Word> wordMap; //hash map with extracted words from tweets
    public static TreeMap<String, Word> sorted_map; // hash map which is sorted by frequenty 
    public static Map<String, String> stopWords;
    public static String delims = ", !.)(\"?\'�_<>|;:/";

    public TweetsExtractor() {

    }

    /**
     * 
     * @param u - a given twitter user
     * @return A Map with words sorted by its frequency count
     */
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
	     	
        //tokenizing on real tweets
        //remove stopWords
        removeStopWords();
        //check for synonyms and use the most common keyword
        checkForSynonyms();
        //apply stemming by provided API
        applyStemming();

        sorted_map.putAll(wordMap);
        return sorted_map;
    }
    
    /**
     * 
     * @param u - a given twitter user
     * @return A Map with words sorted by its frequency count, used for Rocchio relevant feedback
     */
    public Map<String, Word> extractUserM(String u) {
        //init values
        wordMap = new HashMap<>();
        stopWords = new HashMap<>();
        ValueComparator wvc = new ValueComparator(wordMap);

        Queue<Tweet> tweets = queryUser(u);
        tokenizing(tweets);
        //read stopwords from file and store it in hashmap
        readStoreStopWords();
        //remove stopWords
        removeStopWords();
        //check for synonyms and use the most common keyword
        checkForSynonyms();
        //apply stemming by provided API
        applyStemming();
        return wordMap;
    }

    /**
     * Tokenizing from text
     * Also processing bi-words, will happen at the same time.
     *
     * @param text tweet(s) as input
     */
    public Map<String, Word> tokenizing(String text) {
        StringTokenizer st = new StringTokenizer(text, delims);
        // extract each single words
        String concatWord = "";
        while (st.hasMoreTokens()) {
            String nextWord = st.nextToken(); //convert everything to lower case
            if (Character.isUpperCase(nextWord.charAt(0))) {
                concatWord = concatWord + nextWord + " ";
            } else {
                if (concatWord.isEmpty()) {
                    putWordInWordMap(nextWord.trim().toLowerCase());
                } else {
                    putWordInWordMap(nextWord.trim().toLowerCase());
                    putWordInWordMap(concatWord.trim().toLowerCase());
                    concatWord = "";
                }

            }
        }
        if (!concatWord.isEmpty()) {
            putWordInWordMap(concatWord.trim().toLowerCase());
        }
        return wordMap;
    }

    /**
     * Check if word already exist and:
     * 1. if exist, increase frequency count of word
     * 2. not exist, put word in word map
     * @param word 
     */
    public void putWordInWordMap(String word) {
        collectionFrequncy++;
        if (wordMap.get(word) == null) {
            identifyTypeOfWord(word);
        } else {
            //update the word
            Word tempWord = wordMap.get(word);
            tempWord.incrementFreq();
            wordMap.put(word, tempWord);
        }
    }

    /**
     * For every single tweet, use tokenizer to split it in words
     * 
     * @param input a collection as tweets, represented as an queue
     * @return 
     */
    public Map<String, Word> tokenizing(Queue<Tweet> input) {
        String concat = "";
        for (Tweet t : input) {
            tokenizing(t.getTweet());
        }
        return wordMap;
    }

     /**
     * Check for synonyms, and use the most common words. e.g. ny --> New York
     * Notice, it will only perform for the 50 most occured words, otherwise it will takes to much time.
     * 
     */
    public void checkForSynonyms() {
        sorted_map.putAll(wordMap);
        System.setProperty("wordnet.database.dir", "WordNet-3.0//dict//");
        NounSynset nounSynset; 
        int i =50;
        for (Object value : sorted_map.values()) {
            if (i == 0) break; else i--;
            Word word = (Word) value;
            WordNetDatabase database = WordNetDatabase.getFileInstance(); 
            Synset[] synsets = database.getSynsets(word.getWord(), SynsetType.NOUN, true); 
            if(synsets.length > 0)
            {
                nounSynset = (NounSynset)(synsets[0]);    
                int freq = 0;
                //if synonym already exist, sum the current frequency.
                if (wordMap.get(nounSynset.getWordForms()[0].toLowerCase()) != null) {
                    //update frequency
                    freq = ((Word) wordMap.get(nounSynset.getWordForms()[0].toLowerCase())).getFrequency();
                }
                Word newWord = new Word(nounSynset.getWordForms()[0].toLowerCase(), word.getRealType());
                newWord.setFrequency(freq + word.getFrequency());
                wordMap.put(nounSynset.getWordForms()[0].toLowerCase(), newWord);
                wordMap.remove(word.getWord());
            }
        }
        sorted_map.clear();
    }
    /**
     * Using the stemming library (source file) provided by Porter. Stemming
     * will be used as the last step of pre-processing. Three options that we are dealing with: 
     * 1. stemmed word is already stemmed. 
     * 2. Stemmed word does not exist yet 
     * 3. Stemmed word already exist
     */
    public void applyStemming() {
        Map<String, Word> tempMap = new HashMap<String, Word>(wordMap);
        for (Object value : tempMap.values()) {
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
        Map<String, Word> tempMap = new HashMap<String, Word>(wordMap);
        for (Object value : tempMap.values()) {
            Word word = (Word) value;
            if (stopWords.get(word.getWord()) != null) {
                wordMap.remove(word.getWord());
            } else if (!word.getWord().matches("[a-zA-Z0-9 #-]+")) {
                wordMap.remove(word.getWord());
            } else if (word.getWord().matches("\\d+")) {
                wordMap.remove(word.getWord());
            }

        }
    }

    /**
     * Read all stopwords from terrier-stop file, and store it in HashMap
     */
    public void readStoreStopWords() {
        // TODO Auto-generated method stub
        try (BufferedReader br = new BufferedReader(new FileReader("src/Datafiles/terrier-stop.txt"))) {
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

        Random r = new Random();
        int choice = r.nextInt(3);

        ConfigurationBuilder cb = new ConfigurationBuilder();
        TwitterFactory tf;

        switch (choice) {
 
            case 2:
                cb.setDebugEnabled(true)
                        .setOAuthConsumerKey("w1T44RXxbwX2lTnd9E8p5Lvcj")
                        .setOAuthConsumerSecret("SMUclndm8hXAmGgGc6L3zZALOr2G9hzf9oVZ4wspAolMvjsOVd")
                        .setOAuthAccessToken("1186203860-VgllGCnzwmZw6RxvY3dq6Mr9Aofc4gKuUmetHbQ")
                        .setOAuthAccessTokenSecret("w1SssJ4lWFtrUy3qsnZcmzYwew5JONN7Y1kuPzBQPiiZo");
                tf = new TwitterFactory(cb.build());
                return tf.getInstance();
            case 1:
                cb.setDebugEnabled(true)
                        .setOAuthConsumerKey("b0DV73vfaiAinFwkc0BsaGWRi")
                        .setOAuthConsumerSecret("lM35xfcnaJQDaUuoZsB749bNv1GbA8dbItDH9VlglmbAefUZn5")
                        .setOAuthAccessToken("2387531042-QRMVloVxBoNYntQMvKs7dZHN8ybe3ciwS34JzBz")
                        .setOAuthAccessTokenSecret("t3RtJdrcYaf9EfDRxVgD9vO4FXYh8vIv0XVfC1D4ojkF8");
                tf = new TwitterFactory(cb.build());
                return tf.getInstance();
            case 0:
            default:
                cb.setDebugEnabled(true)
                        .setOAuthConsumerKey("kw5huUc5CmdzkhXUjC229GPqa")
                        .setOAuthConsumerSecret("ZjwZhzLJQzPXPCtUKZPJu703SgTvBhFKM3t3Zw30lZ7ceYK4e9")
                        .setOAuthAccessToken("3781208477-LrQhxUXOn5Uq2xI24OYZhv6Mv8bq5meP0nIamcJ")
                        .setOAuthAccessTokenSecret("O29jnk6k2DGHwPlZten6C4T67OTN6V3ybsQnOUlZAjKVN");
                tf = new TwitterFactory(cb.build());
                return tf.getInstance();

        }
    }

    /**
     * Finding tweets by using the top important keywords and store the users, amount of key
     * 
     * @param terms - given terms, which are the top important keywords
     * @param n - the amount of tweets we want to return
     * @return 
     */
    public Queue<Tweet> query(List<String> terms, int n) {
        Queue<Tweet> tweetQueue = new LinkedList<>();
        Map<String, String> diffUsers = new HashMap<String, String>();
        tweetQueue.clear();
        for (String term : terms) {
            query(tweetQueue, diffUsers, term, n/terms.size());
        }

        return tweetQueue;
    }

    /**
     * Finding tweets by using a single term, and store the users.
     * @param tweetQueue
     * @param diffUsers - We only want to store unique users.
     * @param term - given keyword
     * @param n
     * @return 
     */
    public Queue<Tweet> query(Queue<Tweet> tweetQueue, Map<String, String> diffUsers, String term, int n) {
        try {
            Query query = new Query(term + "+exclude:retweets ");
            query.setLang("en");
            query.setCount(n);

            System.out.println(query.toString());
            QueryResult result;
            //    do {

            result = twitter.search(query);
            //tweetQueue.clear();
            List<Status> tweets = result.getTweets();
            for (Status tweet : tweets) {
                if (diffUsers.get(tweet.getUser().getName()) == null) {
                    Tweet t = new Tweet(tweet.getUser(), tweet.getText(), tweet.getLang());
                    tweetQueue.add(t);
                    diffUsers.put(tweet.getUser().getName(), tweet.getUser().getName());
                }

            } //while ((query = result.nextQuery()) != null);
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to search tweets: " + te.getMessage());
        }
        return tweetQueue;
    }

    /**
     * Finding tweets of a given user, could be initial user or other users.
     * Just perform a single request and return (at most) 200 tweets.
     * @param user - given user
     * @return 
     */
    public Queue<Tweet> queryUser(String user) {
        Queue<Tweet> tweetQueue = new LinkedList<>();
        try {
            int i = 1;
            Paging paging = new Paging(1, 200);
            List<Status> tweets = twitter.getUserTimeline(user, paging);
            while (!tweets.isEmpty() && i < 2) {
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
