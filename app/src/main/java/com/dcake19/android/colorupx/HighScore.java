package com.dcake19.android.colorupx;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class HighScore {

    private String HIGH_SCORE = "high_score";
    private SharedPreferences mSharedPreferences;
    private int mHighScore = 0;

    public HighScore(SharedPreferences sharedPreferences) {
        mSharedPreferences = sharedPreferences;
        mHighScore =  mSharedPreferences.getInt(HIGH_SCORE,0);
    }

    public  HighScore(Activity activity,String gameSize){
        HIGH_SCORE = gameSize + "_" + HIGH_SCORE;
        mSharedPreferences = activity.getApplicationContext().getSharedPreferences(HIGH_SCORE, Context.MODE_PRIVATE);
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
            editor.commit();
        }
    }
}
