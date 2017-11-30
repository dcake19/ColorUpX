package com.example.android.colorupx;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InstructionsActivity extends AppCompatActivity {

    @BindView(R.id.pager) ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instructions_activity);

        ButterKnife.bind(this);

        mPager.setAdapter(new InstructionsPagerAdapter(getSupportFragmentManager()));
    }

    class InstructionsPagerAdapter extends FragmentStatePagerAdapter {

        //String[] tabs;

        public InstructionsPagerAdapter(FragmentManager fm) {
            super(fm);
           // tabs = getResources().getStringArray(R.array.my_shows_tabs);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new InstructionsFragment();
//            if(position==0) {
//                fragment = ShowsFragment.getInstance();
//            }
//            else {
//                fragment = CurrentFragment.getInstance(position);
//            }
            return fragment;
        }

//        @Override
//        public CharSequence getPageTitle(int position) {
//            return tabs[position];
//        }

        @Override
        public int getCount() {
            return 3;
        }


    }
}
