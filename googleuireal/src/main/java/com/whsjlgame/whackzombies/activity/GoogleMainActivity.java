package com.whsjlgame.whackzombies.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.whsjlgame.whackzombies.R;
import com.yc.adsdk.core.AdCallback;
import com.yc.adsdk.core.AdType;
import com.yc.adsdk.core.AdTypeHind;
import com.yc.adsdk.core.Error;
import com.yc.adsdk.core.InitAdCallback;
import com.yc.adsdk.core.SAdSDK;
import com.yc.adsdk.ui.BasePermissionActivity;

public class GoogleMainActivity extends BasePermissionActivity implements View.OnClickListener {
    private String TAG = "GameSdkLog_GoogleMainActivity";

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
        int i = v.getId();
        if (i == R.id.google_btn_banner) {
            showBanner();
        } else if (i == R.id.google_btn_hide_banner) {
            hideBanner();
        } else if (i == R.id.google_btn_video) {
            showVideo();
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

   /* @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: ");
        SAdSDK.getImpl().logout(GoogleMainActivity.this, new IUserApiCallback() {
            @Override
            public void onSuccess(String msg) {
                Log.d(TAG, "onSuccess: ");
            }

            @Override
            public void onFailure(String msg, int code) {
                Log.d(TAG, "onFailure: ");
            }
        });
//        super.onBackPressed();
    }*/
}
