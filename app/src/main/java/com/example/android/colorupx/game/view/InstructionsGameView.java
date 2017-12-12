package com.example.android.colorupx.game.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.example.android.colorupx.game.controller.GameController;
import com.example.android.colorupx.game.model.GameBoard;


import java.util.ArrayList;
import java.util.List;

public class InstructionsGameView extends GameView {

    public InstructionsGameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public InstructionsGameView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public InstructionsGameView(Context context) {
        super(context);
    }

    public InstructionsGameView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setBoardDemo(int[][] board,int maxWidth,int maxHeight){
        mFlingLocks = 1000;
        mMaxWidth = maxWidth;
        mMaxHeight = maxHeight;
        mRows = 6;
        mColumns = 6;
        mBoardStartRow = 3;
        setDimensions();
        mGamePaused = false;

        mController = new GameController(this, board, 0, mBoardStartRow, 3,9,true);

        invalidate();
    }

    public void setFallingSquareDemo(int maxWidth,int maxHeight){
        mFlingLocks = 1000;
        mMaxWidth = maxWidth;
        mMaxHeight = maxHeight;
        mRows = 6;
        mColumns = 6;
        mBoardStartRow = 5;
        setDimensions();
        mGamePaused = false;

        int[][] board = {{0,0,0,0,0,0},{0,0,0,0,0,0},{0,0,0,0,0,0},
                {0,0,0,0,0,0},{0,0,0,0,0,0},{0,0,0,0,0,0}};
        mController = new GameController(this, board, 0, mBoardStartRow, 0,9,true);

        invalidate();
    }

    public void swipeBoard(int direction){
        try {
            mController.swipe(direction);
        }catch (Exception e){
        }
    }

    public void tapSquareRight(int id){
        tapSquare(GameBoard.DIRECTION_RIGHT,mFallingSquares.get(id));
    }

    public void swipeSquareLeft(int id){
        swipeSquare(GameBoard.DIRECTION_LEFT,mFallingSquares.get(id));
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return true;
    }

    @Override
    public boolean onFling(MotionEvent motionEvent1, MotionEvent motionEvent2, float velocityX, float velocityY) {
        return true;
    }
}
