package com.dcake19.android.colorupx;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.dcake19.android.colorupx.utils.GameSize;
import com.dcake19.android.colorupx.utils.TextUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainMenuActivity extends AppCompatActivity {

    @BindView(R.id.game_title_number) TextView mTextViewTitleNumber;
    @BindView(R.id.game_title_letters) TextView mTextViewTitleLetters;
    @BindView(R.id.play_game) TextView mTextViewPlayGame;
    @BindView(R.id.btn_play_game_normal) Button mButtonPlayGameNormal;
    @BindView(R.id.btn_play_game_large) Button mButtonPlayGameLarge;
    @BindView(R.id.btn_instructions) Button mButtonInstructions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu_activity);
        ButterKnife.bind(this);
        setTextColors();
    }

    private void setTextColors(){
        mTextViewTitleNumber.setText(TextUtil.getMultiColorString(this,"2048"));
        mTextViewTitleLetters.setText(TextUtil.getMultiColorString(this,getString(R.string.downfall),4));
        mButtonInstructions.setAllCaps(false);
        mButtonInstructions.setText(TextUtil.getMultiColorString(this,getString(R.string.instructions)));
        mTextViewPlayGame.setText(TextUtil.getMultiColorString(this,getString(R.string.select_game)));
        mButtonPlayGameNormal.setAllCaps(false);
        mButtonPlayGameNormal.setText(TextUtil.getMultiColorString(this,getString(R.string.normal)));
        mButtonPlayGameLarge.setAllCaps(false);
        mButtonPlayGameLarge.setText(TextUtil.getMultiColorString(this,getString(R.string.large)));
    }

    @OnClick(R.id.btn_instructions)
    public void instructions(){
        startActivity(new Intent(this,InstructionsActivity.class));
    }

    @OnClick(R.id.btn_play_game_normal)
    public void playNormal(){
        startActivity(GameActivity.getIntent(this, GameSize.NORMAL));
    }

    @OnClick(R.id.btn_play_game_large)
    public void playLarge(){
        startActivity(GameActivity.getIntent(this, GameSize.LARGE));
    }

}
