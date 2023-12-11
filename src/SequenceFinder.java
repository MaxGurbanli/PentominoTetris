
// Brute-force backtracking algorithm with optimizations

// Optimizations: Recursive backtrack search, dead spot detection, constant time access to pentomino IDs
// Made by: Max Gurbanli

import java.util.ArrayList;
import java.util.Arrays;

import GUI.UI;
import Pentominoes.PentominoDatabase;

import java.util.HashMap;
import java.util.Map;

public class SequenceFinder {

    final int horiGridSize = 5;
    final int vertGridSize = 12;
//    public char[] input = {'X', 'I', 'Z', 'T', 'U', 'V', 'W', 'Y', 'P','L', 'N', 'F'};
    public char[] input = {'P', 'U', 'W', 'F', 'X', 'I', 'N', 'L', 'Z', 'T', 'V', 'Y'};
    // This key-value store maps a pentomino letter to its ID
    //    // This is used to get the ID of a pentomino in constant time compared to using
    // if-else statements
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
    private static final Map<Integer, Character> pentominoIDToKey = new HashMap<>();
    static {
        pentominoIDToKey.put(0,'X');
        pentominoIDToKey.put(1, 'I');
        pentominoIDToKey.put(2, 'Z');
        pentominoIDToKey.put(3, 'T');
        pentominoIDToKey.put(4, 'U');
        pentominoIDToKey.put(5, 'V');
        pentominoIDToKey.put(6, 'W');
        pentominoIDToKey.put(7, 'Y');
        pentominoIDToKey.put(8, 'L');
        pentominoIDToKey.put(9, 'P');
        pentominoIDToKey.put(10, 'N');
        pentominoIDToKey.put(11, 'F');
    }

    // Create the UI object
    public UI ui;

    public void search() {
        int[][] field = new int[horiGridSize][vertGridSize];
        for (int[] ints : field) {
            Arrays.fill(ints, -1);
        }
        long startTime = System.currentTimeMillis();
        boolean foundSolution = optimizedRecursiveSearch(field, 0, ui);
        long endTime = System.currentTimeMillis();
        if (foundSolution) {
            System.out.println("Solution found");
            //turn gui on

            for(int[] item : field) System.out.println(Arrays.toString(item));
            System.out.println("\n");
            System.out.print("The Sequence: ");
            char[] sequence = sequenceFinder(field);
            for(char c : sequence){
                System.out.print(c + ", ");
            }
            System.out.println("\n");
        } else {
            System.out.println("No solution found");
        }
        System.out.println("Found a solution in " + (endTime - startTime) + " ms" + "\n");
    }

    /**
     * Performs an optimized recursive search.
     * Iterates through all possible mutations of the pentomino and tries to place
     * it on the field.
     * Once a pentomino can be placed, the method recursively places the next
     * pentomino.
     * If a dead spot is found on the field, the method backtracks.
     * 
     * @param field          a matrix representing the board to be fulfilled with
     *                       pentominoes
     * @param pentominoIndex the index of the pentomino to be placed
     * @param ui             the UI object
     * @return true if a solution is found, false otherwise
     */
    private boolean optimizedRecursiveSearch(int[][] field, int pentominoIndex, UI ui) {
        if (pentominoIndex == input.length) {
            return true; // all pentominos have been placed, the solution is found
        }

        int pentominoID = pentominoKeyToID.get(input[pentominoIndex]);

        for (int mutation = 0; mutation < PentominoDatabase.data[pentominoID].length; mutation++) {
            int[][] pieceToPlace = PentominoDatabase.data[pentominoID][mutation];

            for (int x = 0; x <= horiGridSize - pieceToPlace.length; x++) {
                for (int y = 0; y <= vertGridSize - pieceToPlace[0].length; y++) {
                    if (canPlace(field, pieceToPlace, x, y)) {

                        addPiece(field, pieceToPlace, pentominoID, x, y);

                        if (ui != null) {
                            ui.setState(field);
                        }

                        if (!hasDeadSpot(field) && optimizedRecursiveSearch(field, pentominoIndex + 1, ui)) {
                            return true; // Found a solution
                        }

                        removePiece(field, pieceToPlace, x, y); // Backtrack
                    }
                }
            }
        }
        return false; // Couldn't place this pentomino
    }

    /**
     * Checks if the given field has a dead spot, i.e. a region of empty cells that
     * is not a multiple of 5 in size.
     * Uses flood fill algorithm to count the size of each empty region.
     * 
     * @param field a matrix representing the game board
     * @return true if the field has a dead spot, false otherwise
     */
    private static boolean hasDeadSpot(int[][] field) {
        boolean[][] visited = new boolean[field.length][field[0].length];
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[i].length; j++) {
                if (field[i][j] == -1 && !visited[i][j]) {
                    int size = floodFill(field, i, j, visited);
                    if (size % 5 != 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Performs a flood fill algorithm on a 2D integer array, starting from the
     * specified cell (i, j).
     * Marks all cells that are reachable from the starting cell and have a value of
     * -1.
     *
     * @param field   a matrix representing the game board
     * @param i       the row index of the starting cell
     * @param j       the column index of the starting cell
     * @param visited a 2D boolean array representing which cells have already been
     *                visited
     * @return the number of cells that were marked by the flood fill algorithm
     */
    private static int floodFill(int[][] field, int i, int j, boolean[][] visited) {
        if (i < 0 || i >= field.length || j < 0 || j >= field[0].length || visited[i][j] || field[i][j] != -1) {
            return 0;
        }
        visited[i][j] = true;
        return 1 + floodFill(field, i + 1, j, visited) + floodFill(field, i - 1, j, visited) +
                floodFill(field, i, j + 1, visited) + floodFill(field, i, j - 1, visited);
    }

    /**
     * Determines whether a given piece can be placed on the field at the specified
     * position.
     * 
     * @param field a matrix representing the game board
     * @param piece a matrix representing the pentomino to be placed in the board
     * @param x     x position of the pentomino
     * @param y     y position of the pentomino
     * @return true if the piece can be placed at the specified position, false
     *         otherwise
     */
    private boolean canPlace(int[][] field, int[][] piece, int x, int y) {
        for (int i = 0; i < piece.length; i++) {
            for (int j = 0; j < piece[i].length; j++) {
                if (piece[i][j] == 1
                        && (x + i >= horiGridSize || y + j >= vertGridSize || field[x + i][y + j] != -1)) {
                    return false; // Piece goes out of bounds or overlaps another piece
                }
            }
        }
        return true;
    }

    /**
     * Removes a pentomino from the position on the field
     * 
     * @param field   a matrix representing the game board
     * @param piece   a matrix representing the pentomino to be removed from the
     *                board
     * @param x       x position of the pentomino
     * @param y       y position of the pentomino
     */
    private static void removePiece(int[][] field, int[][] piece, int x, int y) {
        for (int i = 0; i < piece.length; i++) {
            for (int j = 0; j < piece[i].length; j++) {
                if (piece[i][j] == 1) {
                    field[x + i][y + j] = -1;
                }
            }
        }
    }

    /**
     * Adds a pentomino to the position on the field (overriding current board at
     * that position)
     * 
     * @param field   a matrix representing the game board
     * @param piece   a matrix representing the pentomino to be placed in the board
     * @param pieceID ID of the relevant pentomino
     * @param x       x position of the pentomino
     * @param y       y position of the pentomino
     */
    public void addPiece(int[][] field, int[][] piece, int pieceID, int x, int y) {
        for (int i = 0; i < piece.length; i++) // loop over x position of pentomino
        {
            for (int j = 0; j < piece[i].length; j++) // loop over y position of pentomino
            {
                if (piece[i][j] == 1) {
                    // Add the ID of the pentomino to the board if the pentomino occupies this
                    // square
                    field[x + i][y + j] = pieceID;
                    if (ui != null) {
                        ui.setState(field);
                    }
                }
            }
        }
    }
    /**
     * Sequence Finder. This will work backwords from the solution that the search found and see what the
     * sequence of the pentominoes are as if they are dropped down like a tetris game.
     * this algorithm finds the solution 80% of the time as it is solely based on a heursitic.
     * The heuristic is comparing the frequence of each pentomino on each row brom bottom to top.
     */
    public static char[] sequenceFinder(int[][] grid) {
        ArrayList<Integer> Sequence= new ArrayList<>();

        for(int i = 0; i < grid[0].length; i++){ //loop over collumns
            //the index of counterArrar is used to see which block is the counter being represented on
            int[] counterArray = new int[12]; //counts the frequence of a piece at a row
            Arrays.fill(counterArray, -1);//filled with -1 as the blocks are represented from 0-11

            for (int[] ints : grid) { //loop over rows(starting from bottom)
                int index = ints[i];
                if (!Sequence.contains(index)) {//skips blocks that were encountered before
                    counterArray[index]++;
                }
            }
            /*Create a copy of the counter array and sort it to find the highest frequencies, 
            then getting their indexes which represent their ID.*/
            int[] counterArraySorted = new int[12];
            System.arraycopy(counterArray, 0, counterArraySorted, 0, 12);
            Arrays.sort(counterArraySorted);

            for(int k = counterArraySorted.length-1; counterArraySorted[k] != -1; k--){//loop over sortedArray backwards as it is sorted from smallest to greatest
                int count = counterArraySorted[k];
                
                for(int l = 0; l < counterArray.length; l++){//loop over the main counter array to find the block's ID using its index
                    if (counterArray[l] == count){
                        counterArray[l] = -1;//reset the blocks counter
                        Sequence.add(l);//add the block to the sequence
                    }
                }
            }
        }
        //Convert the pentID into their corresponding names
        char[] sequenceInLetters = new char[12];
        for(int i = 0; i < sequenceInLetters.length; i++){
            sequenceInLetters[i] = pentominoIDToKey.get(Sequence.get(i));
        }
        return sequenceInLetters;

    }

    /**
     * Main function. Needs to be executed to start the search algorithm
     */
    public static void main(String[] args) {
        // TESTING CODE
        SequenceFinder search = new SequenceFinder();
        System.out.println("Starting search...");
        search.search();
    }
}
