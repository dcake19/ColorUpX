package com.dcake19.android.colorupx;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Map;

public class HighScore {
    //current high scores
    // 1_normal_high_score
    // 1_large_high_score
    // use  Map<String, ?> allEntries = mSharedPreferences.getAll();
    // for(String key:allEntries.keySet())
    // to retrieve all high score keys
    // shared preferences can be deleted individually or all
    private final Integer VERSION = 1;

    private String HIGH_SCORE = "high_score";
    private SharedPreferences mSharedPreferences;
    private int mHighScore = 0;

    public  HighScore(Activity activity,String gameType){
        mSharedPreferences = activity.getApplicationContext().getSharedPreferences(HIGH_SCORE, Context.MODE_PRIVATE);
        HIGH_SCORE = VERSION.toString() + "_" + gameType + "_" + HIGH_SCORE;

        mHighScore =  mSharedPreferences.getInt(HIGH_SCORE,0);
    }

    public int getHighScore(){
        return mHighScore;
    }

    public void setHighScore(int score){
        if(score>mHighScore) {
            mHighScore = score;
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putInt(HIGH_SCORE,score);
            editor.apply();
        }
    }
}
