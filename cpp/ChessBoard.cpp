#include "ChessBoard.h"
#include <vector>
#include <cmath>
#include <array>

const auto BOARD_SIZE = int(8);
const auto PIECE_VALUES = std::array<int, 7>{0, 5, 3, 3, 9, 0, 1};

enum Pieces  {
    EMPTY = 0,
    W_ROOK = 1,
    W_KNIGHT = 2,
    W_BISHOP = 3,
    W_QUEEN = 4,
    W_KING = 5,
    W_PAWN = 6,
    B_ROOK = -1,
    B_KNIGHT = -2,
    B_BISHOP = -3,
    B_QUEEN = -4,
    B_KING = -5,
    B_PAWN = -6
};

auto gameOver = bool(false);
auto isWhiteTurn = bool(true);
auto whiteScore = int(0);
auto blackScore = int(0);
auto board = std::vector<std::vector<jint>>(BOARD_SIZE, std::vector<jint>(BOARD_SIZE, EMPTY));

auto getPieceValue(jint piece) -> int {
    auto index = abs(piece);
    return (piece > 0) ? PIECE_VALUES[index] : -PIECE_VALUES[index];
}

auto isPathClear(int fromX, int fromY, int toX, int toY) -> bool {
    auto stepX = (toX > fromX) - (toX < fromX);
    auto stepY = (toY > fromY) - (toY < fromY);

    auto x = fromX + stepX;
    auto y = fromY + stepY;

    while (x != toX || y != toY) {
        if (board[x][y] != EMPTY) {
            return false;
        }
        x += stepX;
        y += stepY;
    }
    return true;
}

auto isValidRookMove(int fromX, int fromY, int toX, int toY) -> bool {
    // Rook moves in straight lines either horizontally or vertically
    auto sameRow = (fromX == toX);
    auto sameColumn = (fromY == toY);
    return sameRow || sameColumn;
}

auto isValidBishopMove(int fromX, int fromY, int toX, int toY) -> bool {
    // Bishop moves diagonally - with each move to the right/left, it must also move the same distance up/down
    auto dx = abs(toX - fromX);
    auto dy = abs(toY - fromY);
    return dx == dy;
}

auto isValidQueenMove(int fromX, int fromY, int toX, int toY) -> bool {
    // Queen is a combination of Rook and Bishop
    return isValidRookMove(fromX, fromY, toX, toY) || isValidBishopMove(fromX, fromY, toX, toY);
}

auto isValidKingMove(int fromX, int fromY, int toX, int toY) -> bool {
    // King moves only once in any direction
    return std::max(abs(toX - fromX), abs(toY - fromY)) == 1;
}

auto isValidKnightMove(int fromX, int fromY, int toX, int toY) -> bool {
    // Knight moves in "L" shape: 2 squares in one direction and 1 in the other
    auto dx = abs(toX - fromX);
    auto dy = abs(toY - fromY);
    return (dx == 2 && dy == 1) || (dx == 1 && dy == 2);
}

auto isValidPawnMove(int fromX, int fromY, int toX, int toY, bool isWhite) -> bool {
    auto direction = isWhite ? int(-1) : int(1);

    if (fromY == toY && board[toX][toY] == EMPTY) {
        if (toX - fromX == direction)
            return true;

        if ((isWhite && fromX == 6) || (!isWhite && fromX == 1)) {
            if (toX - fromX == 2 * direction && board[fromX + direction][fromY] == EMPTY)
                return true;
        }
    }
    if (abs(toY - fromY) == 1 && toX - fromX == direction && board[toX][toY] != EMPTY && (board[toX][toY] * board[fromX][fromY] < 0)) {
        return true;
    }
    return false;
}

auto isInCheck(bool isWhite) -> bool {
    auto kingX = int(-1);
    auto kingY = int(-1);
    auto kingValue = isWhite ? W_KING : B_KING;

    for (auto x = 0; x < BOARD_SIZE && kingX == -1; ++x) {
        for (auto y = 0; y < BOARD_SIZE; ++y) {
            if (board[x][y] == kingValue) {
                kingX = x;
                kingY = y;
                break;
            }
        }
    }
    if (kingX == -1) return false;

    for (auto x = 0; x < BOARD_SIZE; ++x) {
        for (auto y = 0; y < BOARD_SIZE; ++y) {
            auto piece = board[x][y];
            if ((isWhite && piece < 0) || (!isWhite && piece > 0)) {
                auto attack = bool(false);
                switch (abs(piece)) {
                    case W_ROOK:
                        attack = isValidRookMove(x, y, kingX, kingY) && isPathClear(x, y, kingX, kingY);
                        break;
                    case W_BISHOP:
                        attack = isValidBishopMove(x, y, kingX, kingY) && isPathClear(x, y, kingX, kingY);
                        break;
                    case W_QUEEN:
                        attack = isValidQueenMove(x, y, kingX, kingY) && isPathClear(x, y, kingX, kingY);
                        break;
                    case W_KNIGHT:
                        attack = isValidKnightMove(x, y, kingX, kingY);
                        break;
                    case W_PAWN:
                        attack = (isWhite && (x == kingX - 1) && (abs(y - kingY) == 1)) || (!isWhite && (x == kingX + 1) && (abs(y - kingY) == 1));
                        break;
                    case W_KING:
                        attack = isValidKingMove(x, y, kingX, kingY);
                        break;
                }
                if (attack)
                    return true;
            }
        }
    }
    return false;
}

auto isMoveLegal(int fromX, int fromY, int toX, int toY, bool isWhite) -> bool {
    if (fromX < 0 || fromX >= BOARD_SIZE || fromY < 0 || fromY >= BOARD_SIZE || toX < 0 || toX >= BOARD_SIZE || toY < 0 || toY >= BOARD_SIZE)
        return false;

    if (fromX == toX && fromY == toY)
        return false;

    auto piece = board[fromX][fromY];
    auto target = board[toX][toY];

    if (piece == EMPTY || (isWhite && piece < 0) || (!isWhite && piece > 0))
        return false;

    if ((isWhite && target > 0) || (!isWhite && target < 0))
        return false;

    if ((isWhite && target == B_KING) || (!isWhite && target == W_KING))
        return false;

    auto valid = bool(false);
    switch (abs(piece)) {
        case W_ROOK:
            valid = isValidRookMove(fromX, fromY, toX, toY) && isPathClear(fromX, fromY, toX, toY);
            break;
        case W_KNIGHT:
            valid = isValidKnightMove(fromX, fromY, toX, toY);
            break;
        case W_BISHOP:
            valid = isValidBishopMove(fromX, fromY, toX, toY) && isPathClear(fromX, fromY, toX, toY);
            break;
        case W_QUEEN:
            valid = isValidQueenMove(fromX, fromY, toX, toY) && isPathClear(fromX, fromY, toX, toY);
            break;
        case W_KING:
            valid = isValidKingMove(fromX, fromY, toX, toY);
            break;
        case W_PAWN:
            valid = isValidPawnMove(fromX, fromY, toX, toY, isWhite);
            break;
    }

    if (!valid)
        return false;

    // Simulate move to check if the king would be in check
    auto tempPiece = board[toX][toY];
    board[toX][toY] = board[fromX][fromY];
    board[fromX][fromY] = EMPTY;

    auto kingInCheck = isInCheck(isWhite);

    // Undo move
    board[fromX][fromY] = board[toX][toY];
    board[toX][toY] = tempPiece;

    return !kingInCheck; // Move is legal if king is not in check after the move
}

auto isCheckmate(bool isWhite) -> bool {
    if (!isInCheck(isWhite))
        return false;

    for (auto fromX = 0; fromX < BOARD_SIZE; ++fromX) {
        for (auto fromY = 0; fromY < BOARD_SIZE; ++fromY) {
            auto piece = board[fromX][fromY];
            if ((isWhite && piece > 0) || (!isWhite && piece < 0)) {
                for (auto toX = 0; toX < BOARD_SIZE; ++toX) {
                    for (auto toY = 0; toY < BOARD_SIZE; ++toY) {
                        if (isMoveLegal(fromX, fromY, toX, toY, isWhite))
                            return false;
                    }
                }
            }
        }
    }
    return true;
}


auto isStalemate(bool isWhite) -> bool {
    if (isInCheck(isWhite))
        return false;

    for (auto fromX = 0; fromX < BOARD_SIZE; ++fromX) {
        for (auto fromY = 0; fromY < BOARD_SIZE; ++fromY) {
            auto piece = board[fromX][fromY];
            if ((isWhite && piece > 0) || (!isWhite && piece < 0)) {
                for (auto toX = 0; toX < BOARD_SIZE; ++toX) {
                    for (auto toY = 0; toY < BOARD_SIZE; ++toY) {
                        if (isMoveLegal(fromX, fromY, toX, toY, isWhite))
                            return false;
                    }
                }
            }
        }
    }
    return true;
}


auto promotePawn(int row, int col, jint promotedPiece) -> void {
    board[row][col] = promotedPiece;
    auto value = abs(getPieceValue(promotedPiece));

    if (promotedPiece > 0)
        whiteScore += value;
    else
        blackScore += value;
}

auto performMove(int fromX, int fromY, int toX, int toY) -> bool {
    if (gameOver || !isMoveLegal(fromX, fromY, toX, toY, isWhiteTurn))
        return false;

    auto movingPiece = board[fromX][fromY];
    auto targetPiece = board[toX][toY];

    board[toX][toY] = movingPiece;
    board[fromX][fromY] = EMPTY;

    if (targetPiece != EMPTY) {
        auto capturedValue = getPieceValue(targetPiece);
        if (capturedValue > 0)
            blackScore += capturedValue;
        else
            whiteScore += -capturedValue;
    }
    if ((movingPiece == W_PAWN && toX == 0) || (movingPiece == B_PAWN && toX == 7))
        promotePawn(toX, toY, isWhiteTurn ? W_QUEEN : B_QUEEN);

    if (isCheckmate(!isWhiteTurn) || isStalemate(!isWhiteTurn))
        gameOver = true;
    else
        isWhiteTurn = !isWhiteTurn;

    return true;
}

extern "C" {
JNIEXPORT void JNICALL Java_ChessBoard_initializeBoard(JNIEnv*, jobject) {
    gameOver = false;
    whiteScore = blackScore = 0;
    isWhiteTurn = true;
    board = {
            {B_ROOK, B_KNIGHT, B_BISHOP, B_QUEEN, B_KING, B_BISHOP, B_KNIGHT, B_ROOK},
            {B_PAWN, B_PAWN, B_PAWN, B_PAWN, B_PAWN, B_PAWN, B_PAWN, B_PAWN},
            {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
            {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
            {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
            {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
            {W_PAWN, W_PAWN, W_PAWN, W_PAWN, W_PAWN, W_PAWN, W_PAWN, W_PAWN},
            {W_ROOK, W_KNIGHT, W_BISHOP, W_QUEEN, W_KING, W_BISHOP, W_KNIGHT, W_ROOK}
    };
}

JNIEXPORT jboolean JNICALL Java_ChessBoard_movePiece(JNIEnv*, jobject, jint fromX, jint fromY, jint toX, jint toY) {
    return performMove(fromX, fromY, toX, toY) ? JNI_TRUE : JNI_FALSE; // return true or false whether move was legal
}

JNIEXPORT jobjectArray JNICALL Java_ChessBoard_getBoardState(JNIEnv* env, jobject) {
    jclass intArrayClass = env->FindClass("[I"); // Find java int[] class
    jobjectArray result = env->NewObjectArray(BOARD_SIZE, intArrayClass, NULL); // Create 2D array

    for (int i = 0; i < BOARD_SIZE; ++i) {
        jintArray row = env->NewIntArray(BOARD_SIZE);
        env->SetIntArrayRegion(row, 0, BOARD_SIZE, board[i].data()); // Copy data from C++ to Java
        env->SetObjectArrayElement(result, i, row);
        env->DeleteLocalRef(row);
    }
    return result;
}

JNIEXPORT jboolean JNICALL Java_ChessBoard_isInCheck(JNIEnv*, jobject) {
    return isInCheck(isWhiteTurn) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL Java_ChessBoard_isCheckmate(JNIEnv*, jobject, jboolean checkWhite) {
    bool isWhiteInCheckmate = isCheckmate(checkWhite == JNI_TRUE);
    return isWhiteInCheckmate ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT void JNICALL Java_ChessBoard_setBoardState(JNIEnv* env, jobject, jobjectArray newBoard, jboolean jIsWhiteTurn) {
    for (int i = 0; i < BOARD_SIZE; ++i) {
        jintArray row = (jintArray)env->GetObjectArrayElement(newBoard, i); // Retreive each row from Java
        jint* rowElements = env->GetIntArrayElements(row, 0);
        for (int j = 0; j < BOARD_SIZE; ++j)
            board[i][j] = rowElements[j]; // copy elements to the C++ board
        env->ReleaseIntArrayElements(row, rowElements, 0);
        env->DeleteLocalRef(row);
    }
    isWhiteTurn = (jIsWhiteTurn == JNI_TRUE); //set turn based on Java
    gameOver = false;
}


JNIEXPORT jboolean JNICALL Java_ChessBoard_isWhiteTurn(JNIEnv*, jobject) {
    return isWhiteTurn ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL Java_ChessBoard_isStalemate(JNIEnv*, jobject, jboolean checkWhite) {
    return isStalemate(checkWhite == JNI_TRUE) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jintArray JNICALL Java_ChessBoard_getCurrentScore(JNIEnv* env, jobject) {
    jintArray scoreArray = env->NewIntArray(2);
    jint scores[2] = {whiteScore, blackScore};
    env->SetIntArrayRegion(scoreArray, 0, 2, scores);
    return scoreArray;
}

JNIEXPORT void JNICALL Java_ChessBoard_promotePawn(JNIEnv*, jobject, jint row, jint col, jint promotedPiece) {
    board[row][col] = promotedPiece;
}
}
