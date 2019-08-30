package com.ymm.googleui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;

import com.yc.adsdk.core.AdCallback;
import com.yc.adsdk.core.AdType;
import com.yc.adsdk.core.AdTypeHind;
import com.yc.adsdk.core.Error;
import com.yc.adsdk.core.InitAdCallback;
import com.yc.adsdk.core.SAdSDK;
import com.yc.adsdk.ui.BasePermissionActivity;
import com.ymm.googleui.R;

public class GoogleMainActivity extends BasePermissionActivity implements View.OnClickListener {
    private static final String TAG = "GameSdkLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_google_main);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onRequestPermissionSuccess() {
        SAdSDK.getImpl().initAd(this, new InitAdCallback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(Error error) {

            }
        });

        initViews();


        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 2000);


    }

    private void initViews() {
        Button banner = findViewById(R.id.google_btn_banner);
        Button hide_banner = findViewById(R.id.google_btn_hide_banner);
        Button video = findViewById(R.id.google_btn_video);

        hide_banner.setOnClickListener(this);
        banner.setOnClickListener(this);
        video.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.google_btn_banner:
                showBanner();
                break;
            case R.id.google_btn_hide_banner:
                hideBanner();
                break;
            case R.id.google_btn_video:
                showVideo();
                break;
        }
    }

    private void showVideo() {
        SAdSDK.getImpl().showAd(GoogleMainActivity.this, AdType.VIDEO, new AdCallback() {
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

    private void hideBanner() {
        SAdSDK.getImpl().hindAd(AdTypeHind.BANNER);
    }

    private void showBanner() {
        SAdSDK.getImpl().showAd(GoogleMainActivity.this, AdType.BANNER, new AdCallback() {
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
