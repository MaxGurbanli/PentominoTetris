import GUI.Panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main extends JFrame implements ActionListener {

    static JButton playerButton = new JButton("The User");

    static JButton botButton = new JButton("The Bot");
    private final JButton botButtonSeq = new JButton("Sequence");


    public Main(){
        setSize(250,250);
        setLayout(new FlowLayout());
        playerButton.addActionListener(this);
        botButton.addActionListener(this);
        botButtonSeq.addActionListener(this);
        add(playerButton);
        add(botButton);
        add(botButtonSeq);
    }

    public static void main(String[] args){
        Main main = new Main();
        main.setVisible(true);
    }

    public static void bot(boolean playRandom){
        //true if random
        //false if sequence
        BotMainFrame botMainFrame = new BotMainFrame(playRandom);

        Panels.initPanels();

        botMainFrame.add_ByUser(Panels.GamePanel, Panels.gbc_GamePanel);
        botMainFrame.add_ByUser(Panels.NextPiece_Panel, Panels.gbc_nextPiece);
        botMainFrame.add_ByUser(Panels.Score_Lines_Panel, Panels.gbc_Score_Lines);
        botMainFrame.add_ByUser(Panels.High_Score_Panel, Panels.gbc_High_Score);
        //Manages the game - field // board
        GameBoard.createGameField(5,12);
        GameBoard.emptyUI();
        //Start game Loop
        botMainFrame.startGameThread();
    }

    public static void user(){
        MainFrame mainframe = new MainFrame();
        //Get the panels ready in class Panels
        Panels.initPanels();
        //Add the panels
        mainframe.add_ByUser(Panels.GamePanel, Panels.gbc_GamePanel);
        mainframe.add_ByUser(Panels.NextPiece_Panel, Panels.gbc_nextPiece);
        mainframe.add_ByUser(Panels.Score_Lines_Panel, Panels.gbc_Score_Lines);
        mainframe.add_ByUser(Panels.High_Score_Panel, Panels.gbc_High_Score);
        //Manages the game - field // board
        GameBoard.createGameField(5,12);
        GameBoard.emptyUI();
        //Start game Loop
        mainframe.startGameThread();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(playerButton)){
            setVisible(false);
            user();
        } else if (e.getSource().equals(botButton)){
            setVisible(false);
            bot(true);
        } else if (e.getSource().equals(botButtonSeq)){
            setVisible(false);
            bot(false);
        }
    }
}