public class ChessBoard {
    static {
        System.loadLibrary("libChess");
    }


    public native void initializeBoard();
    public native int[][] getBoardState();
    public native boolean movePiece(int fromRow, int fromCol, int toRow, int toCol);
    public native boolean isCheckmate(boolean checkWhite);
    public native boolean isWhiteTurn();
    public native boolean isInCheck();
    public native void setBoardState(int[][] newBoard, boolean isWhiteTurn);
    public native boolean isStalemate(boolean checkWhite);
    public native int[] getCurrentScore();
    public native void promotePawn(int row, int col, int promotedPiece);



}

