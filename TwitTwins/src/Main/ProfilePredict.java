/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Main;

import com.facepp.error.FaceppParseException;
import com.facepp.http.PostParameters;
import com.facepp.result.FaceppResult;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 *
 * @author chung_000
 */
public class ProfilePredict {
    
        private final static Twitter twitter = authenticate();
	public static String twitterUser = "Eriksoonieus"; //some given (real) twitter user
        
        public static void main(String[] args) throws FaceppParseException {
            
        HttpRequests httpRequests = new HttpRequests("25ad86cdad3ea4ac04204464a210058d", "phbtAB6cTT4LcArmskLCin2O5v6hDZfa");
        PostParameters postParameters;
        String imageURL = queryUserIMG(twitterUser);
        postParameters = new PostParameters().setUrl(imageURL).setAttribute("all");
        httpRequests.detectionDetect(postParameters);
        FaceppResult result = httpRequests.detectionDetect(postParameters);
        System.out.println("Twitter user:"+twitterUser+ " is "+
                             result.get("face").get(0).get("attribute").get("age").get("value", FaceppResult.JsonType.INT)+
                           "(+/-"+result.get("face").get(0).get("attribute").get("age").get("range", FaceppResult.JsonType.INT)+") years old.");
         System.out.println("Gender of "+twitterUser+ " is a "+result.get("face").get(0).get("attribute").get("gender").get("value", FaceppResult.JsonType.STRING)+", race is "+ result.get("face").get(0).get("attribute").get("race").get("value", FaceppResult.JsonType.STRING)+".");
    }
        

    public static String queryUserIMG(String user) {
        try {
            return twitter.showUser(user).getOriginalProfileImageURL();
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to search tweets: " + te.getMessage());
        }
             return null;
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
    
}
