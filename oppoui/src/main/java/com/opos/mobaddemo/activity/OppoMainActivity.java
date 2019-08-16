package com.opos.mobaddemo.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.opos.mobaddemo.R;
import com.yc.adsdk.core.AdCallback;
import com.yc.adsdk.core.AdType;
import com.yc.adsdk.core.AdTypeHind;
import com.yc.adsdk.core.Error;
import com.yc.adsdk.core.SAdSDK;
import com.yc.adsdk.oppo.SOppoAdSDk;

public class OppoMainActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = "GameSdkLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oppo_main);

        initViews();
    }

    private void initViews() {
        Button splash = findViewById(R.id.btn_splash);
        Button banner = findViewById(R.id.btn_banner);
        Button bannerHind = findViewById(R.id.btn_banner_hind);
        Button video = findViewById(R.id.btn_video);
        Button insert = findViewById(R.id.btn_insert);

        splash.setOnClickListener(this);
        banner.setOnClickListener(this);
        bannerHind.setOnClickListener(this);
        video.setOnClickListener(this);
        insert.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_splash:
                startActivity(new Intent(OppoMainActivity.this, OppoSplashActivity.class));
                break;
            case R.id.btn_banner:
                showBannerAd();
                break;
            case R.id.btn_banner_hind:
                SAdSDK.getImpl().hindAd(AdTypeHind.BANNER);
                break;
            case R.id.btn_video:
                showVideoAd();
                break;
            case R.id.btn_insert:
                showInsertAd();
                break;
        }
    }

    private void showInsertAd() {
        SAdSDK.getImpl().showAd(this, AdType.INSTER, new AdCallback() {
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
    }

    private void showVideoAd() {
        SAdSDK.getImpl().showAd(this, AdType.VIDEO, new AdCallback() {
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
    }

    private void showBannerAd() {
        SAdSDK.getImpl().showAd(this, AdType.BANNER, new AdCallback() {
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
    }
}
