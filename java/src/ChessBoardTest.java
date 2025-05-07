import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

//    {-1, -2, -3, -4, -5, -3, -2, -1},
//    {-6, -6, -6, -6, -6, -6, -6, -6},
//    { 0,  0,  0,  0,  0,  0,  0,  0},
//    { 0,  0,  0,  0,  0,  0,  0,  0},
//    { 0,  0,  0,  0,  0,  0,  0,  0},
//    { 0,  0,  0,  0,  0,  0,  0,  0},
//    { 6,  6,  6,  6,  6,  6,  6,  6},
//    { 1,  2,  3,  4,  5,  3,  2,  1}


class ChessBoardTest {
    private ChessBoard chessBoard;

    @BeforeEach
    public void setUp() {
        chessBoard = new ChessBoard();
        chessBoard.initializeBoard();
    }

    @Test
    public void testBoardInitialization() {
        int[][] boardState = chessBoard.getBoardState();

        // Verify board size
        assertEquals(8, boardState.length, "Board should have 8 rows");
        for (int[] row : boardState) {
            assertEquals(8, row.length, "Each row should have 8 columns");
        }

        // Verify initial positions of white pieces
        assertEquals(1, boardState[7][0], "White rook should be at (7,0)");
        assertEquals(2, boardState[7][1], "White knight should be at (7,1)");
        assertEquals(3, boardState[7][2], "White bishop should be at (7,2)");
        assertEquals(4, boardState[7][3], "White queen should be at (7,3)");
        assertEquals(5, boardState[7][4], "White king should be at (7,4)");
        assertEquals(3, boardState[7][5], "White bishop should be at (7,5)");
        assertEquals(2, boardState[7][6], "White knight should be at (7,6)");
        assertEquals(1, boardState[7][7], "White rook should be at (7,7)");

        // Verify initial positions of white pawns
        for (int col = 0; col < 8; col++) {
            assertEquals(6, boardState[6][col], "White pawn should be at (6," + col + ")");
        }

        // Verify initial positions of black pieces
        assertEquals(-1, boardState[0][0], "Black rook should be at (0,0)");
        assertEquals(-2, boardState[0][1], "Black knight should be at (0,1)");
        assertEquals(-3, boardState[0][2], "Black bishop should be at (0,2)");
        assertEquals(-4, boardState[0][3], "Black queen should be at (0,3)");
        assertEquals(-5, boardState[0][4], "Black king should be at (0,4)");
        assertEquals(-3, boardState[0][5], "Black bishop should be at (0,5)");
        assertEquals(-2, boardState[0][6], "Black knight should be at (0,6)");
        assertEquals(-1, boardState[0][7], "Black rook should be at (0,7)");

        // Verify initial positions of black pawns
        for (int col = 0; col < 8; col++) {
            assertEquals(-6, boardState[1][col], "Black pawn should be at (1," + col + ")");
        }

        // Verify if middle squares are empty
        for (int row = 2; row <= 5; row++) {
            for (int col = 0; col < 8; col++) {
                assertEquals(0, boardState[row][col], "Square (" + row + "," + col + ") should be empty");
            }
        }
    }

    @Test
    public void testValidMovePawnForward() {
        // Move white pawn from (6,0) to (5,0)
        boolean moveResult = chessBoard.movePiece(6, 0, 5, 0);
        assertTrue(moveResult, "White pawn should be able to move forward one square");

        int[][] boardState = chessBoard.getBoardState();
        assertEquals(6, boardState[5][0], "White pawn should be at (5,0)");
        assertEquals(0, boardState[6][0], "Square (6,0) should be empty");
    }

    @Test
    public void testInvalidMovePawnBackward() {
        // Try to move white pawn backward from (6,0) to (7,0)
        boolean moveResult = chessBoard.movePiece(6, 0, 7, 0);
        assertFalse(moveResult, "White pawn should not be able to move backward");

        int[][] boardState = chessBoard.getBoardState();
        assertEquals(6, boardState[6][0], "White pawn should still be at (6,0)");
        assertEquals(1, boardState[7][0], "Square (7,0) should still have the white rook");
    }


    @Test
    public void testValidCapture() {
        // Move white pawn from (6,4) to (4,4)
        chessBoard.movePiece(6, 4, 4, 4);
        // Move black pawn from (1,3) to (3,3)
        chessBoard.movePiece(1, 3, 3, 3);
        // Move white pawn from (4,4) to (3,3) to capture black pawn
        boolean moveResult = chessBoard.movePiece(4, 4, 3, 3);
        assertTrue(moveResult, "White pawn should be able to capture black pawn diagonally");

        int[][] boardState = chessBoard.getBoardState();
        assertEquals(6, boardState[3][3], "White pawn should be at (3,3)");
        assertEquals(0, boardState[4][4], "Square (4,4) should be empty");
        assertEquals(0, boardState[1][3], "Black pawn should be captured from (1,3)");
    }

    @Test
    public void testInvalidMoveKnight() {
        // Move white knight in invalid way from (7,1) to (5,3)
        boolean moveResult = chessBoard.movePiece(7, 1, 5, 3);
        assertFalse(moveResult, "Knight should not be able to move to (5,3)");

        int[][] boardState = chessBoard.getBoardState();
        assertEquals(2, boardState[7][1], "White knight should still be at (7,1)");
        assertEquals(0, boardState[5][3], "Square (5,3) should be empty");
    }

    @Test
    public void testValidMoveKnight() {
        // Move white knight from (7,1) to (5,2)
        boolean moveResult = chessBoard.movePiece(7, 1, 5, 2);
        assertTrue(moveResult, "Knight should be able to move to (5,2)");

        int[][] boardState = chessBoard.getBoardState();
        assertEquals(2, boardState[5][2], "White knight should be at (5,2)");
        assertEquals(0, boardState[7][1], "Square (7,1) should be empty");
    }

    @Test
    public void testCaptureOpponentPiece() {
        // Move white pawn from (6,0) to (4,0)
        chessBoard.movePiece(6, 0, 4, 0);
        // Move black pawn from (1,1) to (3,1)
        chessBoard.movePiece(1, 1, 3, 1);
        // Move white pawn from (4,0) to (3,1) to capture black pawn
        boolean moveResult = chessBoard.movePiece(4, 0, 3, 1);
        assertTrue(moveResult, "White pawn should be able to capture black pawn at (3,1)");

        int[][] boardState = chessBoard.getBoardState();
        assertEquals(6, boardState[3][1], "White pawn should be at (3,1)");
        assertEquals(0, boardState[4][0], "Square (4,0) should be empty");
        assertEquals(0, boardState[1][1], "Black pawn should be captured from (1,1)");
    }

    @Test
    public void testInvalidMoveThroughPiece() {
        // Try to move white bishop from (7,2) to (5,0) which is blocked by pawn at (6,1)
        boolean moveResult = chessBoard.movePiece(7, 2, 5, 0);
        assertFalse(moveResult, "Bishop should not be able to move through another piece");

        int[][] boardState = chessBoard.getBoardState();
        assertEquals(3, boardState[7][2], "White bishop should still be at (7,2)");
        assertEquals(0, boardState[5][0], "Square (5,0) should be empty");
    }

    @Test
    public void testPawnPromotion() {
        // Manual set-up to promote pawn
        chessBoard.setBoardState(new int[][] {
                {-1, -2, -3, -4, -5, -3, -2, -1},
                {-6, -6, -6, -6, -6, -6, -6, -6},
                { 0,  0,  0,  0,  0,  0,  0,  0},
                { 0,  0,  0,  0,  0,  0,  0,  0},
                { 6,  0,  0,  0,  0,  0,  0,  0}, // Pawn from this row will be promoted
                { 0,  0,  0,  0,  0,  0,  0,  0},
                { 0,  6,  6,  6,  6,  6,  6,  6},
                { 1,  2,  3,  4,  5,  3,  2,  1}
        }, true);

        // Series of moves to promote pawn at (4,0) to (0,0)
        chessBoard.movePiece(4, 0, 3, 0);
        chessBoard.movePiece(1, 0, 2, 0);
        chessBoard.movePiece(3, 0, 2, 0);
        chessBoard.movePiece(1, 1, 2, 1);
        chessBoard.movePiece(2, 0, 1, 0);
        chessBoard.movePiece(1, 2, 2, 2);
        chessBoard.movePiece(1, 0, 0, 0);

        chessBoard.promotePawn(0, 0, 4);

        int[][] boardState = chessBoard.getBoardState();
        // The pawn should now be promoted to queen (value 4)
        assertEquals(4, boardState[0][0], "Pawn should be promoted to a queen at (0,0)");
    }


    @Test
    public void testCheckmateDetection() {

        chessBoard.initializeBoard();

        // White pawn from (6,5) to (5,5)
        chessBoard.movePiece(6, 5, 5, 5);
        // Black pawn from (1,4) to (3,4)
        chessBoard.movePiece(1, 4, 3, 4);
        // White pawn from (6,6) to (4,6)
        chessBoard.movePiece(6, 6, 4, 6);
        // Black queen from (0,3) to (4,7)
        boolean moveResult = chessBoard.movePiece(0, 3, 4, 7);
        assertTrue(moveResult, "Black queen should move to (4,7)");

        // White is in checkmate
        boolean isCheckmate = chessBoard.isCheckmate(true);
        assertTrue(isCheckmate, "White should be in checkmate");
    }

    @Test
    public void testStalemateDetection() {
        int[][] stalemateBoard = {
                { 0,  0,  0,  0,  0,  0,  0, -5}, // Black King
                { 0,  0,  0,  0,  0,  4,  0,  0}, // White Queen
                { 0,  0,  0,  0,  0,  0,  4,  0}, // White Queen
                { 0,  0,  0,  0,  0,  0,  0,  0},
                { 0,  0,  0,  0,  0,  0,  0,  0},
                { 0,  0,  0,  0,  0,  0,  0,  0},
                { 0,  0,  0,  0,  0,  0,  0,  0},
                { 0,  0,  0,  0,  0,  0,  5,  0}  // White King
        };
        chessBoard.setBoardState(stalemateBoard, false); // Black turn

        boolean isStalemate = chessBoard.isStalemate(false);
        assertTrue(isStalemate, "Black should be in stalemate");
    }
}
