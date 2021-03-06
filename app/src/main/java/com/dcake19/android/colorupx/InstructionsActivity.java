package com.dcake19.android.colorupx;

import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dcake19.android.colorupx.utils.TextUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class InstructionsActivity extends AppCompatActivity {

    private final String FRAGMENT = "Instructions Fragment";
    public static final String PAGE = "page";
    private InstructionsFragment mFragment;
    @BindView(R2.id.instructions_title) TextView mTitle;
    @BindView(R2.id.btn_previous) ImageButton mButtonPrevious;
    @BindView(R2.id.btn_next) ImageButton mButtonNext;
    @BindView(R2.id.page_number) TextView mTextPageNumber;

    private int mPageNumber = 1;
    private final int TOTAL_PAGES = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instructions_activity);

        ButterKnife.bind(this);

        setTextColors();

        mTextPageNumber.setText(mPageNumber + "/" + TOTAL_PAGES);

        mButtonPrevious.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        FragmentManager fm = getSupportFragmentManager();
        mFragment = (InstructionsFragment) fm.findFragmentByTag(FRAGMENT);

        if(mFragment != null){
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.remove(mFragment);
            fragmentTransaction.commit();
            mFragment = null;
        }

        mFragment = new InstructionsFragment();
        Bundle args = new Bundle();
        args.putInt(PAGE,mPageNumber);
        mFragment.setArguments(args);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.add(R.id.instructions_content, mFragment, FRAGMENT);
        fragmentTransaction.commit();

    }

    @Override
    protected void onPause() {
        super.onPause();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.remove(mFragment);
        fragmentTransaction.commit();
        mFragment = null;
    }

    private void setTextColors(){
        mTitle.setText(TextUtil.getMultiColorString(this,getString(R.string.instructions)));
    }

    @OnClick(R.id.btn_previous)
    public void previousPage(){
        mPageNumber--;
        mTextPageNumber.setText(mPageNumber + "/" + TOTAL_PAGES);
        if (mPageNumber==1) mButtonPrevious.setVisibility(View.INVISIBLE);
        if(mPageNumber==TOTAL_PAGES-1) mButtonNext.setVisibility(View.VISIBLE);

        changeFragment();
    }

    @OnClick(R.id.btn_next)
    public void nextPage(){
        mPageNumber++;
        mTextPageNumber.setText(mPageNumber + "/" + TOTAL_PAGES);
        if (mPageNumber==2) mButtonPrevious.setVisibility(View.VISIBLE);
        if(mPageNumber==TOTAL_PAGES) mButtonNext.setVisibility(View.INVISIBLE);
        changeFragment();
    }

    private void changeFragment(){
        mFragment = new InstructionsFragment();
        Bundle args = new Bundle();
        args.putInt(PAGE,mPageNumber);
        mFragment.setArguments(args);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.instructions_content, mFragment, FRAGMENT);
        fragmentTransaction.commit();
    }

}
