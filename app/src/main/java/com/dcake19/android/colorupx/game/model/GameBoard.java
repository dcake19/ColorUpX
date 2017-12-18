package com.dcake19.android.colorupx.game.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GameBoard {
    public static int DIRECTION_LEFT = 1;
    public static int DIRECTION_RIGHT = 2;
    public static int DIRECTION_UP = 3;
    public static int DIRECTION_DOWN = 4;

    private int[][] board;
    private int mWellRows = 0;
    private int mMaxWellRows = 0;
    private int mLastDirection = 0;
    private int mScore = 0;
    private boolean mGameOver = false;
    private int mMaxSquareValue = 10;


    public GameBoard(int rows,int columns,int wellRows,int minBoardRows,int maxSquareValue) {
        board = new int[rows][columns];
        mWellRows = wellRows;
        mMaxWellRows = board.length-minBoardRows;
        mMaxSquareValue = maxSquareValue;
    }

    public void createRandomBoard(int startingSquares){
        ArrayList<Coordinates> emptySquares = new ArrayList();
        for (int i=mWellRows;i<board.length;i++){
            for (int j=0;j<board[i].length;j++){
                emptySquares.add(new Coordinates(i,j));
            }
        }
        Collections.shuffle(emptySquares);

        Random random = new Random();

        for(int k=0;k<startingSquares;k++){
            board[emptySquares.get(k).i][emptySquares.get(k).j] = random.nextInt(mMaxSquareValue) + 1;
        }
    }

    public GameBoard(int[][] board,int score,int wellRows,int minBoardRows,int maxSquareValue) {
        this.board = board;
        mScore = score;
        mWellRows = wellRows;
        mMaxWellRows = board.length-minBoardRows;
        mMaxSquareValue = maxSquareValue;
    }


    public boolean gameOver(){
        return mGameOver;
    }


    public int[][] getBoard() {
        return board;
    }

    public int getWellRows(){
        if (mGameOver)
            return 0;

        boolean complete = false;
        while (!complete) {
            boolean zeroRow = true;
            for (int j = 0; j < board[mWellRows].length; j++) {
                if (board[mWellRows][j] != 0) {
                    zeroRow = false;
                    break;
                }
            }
            if (mWellRows < mMaxWellRows && zeroRow) mWellRows++;
            else complete = true;
        }
        return mWellRows;
    }

    public ArrayList<UpdateSquare> getUpdates(int direction){

        ArrayList<UpdateSquare> allUpdates = new ArrayList<>();

        if (direction == DIRECTION_LEFT) {
            for (int i = mWellRows; i < board.length; i++) {
                ArrayList<Coordinates> nonZeroCoords = new ArrayList<>();
                for (int j = 0; j < board[i].length; j++) {
                    if (board[i][j] > 0)
                        nonZeroCoords.add(new Coordinates(i, j));
                }
                addUpdatesFromCoords(allUpdates,nonZeroCoords,direction);
            }
        }
        else if (direction == DIRECTION_RIGHT) {
            for (int i = mWellRows; i < board.length; i++) {
                ArrayList<Coordinates> nonZeroCoords = new ArrayList<>();
                for (int j = board[i].length-1; j>=0; j--) {
                    if (board[i][j] > 0)
                        nonZeroCoords.add(new Coordinates(i, j));
                }
                addUpdatesFromCoords(allUpdates,nonZeroCoords,direction);
            }
        }
        else if (direction == DIRECTION_UP) {
            for (int j = 0; j < board[0].length; j++) {
                ArrayList<Coordinates> nonZeroCoords = new ArrayList<>();
                for (int i = mWellRows; i < board.length; i++) {
                    if (board[i][j] > 0)
                        nonZeroCoords.add(new Coordinates(i, j));
                }
                addUpdatesFromCoords(allUpdates,nonZeroCoords, direction);
            }
        }
        else  {
            for (int j = 0; j < board[0].length; j++) {
                ArrayList<Coordinates> nonZeroCoords = new ArrayList<>();
                for (int i = board.length-1; i>=mWellRows; i--) {
                    if (board[i][j] > 0)
                        nonZeroCoords.add(new Coordinates(i, j));
                }
                addUpdatesFromCoords(allUpdates,nonZeroCoords, direction);
            }
        }

        if(allUpdates.size()>0){
            updateBoard(allUpdates,direction);
        }

        return allUpdates;
    }

    //return -1 if the board is increases, the new row otherwise
    //return -2 if nothing is added
    public int addFallenSquare(int column,int value){
        if(mWellRows>=0 && board[mWellRows][column]!=0){
            mWellRows--;
            if(mWellRows>=0)
                board[mWellRows][column] = value;
            else
                mGameOver = true;
            return -1;
        }
        else if(mWellRows>=0){
            for(int i=mWellRows;i<board.length-1;i++){
                if( board[i+1][column]!=0) {
                    board[i][column] = value;
                    return i;
                }
            }
            board[board.length-1][column] = value;
            return board.length-1;
        }
        return -2;

    }

    public int getLastDirection() {
        return mLastDirection;
    }

    public Integer getScore() {
        return mScore;
    }

    public int getMinBoardRows() {
        return board.length - mMaxWellRows;
    }

    public int getMaxSquareValue() {
        return mMaxSquareValue;
    }

    private void updateBoard(ArrayList<UpdateSquare> allUpdates, int direction){

        mLastDirection = direction;

        int[][] originalBoard = board;
        // set true if the square has been changed and should not be set to zero
        boolean[][] squareChanged = new boolean[board.length][board[0].length];

        for(int i=0;i<allUpdates.size();i++){
            int squareValue = originalBoard[allUpdates.get(i).getStartRow()][allUpdates.get(i).getStartColumn()];
            if(allUpdates.get(i).getIncrease()){
                if(squareValue < mMaxSquareValue) squareValue++;
                else squareValue = 0;
                mScore++;
            }

            if(!squareChanged[allUpdates.get(i).getStartRow()][allUpdates.get(i).getStartColumn()]) board[allUpdates.get(i).getStartRow()][allUpdates.get(i).getStartColumn()] = 0;

            if(direction == DIRECTION_LEFT){
                board[allUpdates.get(i).getStartRow()][allUpdates.get(i).getStartColumn()-allUpdates.get(i).getDistance()] = squareValue;
                squareChanged[allUpdates.get(i).getStartRow()][allUpdates.get(i).getStartColumn()-allUpdates.get(i).getDistance()] = true;
            }
            else if(direction == DIRECTION_RIGHT){
                board[allUpdates.get(i).getStartRow()][allUpdates.get(i).getStartColumn()+allUpdates.get(i).getDistance()] = squareValue;
                squareChanged[allUpdates.get(i).getStartRow()][allUpdates.get(i).getStartColumn()+allUpdates.get(i).getDistance()] = true;
            }
            else if(direction == DIRECTION_UP){
                board[allUpdates.get(i).getStartRow()-allUpdates.get(i).getDistance()][allUpdates.get(i).getStartColumn()] = squareValue;
                squareChanged[allUpdates.get(i).getStartRow()-allUpdates.get(i).getDistance()][allUpdates.get(i).getStartColumn()] = true;
            }
            else{
                board[allUpdates.get(i).getStartRow()+allUpdates.get(i).getDistance()][allUpdates.get(i).getStartColumn()] = squareValue;
                squareChanged[allUpdates.get(i).getStartRow()+allUpdates.get(i).getDistance()][allUpdates.get(i).getStartColumn()] = true;
            }
        }
    }

    private void addUpdatesFromCoords(ArrayList<UpdateSquare> allUpdates, ArrayList<Coordinates> nonZeroCoords, int direction){
        int mergeCount = 0;
        boolean mergeNextEqual = true;
        for (int k = 0; k < nonZeroCoords.size(); k++) {
            boolean increaseNumber = false;
            if (k > 0) {
                if (mergeNextEqual) {
                    increaseNumber = board[nonZeroCoords.get(k - 1).i][nonZeroCoords.get(k - 1).j] == board[nonZeroCoords.get(k).i][nonZeroCoords.get(k).j];
                    if (increaseNumber) {
                        mergeNextEqual = false;
                        mergeCount++;
                    }
                } else if (!mergeNextEqual) {
                    mergeNextEqual = true;
                }
            }

            int distance;
            if (direction == DIRECTION_LEFT)
                distance = nonZeroCoords.get(k).j - k + mergeCount;
            else if (direction == DIRECTION_RIGHT)
                distance = board[0].length - 1 - nonZeroCoords.get(k).j - k + mergeCount;
            else if (direction == DIRECTION_UP)
                distance = nonZeroCoords.get(k).i - k + mergeCount - mWellRows;
            else
                distance = board.length - 1 - nonZeroCoords.get(k).i - k + mergeCount;

            if (distance > 0)
                allUpdates.add(new UpdateSquare(nonZeroCoords.get(k),
                        distance,
                        direction,
                        increaseNumber));
        }
    }

}