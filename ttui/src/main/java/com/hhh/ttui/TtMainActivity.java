package com.hhh.ttui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.yc.adsdk.core.AdCallback;
import com.yc.adsdk.core.AdType;
import com.yc.adsdk.core.Error;
import com.yc.adsdk.core.SAdSDK;

public class TtMainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "STtAdSDk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tt_main);


        Button btnInster = findViewById(R.id.ad_sdk_main_btn_inster);
        Button btnInsterDownload = findViewById(R.id.ad_sdk_main_btn_inster_download);
        Button btnVideoV = findViewById(R.id.ad_sdk_main_btn_video_v);
        Button btnVideoH = findViewById(R.id.ad_sdk_main_btn_video_h);
        Button btnVideoReward = findViewById(R.id.ad_sdk_main_btn_video_reward);
        Button btnVideoNative = findViewById(R.id.ad_sdk_main_btn_video_native);
        Button btnSplash = findViewById(R.id.ad_sdk_main_btn_splash);
        Button btnBanner = findViewById(R.id.ad_sdk_main_btn_banner);
        Button btnBannerDownload = findViewById(R.id.ad_sdk_main_btn_banner_download);
        Button btnBannerNative = findViewById(R.id.ad_sdk_main_btn_banner_native);

        btnInster.setOnClickListener(this);
        btnInsterDownload.setOnClickListener(this);
        btnVideoV.setOnClickListener(this);
        btnVideoH.setOnClickListener(this);
        btnVideoReward.setOnClickListener(this);
        btnVideoNative.setOnClickListener(this);
        btnSplash.setOnClickListener(this);
        btnBanner.setOnClickListener(this);
        btnBannerDownload.setOnClickListener(this);
        btnBannerNative.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ad_sdk_main_btn_inster) {
            SAdSDK.getImpl().showAd(TtMainActivity.this, AdType.INSTER, new AdCallback() {
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
        } else if (id == R.id.ad_sdk_main_btn_inster_download) {
            SAdSDK.getImpl().showAd(TtMainActivity.this, AdType.INSTER_DOWNLOAD, new AdCallback() {
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
        } else if (id == R.id.ad_sdk_main_btn_video_h) {
            SAdSDK.getImpl().showAd(TtMainActivity.this, AdType.VIDEO, new AdCallback() {
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
        } else if (id == R.id.ad_sdk_main_btn_video_reward) {
            SAdSDK.getImpl().showAd(TtMainActivity.this, AdType.VIDEO_REWARD, new AdCallback() {
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
        } else if (id == R.id.ad_sdk_main_btn_video_v) {
            SAdSDK.getImpl().showAd(TtMainActivity.this, AdType.VIDEO_V, new AdCallback() {
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
        } else if (id == R.id.ad_sdk_main_btn_video_native) {
            SAdSDK.getImpl().showAd(TtMainActivity.this, AdType.VIDEO_NATIVE, new AdCallback() {
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
        } else if (id == R.id.ad_sdk_main_btn_splash) {
            startActivity(new Intent(TtMainActivity.this, TtSplashActivity.class));
            finish();
        } else if (id == R.id.ad_sdk_main_btn_banner) {
            SAdSDK.getImpl().showAd(TtMainActivity.this, AdType.BANNER, new AdCallback() {
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
        } else if (id == R.id.ad_sdk_main_btn_banner_download) {
            SAdSDK.getImpl().showAd(TtMainActivity.this, AdType.BANNER_DOWNLOAD, new AdCallback() {
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
        } else if (id == R.id.ad_sdk_main_btn_banner_native) {
            SAdSDK.getImpl().showAd(TtMainActivity.this, AdType.BANNER_NATIVE, new AdCallback() {
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(TtMainActivity.this, TtMainActivity.class));
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}