package com.dcake19.android.colorupx;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import com.dcake19.android.colorupx.utils.GameType;
import com.dcake19.android.colorupx.utils.TextUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class GameFragment extends Fragment {

    @BindView(R2.id.game_view) GameView mGameView;
    @BindView(R2.id.score) TextView mTextViewScore;
    @BindView(R2.id.high_score) TextView mTextViewHighScore;
    @BindView(R2.id.btn_pause) ImageButton mButtonPause;
    @BindView(R2.id.layout_game_paused) LinearLayout mLayoutGamePaused;
    @BindView(R2.id.layout_game_over) LinearLayout mLayoutGameOver;
    @BindView(R2.id.layout_game_win) LinearLayout mLayoutGameWin;
    @BindView(R2.id.btn_start) Button mButtonStartGame;
    @BindView(R2.id.btn_resume) Button mButtonResume;
    @BindView(R2.id.btn_new_game) Button mButtonNewGame;
    @BindView(R2.id.btn_start_new_game) Button mButtonStartNewGame;
    @BindView(R2.id.game_over_title) TextView mGameOverTitle;
    @BindView(R2.id.game_paused_title) TextView mGamePausedTitle;
    @BindView(R2.id.you_win_title) TextView mTextViewYouWin;
    @BindView(R2.id.btn_play) Button mButtonPlay;
    @BindView(R2.id.next_level_name) TextView mTextViewNextLevel;
    private SaveGame mSaveGame;
    private HighScore mSavedHighScore;
    private String mGameType;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        mGameType = intent.getStringExtra(GameActivity.GAME_TYPE);
        mSaveGame = new SaveGame(getContext(),mGameType);
        mSavedHighScore = new HighScore(getActivity(),mGameType);

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

        mGameView.addNewLevelListener(new GameView.NewLevelListener() {
            @Override
            public void newLevel(int maxValue) {
                mButtonPause.setEnabled(false);
                mTextViewNextLevel.setText(getValueAsString(maxValue));
                mLayoutGameWin.setVisibility(View.VISIBLE);
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

    @Override
    public void onPause() {
        mGameView.pause();
        saveGame();
        mGameView.stop();
        super.onPause();
    }

    public String getValueAsString(int value){
        if(value<20) {
            Integer text = (int) Math.pow(2, value);
            return text.toString();
        }else if(value<30){
            Integer text = (int) Math.pow(2, value-20);
            return text.toString() + "M";
        }else if(value<40){
            Integer text = (int) Math.pow(2, value-30);
            return text.toString() + "B";
        }else if(value<50){
            Integer text = (int) Math.pow(2, value-40);
            return text.toString() + "T";
        }else if(value<60){
            Integer text = (int) Math.pow(2, value-50);
            return text.toString() + "Q";
        }else {
            return "E"+ value;
        }
    }

    private void setTextColors(){
        mButtonStartGame.setAllCaps(false);
        mButtonStartGame.setText(TextUtil.getMultiColorString(getContext(),getString(R.string.start_game)));
        mGamePausedTitle.setText(TextUtil.getMultiColorString(getContext(),getString(R.string.game_paused)));
        mButtonResume.setAllCaps(false);
        mButtonResume.setText(TextUtil.getMultiColorString(getContext(),getString(R.string.resume)));
        mButtonNewGame.setAllCaps(false);
        mButtonNewGame.setText(TextUtil.getMultiColorString(getContext(),getString(R.string.new_game)));
        mGameOverTitle.setText(TextUtil.getMultiColorString(getContext(),getString(R.string.game_over)));
        mButtonStartNewGame.setAllCaps(false);
        mButtonStartNewGame.setText(TextUtil.getMultiColorString(getContext(),getString(R.string.new_game)));
        mTextViewYouWin.setText(TextUtil.getMultiColorString(getContext(),getString(R.string.you_win)));
        mButtonPlay.setAllCaps(false);
        mButtonPlay.setText(TextUtil.getMultiColorString(getContext(),getString(R.string.play)));
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
            mLayoutGameWin.setVisibility(View.INVISIBLE);
            mTextViewScore.setText(String.valueOf(saveGameState.getScore()));
            mGameView.loadGame(saveGameState.getBoard(), saveGameState.getScore(),
                    saveGameState.getBoardStartRow(), saveGameState.getMinBoardRows(),
                    saveGameState.getMaxSquareValue(), saveGameState.getDelay(),
                    saveGameState.getCurrentBoardPosition(), saveGameState.getDirection(),
                    saveGameState.getAddSquareFromWell(),saveGameState.getSavedFallingSquare(),
                    maxWidth,maxHeight,saveGameState.getInitialInterval(),
                    saveGameState.getMinInterval(),saveGameState.getLevelUpScore());
        }else{
            mButtonStartGame.setVisibility(View.VISIBLE);
            mLayoutGamePaused.setVisibility(View.INVISIBLE);
            mLayoutGameWin.setVisibility(View.INVISIBLE);
            if(mGameType.equals(GameType.GAME_SIZE_NORMAL))
                mGameView.setParamters(8, 4, 5, 3, 6, 11,
                        maxWidth, maxHeight,3000,2000,250);
            else
                mGameView.setParamters(10, 6, 7, 3, 8, 11,
                        maxWidth, maxHeight,2400,1400,250);
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

    public void saveGame(){
        mGameView.getFallingSquares();

        SaveGameState saveGameState = new SaveGameState(
                mGameView.getViewBoard(),mGameView.getScore(),
                mGameView.getBoardStartRow(),mGameView.getMinBoardRows(),
                mGameView.getMaxSquareValue(),mGameView.getDelay(),
                mGameView.getCurrentBoardPosition(),
                mGameView.getDirection(),
                mGameView.getMoveFromWellToNewRow(),
                mGameView.getFallingSquares(),
                mGameView.getInitialInterval(),
                mGameView.getMinInterval(),
                mGameView.getLevelUpScore());
        mSaveGame.saveGameToFile(saveGameState);
    }

    @OnClick(R.id.btn_pause)
    public void pause(){
        mGameView.pause();
        if(mGameView.isGamePaused()){
            mButtonPause.setEnabled(false);
            mLayoutGamePaused.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.btn_new_game,R.id.btn_start_new_game})
    public void newGame(){
        mButtonStartGame.setVisibility(View.VISIBLE);
        mLayoutGamePaused.setVisibility(View.INVISIBLE);
        mLayoutGameOver.setVisibility(View.INVISIBLE);
        mTextViewScore.setText("0");
        mGameView.startNewGame();
    }

    @OnClick(R.id.btn_play)
    public void play(){
        mLayoutGameWin.setVisibility(View.INVISIBLE);
        mButtonPause.setEnabled(true);
        mGameView.playNextLevel();
    }

}
