package com.example.android.colorupx.game.view;

import android.content.Context;
import android.util.AttributeSet;

import com.example.android.colorupx.game.controller.GameController;
import com.example.android.colorupx.game.model.GameBoard;
import com.example.android.colorupx.game.view.GameView;

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

    public void setSwipeDemoBoard(int maxWidth,int maxHeight){
        mFlingLocks = 1000;
        mMaxWidth = maxWidth;
        mMaxHeight = maxHeight;
        mRows = 6;
        mColumns = 6;
        mBoardStartRow = 3;
        setDimensions();
        mGamePaused = false;
        int[][] board = {{0,0,0,0,0,0},{0,0,0,0,0,0},{0,0,0,0,0,0},
                {7,0,0,7,0,3},{0,8,4,0,0,0},{0,9,0,2,6,0}};
        mController = new GameController(this, board, 0, mBoardStartRow, 3,9,500,true);

        invalidate();

    }

    public void swipeBoard(int direction){
        mController.swipe(direction);
       // mController.swipe(GameBoard.DIRECTION_DOWN);
      //  mController.swipe(GameBoard.DIRECTION_LEFT);
//        mFlingLocks = 1000;
//        mGamePaused = false;
//        mController.swipe(direction);
    }

    public void swipeBoardDemo(){

    }

}
