import GUI.Panels;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame implements Runnable{
    public Thread gameThread;
    KeyHandler keyH = new KeyHandler();
    int[][] gameField;

    int FrameCount = 0;
    int FPS = 11;
    private int pieceFrameCount = 0;

    public MainFrame(){
        super("Tetris");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setSize(540,750);
        this.setLayout(new GridBagLayout());
        this.setResizable(false);
        this.addKeyListener(keyH);
        this.setFocusable(true);
    }
    public void add_ByUser(JPanel panel, GridBagConstraints gbc){
        this.add(panel,gbc);
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run(){
        double drawInterval = (double) 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        gameField = GameBoard.getGameField();
        while (gameThread != null){
            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;

            lastTime = currentTime;

            if (delta >= 1){
                update();
                paint();
                delta--;
            }
        }
    }
    public void update() {
        Piece piece = GameBoard.getPiece();
        if (keyH.rightPressed && piece.row + piece.pentominoe_piece.length < gameField.length) {
            if (!GameBoard.tryRight()){
                piece.atTheEnd = true;
            }
            else{
                piece.row++;
            }
        } else if (keyH.leftPressed && piece.row > 0) {
            if (!GameBoard.tryLeft()){
                piece.atTheEnd = true;
            }
            else{
                piece.row--;
            }
        } else if (keyH.downPressed && piece.col < gameField[0].length + piece.pentominoe_piece[0].length){
            if (GameBoard.tryDown()){
                piece.atTheEnd = true;
            }
            else{
                piece.col++;
            }
        } else if (keyH.rPressed) {
            GameBoard.tryRotate();
        } else if (keyH.spacePressed){
            GameBoard.trySlam();
        }
        GameBoard.placePiece();

        if (GameBoard.checkIfItsAtTheEnd()){
            OuterLoop:
            if (FrameCount == 5){
                //Check for lines
                if (GameBoard.tryDown()){
                    PieceHelperFunctions.addPiece(GameBoard.shadowGameField, piece.pentominoe_piece, piece.pieceID, piece.row, piece.col);
                    piece.atTheEnd = true;
                } else {
                    piece.col++;
                    break OuterLoop;
                }
                GameBoard.UserClearLinesIfPossible();
                GameBoard.nextPiece();
                //show next piece

                GameBoard.UserCheckIfGameOver();
            }  else {
                FrameCount++;
            }
        }else{
            if (GameBoard.tryDown()){
                piece.atTheEnd = true;
            }
            else{
                if (pieceFrameCount == 10){
                    piece.col++;
                    pieceFrameCount = 0;
                } else {
                    pieceFrameCount++;
                }
            }
        }
    }
    public void paint(){
        //sets the ui to the state of the gameField from GameBoard
        Panels.getUI().setState(GameBoard.getGameField());
    }
}
