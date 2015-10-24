/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import Model.Word;
import com.facepp.error.FaceppParseException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import static java.awt.SystemColor.window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;
import static javax.swing.JFrame.EXIT_ON_CLOSE;

/**
 *
 * @author s080440
 */
public class TwitTwinsGUI extends JFrame {

    static int NUMBER_KEYWORDS = 5;
    private UsernamePanel upanel;
    private KeyWordPanel kpanel;
    private ResultsPanel rpanel;
    private TweetsExtractor te;
    private UserData ud;
    private NameLookup nl = new NameLookup();
    private Boolean searchAge = false;
    private Boolean searchGender = false;

    private final int METHOD_VSR = 0;
    private final int METHOD_PRB = 1;
    private final int method = 0;

    public static void main(String[] args) {
        new TwitTwinsGUI();
    }

    public TwitTwinsGUI() {
        createGUI();
        setVisible(true);
        forceRepaints(100);
    }

    private void forceRepaints(int time) {
        Timer t = new Timer(time, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                TwitTwinsGUI.this.update();
            }
        });
        t.start();
    }

    public void update() {
        repaint();
    }

    static void printScores(UserData udata) {

        for (Object o : udata) {
            UserData.User u = (UserData.User) o;
            System.out.print(u.getName() + "\t");
            UserData.KeyWord keyword = u.getFirstKeyWord();
            Iterator iter = u.iterator();
            while (iter.hasNext()) {
                UserData.KeyWord keyW = (UserData.KeyWord) iter.next();
                //userInfo = keyW.getKeyWord() + ": " + keyW.getCount() + "\t";
                System.out.format("%s: %2.0f \t", keyW.getKeyWord(), keyW.getCount());
            }

            System.out.println("");
            //System.out.format("%10.3f%n", keyW.getCount());
        }
    }

    private void createGUI() {
        setResizable(false);
        setSize(650 + 6, 600 + 28);
        setTitle("TwitTwins");
        BufferedImage icon = null;
        try {
            icon = ImageIO.read(new File("src/Datafiles/TwitTwins_icon.png"));
        } catch (IOException e) {
        }
        this.setIconImage(icon);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        //setIgnoreRepaint(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(upanel = new UsernamePanel(), BorderLayout.NORTH);
        contentPane.add(kpanel = new KeyWordPanel(), BorderLayout.EAST);
        contentPane.add(rpanel = new ResultsPanel(), BorderLayout.WEST);
        //addKeyListener(this);
        repaint();
    }

    private class UsernamePanel extends JPanel {

        Dimension preferred;
        BufferedImage banner = null;

        JTextField field;
        JButton submit;
        JCheckBox genderbox;
        JCheckBox agebox;

        int fieldwidth = 120;
        int buttonwidth = 80;
        int margin = 20;
        int xstart = 140;
        int stringcorrection = 80;
        int ystart = 100 + 17;
        int height = 26;

        public UsernamePanel() {
            preferred = new Dimension(600, 100 + 60);
            try {
                banner = ImageIO.read(new File("src/Datafiles/TwitTwins_banner.png"));
                //banner = null;
            } catch (IOException e) {
            }
            this.setLayout(null);
            this.setPreferredSize(preferred);
            //this.setBackground(Color.red); //color background to see boundaries between panels
            field = new JTextField();
            field.setText("");
            field.setBounds(xstart, ystart, fieldwidth, height);
            this.add(field);
            submit = new JButton();
            submit.setText("Start!");
            submit.setBounds(xstart + margin + fieldwidth, ystart, buttonwidth, height);
            submit.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Starting now for " + field.getText() + "!");
                    List<Score> keywords = queryUser(field.getText());
                    kpanel.setKeyWords(keywords);
                    try {
                        performQuery(keywords);
                    } catch (FaceppParseException ex) {
                        Logger.getLogger(TwitTwinsGUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            this.add(submit);
            genderbox = new JCheckBox();
            genderbox.setText("Gender");
            genderbox.setBounds(xstart + fieldwidth + 2 * margin + buttonwidth, ystart, buttonwidth, height);
            genderbox.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {//checkbox has been selected
                        searchGender = true;
                    } else {//checkbox has been deselected
                        searchGender = false;
                    };
                }
            });
            this.add(genderbox);
            agebox = new JCheckBox();
            agebox.setText("Age");
            agebox.setBounds(xstart + fieldwidth + 3 * margin + 2 * buttonwidth, ystart, buttonwidth, height);
            agebox.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {//checkbox has been selected
                        searchAge = true;
                    } else {//checkbox has been deselected
                        searchAge = false;
                    };
                }
            });
            this.add(agebox);

        }

        @Override
        public void paint(Graphics gr) {
            super.paint(gr);
            Graphics2D g = (Graphics2D) gr;

            g.drawImage(banner, 150, 0, this);
            Font tfont = new Font("Times New Roman", Font.BOLD, 15);

            g.setFont(tfont);
            g.drawString("Username:", xstart - stringcorrection, ystart + 15);

        }

    }

    private class KeyWordPanel extends JPanel implements MouseListener {

        List<Score> keywords = new ArrayList();
        List<Rectangle> closeRectangles = new ArrayList();

        JTextField addField;
        JButton addButton;
        JButton updateButton;

        //basic margins
        int x_start = 50;
        int y_start = 10;
        int keywordstart = 100;
        int totalWidth = 200;
        int leftMargin = 20;
        int rightMargin = 20;
        int totalHeight = 200;
        int heightMargin = 10;

        int buttonheight = 30;
        int buttonwidth = 80;

        Dimension preferred = new Dimension(300, 440);

        public KeyWordPanel() {
            this.setLayout(null);
            this.setPreferredSize(preferred);
            addField = new JTextField();
            addField.setBounds(x_start, y_start, 150, 30);
            addButton = new JButton("Add");
            addButton.setBounds(x_start + 160, y_start, buttonwidth, buttonheight);
            addButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    String s = addField.getText();
                    if (s.length() == 0) {
                        return;
                    }

                    if (s.contains(" ")) {
                        addField.setText("");
                        return;
                    }

                    boolean match = false;
                    for (Score sc : keywords) {
                        if (sc.name.toLowerCase().equals(s.toLowerCase())) {
                            match = true;
                            break;
                        }
                    }

                    if (match) {
                        addField.setText("");
                        return;
                    } else {
                        addField.setText("");
                        keywords.add(new Score(1, s)); /*What is the score?????*/

                        return;
                    }
                }

            });
            updateButton = new JButton("Update");
            updateButton.setBounds(x_start + 160, y_start + buttonheight + 10, buttonwidth, buttonheight);
            updateButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Run query with new keywords");
                    try {
                        performQuery(keywords);
                    } catch (FaceppParseException ex) {
                        Logger.getLogger(TwitTwinsGUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            });
            this.add(addField);
            this.add(addButton);
            this.add(updateButton);
            this.addMouseListener(this);
        }

        @Override
        public void paint(Graphics gr) {
            super.paint(gr);
            closeRectangles.clear();
            Graphics2D g = (Graphics2D) gr;
            Font tfont = new Font("Times New Roman", Font.BOLD, 15);
            Font kfont = new Font("Times New Roman", Font.BOLD, 13);

            g.setFont(tfont);
            g.drawString("Matching Keywords", x_start, y_start + keywordstart);
            g.setFont(kfont);

            int row = 0;
            int progress = 0;
            int lmargin = 3;
            int xsize = 12;
            int rmargin = lmargin + xsize;
            int margin = 6;
            int hmargin = 28;
            int y_adjust = 30;

            for (Score kw : keywords) {
                Rectangle2D rect = kfont.getStringBounds(kw.getName(), g.getFontRenderContext());
                int size = lmargin + (int) rect.getWidth() + rmargin;

                if (totalWidth < progress + size) {
                    progress = 0;
                    row++;
                }

                g.setColor(Color.YELLOW);
                g.fill(new Rectangle(x_start + progress, y_start + keywordstart + y_adjust + hmargin * row, size, 19));
                g.setColor(Color.BLACK);
                g.drawString(kw.getName(), x_start + progress + lmargin, y_start + keywordstart + y_adjust + hmargin * row + 14);
                g.setColor(Color.RED);
                Rectangle r = new Rectangle(x_start + progress + size - xsize, y_start + keywordstart + y_adjust + hmargin * row, xsize, xsize);
                g.fill(r);
                closeRectangles.add(r);

                progress += size + margin;
            }
        }

        private void addKeyWord(Score s) {
            keywords.add(s);
        }

        private void setKeyWords(List<Score> ls) {
            keywords = ls;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            //nothing
        }

        @Override
        public void mousePressed(MouseEvent e) {
            Point p = e.getPoint();
            for (Rectangle r : closeRectangles) {
                if (r.contains(p)) {
                    keywords.remove(closeRectangles.indexOf(r));
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            //nothing
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            //nothing
        }

        @Override
        public void mouseExited(MouseEvent e) {
            //nothing
        }
    }

    private class ResultsPanel extends JPanel implements MouseListener {

        List<RankingEntry> ranking = new ArrayList<>();
        List<RankingEntry> relevant = new ArrayList<>();
        List<Rectangle> rankingBoxes = new ArrayList<>();
        List<Rectangle> rfbBoxes = new ArrayList<>();
        List<Rectangle> pcbBoxes = new ArrayList<>();
        Dimension preferred = new Dimension(350, 440);

        JButton rfbButton;

        //basic margins
        int x_start = 50;
        int y_start = 40;
        int totalWidth = 200;
        int leftMargin = 20;
        int rightMargin = 20;
        int totalHeight = 200;
        int heightMargin = 10;

        public ResultsPanel() {
            ArrayList<String> keys = new ArrayList<>();

            this.setPreferredSize(preferred);
            this.addMouseListener(this);

            this.setLayout(null);
            rfbButton = new JButton("Rocchio");
            rfbButton.setBounds(x_start + 150, 10, 80, 30);
            rfbButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Returning a list of " + relevant.size() + " items for RFB");
                    List<RankingEntry> irrelevant = new ArrayList<>(ranking);
                    for (RankingEntry re : relevant) {
                        irrelevant.remove(re);
                    }

                    Map<String, Word> rfbRMap = new HashMap<>();
                    Map<String, Word> rfbNMap = new HashMap<>();

                    for (RankingEntry re : relevant) {
                        Map<String, Word> tm = new HashMap<>();
                        tm = te.extractUserM(re.getUserName());
                        for (String key : tm.keySet()) {
                            Word w = tm.get(key);
                            if (rfbRMap.containsKey(w)) {
                                rfbRMap.get(w).setFrequency((rfbRMap.get(w).getFrequency() + w.getFrequency()));
                            } else {
                                rfbRMap.put(w.getWord(), w);
                            }
                        }
                    }
                    for (RankingEntry re : irrelevant) {
                        Map<String, Word> tm = new HashMap<>();
                        tm = te.extractUserM(re.getUserName());
                        for (String key : tm.keySet()) {
                            Word w = tm.get(key);
                            if (rfbNMap.containsKey(w)) {
                                rfbNMap.get(w).setFrequency((rfbNMap.get(w).getFrequency() + w.getFrequency()));
                            } else {
                                rfbNMap.put(w.getWord(), w);
                            }
                        }
                    }
                    RocchioRFB rfb = new RocchioRFB(ud.getKeyWords(), rfbRMap, rfbNMap, ranking, relevant, 1.0, 0.3, 0.4); // With values alpha, beta and gamma respectively
                    List<String> newQuery = rfb.getUpdatedQuery();
                    System.out.println("Rocchio Relevance Feedback, new search query: " + newQuery);
                    List<Score> newQueryAsScore = new ArrayList<>();
                    for(String keyword:newQuery){
                        Score s = new Score(0,keyword);
                        newQueryAsScore.add(s);
                    }
                    try {
                        performQuery(newQueryAsScore);
                    } catch (FaceppParseException ex) {
                        Logger.getLogger(TwitTwinsGUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    kpanel.keywords.clear();
                    for(Score s:newQueryAsScore){
                        kpanel.keywords.add(s);
                    }


                }

            });
            this.add(rfbButton);

        }

        private void createRanking(List<Score> scores) {
            ranking.clear();
            for (Object o : ud) {
                UserData.User u = (UserData.User) o;
                //System.out.println(u.getName());
            }
            for (Score s : scores) {
                //System.out.println(s.getName());
                UserData.User user = ud.getUser(s.getName());
                ArrayList<String> userKeywords = new ArrayList();
                for (Object o : user) {
                    UserData.KeyWord kw = (UserData.KeyWord) o;
                    if (kw.getCount() != 0) {
                        userKeywords.add(kw.getKeyWord());
                    }
                }
//                String ProfilePicURL = user. .getOriginalProfileImageURL();
//                ProfilePredict pp = new ProfilePredict();
//                String gender = pp.getGender(ProfilePicURL);
//                int age = pp.getAge(ProfilePicURL);
                RankingEntry re = new RankingEntry(s.getName(), user.getGender(), user.getAge(), null, userKeywords);
                ranking.add(re);
                if (ranking.size() == 10) {
                    return;
                }
            }
        }

        @Override
        public void paint(Graphics gr) {
            super.paint(gr);
            rankingBoxes.clear();
            rfbBoxes.clear();
            pcbBoxes.clear();
            Graphics2D g = (Graphics2D) gr;
            Font tfont = new Font("Times New Roman", Font.BOLD, 18);
            Font rfont = new Font("Times New Roman", Font.BOLD, 16);
            Font nfont = new Font("Times New Roman", Font.BOLD, 12);
            Font kfont = new Font("Times New Roman", Font.PLAIN, 12);

            g.setFont(tfont);
            g.drawString("Top " + ranking.size() + " results:", x_start, y_start - 10);

            int hmargin = 38;
            int height = 35;
            int width = 270;
            int ranknum = 30;
            int picmargin = 35;

            int row = 0;

            for (RankingEntry re : ranking) {

                g.setColor(Color.BLACK);
                Rectangle r = new Rectangle(x_start + ranknum + picmargin, y_start + row * hmargin, width - ranknum -picmargin, height);
                g.draw(r);
                rankingBoxes.add(r);
                Rectangle rlfb = new Rectangle(x_start, y_start + row * hmargin, ranknum, height);
                g.draw(rlfb);
                if (relevant.contains(re)) {
                    g.setColor(Color.GREEN);
                    g.fill(rlfb);
                    g.setColor(Color.BLACK);
                } else {
                    g.setColor(Color.RED);
                    g.fill(rlfb);
                    g.setColor(Color.BLACK);
                }
                rfbBoxes.add(rlfb);
                Rectangle r2 = new Rectangle(x_start + ranknum, y_start + row * hmargin, picmargin, height);
                BufferedImage img = null;
                try {
                    img = ImageIO.read(new File("src/Datafiles/Twitlink.png"));
                } catch (IOException e) {
                }
                g.draw(r);
                g.drawImage(img, r2.x, r2.y, r2.width, r2.height, null);
                pcbBoxes.add(r2);
                g.setFont(rfont);
                g.drawString(row + 1 + ".", x_start + ranknum / 3 - ((row == 9) ? 4 : 0), y_start + 2 * height / 3 + row * hmargin);

                String age;
                if (re.age == -1) {
                    age = "n.a.";
                } else {
                    age = Integer.toString(re.age);
                }
                String result1;
                if (re.gender == "n.a." && age == "n.a.") {
                    result1 = re.username;
                } else if (re.gender == "n.a.") {
                    result1 = re.username + ", " + age;
                } else if (age == "n.a.") {
                    result1 = re.username + ", " + re.gender;
                } else {
                    result1 = re.username + ", " + re.gender + ", " + age;
                }
                String result2 = "";

                for (String s : re.keywords) {
                    result2 += s + " ";
                }

                g.setFont(nfont);
                g.drawString(result1, x_start + ranknum + picmargin + 5, y_start + row * hmargin + 14);
                g.setFont(kfont);
                g.drawString(result2, x_start + ranknum + picmargin + 5, y_start + row * hmargin + 26);

                row++;
            }

        }

        @Override
        public void mouseClicked(MouseEvent e) {
            //do nothing
        }

        @Override
        public void mousePressed(MouseEvent e) {
            Point p = e.getPoint();
            for (Rectangle r : rankingBoxes) {

                if (r.contains(p)) {
                    RankingEntry re = ranking.get(rankingBoxes.indexOf(r));
                    System.out.println("Performing QBE on " + re.username + "\nWell, once it works..");
                    //todo: QBE
                    List<Score> scores_temporary = new ArrayList<>();
                    List<Score> scores_updated = new ArrayList<>();

                    UserData.User user = ud.getUser(re.getUserName());             
                    //double tot = user.getWordTweetCount();
                    for (Object o : user) {
                        UserData.KeyWord kw = (UserData.KeyWord) o;
                        double count = kw.getCount();
                        scores_temporary.add(new Score(count, kw.getKeyWord()));
                    }
                    scores_updated = performVSR(ud, scores_temporary);
                    rpanel.createRanking(scores_updated);
                }

            }

            for (Rectangle r : rfbBoxes) {
                if (r.contains(p)) {
                    RankingEntry re = ranking.get(rfbBoxes.indexOf(r));

                    if (relevant.contains(re)) {
                        relevant.remove(re);
                    } else {
                        relevant.add(re);
                    }
                }
            }
            for (Rectangle r : pcbBoxes) {
                if (r.contains(p)) {
                    RankingEntry re = ranking.get(pcbBoxes.indexOf(r));
                    if(Desktop.isDesktopSupported())
                    {
                        try {
                            Desktop.getDesktop().browse(new URI("http://www.twitter.com/"+re.getUserName()));
                        } catch (IOException ex) {
                            Logger.getLogger(TwitTwinsGUI.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (URISyntaxException ex) {
                            Logger.getLogger(TwitTwinsGUI.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }

        }

        @Override
        public void mouseReleased(MouseEvent e) {
            //do nothing
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            //do nothing
        }

        @Override
        public void mouseExited(MouseEvent e) {
            //do nothing
        }
    }

    public class RankingEntry {

        private String username;
        private String gender;
        private int age;
        private BufferedImage picture;
        private List<String> keywords;

        private RankingEntry(String name, String gender, int age, String picture, List<String> keywords) {
            this.username = name;
            this.gender = gender;
            this.age = age;
            this.picture = null; //todo: retrieve picture!
            this.keywords = keywords;
        }

        public List<String> getKeywords() {
            return keywords;
        }

        public String getUserName() {
            return username;
        }
    }

    private List<Score> queryUser(String username) {
        te = new TweetsExtractor();
        TreeMap<String, Word> data = te.extractUser(username);
        int i = NUMBER_KEYWORDS;
        ArrayList<Score> searchedUserKeywordFrequency = new ArrayList();
        for (Word w : data.values()) {
            if (i == 0) {
                break;
            } else {
                i--;
            }
            searchedUserKeywordFrequency.add(new Score(w.getFrequency(), w.getWord()));
        }
        return searchedUserKeywordFrequency;
    }

    private void performQuery(List<Score> keywords) throws FaceppParseException {
        List<String> stringKeywords = new ArrayList();
        for (int i = 0; i < keywords.size(); i++) {
            stringKeywords.add(keywords.get(i).getName());
        }

        List<Score> scores = null;
        ud = queryRelatedUsers(stringKeywords);
        printScores(ud);

        switch (method) {
            case METHOD_PRB:
                KMeans km = new KMeans(keywords.size(), ud);
                boolean clusteringOK = km.calculateClusteringSetK();
                ProbabRetrieval pr = new ProbabRetrieval();
                pr.rank(ud, keywords, km.getClusterByK(), false, 0.7, 0.2, 0.1);
                scores = pr.rank(ud, keywords, km.getClusterByK(), clusteringOK, 0.7, 0.2, 0.1);
                break;
            case METHOD_VSR:
            default:
                scores = performVSR(ud, keywords); // 
                break;
        }
        rpanel.createRanking(scores);
    }

    private List<Score> performVSR(UserData ud, List<Score> keywords) {
        String word;
        Double tf;
        Map KwTfdata = new HashMap(); //KeyWord + TermFrequency data of single user
        Map QueryData = new HashMap(); // Store the keywords in a Map (for processing in VectorIR class)
        ArrayList<Map> KwTfdataList = new ArrayList<>(); //KeyWord + TermFrequency data of all users
        List<Score> scores = new ArrayList<Score>(); // Stores the cosine similarity score between keywords and all users

        for (int i = 0; i < keywords.size(); i++) {
            QueryData.put(keywords.get(i).getName(), keywords.get(i).getScore());
        }
        // Retrieve all keywords (including their term frequency) from every user and put it in a map
        for (Object o : ud) {
            UserData.User u = (UserData.User) o;
            KwTfdata.clear();
            Iterator iter = u.iterator();
            while (iter.hasNext()) {
                UserData.KeyWord keyW = (UserData.KeyWord) iter.next(); // Get next keyword of user
                word = keyW.getKeyWord();
                tf = (double) keyW.getCount();
                KwTfdata.put(word, tf);
            }
            KwTfdataList.add(KwTfdata);
            // Calculate cosine similarity of every user with the keywords and add to scores list.
            scores.add(new Score(VectorIR.cosine_similarity(QueryData, KwTfdata), u.getName())); // Generate a new Score class containing (Score,Username)

        }

        // Sort the scores list in ascending order of scores (and their corresponding users)
        System.out.println("-------- VSR Ranking results --------");
        Collections.sort(scores);
        Collections.reverse(scores); // Changes the list to an ascending order.
        int rank = 0;
        for (Object o : scores) {
            Score s = (Score) o;
            rank++;
            System.out.format("#%d: \t %-20s \t (CosineScore: %f)%n", rank, s.getName(), s.getScore());
        }
        
        return scores;
    }

    private UserData queryRelatedUsers(List<String> keywords) throws FaceppParseException {
        int n = 20;
        UserData udata = new UserData(keywords);
        Queue<Tweet> names = te.query(keywords, n);
        while (n > 0 && !names.isEmpty()) {
            n--;

            Tweet t = names.poll();
            String name = t.getUser().getScreenName();
            String actualName = t.getUser().getName();
            Scanner s = new Scanner(actualName);
            String actualFirstName = s.next().toLowerCase();
            String genderFromList = nl.getGender(actualFirstName);
            // System.out.println(actualFirstName);
            String gender = "n.a.";
            String ProfilePicURL = t.getUser().getOriginalProfileImageURL();
            ProfilePredict pp = new ProfilePredict();
            //////**////
            if (searchGender) {

                String genderFromPic = pp.getGender(ProfilePicURL).toLowerCase();

                if (genderFromList == genderFromPic) {
                    gender = genderFromList;
                } else if (genderFromList != "n.a." && genderFromPic == "n.a.") {
                    gender = genderFromList;
                } else if (genderFromList == "n.a." && genderFromPic != "n.a.") {
                    gender = genderFromPic;
                } else if (genderFromList == "male" && genderFromPic == "female" || genderFromList == "female" && genderFromPic == "male") {
                    gender = genderFromPic;
                } else {
                    gender = "n.a.";
                }
            }
            int age = 0;
            if (searchAge) {
                age = pp.getAge(ProfilePicURL);
            }

            TreeMap<String, Word> user = te.extractUser(name);
            udata.addUser(name, age, gender, -1, user);

//            collectionLenght+=TweetCount;
        }
        return udata;
    }

}
