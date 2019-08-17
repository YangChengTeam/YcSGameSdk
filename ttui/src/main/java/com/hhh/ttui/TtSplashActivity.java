package com.hhh.ttui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.yc.adsdk.core.AdCallback;
import com.yc.adsdk.core.AdType;
import com.yc.adsdk.core.Error;
import com.yc.adsdk.core.InitCallback;
import com.yc.adsdk.core.SAdSDK;
import com.yc.adsdk.tt.STtAdSDk;

public class TtSplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tt_splash);

        SAdSDK.getImpl().showAd(TtSplashActivity.this, AdType.SPLASH, new AdCallback() {
            @Override
            public void onDismissed() {
                goToMainActivity();
            }

            @Override
            public void onNoAd(Error error) {
                goToMainActivity();
            }

            @Override
            public void onPresent() {

            }

            @Override
            public void onClick() {

            }
        });
    }
    /**
     * 跳转到主页面
     */
    private void goToMainActivity() {
        Intent intent = new Intent(TtSplashActivity.this, TtMainActivity.class);
        startActivity(intent);
//        mSplashContainer.removeAllViews();
        this.finish();
    }
}
