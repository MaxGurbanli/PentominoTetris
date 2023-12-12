import GUI.Panels;
import Pentominoes.PentominoDatabase;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class BotMainFrame extends JFrame implements Runnable{
    public Thread gameThread;
    int[][] gameField;
    int frameCount = 0;
    int FPS = 120;
    Object[] PresetMoves;
    public static int pieceIndex = 0;

    static ArrayList<Integer> scores = new ArrayList<>();
    private final boolean playRandom;

    public BotMainFrame(boolean playItRandom){
        super("AI Tetris");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setSize(540,750);
        this.setLayout(new GridBagLayout());
        this.setResizable(false);
        this.setFocusable(true);
        playRandom = playItRandom;
    }

    private static final Map<Character, Integer> pentominoKeyToID = new HashMap<>();
    static {
        pentominoKeyToID.put('X', 0);
        pentominoKeyToID.put('I', 1);
        pentominoKeyToID.put('Z', 2);
        pentominoKeyToID.put('T', 3);
        pentominoKeyToID.put('U', 4);
        pentominoKeyToID.put('V', 5);
        pentominoKeyToID.put('W', 6);
        pentominoKeyToID.put('Y', 7);
        pentominoKeyToID.put('L', 8);
        pentominoKeyToID.put('P', 9);
        pentominoKeyToID.put('N', 10);
        pentominoKeyToID.put('F', 11);
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
    public void update(){
        gameField = GameBoard.getGameField();
        //update current board to allow new board generation logic
        if (frameCount == 5){
            if (playRandom){
                PlayTheGameWithARandomPiece();
            } else {
                if (PresetMoves == null || BotMainFrame.scores.size() == 100){
                    PresetMoves =  PlayTheGameWithThe12Pieces();
                    BotMainFrame.scores.clear();
                }
                Character moveChar = (Character) PresetMoves[pieceIndex];
                int pieceID = pentominoKeyToID.get(moveChar);

                //All Board States
                int[][] bestMove = GenerateAllBoardStatesGivenPieceAndRotationAndReturnBestMove(gameField, pieceID);

                GameBoard.setGameField(bestMove);
                GameBoard.AIClearLinesIfPossible();
                GameBoard.AICheckIfGameOver();
            }
            frameCount = 0;
            if (pieceIndex == 12){
                pieceIndex = 0;
            }
        } else {
            frameCount ++;
        }
    }

    private Character[] PlayTheGameWithThe12Pieces() {
        gameField = GameBoard.getGameField();
        Piece piece = new Piece();
        //piece.fillUpRandomMoves();
        piece.fillUpSetPiece();
        return piece.moves;
    }

    private void PlayTheGameWithARandomPiece() {
        gameField = GameBoard.getGameField();
        //Place piece
        //Generate piece
        Piece piece = GameBoard.getPiece();
        //All Board States
        int[][] bestMove = GenerateAllBoardStatesGivenPieceAndRotationAndReturnBestMove(gameField, piece.pieceID);

        GameBoard.setGameField(bestMove);
        GameBoard.nextPiece();
        GameBoard.AIClearLinesIfPossible();
        GameBoard.AICheckIfGameOver();
    }

    private int[][] GenerateAllBoardStatesGivenPieceAndRotationAndReturnBestMove(int[][] currentBoard, int PieceID)  {
        int maxScore = Integer.MIN_VALUE;
        int[][] winningBoard = new int[currentBoard.length][currentBoard[0].length];

        for (int col = 0; col < currentBoard[0].length; col++) {
            //put rotations here
            for (int rotation = 0 ; rotation < PentominoDatabase.data[PieceID].length; rotation++){
                int[][] givenPiece = PentominoDatabase.data[PieceID][rotation];
                int[][] localBoard = GameBoard.copy(currentBoard);
                if (PieceHelperFunctions.canPlace(currentBoard, givenPiece, col, 0)){
                    int secValue = 0;
                    while(PieceHelperFunctions.canPlace(currentBoard, givenPiece, col, secValue)){
                        secValue++;
                    }
                    secValue--;

                    int row = secValue;
                    int landingHeight;
                    int erodedPiecesScore;
                    int rowTransition;
                    int colTransition;
                    int numberOfHoles;
                    int cumulativeWells;

                    landingHeight = row + givenPiece[0].length - 1;
                    erodedPiecesScore = FeatureFunctions.calculateErodedPieces(localBoard, givenPiece, PieceID, col, secValue);

                    PieceHelperFunctions.addPiece(localBoard, givenPiece, PieceID, col, secValue);

                    rowTransition = FeatureFunctions.calculateRowTransition(localBoard);
                    colTransition = FeatureFunctions.calculateColTransition(localBoard);
                    numberOfHoles = FeatureFunctions.calculateNumberHoles(localBoard);
                    cumulativeWells = FeatureFunctions.calculateCumulativeWells(localBoard);

                    int sum = (50 * erodedPiecesScore) - (9 * landingHeight) - (6 * rowTransition) - (4 * colTransition) - (9 * numberOfHoles) - (3 * cumulativeWells);
                    if (sum >= maxScore){
                        winningBoard = localBoard;
                        maxScore = sum;
                    }
                }
            }
        }
        return winningBoard;
    }

    public void paint(){
        //sets the ui to the state of the gameField from GameBoard
        Panels.getUI().setState(GameBoard.getGameField());
    }
}
