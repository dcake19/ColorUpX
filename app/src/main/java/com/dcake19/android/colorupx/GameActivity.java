package com.dcake19.android.colorupx;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class GameActivity extends AppCompatActivity {

    private final String FRAGMENT = "Game Fragment";

    private GameFragment mFragment;

    protected static final String GAME_SIZE = "game_size";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);

        FragmentManager fm = getSupportFragmentManager();
        mFragment = (GameFragment) fm.findFragmentByTag(FRAGMENT);

        if(mFragment ==null) {
            mFragment = new GameFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

            fragmentTransaction.add(R.id.game_content, mFragment, FRAGMENT);
            fragmentTransaction.commit();
        }

    }

    public static Intent getIntent(Context context,String gameSize){
        Intent intent = new Intent(context,GameActivity.class);
        intent.putExtra(GAME_SIZE,gameSize);
        return intent;
    }

}