package com.example.android.colorupx;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.colorupx.game.view.GameView;
import com.example.android.colorupx.saving.SaveGame;
import com.example.android.colorupx.saving.SaveGameState;
import com.example.android.colorupx.saving.SavedAnimatableRectF;
import com.example.android.colorupx.utils.TextUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class GameFragment extends Fragment {

    @BindView(R.id.game_view) GameView mGameView;
    @BindView(R.id.score) TextView mTextViewScore;
    @BindView(R.id.btn_pause) ImageButton mButtonPause;
    @BindView(R.id.layout_game_paused) LinearLayout mLayoutGamePaused;
    @BindView(R.id.btn_start) Button mButtonStartGame;
    @BindView(R.id.btn_resume) Button mButtonResume;
    @BindView(R.id.btn_save_game) Button mButtonSaveGame;
    @BindView(R.id.btn_new_game) Button mButtonNewGame;
    @BindView(R.id.game_paused_title) TextView mGamePausedTitle;
    private SaveGame mSaveGame;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mSaveGame = new SaveGame(getContext());

        View rootview = inflater.inflate(R.layout.game_fragment, container, false);

        ButterKnife.bind(this,rootview);
        mButtonPause.setEnabled(false);

        //mGameView.setParamters(12,6,9,3,6,9);

//        int[][] board = {{0,0,0,0,0,0},{0,0,0,0,0,0},{0,0,0,0,0,0},{0,0,0,0,0,0},
//                {0,0,0,0,0,0},{0,0,0,0,0,0},{0,0,0,0,0,0},{0,0,0,0,0,0},
//                {6,0,4,0,7,0},{3,2,0,0,0,9},{1,1,2,0,0,9},{0,0,0,0,0,0}};
//        float distanceMoved = (float) 97.0;
//        SavedAnimatableRectF[] savedRects = new SavedAnimatableRectF[10];
//        savedRects[0] = new SavedAnimatableRectF(8,0,getPxLocation(0),getPxLocation(8),6);
//        savedRects[1] = new SavedAnimatableRectF(8,2,getPxLocation(2)-distanceMoved,getPxLocation(8),4);
//        savedRects[2] = new SavedAnimatableRectF(8,4,getPxLocation(4)-distanceMoved,getPxLocation(8),7);
//        savedRects[3] = new SavedAnimatableRectF(9,0,getPxLocation(0),getPxLocation(9),3);
//        savedRects[4] = new SavedAnimatableRectF(9,1,getPxLocation(1),getPxLocation(9),2);
//        savedRects[5] = new SavedAnimatableRectF(9,5,getPxLocation(5)-distanceMoved,getPxLocation(9),9);
//        savedRects[6] = new SavedAnimatableRectF(10,0,getPxLocation(0),getPxLocation(10),1);
//        savedRects[7] = new SavedAnimatableRectF(10,1,getPxLocation(1)-distanceMoved,getPxLocation(10),1);
//        savedRects[8] = new SavedAnimatableRectF(10,2,getPxLocation(2)-distanceMoved,getPxLocation(10),2);
//        savedRects[9] = new SavedAnimatableRectF(10,5,getPxLocation(5)-distanceMoved,getPxLocation(10),9);
        // mGameView.loadGame(board,100,8,3,9,0);
       //mGameView.loadGame(board,100,8,3,9,0,savedRects,GameBoard.DIRECTION_LEFT);
//        for (int i=0;i<12;i++) {
//            Log.v("i = "+i,String.valueOf(mGameView.getPxLocation(i)));
//        }

       // mGameView.loadGame(board,100,8,3,9,0,new SavedAnimatableRectF(7,1,getPxLocation(1),getPxLocation(7)+distanceMoved,8));


        mGameView.addScoreUpdateListener(new GameView.ScoreUpdateListener() {
            @Override
            public void scoreUpdated(Integer score) {
                mTextViewScore.setText(score.toString());
            }
        });

        return rootview;
    }

    @Override
    public void onStart() {
        super.onStart();
        setTextColors();
        loadGame();
    }

    private void setTextColors(){
        mButtonStartGame.setAllCaps(false);
        mButtonStartGame.setText(TextUtil.getMultiColorString(getContext(),getString(R.string.start_game)));
        mGamePausedTitle.setText(TextUtil.getMultiColorString(getContext(),getString(R.string.game_paused)));
        mButtonResume.setAllCaps(false);
        mButtonResume.setText(TextUtil.getMultiColorString(getContext(),getString(R.string.resume)));
        mButtonSaveGame.setAllCaps(false);
        mButtonSaveGame.setText(TextUtil.getMultiColorString(getContext(),getString(R.string.save_game)));
        mButtonNewGame.setAllCaps(false);
        mButtonNewGame.setText(TextUtil.getMultiColorString(getContext(),getString(R.string.new_game)));
    }

    private void loadGame(){
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int maxWidth = metrics.widthPixels;
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        int navigation = 0;
        if (resourceId > 0){
           navigation = getResources().getDimensionPixelSize(resourceId);
        }
        int maxHeight = metrics.heightPixels
                - (int) (2*getResources().getDimension(R.dimen.standard_padding))
                - (int) getResources().getDimension(R.dimen.pause_btn_size)
                - navigation;

        SaveGameState saveGameState = mSaveGame.loadGameState();
        if(saveGameState!=null){
            mButtonStartGame.setVisibility(View.INVISIBLE);
            mLayoutGamePaused.setVisibility(View.VISIBLE);
            mGameView.loadGame(saveGameState.getBoard(), saveGameState.getScore(),
                    saveGameState.getBoardStartRow(), saveGameState.getMinBoardRows(),
                    saveGameState.getMaxSquareValue(), saveGameState.getDelay(),
                    saveGameState.getCurrentBoardPosition(), saveGameState.getDirection(),
                    saveGameState.getAddSquareFromWell(),saveGameState.getSavedFallingSquare(),
                    maxWidth,maxHeight);
        }else{
            mButtonStartGame.setVisibility(View.VISIBLE);
            mLayoutGamePaused.setVisibility(View.INVISIBLE);
            mGameView.setParamters(12,6,9,3,6,9,
                    maxWidth,maxHeight);
        }
    }



//    float getPxLocation(int index){
//
//        switch (index){
//            case 0: return (float) 22.0;
//            case 1: return (float) 147.0;
//            case 2: return (float) 272.0;
//            case 3: return (float) 397.0;
//            case 4: return (float) 522.0;
//            case 5: return (float) 647.0;
//            case 6: return (float) 772.0;
//            case 7: return (float) 897.0;
//            case 8: return (float) 1022.0;
//            case 9: return (float) 1147.0;
//            case 10: return (float) 1272.0;
//            case 11: return (float) 1397.0;
//            default: return (float) 0.0;
//        }
//    }

    @OnClick({R.id.btn_start,R.id.btn_resume})
    public void resume(View view){
        if(view.getId()==R.id.btn_start)
            mButtonStartGame.setVisibility(View.INVISIBLE);

        if(mGameView.isGamePaused()){
            mButtonPause.setEnabled(true);
            mLayoutGamePaused.setVisibility(View.INVISIBLE);
            mGameView.resume();
        }
    }

    @OnClick(R.id.btn_pause)
    public void pause(){
        mGameView.pause();

        if(mGameView.isGamePaused()){
            mButtonPause.setEnabled(false);
            mLayoutGamePaused.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.btn_save_game)
    public void saveGame(){
        mGameView.getFallingSquares();

        SaveGameState saveGameState = new SaveGameState(
                mGameView.getViewBoard(),mGameView.getScore(),
                mGameView.getBoardStartRow(),mGameView.getMinBoardRows(),
                mGameView.getMaxSquareValue(),mGameView.getDelay(),
                mGameView.getCurrentBoardPosition(),
                mGameView.getDirection(),
                mGameView.getMoveFromWellToNewRow(),
                mGameView.getFallingSquares());
        mSaveGame.saveGameToFile(saveGameState);
    }

    @OnClick(R.id.btn_new_game)
    public void newGame(){
        mButtonStartGame.setVisibility(View.VISIBLE);
        mLayoutGamePaused.setVisibility(View.INVISIBLE);
        mGameView.startNewGame();
    }

}
