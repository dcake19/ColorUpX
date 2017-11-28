package com.example.android.colorupx;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class GameActivity extends AppCompatActivity {

    private final String FRAGMENT = "Game Fragment";

    private GameFragment mFragment;

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

}
