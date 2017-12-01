package com.example.android.colorupx;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.colorupx.game.model.GameBoard;
import com.example.android.colorupx.game.view.GameView;
import com.example.android.colorupx.game.view.InstructionsGameView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class InstructionsFragment extends Fragment {

    @BindView(R.id.game_view)
    InstructionsGameView mGameView;

    private boolean mFirst = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.instructions_page_1,container,false);
        ButterKnife.bind(this,rootview);

        setGameView();

        return rootview;
    }

    private void setGameView(){
        setGameViewStartingPoint();
        mGameView.addScoreUpdateListener(new GameView.ScoreUpdateListener() {
            @Override
            public void scoreUpdated(Integer score) {
                if(score<4) {
                    if (score == 1)
                        startAnimation(GameBoard.DIRECTION_DOWN);
                    else if (score == 2)
                        startAnimation(GameBoard.DIRECTION_LEFT);
                    else
                        startAnimation(GameBoard.DIRECTION_UP);
                }
                else {
                    resetBoard();
                    try {
//                        setGameViewStartingPoint();
//                        startAnimation(GameBoard.DIRECTION_RIGHT);
                    }catch (IllegalStateException e){}
                }
            }
        });
    }

    private void setGameViewStartingPoint(){
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int maxWidth = metrics.widthPixels/2;
        int maxHeight = metrics.heightPixels/2;
        int[][] board = {{0,0,0,0,0,0},{0,0,0,0,0,0},{0,0,0,0,0,0},
                {3,7,0,7,0,3},{2,8,4,0,0,0},{5,9,0,2,6,0}};
        mGameView.setSwipeDemoBoard(board,maxWidth,maxHeight);
       // startAnimation(GameBoard.DIRECTION_RIGHT);
    }

    private void startAnimation(int direction){
        Observable observable = Observable.just(direction);
        Consumer<Integer> consumer = new Consumer<Integer>() {
            @Override
            public void accept(@NonNull Integer i) throws Exception {
                mGameView.swipeBoard(i);
            }
        };
        observable.subscribeOn(Schedulers.newThread())
                .delay(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread()).subscribe(consumer);
    }

    private void resetBoard(){
        Observable.timer(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long l) throws Exception {
                        setGameViewStartingPoint();
                        startAnimation(GameBoard.DIRECTION_RIGHT);
                    }
                });
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if(menuVisible) {
            // setGameViewStartingPoint();
            if (mFirst) {
                mFirst = false;
                startAnimation(GameBoard.DIRECTION_RIGHT);
            } else {
                setGameViewStartingPoint();
                startAnimation(GameBoard.DIRECTION_RIGHT);
            }
        }

    }


    @Override
    public void onResume() {
        super.onResume();
    }


}
