package com.example.android.colorupx;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.colorupx.game.model.GameBoard;
import com.example.android.colorupx.game.view.GameView;
import com.example.android.colorupx.game.view.InstructionsGameView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InstructionsFragment extends Fragment {

    @BindView(R.id.game_view)
    InstructionsGameView mGameView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.instructions_page_1,container,false);
        ButterKnife.bind(this,rootview);

        setGameView();

        return rootview;
    }

    private void setGameView(){
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int maxWidth = metrics.widthPixels/2;
        int maxHeight = metrics.heightPixels/2;
//        mGameView.setParamters(6,6,3,3,6,9,
//                maxWidth,maxHeight);
        mGameView.setSwipeDemoBoard(maxWidth,maxHeight);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @OnClick(R.id.instructions)
    public void swipe(){
        mGameView.swipeBoard(GameBoard.DIRECTION_RIGHT);
    }

}
