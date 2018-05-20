package com.dcake19.android.colorupx;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.dcake19.android.colorupx.utils.GameType;
import com.dcake19.android.colorupx.utils.TextUtil;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainMenuActivity extends AppCompatActivity {

    @BindView(R.id.game_title_number) TextView mTextViewTitleNumber;
    @BindView(R.id.game_title_letters) TextView mTextViewTitleLetters;
    @BindView(R.id.play_game) TextView mTextViewPlayGame;
    @BindView(R.id.btn_play_game_normal) Button mButtonPlayGameNormal;
    @BindView(R.id.btn_play_game_large) Button mButtonPlayGameLarge;
    @BindView(R.id.ad_view) AdView mBannerAdView;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu_activity);
        ButterKnife.bind(this);
        MobileAds.initialize(this, getString(R.string.admob_app_id));

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.play_game_interstitial_admob_id));
        requestNewInterstitial();

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("E10756F1ADABBEB6CAA9455E812C7D30")
             //  .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        mBannerAdView.loadAd(adRequest);
        setTextColors();
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(!mInterstitialAd.isLoaded() && !mInterstitialAd.isLoading()) requestNewInterstitial();
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("E10756F1ADABBEB6CAA9455E812C7D30")
             //   .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mInterstitialAd.loadAd(adRequest);
    }

    private void playGame(final String gameType) {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    startActivity(GameActivity.getIntent(MainMenuActivity.this, gameType));
                }
            });
            mInterstitialAd.show();
        }else{
            startActivity(GameActivity.getIntent(MainMenuActivity.this, gameType));
        }
    }

    private void setTextColors(){
        mTextViewTitleNumber.setText(TextUtil.getMultiColorString(this,"2048"));
        mTextViewTitleLetters.setText(TextUtil.getMultiColorString(this,getString(R.string.downfall),4));
        mTextViewPlayGame.setText(TextUtil.getMultiColorString(this,getString(R.string.select_game)));
        mButtonPlayGameNormal.setAllCaps(false);
        mButtonPlayGameNormal.setText(TextUtil.getMultiColorString(this,getString(R.string.normal)));
        mButtonPlayGameLarge.setAllCaps(false);
        mButtonPlayGameLarge.setText(TextUtil.getMultiColorString(this,getString(R.string.large)));
    }

    @OnClick(R.id.btn_watch_video)
    public void watchVideo(){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.webpage_video)));
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivity(intent);
    }

    @OnClick(R.id.btn_instructions)
    public void instructions(){
        startActivity(new Intent(this,InstructionsActivity.class));
    }

    @OnClick(R.id.btn_play_game_normal)
    public void playNormal(){
        playGame(GameType.GAME_SIZE_NORMAL);
    }

    @OnClick(R.id.btn_play_game_large)
    public void playLarge(){
        playGame(GameType.GAME_SIZE_LARGE);
    }

    @OnClick(R.id.btn_rate)
    public void rateApp(){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_rate)));
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivity(intent);
    }
}
