package com.dcake19.android.colorupx.game.controller;


import android.support.annotation.NonNull;

import com.dcake19.android.colorupx.game.view.AnimatableRectF;
import com.dcake19.android.colorupx.game.view.GameView;
import com.dcake19.android.colorupx.game.model.GameBoard;
import com.dcake19.android.colorupx.game.model.UpdateSquare;

import java.util.ArrayList;
import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class GameController {

    private GameView mGameView;
    private GameBoard mGameBoard;
    private IntervalObservableOnSubscribe mIntervalObservableOnSubscribe;
    //private int mInterval = 1250;
    private int mInitialInterval = 3000;
    private int mMinInterval = 2000;
    private boolean mSwipeBoardOnResume = false;
    private int mSwipeDirectionOnResume = 0;
    private boolean mAddFromWellOnResume = false;
    private AddSquareToWell mAddSquareToWellOnResume;

    public GameController(GameView gameView,int rows,int columns,int boardStartRow,
                          int minBoardRows,int intialSquares,int maxSquareValue,int delay) {
        mGameView = gameView;
        mGameBoard = new GameBoard(rows,columns,boardStartRow,minBoardRows,maxSquareValue);

        mGameBoard.createRandomBoard(intialSquares);
        mGameView.setBoard(mGameBoard.getBoard());
        addFallingSquares(delay);
    }

    // constructor for loading
    public GameController(GameView gameView,int[][] board,int score,int boardStartRow,
                          int minBoardRows,int maxSquareValue,long delay,boolean displayBoard) {
        mGameView = gameView;
        mGameBoard = new GameBoard(board,score,boardStartRow,minBoardRows,maxSquareValue);
        if(displayBoard) mGameView.setBoard(mGameBoard.getBoard());
        addFallingSquares(delay);
    }

    public GameController(GameView gameView,int[][] board,int score,int boardStartRow,
                          int minBoardRows,int maxSquareValue,boolean displayBoard) {
        mGameView = gameView;
        mGameBoard = new GameBoard(board,score,boardStartRow,minBoardRows,maxSquareValue);
        if(displayBoard) mGameView.setBoard(mGameBoard.getBoard());
    }

    public void pause(){
        mIntervalObservableOnSubscribe.pause();
    }

    public void stop(){
        mIntervalObservableOnSubscribe.stop();
    }

    public void resume(){
        mIntervalObservableOnSubscribe.resume();

        if(mSwipeBoardOnResume){
            mSwipeBoardOnResume = false;
            swipe(mSwipeDirectionOnResume);
        }
        if(mAddFromWellOnResume){
            mAddFromWellOnResume = false;
            addSquareFromWell(mAddSquareToWellOnResume.column,
                    mAddSquareToWellOnResume.value,
                    mAddSquareToWellOnResume.rect);
        }

    }

    public void swipeOnResumeAfterLoad(int direction){
        mSwipeBoardOnResume = true;
        mSwipeDirectionOnResume = direction;
    }

    public void addFromWellOnResumeAfterLoad(int column,int value,AnimatableRectF rect){
        mAddSquareToWellOnResume = new AddSquareToWell(column,value,rect);
        mAddFromWellOnResume = true;
    }

    private void addFallingSquares(long remaining){

        mIntervalObservableOnSubscribe = new IntervalObservableOnSubscribe(mInitialInterval,false,remaining);

        Observable<FallingSquare> mObservable = Observable
                .create(mIntervalObservableOnSubscribe)
                .map(new Function<Integer, FallingSquare>() {
                    @Override
                    public FallingSquare apply(Integer id) throws Exception {
                        return new FallingSquare(id);
                    }
                });

        Consumer<FallingSquare> consumer = new Consumer<FallingSquare>() {
            @Override
            public void accept(@NonNull FallingSquare fs) throws Exception {
                mGameView.addFallingSquare(fs.position,fs.value,fs.key);
            }
        };

        mObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);
    }

    public synchronized void swipe(int direction) {
        if (mGameBoard.gameOver()) return;

        ArrayList<UpdateSquare> updates = mGameBoard.getUpdates(direction);

        if(updates.size()>0) {
            mIntervalObservableOnSubscribe.setInterval(Math.max(mMinInterval,mInitialInterval - mGameBoard.getScore()));
            mGameView.update(updates,mGameBoard.getBoard(),mGameBoard.getLastDirection());
            mGameView.setWellRows(mGameBoard.getWellRows());
        }else{
            mGameView.modelFinishedUpdating();
            mGameView.allowFling();
        }
    }

    private synchronized void addSquareFromWell(int column,int value,AnimatableRectF rect){
        if (mGameBoard.gameOver()) return;

        int newRow = mGameBoard.addFallenSquare(column, value);

        if(newRow==-1){
            if(!mGameBoard.gameOver())
                mGameView.increaseBoard(column,rect);
            else {
                mIntervalObservableOnSubscribe.stop();
                mGameView.gameOver();
            }
        }else if(!mGameBoard.gameOver()){
            mGameView.moveFallingSquareToNewRow(newRow,column,rect);
        }
    }

    public synchronized void addSquareFromWell(int column,int value,int fallingSquaresIndex){
        if (mGameBoard.gameOver()) return;

        int newRow = mGameBoard.addFallenSquare(column, value);

        if(newRow==-1){
            if(!mGameBoard.gameOver())
                mGameView.increaseBoard(column,fallingSquaresIndex);
            else {
                mIntervalObservableOnSubscribe.stop();
                mGameView.gameOver();
            }
        }else if(!mGameBoard.gameOver()){
            mGameView.moveFallingSquareToNewRow(newRow,column,fallingSquaresIndex);
        }
    }

    public void stopFallingSquares(){
        mIntervalObservableOnSubscribe.stop();
    }

    public int getScore(){
        return mGameBoard.getScore();
    }

    public int getMinBoardRows(){
        return mGameBoard.getMinBoardRows();
    }

    public int getMaxSquarevalue(){
        return mGameBoard.getMaxSquareValue();
    }

    public long getRemainingTimeForDelay(){
        return mIntervalObservableOnSubscribe.getRemaining();
    }

    private class FallingSquare{
        int key;
        int position;
        int value;

        FallingSquare(int id){
            key = id;
            Random random = new Random();
            position = random.nextInt(mGameBoard.getBoard()[0].length);
            value = random.nextInt(mGameBoard.getMaxSquareValue()) + 1;
        }
    }

    public class AddSquareToWell{
        int column,value;
        AnimatableRectF rect;

        public AddSquareToWell(int column, int value, AnimatableRectF rect) {
            this.column = column;
            this.value = value;
            this.rect = rect;
        }
    }

}

