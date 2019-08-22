package com.whsjlgame.zzx2.aligames;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.yc.adsdk.core.AdCallback;
import com.yc.adsdk.core.Error;
import com.yc.adsdk.core.AdType;
import com.yc.adsdk.core.AdTypeHind;
import com.yc.adsdk.core.SAdSDK;
import com.yc.adsdk.ui.BasePermissionActivity;

public class GameSdkMainActivity  extends BasePermissionActivity implements View.OnClickListener {

    private String TAG = "GameSdkLog";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_sdk_main);

        initViews();

    }

    @Override
    protected void onRequestPermissionError() {

    }

    @Override
    protected void onRequestPermissionSuccess() {

    }

    private void initViews() {
        Button btnSplash = findViewById(R.id.mainb_btn_splash);
        Button btnBanner = findViewById(R.id.mainb_btn_banner);
        Button btnVideo = findViewById(R.id.mainb_btn_video);
        Button btnInsert = findViewById(R.id.mainb_btn_insert);
        Button btnNext = findViewById(R.id.mainb_btn_next);
        Button btnHind = findViewById(R.id.mainb_btn_hind);

        btnSplash.setOnClickListener(this);
        btnBanner.setOnClickListener(this);
        btnVideo.setOnClickListener(this);
        btnInsert.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnHind.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.mainb_btn_splash) {
            startActivity(new Intent(GameSdkMainActivity.this, GameSdkSplashActivity.class));
        } else if (i == R.id.mainb_btn_banner) {
            SAdSDK.getImpl().showAd(GameSdkMainActivity.this, AdType.BANNER, new AdCallback() {
                @Override
                public void onDismissed() {

                }

                @Override
                public void onNoAd(Error error) {

                }

                @Override
                public void onPresent() {

                }

                @Override
                public void onClick() {

                }
            });
        } else if (i == R.id.mainb_btn_video) {
            SAdSDK.getImpl().showAd(GameSdkMainActivity.this, AdType.VIDEO, new AdCallback() {
                @Override
                public void onDismissed() {

                }

                @Override
                public void onNoAd(Error error) {

                }

                @Override
                public void onPresent() {

                }

                @Override
                public void onClick() {

                }
            });
        } else if (i == R.id.mainb_btn_insert) {
            SAdSDK.getImpl().showAd(GameSdkMainActivity.this, AdType.INSTER, new AdCallback() {
                @Override
                public void onDismissed() {

                }

                @Override
                public void onNoAd(Error error) {

                }

                @Override
                public void onPresent() {

                }

                @Override
                public void onClick() {

                }
            });
        } else if (i == R.id.mainb_btn_next) {
            startActivity(new Intent(GameSdkMainActivity.this, GameSdkMainActivity.class));
        } else if (i == R.id.mainb_btn_hind) {
            SAdSDK.getImpl().hindAd(AdTypeHind.BANNER);
        }
    }
}