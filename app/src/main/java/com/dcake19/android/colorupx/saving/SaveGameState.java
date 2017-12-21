package com.dcake19.android.colorupx.saving;

import java.io.Serializable;


public class SaveGameState implements Serializable {

    private int score,boardStartRow,minBoardRows,maxSquareValue;
    private long delay;
    private int[][] board;
    private SavedAnimatableRectF[] currentBoardPosition;
    private int direction = 0;
    private SavedAnimatableRectF addSquareFromWell;
    private SavedFallingSquare[] savedFallingSquares;
    private int initialInterval;
    private int minInterval;
    private int levelUpScore;

    public SaveGameState(int[][] board,int score, int boardStartRow,
                         int minBoardRows, int maxSquareValue, long delay,
                         SavedAnimatableRectF[] currentBoardPosition,
                         int direction,
                         SavedAnimatableRectF addSquareFromWell,
                         SavedFallingSquare[] savedFallingSquares,
                         int initialInterval,int minInterval,int levelUpScore) {
        this.board = board;
        this.score = score;
        this.boardStartRow = boardStartRow;
        this.minBoardRows = minBoardRows;
        this.maxSquareValue = maxSquareValue;
        this.delay = delay;
        this.currentBoardPosition = currentBoardPosition;
        this.direction = direction;
        this.addSquareFromWell = addSquareFromWell;
        this.savedFallingSquares = savedFallingSquares;
        this.initialInterval = initialInterval;
        this.minInterval = minInterval;
        this.levelUpScore = levelUpScore;
    }

    public int getScore() {
        return score;
    }

    public int getBoardStartRow() {
        return boardStartRow;
    }

    public int getMinBoardRows() {
        return minBoardRows;
    }

    public int getMaxSquareValue() {
        return maxSquareValue;
    }

    public long getDelay() {
        return delay;
    }

    public int[][] getBoard() {
        return board;
    }

    public SavedAnimatableRectF[] getCurrentBoardPosition() {
        return currentBoardPosition;
    }

    public int getDirection() {
        return direction;
    }

    public SavedAnimatableRectF getAddSquareFromWell() {
        return addSquareFromWell;
    }

    public SavedFallingSquare[] getSavedFallingSquare() {
        return savedFallingSquares;
    }

    public int getInitialInterval() {
        return initialInterval;
    }

    public int getLevelUpScore() {
        return levelUpScore;
    }

    public int getMinInterval() {
        return minInterval;
    }
}
