import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class Petris extends JFrame {
    private JLabel statusbar;

    public Petris() {
        statusbar = new JLabel(" 0");
        add(statusbar, BorderLayout.SOUTH);
        initUI();
    }

    private void initUI() {
        setTitle("Pentomino Tetris Game");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(200, 400);
        setLocationRelativeTo(null);

        GameBoard gameBoard = new GameBoard(statusbar);
        add(gameBoard);
        gameBoard.start();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            Petris game = new Petris();
            game.setVisible(true);
        });
    }
}

class GameBoard extends JPanel implements ActionListener {
    private JLabel statusbar;
    private final int BOARD_WIDTH = 5;
    private final int BOARD_HEIGHT = 15;
    private Timer timer;
    private boolean isFallingFinished = false;
    private boolean isPaused = false;
    private int numLinesRemoved = 0;
    private int curX = 0;
    private int curY = 0;
    private Shape curPiece;
    private Tetrominoes[] board;
    private boolean isStarted = false;

    public GameBoard(JLabel statusbar) {
        this.statusbar = statusbar;
        this.isStarted = false; // Initialize isStarted
        addKeyListener(new TAdapter());
        setFocusable(true);
        curPiece = new Shape();
        timer = new Timer(400, this);
        board = new Tetrominoes[BOARD_WIDTH * BOARD_HEIGHT];
        clearBoard();
    }



    public void start() {
        isStarted = true; // Set isStarted to true when the game starts
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
    }

    private void newPiece() {
        curPiece.setRandomShape();
        curX = BOARD_WIDTH / 2 + 1;
        curY = BOARD_HEIGHT - 1 + curPiece.minY();

        if (!tryMove(curPiece, curX, curY - 1)) {
            curPiece.setShape(Tetrominoes.NoShape);
            timer.stop();
            isStarted = false;
            statusbar.setText("Game over");
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
    for (int i = 0; i < 4; ++i) {
        int x = newX + newPiece.x(i);
        int y = newY - newPiece.y(i);
        
        if (x < 0 || x >= BOARD_WIDTH || y < 0 || y >= BOARD_HEIGHT)
            return false;
        
        if (shapeAt(x, y) != Tetrominoes.NoShape)
            return false;
    }

    curPiece = newPiece;
    curX = newX;
    curY = newY;
    repaint();
    return true;
}


private void pieceDropped() {
    for (int i = 0; i < 4; ++i) {
        int x = curX + curPiece.x(i);
        int y = curY - curPiece.y(i);

        // Check if the coordinates are within the board bounds
        if (x >= 0 && x < BOARD_WIDTH && y >= 0 && y < BOARD_HEIGHT) {
            board[(y * BOARD_WIDTH) + x] = curPiece.getShape();
        } else {
            // Handle out-of-bounds coordinates
            // For example, you might set a game over condition or log an error
        }
    }

    removeFullLines();

    if (!isFallingFinished)
        newPiece();
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
        for (int i = 0; i < 4; ++i) {
            int x = curX + curPiece.x(i);
            int y = curY - curPiece.y(i);
            drawSquare(g, x * squareWidth, (BOARD_HEIGHT - y - 1) * squareHeight, curPiece.getShape());
        }
    }
}

private void drawSquare(Graphics g, int x, int y, Tetrominoes shape) {
    Color[] colors = {
        new Color(0, 0, 0), new Color(204, 102, 102), 
        new Color(102, 204, 102), new Color(102, 102, 204), 
        new Color(204, 204, 102), new Color(204, 102, 204), 
        new Color(102, 204, 204), new Color(218, 170, 0)
    };
    Color color = colors[shape.ordinal()];

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
    private int[][] coords;

    public Shape() {
        coords = new int[4][2];
        setShape(Tetrominoes.NoShape);
    }

    public void setShape(Tetrominoes shape) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 2; ++j) {
                coords[i][j] = shapeCoordinates[shape.ordinal()][i][j];
            }
        }
        pieceShape = shape;
    }

    public Tetrominoes getShape() {
        return pieceShape;
    }

    public void setRandomShape() {
        var r = new Random();
        int x = Math.abs(r.nextInt()) % 7 + 1;
        Tetrominoes[] values = Tetrominoes.values();
        setShape(values[x]);
    }

    public int x(int index) {
        return coords[index][0];
    }

    public int y(int index) {
        return coords[index][1];
    }

    public int minY() {
        int m = coords[0][1];
        for (int i = 0; i < 4; i++) {
            m = Math.min(m, coords[i][1]);
        }
        return m;
    }

    // Coordinates for each Tetromino shape
    private final int[][][] shapeCoordinates = {
        {{0, 0}, {0, 0}, {0, 0}, {0, 0}}, // NoShape
        {{0, -1}, {0, 0}, {-1, 0}, {-1, 1}}, // ZShape
        {{0, -1}, {0, 0}, {1, 0}, {1, 1}}, // SShape
        {{0, -1}, {0, 0}, {0, 1}, {0, 2}}, // LineShape
        {{-1, 0}, {0, 0}, {1, 0}, {0, 1}}, // TShape
        {{0, 0}, {1, 0}, {0, 1}, {1, 1}}, // SquareShape
        {{-1, -1}, {0, -1}, {0, 0}, {0, 1}}, // LShape
        {{1, -1}, {0, -1}, {0, 0}, {0, 1}}  // MirroredLShape
    };

    public Shape rotateLeft() {
        if (pieceShape == Tetrominoes.SquareShape)
            return this;

        Shape result = new Shape();
        result.pieceShape = pieceShape;

        for (int i = 0; i < 4; ++i) {
            result.setX(i, y(i));
            result.setY(i, -x(i));
        }
        return result;
    }

    public Shape rotateRight() {
        if (pieceShape == Tetrominoes.SquareShape)
            return this;

        Shape result = new Shape();
        result.pieceShape = pieceShape;

        for (int i = 0; i < 4; ++i) {
            result.setX(i, -y(i));
            result.setY(i, x(i));
        }
        return result;
    }

    private void setX(int index, int x) { coords[index][0] = x; }
    private void setY(int index, int y) { coords[index][1] = y; }


    }

    enum Tetrominoes {
        NoShape, ZShape, SShape, LineShape, TShape, SquareShape, LShape, MirroredLShape
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
