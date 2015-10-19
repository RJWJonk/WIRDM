/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import static Main.TwitMain.NUMBER_KEYWORDS;
import Model.Word;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import javax.swing.*;
import static javax.swing.JFrame.EXIT_ON_CLOSE;

/**
 *
 * @author s080440
 */
public class TwitTwinsGUI extends JFrame {

    private UsernamePanel upanel;
    private KeyWordPanel kpanel;
    private ResultsPanel rpanel;
    private TweetsExtractor te;

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

    private void createGUI() {
        setResizable(false);
        setSize(650 + 6, 500 + 28);
        setTitle("TwitTwins");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        //setIgnoreRepaint(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(upanel = new UsernamePanel(), BorderLayout.NORTH);
        contentPane.add(kpanel = new KeyWordPanel(), BorderLayout.WEST);
        contentPane.add(rpanel = new ResultsPanel(), BorderLayout.EAST);
        //addKeyListener(this);
        repaint();
    }

    private class UsernamePanel extends JPanel {

        Dimension preferred = new Dimension(600, 60);

        JTextField field;
        JButton submit;

        public UsernamePanel() {
            this.setPreferredSize(preferred);
            //this.setBackground(Color.red); //color background to see boundaries between panels
            field = new JTextField();
            field.setText("Username");
            field.setPreferredSize(new Dimension(200, 30));
            this.add(field);
            submit = new JButton();
            submit.setText("Start!");
            submit.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Starting now for " + field.getText() + "!\nNo functionality though..");
                    List<String> keywords = queryUser(field.getText());
                    kpanel.setKeyWords(keywords);
                    queryRelatedUsers(keywords);
                }
            });
            this.add(submit);
        }
    }

    private class KeyWordPanel extends JPanel implements MouseListener {

        List<String> keywords = new ArrayList();
        List<Rectangle> closeRectangles = new ArrayList();

        JTextField addField;
        JButton addButton;
        JButton updateButton;

        //basic margins
        int x_start = 50;
        int y_start = 30;
        int totalWidth = 200;
        int leftMargin = 20;
        int rightMargin = 20;
        int totalHeight = 200;
        int heightMargin = 10;

        Dimension preferred = new Dimension(300, 440);

        public KeyWordPanel() {
            this.setLayout(null);
            this.setPreferredSize(preferred);
            addField = new JTextField();
            addField.setBounds(x_start, y_start + 240, 150, 30);
            addButton = new JButton("Add");
            addButton.setBounds(x_start + 150 + 10, y_start + 240, 60, 30);
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

                    if (keywords.contains(s)) {
                        addField.setText("");
                        return;
                    } else {
                        addField.setText("");
                        keywords.add(s);
                        return;
                    }
                }

            });
            updateButton = new JButton("Update");
            updateButton.setBounds(x_start + 50 + 10, y_start + 280, 100, 30);
            updateButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Run query with new keywords\nNot actually working..");
                    //add functionality
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
            g.drawString("Matching Keywords", x_start, y_start);
            g.setFont(kfont);

            int row = 0;
            int progress = 0;
            int lmargin = 3;
            int xsize = 12;
            int rmargin = lmargin + xsize;
            int margin = 6;
            int hmargin = 28;
            int y_adjust = 30;

            for (String kw : keywords) {
                Rectangle2D rect = kfont.getStringBounds(kw, g.getFontRenderContext());
                int size = lmargin + (int) rect.getWidth() + rmargin;

                if (totalWidth < progress + size) {
                    progress = 0;
                    row++;
                }

                g.setColor(Color.YELLOW);
                g.fill(new Rectangle(x_start + progress, y_start + y_adjust + hmargin * row, size, 19));
                g.setColor(Color.BLACK);
                g.drawString(kw, x_start + progress + lmargin, y_start + y_adjust + hmargin * row + 14);
                g.setColor(Color.RED);
                Rectangle r = new Rectangle(x_start + progress + size - xsize, y_start + y_adjust + hmargin * row, xsize, xsize);
                g.fill(r);
                closeRectangles.add(r);

                progress += size + margin;
            }
        }

        private void addKeyWord(String s) {
            keywords.add(s);
        }

        private void setKeyWords(List<String> ls) {
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
            rfbButton = new JButton("RFB");
            rfbButton.setBounds(x_start+150, 0, 60, 30);
            rfbButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Returning a list of " + relevant.size() + " items for RFB");
                    //todo add relevance feedback
                }

            });
            this.add(rfbButton);

            keys.add("Formula1");
            keys.add("Ponies");
            keys.add("Facebook");
            keys.add("Cats");
            ranking.add(new RankingEntry("Adam", "male", "", keys));
            ranking.add(new RankingEntry("Ben", "male", "", keys));
            ranking.add(new RankingEntry("Ruben", "male", "", new ArrayList<>()));
            ranking.add(new RankingEntry("Chunkie", "male", "", new ArrayList<>()));
            ranking.add(new RankingEntry("Philip", "male", "", keys));
            ranking.add(new RankingEntry("Mykola", "male", "", new ArrayList<>()));
            ranking.add(new RankingEntry("Bob", "male", "", new ArrayList<>()));
            ranking.add(new RankingEntry("Simon", "male", "", new ArrayList<>()));
            ranking.add(new RankingEntry("Rei", "female", "", keys));
            ranking.add(new RankingEntry("Obama", "male", "", new ArrayList<>()));

        }

        @Override
        public void paint(Graphics gr) {
            super.paint(gr);
            rankingBoxes.clear();
            rfbBoxes.clear();
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
                Rectangle r = new Rectangle(x_start + ranknum, y_start + row * hmargin, width - ranknum, height);
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
                g.setFont(rfont);
                g.drawString(row + 1 + ".", x_start + ranknum / 3 - ((row == 9) ? 4 : 0), y_start + 2 * height / 3 + row * hmargin);

                String result1 = re.username + ", " + re.gender;
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

    private class RankingEntry {

        private String username;
        private String gender;
        private BufferedImage picture;
        private List<String> keywords;

        private RankingEntry(String name, String gender, String picture, List<String> keywords) {
            this.username = name;
            this.gender = gender;
            this.picture = null; //todo: retrieve picture!
            this.keywords = keywords;
        }

        public String getUserName() {
            return username;
        }
    }

    private List<String> queryUser(String username){
        te = new TweetsExtractor();
        TreeMap<String, Word> data = te.extractUser(username);
        int i = NUMBER_KEYWORDS;
        int keywordSearchedUserCount = 0;
        
        ArrayList<Integer> searchedUserKeywordFrequency = new ArrayList();
        ArrayList<String> keywords = new ArrayList();
        for (Word w : data.values()) {
            if (i == 0) break; else i--;
            keywords.add(w.getWord());
            searchedUserKeywordFrequency.add(w.getFrequency());
            keywordSearchedUserCount+=w.getFrequency();
        }
        return keywords;
    }

    private void queryRelatedUsers(List<String> keywords){
        UserData udata = new UserData(keywords);
        
        Queue<Tweet> names = te.query(keywords);
        
        int collectionWordLenght = 0;
        int userWordLenght;
        int n = 10;
        while (n > 0 && !names.isEmpty()) {
            n--;
            
            Tweet t = names.poll();
            String name = t.getUser().getScreenName();
           /* String ProfilePicURL = t.getUser().getOriginalProfileImageURL();
            ProfilePredict pp = new ProfilePredict();
            String gender = pp.getGender(ProfilePicURL);
            int age = pp.getAge(ProfilePicURL);*/
            String gender = "male";
            TreeMap<String, Word> user = te.extractUser(name);
            
            
            userWordLenght = 0;
            for(Map.Entry<String,Word> entry : user.entrySet()) {
                Word value = entry.getValue();
                userWordLenght+= value.getFrequency();
              }
            udata.addUser(name, 0, gender, userWordLenght, user);
            collectionWordLenght+=userWordLenght;
//            collectionLenght+=TweetCount;
            
        }
    }

}
