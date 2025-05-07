import javax.sound.sampled.*;
import java.io.File;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.HashMap;
import java.util.Map;


public class ChessBoardGUI extends JFrame {

    private static final int W_KING = 5;
    private static final int B_KING = -5;
    private static final int BOARD_SIZE = 8;
    private Clip moveSound;
    private Clip captureSound;
    private JPanel boardPanel;
    private JButton[][] squares = new JButton[BOARD_SIZE][BOARD_SIZE];
    private ImageIcon selectedPiece = null;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private int fromRow = -1;
    private int fromCol = -1;
    private HashMap<String, ImageIcon> pieceImages = new HashMap<>();
    private ChessBoard chessBoard = new ChessBoard();
    private JLabel scoreLabelWhite = new JLabel("White: 0");
    private JLabel scoreLabelBlack = new JLabel("Black: 0");
    private final Color lightSquareColor = new Color(255, 255, 224);
    private final Color darkSquareColor = new Color(34, 139, 34);
    private JLabel turnLabel = new JLabel("Turn: White");

    public ChessBoardGUI() {
        setTitle("Chess");
        setSize(700, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        loadSounds();
        loadPieceImages();
        chessBoard.initializeBoard();
        initializeBoard();
        updateBoard();
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.add(turnLabel, BorderLayout.WEST);
        JPanel scorePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        scorePanel.add(scoreLabelWhite);
        scorePanel.add(scoreLabelBlack);
        statusPanel.add(scorePanel, BorderLayout.EAST);
        add(statusPanel, BorderLayout.SOUTH);
    }
    private void updateScoreDisplay() {
        int[] scores = chessBoard.getCurrentScore();
        scoreLabelWhite.setText("White: " + scores[0]);
        scoreLabelBlack.setText("Black: " + scores[1]);
    }
    private void loadPieceImages() {
        String[] pieces = {"rook", "knight", "bishop", "queen", "king", "pawn"};
        for (String piece : pieces) {
            pieceImages.put("white_" + piece, new ImageIcon("figures/W" + piece + ".png"));
            pieceImages.put("black_" + piece, new ImageIcon("figures/B" + piece + ".png"));
        }
    }
    private void loadSounds() {
        moveSound = loadClip("sounds/default.wav");
        captureSound = loadClip("sounds/capture.wav");
    }
    private Clip loadClip(String path) {
        try {
            File soundFile = new File(path);
            if (!soundFile.exists()) {
                return null;
            }
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            return clip;
        } catch (Exception e) {
            return null;
        }
    }
    private void playSound(Clip clip) {
        if (clip == null) return;
        if (clip.isRunning()) {clip.stop();}
        clip.setFramePosition(0);
        clip.start();
    }
    private void initializeBoard() {
        boardPanel = new JPanel(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        setupKeyBindings(boardPanel);

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                JButton squareButton = new JButton();
                squareButton.setPreferredSize(new Dimension(75, 75));
                squareButton.setMargin(new Insets(0, 0, 0, 0));

                squareButton.setBackground((row + col) % 2 == 0 ? lightSquareColor : darkSquareColor);

                int r = row;
                int c = col;
                squareButton.addActionListener(e -> handleSquareClick(r, c));

                squares[row][col] = squareButton;
                boardPanel.add(squareButton);
            }
        }

        add(boardPanel, BorderLayout.CENTER);
    }

    private void setupKeyBindings(JPanel panel) {
        InputMap inputMap = panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = panel.getActionMap();

        String[] keys = {"UP", "DOWN", "LEFT", "RIGHT", "W", "S", "A", "D", "ENTER"};
        String[] actions = {"moveUp", "moveDown", "moveLeft", "moveRight", "moveUp", "moveDown", "moveLeft", "moveRight", "confirmMove"};

        for (int i = 0; i < keys.length; i++) {
            inputMap.put(KeyStroke.getKeyStroke(keys[i]), actions[i]);
        }

        actionMap.put("moveUp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveSelection(-1, 0);
            }
        });

        actionMap.put("moveDown", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveSelection(1, 0);
            }
        });

        actionMap.put("moveLeft", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveSelection(0, -1);
            }
        });

        actionMap.put("moveRight", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveSelection(0, 1);
            }
        });

        actionMap.put("confirmMove", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attemptMove(selectedRow, selectedCol);
            }
        });
    }

    private void moveSelection(int rowChange, int colChange) {
        int newRow = selectedRow + rowChange;
        int newCol = selectedCol + colChange;

        if (newRow >= 0 && newRow < BOARD_SIZE && newCol >= 0 && newCol < BOARD_SIZE) {
            if (selectedRow != -1) {
                squares[selectedRow][selectedCol].setBorder(null);
            }
            selectedRow = newRow;
            selectedCol = newCol;

        }
    }

    private void handleSquareClick(int row, int col) {
        if (selectedPiece == null && squares[row][col].getIcon() != null) {
            int piece = getPieceValueFromIcon((ImageIcon)squares[row][col].getIcon());
            if (isCurrentPlayersPiece(piece)) {
                selectedPiece = (ImageIcon) squares[row][col].getIcon();
                selectedRow = fromRow = row;
                selectedCol = fromCol = col;
                squares[row][col].setBorder(BorderFactory.createLineBorder(Color.YELLOW, 4));
            }
        } else if (selectedPiece != null) {
            attemptMove(row, col);
        }
    }
    private void attemptMove(int toRow, int toCol) {
        int[][] boardBeforeMove = chessBoard.getBoardState();
        if (boardBeforeMove == null) {
            JOptionPane.showMessageDialog(this, "Failed to retrieve board state from C++.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int targetPiece = boardBeforeMove[toRow][toCol];

        if (chessBoard.movePiece(fromRow, fromCol, toRow, toCol)) {
            int movedPiece = boardBeforeMove[fromRow][fromCol];

            if ((movedPiece == 6 && toRow == 0) || (movedPiece == -6 && toRow == 7)) {
                handlePawnPromotion(toRow, toCol, movedPiece > 0);
            }
            updateBoard();
            updateTurnLabel();
            updateScoreDisplay();

            playSound(targetPiece != 0 ? captureSound : moveSound);
            checkForCheckmate();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid move!", "Error", JOptionPane.ERROR_MESSAGE);
        }

        selectedPiece = null;
        selectedRow = selectedCol = fromRow = fromCol = -1;
    }

    private void handlePawnPromotion(int row, int col, boolean isWhite) {
        chessBoard.promotePawn(row, col, isWhite ? 4 : -4);
        updateBoard();
    }

    private void highlightKingInCheck() {
        int[][] boardState = chessBoard.getBoardState();
        if (boardState == null) return;

        boolean inCheck = chessBoard.isInCheck();
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                int piece = boardState[row][col];
                if ((chessBoard.isWhiteTurn() && piece == W_KING) || (!chessBoard.isWhiteTurn() && piece == B_KING)) {
                    squares[row][col].setBorder(inCheck ? BorderFactory.createLineBorder(Color.RED, 4) : null);
                    return;
                }
            }
        }
    }

    private boolean isCurrentPlayersPiece(int piece) {
        return (chessBoard.isWhiteTurn() && piece > 0) || (!chessBoard.isWhiteTurn() && piece < 0);
    }
    private void resetBorders() {
        for (int row = 0; row < BOARD_SIZE; row++)
            for (int col = 0; col < BOARD_SIZE; col++)
                squares[row][col].setBorder(null);
    }

    private void updateBoard() {
        int[][] boardState = chessBoard.getBoardState();
        if (boardState == null) {
            JOptionPane.showMessageDialog(this, "Failed to retrieve board state from C++.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        resetBorders();
        for (int row = 0; row < BOARD_SIZE; row++)
            for (int col = 0; col < BOARD_SIZE; col++)
                squares[row][col].setIcon(getPieceIcon(boardState[row][col]));

        boardPanel.revalidate();
        boardPanel.repaint();
        highlightKingInCheck();
        checkForCheckmate();
    }

    private void checkForCheckmate() {
        if (chessBoard.isCheckmate(!chessBoard.isWhiteTurn())) {
            String winner = chessBoard.isWhiteTurn() ? "White" : "Black";
            JOptionPane.showMessageDialog(this, winner + " wins by checkmate!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
            resetGame();
        } else {
            checkForStalemate();
        }
    }


    private void checkForStalemate() {
        if (chessBoard.isStalemate(!chessBoard.isWhiteTurn())) {
            JOptionPane.showMessageDialog(this, "The game ends in a stalemate!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
            resetGame();
        }
    }

    private void resetGame() {
        chessBoard.initializeBoard();
        selectedPiece = null;
        selectedRow = selectedCol = -1;
        resetBorders();
        updateBoard();
        updateTurnLabel();
        updateScoreDisplay();
    }

    private void updateTurnLabel() {
        turnLabel.setText("Turn: " + (chessBoard.isWhiteTurn() ? "White" : "Black"));
    }
    private ImageIcon getPieceIcon(int piece) {
        String key = "";
        switch (piece) {
            case 1: key = "white_rook"; break;
            case 2: key = "white_knight"; break;
            case 3: key = "white_bishop"; break;
            case 4: key = "white_queen"; break;
            case 5: key = "white_king"; break;
            case 6: key = "white_pawn"; break;
            case -1: key = "black_rook"; break;
            case -2: key = "black_knight"; break;
            case -3: key = "black_bishop"; break;
            case -4: key = "black_queen"; break;
            case -5: key = "black_king"; break;
            case -6: key = "black_pawn"; break;
            default: return null;
        }
        return pieceImages.get(key);
    }
    private int getPieceValueFromIcon(ImageIcon icon) {
        for (Map.Entry<String, ImageIcon> entry : pieceImages.entrySet()) {
            if (entry.getValue().equals(icon)) {
                switch (entry.getKey()) {
                    case "white_rook": return 1;
                    case "white_knight": return 2;
                    case "white_bishop": return 3;
                    case "white_queen": return 4;
                    case "white_king": return 5;
                    case "white_pawn": return 6;
                    case "black_rook": return -1;
                    case "black_knight": return -2;
                    case "black_bishop": return -3;
                    case "black_queen": return -4;
                    case "black_king": return -5;
                    case "black_pawn": return -6;
                }
            }
        }
        return 0;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChessBoardGUI().setVisible(true));
    }
}
