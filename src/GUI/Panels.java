package GUI;

import javax.swing.*;
import java.awt.*;

public class Panels {
    private static final UI ui = new UI(5,12,60);
    private static final UI nextPieceUi = new UI(5,5,45);
    public static int PanelScore = 0;
    public static int HighScore = 0;

    public static final JPanel GamePanel = ui;
    public static final JPanel NextPiece_Panel = nextPieceUi;
    public static final JPanel Score_Lines_Panel = new JPanel();
    public static final JPanel High_Score_Panel = new JPanel();
    public static final GridBagConstraints gbc_GamePanel = new GridBagConstraints();
    public static final GridBagConstraints gbc_nextPiece = new GridBagConstraints();
    public static final GridBagConstraints gbc_Score_Lines = new GridBagConstraints();
    public static final GridBagConstraints gbc_High_Score = new GridBagConstraints();

    public static final JLabel score = new JLabel("Score: " + PanelScore);
    public static final JLabel Label_HighScore = new JLabel("High Score: " + HighScore);

    public static UI getUI(){
        return ui;
    }

    public static UI getnextPieceUI(){
        return nextPieceUi;
    }

    public static void initPanels(){
        //Game Panel
        GamePanel.setVisible(true);
        gbc_GamePanel.gridx=0;
        gbc_GamePanel.gridy=0;
        gbc_GamePanel.weightx=3;
        gbc_GamePanel.gridheight=3;
        gbc_GamePanel.fill = GridBagConstraints.BOTH;
        //Next Piece Panel
        NextPiece_Panel.setBackground(Color.GREEN);
        NextPiece_Panel.setVisible(true);
        gbc_nextPiece.gridx=1;
        gbc_nextPiece.gridy=0;
        gbc_nextPiece.weightx=1;
        gbc_nextPiece.weighty=2;
        gbc_nextPiece.fill = GridBagConstraints.BOTH;
        //Score/Lines Panel
        Score_Lines_Panel.setBackground(Color.GRAY);
        Score_Lines_Panel.setVisible(true);
        Score_Lines_Panel.setLayout(new BorderLayout());
        score.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 20));
        Label_HighScore.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 20));
        Score_Lines_Panel.add(score, BorderLayout.NORTH);
        Score_Lines_Panel.add(Label_HighScore, BorderLayout.CENTER);
        gbc_Score_Lines.gridx=1;
        gbc_Score_Lines.gridy=1;
        gbc_Score_Lines.weightx=1;
        gbc_Score_Lines.weighty=1;
        gbc_Score_Lines.fill = GridBagConstraints.BOTH;
        //High Score Panel
        High_Score_Panel.setBackground(Color.DARK_GRAY);
        High_Score_Panel.setVisible(true);
        gbc_High_Score.gridx=1;
        gbc_High_Score.gridy=2;
        gbc_High_Score.weightx=1;
        gbc_High_Score.weighty=1;
        gbc_High_Score.fill = GridBagConstraints.BOTH;
    }


}
