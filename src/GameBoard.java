import GUI.Panels;
import Pentominoes.PentominoDatabase;

public class GameBoard {
    private static int[][] gameField;
    //special field which holds only placed pieces.
    static int[][] shadowGameField;
    private static Piece piece = new Piece();
    private static Piece nextPiece = new Piece();

    public static void createGameField(int rows, int cols){
        gameField = new int[rows][cols];
    }

    public static int[][] getGameField(){
        return gameField;
    }
    public static void setGameField(int[][] field){
        gameField = field;
    }
    //empties both ui boards to be filled with -1's
    public static void emptyUI() {
        for (int i = 0; i < gameField.length; i++) {
            for (int j = 0; j < gameField[0].length; j++) {
                gameField[i][j] = -1;
            }
        }
        shadowGameField = copy(gameField);
    }
    //function to create a copy of an array
    public static int[][] copy(int[][] src) {
        if (src == null) {
            return null;
        }
        int[][] copy = new int[src.length][];
        for (int i = 0; i < src.length; i++) {
            copy[i] = src[i].clone();
        }
        return copy;
    }

    public static Piece getPiece(){
        doPieceThing();
        return piece;
    }

    public static void doPieceThing(){
        Panels.getnextPieceUI().setState(getNextPieceBoard());
    }

    public static void placePiece(){
        if (PieceHelperFunctions.canPlace(shadowGameField, piece.pentominoe_piece, piece.row, piece.col)){
            gameField = copy(shadowGameField);
            PieceHelperFunctions.addPiece(gameField, piece.pentominoe_piece, piece.pieceID, piece.row, piece.col);
        } else {
            PieceHelperFunctions.addPiece(shadowGameField, piece.pentominoe_piece, piece.pieceID, piece.row, piece.col);
            piece.atTheEnd = true;
        }
    }

    public static boolean tryRight(){
        return PieceHelperFunctions.canPlace(shadowGameField, piece.pentominoe_piece, piece.row + 1, piece.col);
    }
    public static boolean tryLeft() {
        return PieceHelperFunctions.canPlace(shadowGameField, piece.pentominoe_piece, piece.row - 1, piece.col);
    }

    public static boolean tryDown() {
        return !PieceHelperFunctions.canPlace(shadowGameField, piece.pentominoe_piece, piece.row, piece.col + 1);
    }

    public static void trySlam() {
        while(PieceHelperFunctions.canPlace(shadowGameField, piece.pentominoe_piece, piece.row, piece.col+1)){
            piece.col++;
        }
    }

    public static void nextPiece() {
        piece = nextPiece;
        nextPiece = new Piece();
    }
    
    //next move board generation
    public static int[][] getNextPieceBoard(){
        int[][] temp_arr = new int[5][5];
        for (int i = 0; i < temp_arr.length; i++) {
            for (int j = 0; j < temp_arr[0].length; j++) {
                temp_arr[i][j] = -1;
            }
        }
        PieceHelperFunctions.addPiece(temp_arr, nextPiece.pentominoe_piece, nextPiece.pieceID, 0, 0);
        return temp_arr;
    }

    //check if piece is at the end
    //doesn't seem that clean, how is piece.atTheEnd tied into all of this, does it also atTheEnd True when hitting another piece.
    public static boolean checkIfItsAtTheEnd() {
        if (piece.col + piece.pentominoe_piece[0].length == gameField[0].length || piece.atTheEnd){
            piece.atTheEnd = true;
        } else {
            return false;
        }
        return true;
    }


    public static void UserCheckIfGameOver() {
        if (!PieceHelperFunctions.canPlace(shadowGameField, piece.pentominoe_piece, piece.row, piece.col)){
            emptyUI();
            shadowGameField = copy(gameField);

            if (Panels.PanelScore > Panels.HighScore){
                Panels.HighScore = Panels.PanelScore;
                Panels.Label_HighScore.setText("High Score: " + Panels.HighScore);
                Panels.score.setText("Score: " + 0);
                Panels.PanelScore = 0;
            } else {
                Panels.PanelScore = 0;
                Panels.score.setText("Score: " + Panels.PanelScore);
            }
        }
    }

    public static void AICheckIfGameOver() {
        if (!PieceHelperFunctions.canPlace(gameField, piece.pentominoe_piece, piece.row, piece.col)){
            emptyUI();


            BotMainFrame.scores.add(Panels.PanelScore);

            BotMainFrame.pieceIndex = 0;

            if (Panels.PanelScore > Panels.HighScore){
                Panels.HighScore = Panels.PanelScore;
                Panels.Label_HighScore.setText("High Score: " + Panels.HighScore);
                Panels.score.setText("Score: " + 0);
                Panels.PanelScore = 0;
            } else {
                Panels.PanelScore = 0;
                Panels.score.setText("Score: " + Panels.PanelScore);
            }

        } else BotMainFrame.pieceIndex ++;
    }


    public static void tryRotate() {
        piece.rotatePiece();
        if (PieceHelperFunctions.canPlace(shadowGameField, PentominoDatabase.data[piece.pieceID][piece.mutID], piece.row, piece.col)){
            piece.pentominoe_piece = PentominoDatabase.data[piece.pieceID][piece.mutID];
        } else {
            //piece width
            int height = piece.pentominoe_piece[0].length;
            //piece height
            int width = piece.pentominoe_piece.length;

            //this behaviour concerns the 'jump' pieces make left when they try to rotate on the right-side wall.  
            if ((piece.row - (height-width) > -1) && PieceHelperFunctions.canPlace(shadowGameField, PentominoDatabase.data[piece.pieceID][piece.mutID], piece.row - (height-width), piece.col) ){
                piece.row -= (height-width);
                piece.pentominoe_piece = PentominoDatabase.data[piece.pieceID][piece.mutID];
            }

        }
    }

    //This could be improved to also show how many lines we cleared for future multi-line bonus functionality
    public static void AIClearLinesIfPossible() {
        for (int i = 0; i < gameField[0].length; i++) {
            int rowElTracker = 0;
            for (int[] ints : gameField) {
                if (ints[i] != -1) {
                    rowElTracker++;
                }
            }
            if (rowElTracker == gameField.length){
                shiftDown(gameField, i);
            }
        }
    }
    public static void UserClearLinesIfPossible() {
        for (int i = 0; i < gameField[0].length; i++) {
            int rowElTracker = 0;
            for (int[] ints : gameField) {
                if (ints[i] != -1) {
                    rowElTracker++;
                }
            }
            if (rowElTracker == gameField.length){
                shiftDown(shadowGameField, i);
            }
        }
    }
    //shifts everything down, going up from the targetRow, which in our case will be the filled line.
    public static void shiftDown(int[][] field, int targetRow){

        for (int i = 0; i < field.length ; i++) {
            for (int j = targetRow; j > 0 ; j--) {
                field[i][j] = field[i][j-1];
            }
        }
        Panels.PanelScore++;
        Panels.score.setText("Score: " + Panels.PanelScore);
    }

}
