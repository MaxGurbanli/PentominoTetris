import Pentominoes.PentominoDatabase;

import java.util.*;

public class Piece {
    public boolean atTheEnd;
    int[][] pentominoe_piece;
    int row;
    int col;
    int pieceID;
    int mutID;
    int maxMut;

    Character[] moves;

    public Piece(){
        //Get random piece and mutation ID
        Random random = new Random();
        pieceID = random.nextInt(12);
        maxMut = PentominoDatabase.data[pieceID].length;
        mutID = random.nextInt(maxMut);
        //Get Piece
        pentominoe_piece = PentominoDatabase.data[pieceID][mutID];
        //Starting Row and Col are 0,0
        row = 0;
        col = 0;
        //piece is not at the end
        atTheEnd = false;
    }

    public void rotatePiece(){
        if (mutID == maxMut - 1){
            mutID = 0;
        } else {
            mutID++;
        }
    }

    public void fillUpSetPiece() {
        Character[] availPieces = new Character[12];
        availPieces[0] = 'T';
        availPieces[1] = 'L';
        availPieces[2] = 'U';
        availPieces[3] = 'V';
        availPieces[4] = 'N';
        availPieces[5] = 'I';
        availPieces[6] = 'F';
        availPieces[7] = 'X';
        availPieces[8] = 'P';
        availPieces[9] = 'Y';
        availPieces[10] = 'Z';
        availPieces[11] = 'W';
        moves = availPieces;
    }
}
