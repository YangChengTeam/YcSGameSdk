package com.ymm.miui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.yc.adsdk.core.AdCallback;
import com.yc.adsdk.core.AdType;
import com.yc.adsdk.core.AdTypeHind;
import com.yc.adsdk.core.Error;
import com.yc.adsdk.core.SAdSDK;
import com.ymm.miui.R;

public class MiMainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_main);


        Button toHorizontal = findViewById(R.id.btn_to_horizontal);
        Button splashVertical = findViewById(R.id.btn_splash_v);
        Button insertVertical = findViewById(R.id.btn_insert_v);
        Button videoVertical = findViewById(R.id.btn_video_v);
        Button banner = findViewById(R.id.btn_banner);
        Button bannerHind = findViewById(R.id.btn_banner_hind);


        toHorizontal.setOnClickListener(this);
        splashVertical.setOnClickListener(this);
        videoVertical.setOnClickListener(this);
        insertVertical.setOnClickListener(this);
        banner.setOnClickListener(this);
        bannerHind.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_to_horizontal:
                startActivity(new Intent(MiMainActivity.this, MiMainHorizontalActivity.class));
                break;
            case R.id.btn_splash:
                startActivity(new Intent(MiMainActivity.this, MiSplashHorizonActivity.class));
                break;
            case R.id.btn_splash_v:
                startActivity(new Intent(MiMainActivity.this, MiSplashActivity.class));
                break;
            case R.id.btn_video:
                SAdSDK.getImpl().showAd(MiMainActivity.this, AdType.VIDEO_HORIZON, new AdCallback() {
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
                SAdSDK.getImpl().showAd(MiMainActivity.this, AdType.VIDEO, new AdCallback() {
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
                SAdSDK.getImpl().showAd(MiMainActivity.this, AdType.INSTER_HORIZON, new AdCallback() {
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
                SAdSDK.getImpl().showAd(MiMainActivity.this, AdType.INSTER, new AdCallback() {
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
                SAdSDK.getImpl().showAd(MiMainActivity.this, AdType.BANNER, new AdCallback() {
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
