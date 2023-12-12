public class FeatureFunctions {
    public static int calculateErodedPieces(int[][] someBoard, int[][] piece, int pieceID, int PlacedX, int PlacedY) {
        int[][] TestBoard = GameBoard.copy(someBoard);
        PieceHelperFunctions.addPiece(TestBoard, piece, pieceID, PlacedX, PlacedY);
        int coolRes = GiveNumberOfClearedLines(TestBoard);
        return coolRes * coolRes;
    }

    private static int GiveNumberOfClearedLines(int[][] testBoard) {
        int rowElTracker = 0;
        for (int i = 0; i < testBoard[0].length; i++) {
            int j = 0;
            while (j < testBoard.length) {
                if (testBoard[j][i] == -1) {
                    break;
                } else if (j == testBoard.length - 1)
                    rowElTracker++;
                j++;
            }
        }
        return rowElTracker;
    }


    public static int calculateColTransition(int[][] localBoard) {
        //number of filled cells adjacent to empty cells summed over all rows
        int cells = 0;
        for (int i = 0; i < localBoard[0].length; i++) {
            for (int[] ints : localBoard) {
                bigloop:
                if (ints[i] != -1) {
                    //check up
                    if (i != 0) {
                        if (ints[i - 1] == -1) {
                            cells++;
                            break bigloop;
                        }
                    }
                    //check down
                    if (i < localBoard[0].length - 1) {
                        if (ints[i + 1] == -1) {
                            cells++;
                        }
                    }
                }
            }
        }
        return cells;
    }

    public static int calculateRowTransition(int[][] localBoard) {
        //number of filled cells adjacent to empty cells summed over all cols
        int cells = 0;
        for (int i = 0; i < localBoard[0].length; i++) {
            for (int j = 0; j < localBoard.length; j++) {
                BigLoop:
                if (localBoard[j][i] != -1) {
                    //check left
                    if (j != 0) {
                        if (localBoard[j - 1][i] == -1) {
                            cells++;
                            break BigLoop;
                        }
                    }
                    //check right
                    if (j < localBoard.length - 1) {
                        if (localBoard[j + 1][i] == -1) {
                            cells++;
                        }
                    }
                }


            }
        }
        return cells;
    }

    public static int calculateNumberHoles(int[][] localBoard) {
        int cells = 0;
        for (int i = 0; i < localBoard[0].length; i++) {
            for (int[] ints : localBoard) {
                //if its empty
                if (ints[i] == -1) {
                    if (i != 0) {
                        //if top not empty it's a hole
                        if (ints[i - 1] != -1) {
                            cells++;
                        }
                    }
                }
            }
        }
        return cells;
    }

    public static int calculateCumulativeWells(int[][] localBoard) {
        int c_wells_sum = 0;
        for (int i = 0; i < localBoard.length ; i++) {
            for (int j = 0; j < localBoard[0].length; j++) {
                if (localBoard[i][j] != -1){
                    break;
                }
                //if we are at the top
                if (i == 0){
                    if (localBoard[i+1][j] != -1){
                        c_wells_sum += j;
                    }
                }
                //if we are at the bottom
                if (i == localBoard.length-1){
                    if (localBoard[i-1][j] != -1){
                        c_wells_sum += j;
                    }
                }
                //if we are around the middle
                if (i != 0 && i != localBoard.length-1){
                    if (localBoard[i-1][j] != -1 && localBoard[i+1][j] != -1){
                        c_wells_sum += j;
                    }
                }
            }
        }
        return c_wells_sum;
    }

}