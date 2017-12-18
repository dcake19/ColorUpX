package com.dcake19.android.colorupx;

import android.content.Intent;
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

import com.dcake19.android.colorupx.game.view.GameView;
import com.dcake19.android.colorupx.saving.SaveGame;
import com.dcake19.android.colorupx.saving.SaveGameState;
import com.dcake19.android.colorupx.utils.GameSize;
import com.dcake19.android.colorupx.utils.TextUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class GameFragment extends Fragment {

    @BindView(R.id.game_view) GameView mGameView;
    @BindView(R.id.score) TextView mTextViewScore;
    @BindView(R.id.high_score) TextView mTextViewHighScore;
    @BindView(R.id.btn_pause) ImageButton mButtonPause;
    @BindView(R.id.layout_game_paused) LinearLayout mLayoutGamePaused;
    @BindView(R.id.layout_game_over) LinearLayout mLayoutGameOver;
    @BindView(R.id.btn_start) Button mButtonStartGame;
    @BindView(R.id.btn_resume) Button mButtonResume;
    @BindView(R.id.btn_save_game) Button mButtonSaveGame;
    @BindView(R.id.btn_new_game) Button mButtonNewGame;
    @BindView(R.id.btn_start_new_game) Button mButtonStartNewGame;
    @BindView(R.id.game_over_title) TextView mGameOverTitle;
    @BindView(R.id.game_paused_title) TextView mGamePausedTitle;
    private SaveGame mSaveGame;
    private HighScore mSavedHighScore;
    String mGameSize;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Intent intent = getActivity().getIntent();
        mGameSize = intent.getStringExtra(GameActivity.GAME_SIZE);
        mSaveGame = new SaveGame(getContext(),mGameSize);
        mSavedHighScore = new HighScore(getActivity(),mGameSize);

        View rootview = inflater.inflate(R.layout.game_fragment, container, false);

        ButterKnife.bind(this,rootview);
        mButtonPause.setEnabled(false);

        mTextViewHighScore.setText(String.valueOf(mSavedHighScore.getHighScore()));

        mGameView.addScoreUpdateListener(new GameView.ScoreUpdateListener() {
            @Override
            public void scoreUpdated(Integer score) {
                mTextViewScore.setText(score.toString());
                mSavedHighScore.setHighScore(score);
                mTextViewHighScore.setText(String.valueOf(mSavedHighScore.getHighScore()));
            }
        });

        mGameView.addGameOverListener(new GameView.GameOverListener() {
            @Override
            public void gameOver() {
                mButtonPause.setEnabled(false);
                mButtonStartGame.setVisibility(View.INVISIBLE);
                mLayoutGamePaused.setVisibility(View.INVISIBLE);
                mLayoutGameOver.setVisibility(View.VISIBLE);
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
        mGameOverTitle.setText(TextUtil.getMultiColorString(getContext(),getString(R.string.game_over)));
        mButtonStartNewGame.setAllCaps(false);
        mButtonStartNewGame.setText(TextUtil.getMultiColorString(getContext(),getString(R.string.new_game)));
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
            mTextViewScore.setText(String.valueOf(saveGameState.getScore()));
            mGameView.loadGame(saveGameState.getBoard(), saveGameState.getScore(),
                    saveGameState.getBoardStartRow(), saveGameState.getMinBoardRows(),
                    saveGameState.getMaxSquareValue(), saveGameState.getDelay(),
                    saveGameState.getCurrentBoardPosition(), saveGameState.getDirection(),
                    saveGameState.getAddSquareFromWell(),saveGameState.getSavedFallingSquare(),
                    maxWidth,maxHeight);
        }else{
            mButtonStartGame.setVisibility(View.VISIBLE);
            mLayoutGamePaused.setVisibility(View.INVISIBLE);
            if(mGameSize.equals(GameSize.NORMAL))
                mGameView.setParamters(10, 4, 7, 3, 6, 10,
                        maxWidth, maxHeight);
            else
                mGameView.setParamters(12, 6, 9, 3, 6, 10,
                        maxWidth, maxHeight);

        }

        mLayoutGameOver.setVisibility(View.INVISIBLE);
    }

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

    @OnClick({R.id.btn_new_game,R.id.btn_start_new_game})
    public void newGame(){
        mButtonStartGame.setVisibility(View.VISIBLE);
        mLayoutGamePaused.setVisibility(View.INVISIBLE);
        mLayoutGameOver.setVisibility(View.INVISIBLE);
        mTextViewScore.setText("0");
        mGameView.startNewGame();
    }

}
