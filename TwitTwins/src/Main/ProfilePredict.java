/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Main;

import com.facepp.error.FaceppParseException;
import com.facepp.http.PostParameters;
import com.facepp.result.FaceppResult;
import java.awt.Image;
import java.awt.TextField;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
	public static String twitterUser = "SCGOP"; //some given (real) twitter user
        
        public static void main(String[] args) throws FaceppParseException, MalformedURLException, IOException {
            
        HttpRequests httpRequests = new HttpRequests("25ad86cdad3ea4ac04204464a210058d", "phbtAB6cTT4LcArmskLCin2O5v6hDZfa");
        PostParameters postParameters;
        String imageURL = queryUserIMG(twitterUser);
        postParameters = new PostParameters().setUrl(imageURL).setAttribute("all");
        httpRequests.detectionDetect(postParameters);
        FaceppResult result = httpRequests.detectionDetect(postParameters);
//        System.out.println("Twitter user:"+twitterUser+ " is "+
//                             result.get("face").get(0).get("attribute").get("age").get("value", FaceppResult.JsonType.INT)+
//                           "(+/-"+result.get("face").get(0).get("attribute").get("age").get("range", FaceppResult.JsonType.INT)+") years old.");
//         System.out.println("Gender of "+twitterUser+ " is a "+result.get("face").get(0).get("attribute").get("gender").get("value", FaceppResult.JsonType.STRING)+", race is "+ result.get("face").get(0).get("attribute").get("race").get("value", FaceppResult.JsonType.STRING)+".");
    
        URL url = new URL(imageURL);
        BufferedImage c = ImageIO.read(url);
        ImageIcon image = new ImageIcon(c);
        Image img = image.getImage(); // transform it 
        Image newimg = img.getScaledInstance(150, 150,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
        image = new ImageIcon(newimg);  // transform it back
        JLabel jp = new JLabel(image);
        JFrame frame = new JFrame("TwitTwins");//Make a frame
        frame.setSize(500, 500);//Give it a size
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);//Make it go away on close
        JPanel panel = new JPanel();//Make a panel
        TextField bio = new TextField("");
        String a = "" + result.get("face");
        if (!a.contains("[]")){
        bio.setText("Twitter user:"+twitterUser+ " is "+
                            result.get("face").get(0).get("attribute").get("age").get("value", FaceppResult.JsonType.INT)+
                            "(+/-"+result.get("face").get(0).get("attribute").get("age").get("range", FaceppResult.JsonType.INT)+") years old.");
        }
//        TextField bio2 = new TextField("Gender of "+twitterUser+ " is a "+result.get("face").get(0).get("attribute").get("gender").get("value", FaceppResult.JsonType.STRING)+", race is "+ result.get("face").get(0).get("attribute").get("race").get("value", FaceppResult.JsonType.STRING)+".");
        panel.add(bio);
//        panel.add(bio2);
        panel.add(jp);
        frame.add(panel);//Add it to your frame
        frame.setVisible(true);
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
    
    public String getGender(String URL) throws FaceppParseException {
        HttpRequests httpRequests = new HttpRequests("25ad86cdad3ea4ac04204464a210058d", "phbtAB6cTT4LcArmskLCin2O5v6hDZfa");
        PostParameters postParameters;
        postParameters = new PostParameters().setUrl(URL).setAttribute("all");
        httpRequests.detectionDetect(postParameters);
        FaceppResult result = httpRequests.detectionDetect(postParameters);
        String s="n.a.";
        String r=""+result.get("face");
        if (!r.contains("[]")){
           s = ""+result.get("face").get(0).get("attribute").get("gender").get("value", FaceppResult.JsonType.STRING); 
        }
        return s;
    }
    
    public int getAge(String URL) throws FaceppParseException {
        HttpRequests httpRequests = new HttpRequests("25ad86cdad3ea4ac04204464a210058d", "phbtAB6cTT4LcArmskLCin2O5v6hDZfa");
        PostParameters postParameters;
        postParameters = new PostParameters().setUrl(URL).setAttribute("all");
        httpRequests.detectionDetect(postParameters);
        FaceppResult result = httpRequests.detectionDetect(postParameters);
        int a=-1;
        String r=""+result.get("face");
        if (!r.contains("[]")){
            String s;
            s = ""+result.get("face").get(0).get("attribute").get("age").get("value", FaceppResult.JsonType.INT);
            a = Integer.parseInt(s);
        }
        return a;
    }
}