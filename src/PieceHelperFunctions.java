public class PieceHelperFunctions {

    public static void addPiece(int[][] field, int[][] piece, int pieceID, int x, int y) {
        for (int i = 0; i < piece.length; i++) // loop over x position of pentomino
        {
            for (int j = 0; j < piece[i].length; j++) // loop over y position of pentomino
            {
                if (piece[i][j] == 1) {
                    // Add the ID of the pentomino to the board if the pentomino occupies this square
                    field[x + i][y + j] = pieceID;
                }
            }
        }
    }

    public static boolean canPlace(int[][] field, int[][] pentominoe, int row, int col) {
        // Get the dimensions
        int boardHeight = field.length;
        int boardWidth = field[0].length;

        int PentHeight = pentominoe.length;
        int PentWidth = pentominoe[0].length;

        // Check if the shape exceeds the boundaries of the board
        if (row + PentHeight > boardHeight || col + PentWidth > boardWidth) {
            return false;
        }

        // Check for collisions
        for (int i = 0; i < PentHeight; i++) {
            for (int j = 0; j < PentWidth; j++) {
                if (pentominoe[i][j] == 1 && field[row + i][col + j] != -1) {
                    return false; /*
                     * If the field in the "pentominoe" array is equal to '1' (not empty), and its
                     * corresponding field in the "field" array
                     * is not equal to '-1' (not empty), there is a collision.
                     */
                }
            }
        }
        return true;
    }

}
