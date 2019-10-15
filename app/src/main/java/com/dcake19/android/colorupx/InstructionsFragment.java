package com.dcake19.android.colorupx;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dcake19.android.colorupx.game.model.GameBoard;
import com.dcake19.android.colorupx.game.view.GameView;
import com.dcake19.android.colorupx.game.view.InstructionsGameView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class InstructionsFragment extends Fragment {

    @BindView(R2.id.game_view) InstructionsGameView mGameView;
    @BindView(R2.id.instructions) TextView mInstructions;

    private int mPage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mPage = getArguments().getInt(InstructionsActivity.PAGE,1);

        View rootview = inflater.inflate(R.layout.instructions_fragment,container,false);

        ButterKnife.bind(this,rootview);

        setGameView();
        setInstructions();
        return rootview;
    }

    private void setGameView(){
        setGameViewStartingPoint();
        if(mPage==1) {
            mGameView.addScoreUpdateListener(new GameView.ScoreUpdateListener() {
                @Override
                public void scoreUpdated(Integer score) {
                    if (score < 4) {
                        if (score == 1)
                            startAnimation(GameBoard.DIRECTION_DOWN);
                        else if (score == 2)
                            startAnimation(GameBoard.DIRECTION_LEFT);
                        else
                            startAnimation(GameBoard.DIRECTION_UP);
                    } else {
                        resetBoard();
                    }
                }
            });
        } else if(mPage==2){
            mGameView.addFallingSquareAddedListener(
                    new InstructionsGameView.FallingSquareAddedListener() {
                @Override
                public void squareAdded(int column) {
                    if(column==3) mGameView.addFallingSquare(4,1,2);
                    else if(column==4)startAnimation(GameBoard.DIRECTION_DOWN);
                }
            });
            mGameView.addScoreUpdateListener(new GameView.ScoreUpdateListener() {
                @Override
                public void scoreUpdated(Integer score) {
                    resetBoard();
                }
            });
        }else if(mPage==3){
            mGameView.addFallingSquareAddedListener(
                    new InstructionsGameView.FallingSquareAddedListener() {
                        @Override
                        public void squareAdded(int column) {
                            if(column==4) {
                                mGameView.addFallingSquare(5, 1, 2);
                                Observable.timer(1, TimeUnit.SECONDS)
                                        .subscribeOn(Schedulers.newThread())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Consumer<Long>() {
                                            @Override
                                            public void accept(Long l) throws Exception {
                                                try {
                                                    mGameView.swipeSquareLeft(2);
                                                }catch (IllegalStateException e){}
                                            }
                                        });
                            }
                            else if(column==0)resetBoard();
                        }
                    });
        }
    }

    private void setGameViewStartingPoint(){
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int maxWidth = metrics.widthPixels/2;
        int maxHeight = metrics.heightPixels/2;
        if(mPage<3) {
            int[][] board = {{0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0},
                    {3, 9, 0, 9, 0, 3}, {2, 10, 4, 0, 0, 0}, {5, 11, 0, 2, 6, 0}};
            mGameView.setBoardDemo(board, maxWidth, maxHeight);
        }
        else{
            mGameView.setFallingSquareDemo(maxWidth,maxHeight);
        }
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
                        try {
                            setGameViewStartingPoint();
                            startInstructions();
                        }catch (IllegalStateException e){}
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        startInstructions();
    }

    private void startInstructions(){
        if(mPage==1) startAnimation(GameBoard.DIRECTION_RIGHT);
        else if(mPage==2) mGameView.addFallingSquare(3,3,1);
        else if(mPage==3) {
            mGameView.addFallingSquare(3, 3, 1);
            Observable.timer(1, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long l) throws Exception {
                            try {
                                mGameView.tapSquareRight(1);
                            }catch (IllegalStateException e){}
                        }
                    });
        }
    }


    private void setInstructions(){
        if(mPage==1){
            mInstructions.setText(getString(R.string.instructions_page_1));
        }else if(mPage==2){
            mInstructions.setText(getString(R.string.instructions_page_2));
        }else {
            mInstructions.setText(getString(R.string.instructions_page_3));
        }
    }

}
