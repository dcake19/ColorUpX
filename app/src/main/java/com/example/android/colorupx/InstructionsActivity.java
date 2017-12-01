package com.example.android.colorupx;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InstructionsActivity extends AppCompatActivity {

    @BindView(R.id.pager) ViewPager mPager;
    private ArrayList<Fragment> mFragments = new ArrayList<>(3);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instructions_activity);

        ButterKnife.bind(this);

        mFragments.add(new InstructionsFragment());
        mFragments.add(new InstructionsFragment());
        mFragments.add(new InstructionsFragment());

        mPager.setAdapter(new InstructionsPagerAdapter(getSupportFragmentManager()));
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

                Log.i("onPageSelected ", "position: " + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.v("Pager","onPageScrollStateChanged " + state);
            }
        });

    }

    class InstructionsPagerAdapter extends FragmentStatePagerAdapter {

        public InstructionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new InstructionsFragment();
            return fragment;
            //return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
