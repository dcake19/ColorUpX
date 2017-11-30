package com.example.android.colorupx;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.colorupx.utils.TextUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainMenuActivity extends AppCompatActivity {

    @BindView(R.id.game_title) TextView mTextViewTitle;
    @BindView(R.id.btn_play_game) Button mButtonPlayGame;
    @BindView(R.id.btn_instructions) Button mButtonInstructions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu_activity);
        ButterKnife.bind(this);
        setTextColors();
    }

    private void setTextColors(){
        mTextViewTitle.setText(TextUtil.getMultiColorString(this,getString(R.string.app_name)));
        mButtonPlayGame.setAllCaps(false);
        mButtonPlayGame.setText(TextUtil.getMultiColorString(this,getString(R.string.play_game)));
        mButtonInstructions.setAllCaps(false);
        mButtonInstructions.setText(TextUtil.getMultiColorString(this,getString(R.string.instructions)));
    }

    @OnClick(R.id.btn_play_game)
    public void play(){
        startActivity(new Intent(this,GameActivity.class));
    }

    @OnClick(R.id.btn_instructions)
    public void instructions(){
        startActivity(new Intent(this,InstructionsActivity.class));
    }


}