/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.*;

/**
 *
 * @author s080440
 */
public class TwitTwinsGUI {

    //set of panels used in the frame
    JPanel inputPanel;
    JPanel userNamePanel;
    JPanel keywordPanel;

    JPanel outputPanel;
    JPanel rankingPanel;

    public static void main(String[] args) {
        TwitTwinsGUI gui = new TwitTwinsGUI();
        System.out.println(gui.toString());
    }

    public TwitTwinsGUI() {
        JFrame frame = new JFrame("TwitTwins");
        frame.setSize(400 + 6, 500 + 28);
        frame.setResizable(false);
        frame.setBackground(Color.black);

        
        //input view
        inputPanel = new JPanel();
        inputPanel.setSize(200, 500);
        
        userNamePanel = createUserNamePanel();
        keywordPanel = ceateKeywordPanel();
        
        inputPanel.add(userNamePanel);
        inputPanel.add(keywordPanel);

        
        //output view
        outputPanel = new JPanel();
        outputPanel.setSize(200, 500);
        rankingPanel = createRankingPanel();
        
        outputPanel.setLayout(new BorderLayout());
        outputPanel.add(rankingPanel, BorderLayout.CENTER);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(inputPanel, BorderLayout.WEST);
        frame.add(outputPanel, BorderLayout.EAST);
        frame.setVisible(true);

    }

    private JPanel createUserNamePanel() {
        JPanel panel = new JPanel();
        panel.setSize(200, 200);
        panel.setBackground(Color.red);
        //panel.setLayout(new BorderLayout());
        JTextField field = new JTextField("hello");
        field.setSize(100, 100);
        panel.add(field);
        return panel;
    }

    private JPanel ceateKeywordPanel() {
        JPanel panel = new JPanel();
        panel.setSize(200, 300);
        panel.setBackground(Color.blue);
        //panel.setLayout(new BorderLayout());
        return panel;
    }

    private JPanel createRankingPanel() {
        JPanel panel = new JPanel();
        panel.setSize(200, 500);
        panel.setBackground(Color.green);
        return panel;
    }

}
