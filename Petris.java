import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class Petris extends JFrame {
    private JLabel statusbar;
    private List<int[][]> pentominoShapes;

    public Petris() {
        pentominoShapes = createPentominoShapes();
        statusbar = new JLabel(" 0");
        add(statusbar, BorderLayout.SOUTH);
        initUI();
    }

    private void initUI() {
        setTitle("Petris");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(200, 400);
        setLocationRelativeTo(null);

        GameBoard gameBoard = new GameBoard(statusbar, pentominoShapes);
        add(gameBoard);
        gameBoard.start();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            Petris game = new Petris();
            game.setVisible(true);
        });
    }

    private List<int[][]> createPentominoShapes() {
        List<int[][]> shapes = new ArrayList<>();
        for (int[][] basicShape : basicDatabase) {
            List<int[]> coordinatesList = new ArrayList<>();
            for (int row = 0; row < basicShape.length; row++) {
                for (int col = 0; col < basicShape[row].length; col++) {
                    if (basicShape[row][col] == 1) {
                        coordinatesList.add(new int[]{col, row});
                    }
                }
            }
            int[][] coordinates = new int[coordinatesList.size()][2];
            for (int i = 0; i < coordinatesList.size(); i++) {
                coordinates[i] = coordinatesList.get(i);
            }
            shapes.add(coordinates);
        }
        return shapes;
    }
    

    private static int[][][] basicDatabase = {
        {
                // pentomino representation X
                { 0, 1, 0 },
                { 1, 1, 1 },
                { 0, 1, 0 }
        },
        {
                // pentomino representation I
                { 1 },
                { 1 },
                { 1 },
                { 1 },
                { 1 }
        },
        {
                // pentomino representation Z
                { 0, 1, 1 },
                { 0, 1, 0 },
                { 1, 1, 0 }
        },
        {
                // pentomino representation T
                { 1, 1, 1 },
                { 0, 1, 0 },
                { 0, 1, 0 }
        },
        {
                // pentomino representation U
                { 1, 1 },
                { 1, 0 },
                { 1, 1 }
        },
        {
                // pentomino representation V
                { 1, 1, 1 },
                { 1, 0, 0 },
                { 1, 0, 0 }
        },
        {
                // pentomino representation W
                { 0, 0, 1 },
                { 0, 1, 1 },
                { 1, 1, 0 }
        },
        {
                // pentomino representation Y
                { 1, 0 },
                { 1, 1 },
                { 1, 0 },
                { 1, 0 }
        },
        {
                // pentomino representation L
                { 1, 0 },
                { 1, 0 },
                { 1, 0 },
                { 1, 1 }
        },
        {
                // Implement pentomino representation P
                { 1, 1 },
                { 1, 1 },
                { 1, 0 }
        },
        {
                // pentomino representation N
                { 0, 1 },
                { 0, 1 },
                { 1, 1 },
                { 1, 0 }
        },
        {
                // pentomino representation F
                { 0, 1, 1 },
                { 1, 1, 0 },
                { 0, 1, 0 }
        }
    };
    
}

class GameBoard extends JPanel implements ActionListener {
    private JLabel statusbar;
    private final int BOARD_WIDTH = 5;
    private final int BOARD_HEIGHT = 12;
    private Timer timer;
    private boolean isFallingFinished = false;
    private boolean isPaused = false;
    private int numLinesRemoved = 0;
    private int curX = 0;
    private int curY = 0;
    private Shape curPiece;
    private Tetrominoes[] board;
    private boolean isStarted = false;

    public GameBoard(JLabel statusbar, List<int[][]> pentominoShapes) {
        this.statusbar = statusbar;
        addKeyListener(new TAdapter());
        setFocusable(true);
        curPiece = new Shape(pentominoShapes);
        timer = new Timer(400, this);
        board = new Tetrominoes[BOARD_WIDTH * BOARD_HEIGHT];
        clearBoard();
    }

    public void start() {
        isStarted = true;
        curPiece.setRandomShape();
        timer.start();
    }


    private void clearBoard() {
        for (int i = 0; i < BOARD_HEIGHT * BOARD_WIDTH; ++i) {
            board[i] = Tetrominoes.NoShape;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isFallingFinished) {
            isFallingFinished = false;
            newPiece();
        } else {
            oneLineDown();
        }
        repaint();
    }

    private void newPiece() {
        curPiece.setRandomShape();
        curX = BOARD_WIDTH / 2 - 1; // Centering the piece horizontally
        curY = curPiece.minY(); // Placing the piece at the top of the board

        if (!tryMove(curPiece, curX, curY)) {
            System.out.println("Game Over triggered immediately");
            triggerGameOver();
        }
    }


    private void oneLineDown() {
        if (!tryMove(curPiece, curX, curY - 1)) {
            pieceDropped();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoard(g);
    }

private void dropDown() {
    int newY = curY;
    while (newY > 0) {
        if (!tryMove(curPiece, curX, newY - 1))
            break;
        --newY;
    }
    pieceDropped();
}


private boolean tryMove(Shape newPiece, int newX, int newY) {
    for (int i = 0; i < 5; ++i) { // Use 5 for pentominoes
        int x = newX + newPiece.x(i);
        int y = newY - newPiece.y(i);

        if (x < 0 || x >= BOARD_WIDTH || y < 0 || y >= BOARD_HEIGHT) {
            System.out.println("Out of bounds: " + x + ", " + y);
            return false;
        }
        
        if (shapeAt(x, y) != Tetrominoes.NoShape) {
            System.out.println("Collision detected at: " + x + ", " + y);
            return false;
        }
    }

    curPiece = newPiece;
    curX = newX;
    curY = newY;
    return true;
}


private void pieceDropped() {
    for (int i = 0; i < 5; ++i) { // Assuming pentominoes have 5 blocks
        int x = curX + curPiece.x(i);
        int y = curY - curPiece.y(i);

        if (x < 0 || x >= BOARD_WIDTH || y < 0 || y >= BOARD_HEIGHT) {
            System.out.println("Game Over triggered in pieceDropped() due to out of bounds! Coords: " + x + ", " + y);
            triggerGameOver();
            return;
        }

        if (shapeAt(x, y) != Tetrominoes.NoShape) {
            System.out.println("Game Over triggered in pieceDropped() due to collision! Coords: " + x + ", " + y);
            triggerGameOver();
            return;
        }

        board[(y * BOARD_WIDTH) + x] = curPiece.getShape();
    }

    removeFullLines();

    if (!isFallingFinished)
        newPiece();
}

private void triggerGameOver() {
    curPiece.setShape(Tetrominoes.NoShape);
    timer.stop();
    isStarted = false;
    statusbar.setText("GAME OVER");
}



private void removeFullLines() {
    int numFullLines = 0;

    for (int i = BOARD_HEIGHT - 1; i >= 0; --i) {
        boolean lineIsFull = true;

        for (int j = 0; j < BOARD_WIDTH; ++j) {
            if (shapeAt(j, i) == Tetrominoes.NoShape) {
                lineIsFull = false;
                break;
            }
        }

        if (lineIsFull) {
            ++numFullLines;
            for (int k = i; k < BOARD_HEIGHT - 1; ++k) {
                for (int j = 0; j < BOARD_WIDTH; ++j)
                    board[(k * BOARD_WIDTH) + j] = shapeAt(j, k + 1);
            }
        }
    }

    if (numFullLines > 0) {
        numLinesRemoved += numFullLines;
        isFallingFinished = true;
        curPiece.setShape(Tetrominoes.NoShape);
        repaint();
    }
}

private void drawBoard(Graphics g) {
    int squareWidth = getWidth() / BOARD_WIDTH;
    int squareHeight = getHeight() / BOARD_HEIGHT;

    for (int i = 0; i < BOARD_HEIGHT; ++i) {
        for (int j = 0; j < BOARD_WIDTH; ++j) {
            Tetrominoes shape = shapeAt(j, BOARD_HEIGHT - i - 1);
            if (shape != Tetrominoes.NoShape)
                drawSquare(g, j * squareWidth, i * squareHeight, shape);
        }
    }

    if (curPiece.getShape() != Tetrominoes.NoShape) {
        for (int i = 0; i < 5; ++i) {
            int x = curX + curPiece.x(i);
            int y = curY - curPiece.y(i);
            drawSquare(g, x * squareWidth, (BOARD_HEIGHT - y - 1) * squareHeight, curPiece.getShape());
        }
    }
}

    private void drawSquare(Graphics g, int x, int y, Tetrominoes shape) {

        System.out.println("Drawing square at: " + x + ", " + y + " for shape: " + shape);
        
        Color color;

        switch (shape) {
            case XShape:
                color = new Color(204, 102, 102);
                break;
            case IShape:
                color = new Color(102, 204, 204);
                break;
            case ZShape:
                color = new Color(102, 204, 102);
                break;
            case TShape:
                color = new Color(204, 204, 102);
                break;
            case UShape:
                color = new Color(204, 102, 204);
                break;
            case VShape:
                color = new Color(102, 102, 204);
                break;
            case WShape:
                color = new Color(218, 170, 0);
                break;
            case YShape:
                color = new Color(0, 0, 0);
                break;
            case LShape:
                color = new Color(255, 0, 0);
                break;
            case PShape:
                color = new Color(0, 255, 0);
                break;
            case NShape:
                color = new Color(0, 0, 255);
                break;
            case FShape:
                color = new Color(255, 255, 0);
                break;
            default:
                color = Color.BLACK;
                break;
        }

        g.setColor(color);
        g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);

        g.setColor(color.brighter());
        g.drawLine(x, y + squareHeight() - 1, x, y);
        g.drawLine(x, y, x + squareWidth() - 1, y);

        g.setColor(color.darker());
        g.drawLine(x + 1, y + squareHeight() - 1,
                x + squareWidth() - 1, y + squareHeight() - 1);
        g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1,
                x + squareWidth() - 1, y + 1);
    }

private int squareWidth() { return getWidth() / BOARD_WIDTH; }
private int squareHeight() { return getHeight() / BOARD_HEIGHT; }
private Tetrominoes shapeAt(int x, int y) { return board[(y * BOARD_WIDTH) + x]; }

class Shape {
    private Tetrominoes pieceShape;
    private int[][] coords; // Coordinates of the blocks in the shape
    private List<int[][]> pentominoShapes;

    public Shape(List<int[][]> pentominoShapes) {
        this.pentominoShapes = pentominoShapes;
        coords = new int[5][2]; // Updated for pentominoes (5 blocks)
        setShape(Tetrominoes.NoShape);
    }

    public void setShape(Tetrominoes shape) {
        this.pieceShape = shape;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 2; ++j) {
                coords[i][j] = 0; // Initialize with 0
            }
        }

        switch (shape) {
            case XShape:
                coords = new int[][]{{1,2}, {0,3}, {1,3}, {2,3}, {1,4}};
                break;
            case IShape:
                coords = new int[][]{{0,0}, {0,1}, {0,2}, {0,3}, {0,4}};
                break;
            case ZShape:
                coords = new int[][]{{0,0}, {1,0}, {1,1}, {1,2}, {2,2}};
                break;
            case TShape:
                coords = new int[][]{{0,0}, {1,0}, {2,0}, {1,1}, {1,2}};
                break;
            case UShape:
                coords = new int[][]{{0,0}, {0,1}, {1,1}, {2,1}, {2,0}};
                break;
            case VShape:
                coords = new int[][]{{0,0}, {0,1}, {0,2}, {1,2}, {2,2}};
                break;
            case WShape:
                coords = new int[][]{{0,0}, {1,0}, {1,1}, {2,1}, {2,2}};
                break;
            case YShape:
                coords = new int[][]{{0,0}, {0,1}, {0,2}, {0,3}, {1,1}};
                break;
            case LShape:
                coords = new int[][]{{0,0}, {0,1}, {0,2}, {0,3}, {1,0}};
                break;
            case PShape:
                coords = new int[][]{{0,0}, {0,1}, {0,2}, {1,0}, {1,1}};
                break;
            case NShape:
                coords = new int[][]{{0,0}, {0,1}, {0,2}, {1,2}, {1,3}};
                break;
            case FShape:
                coords = new int[][]{{0,0}, {0,1}, {1,1}, {1,2}, {2,2}};
                break;
            default:
                coords = new int[][]{{0,0}, {0,0}, {0,0}, {0,0}, {0,0}};
                break;
        }
    }

    public Tetrominoes getShape() {
        return pieceShape;
    }

    public void setRandomShape() {
        Random r = new Random();
        int x = r.nextInt(pentominoShapes.size());
        coords = pentominoShapes.get(x);
    }

    public int x(int index) {
        return coords[index][0];
    }

    public int y(int index) {
        return coords[index][1];
    }

    public int minY() {
        int m = coords[0][1];
        for (int i = 0; i < 5; i++) {
            m = Math.min(m, coords[i][1]);
        }
        return m;
    }

    public Shape rotateLeft() {

        Shape result = new Shape(pentominoShapes);
        result.pieceShape = pieceShape;

        for (int i = 0; i < 5; ++i) {
            result.setX(i, y(i));
            result.setY(i, -x(i));
        }
        return result;
    }

    public Shape rotateRight() {
        Shape result = new Shape(pentominoShapes);
        result.pieceShape = pieceShape;

        for (int i = 0; i < 5; ++i) {
            result.setX(i, -y(i));
            result.setY(i, x(i));
        }
        return result;
    }

    private void setX(int index, int x) { coords[index][0] = x; }
    private void setY(int index, int y) { coords[index][1] = y; }


    }

    enum Tetrominoes {
        NoShape, XShape, IShape, ZShape, TShape, UShape, VShape, WShape, YShape, LShape, PShape, NShape, FShape
    }


    class TAdapter extends KeyAdapter {
    @Override
    public void keyPressed(KeyEvent e) {
        if (!isStarted || curPiece.getShape() == Tetrominoes.NoShape) {
            return;
        }

        int keycode = e.getKeyCode();

        if (keycode == 'p' || keycode == 'P') {
            pause();
            return;
        }

        if (isPaused)
            return;

        switch (keycode) {
            case KeyEvent.VK_LEFT:
                tryMove(curPiece, curX - 1, curY);
                break;
            case KeyEvent.VK_RIGHT:
                tryMove(curPiece, curX + 1, curY);
                break;
            case KeyEvent.VK_DOWN:
                tryMove(curPiece.rotateRight(), curX, curY);
                break;
            case KeyEvent.VK_UP:
                tryMove(curPiece.rotateLeft(), curX, curY);
                break;
            case KeyEvent.VK_SPACE:
                dropDown();
                break;
            case KeyEvent.VK_D:
                oneLineDown();
                break;
        }
    }

    public void pause() {
        if (!isStarted) {
            return;
        }

        isPaused = !isPaused;
        if (isPaused) {
            timer.stop();
            statusbar.setText("Paused");
        } else {
            timer.start();
            statusbar.setText(String.valueOf(numLinesRemoved));
        }
        repaint();
    }

}
}
    
