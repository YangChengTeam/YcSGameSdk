package com.ymm.miui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.yc.adsdk.core.AdCallback;
import com.yc.adsdk.core.AdType;
import com.yc.adsdk.core.AdTypeHind;
import com.yc.adsdk.core.Error;
import com.yc.adsdk.core.SAdSDK;

public class MiMainHorizontalActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_main_horizontal);


        Button splash = findViewById(R.id.btn_splash);
        Button insert = findViewById(R.id.btn_insert);
        Button video = findViewById(R.id.btn_video);

        splash.setOnClickListener(this);
        video.setOnClickListener(this);
        insert.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_splash:
                startActivity(new Intent(MiMainHorizontalActivity.this,MiSplashHorizonActivity.class));
                break;
            case R.id.btn_splash_v:
                startActivity(new Intent(MiMainHorizontalActivity.this,MiSplashActivity.class));
                break;
            case R.id.btn_video:
                SAdSDK.getImpl().showAd(MiMainHorizontalActivity.this, AdType.VIDEO_HORIZON, new AdCallback() {
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
                break;
            case R.id.btn_video_v:
                SAdSDK.getImpl().showAd(MiMainHorizontalActivity.this, AdType.VIDEO, new AdCallback() {
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
                break;
            case R.id.btn_insert:
                SAdSDK.getImpl().showAd(MiMainHorizontalActivity.this, AdType.INSTER_HORIZON, new AdCallback() {
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
                break;
            case R.id.btn_insert_v:
                SAdSDK.getImpl().showAd(MiMainHorizontalActivity.this, AdType.INSTER, new AdCallback() {
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
                break;
            case R.id.btn_banner:
                SAdSDK.getImpl().showAd(MiMainHorizontalActivity.this, AdType.BANNER, new AdCallback() {
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
                break;
            case R.id.btn_banner_hind:
                SAdSDK.getImpl().hindAd(AdTypeHind.BANNER);
                break;
        }
    }
}
