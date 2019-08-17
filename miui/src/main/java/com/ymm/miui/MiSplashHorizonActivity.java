package com.ymm.miui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;

import com.yc.adsdk.core.AdCallback;
import com.yc.adsdk.core.AdType;
import com.yc.adsdk.core.Error;
import com.yc.adsdk.core.SAdSDK;

public class MiSplashHorizonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_splash_horizon);

        show();
    }

    private void show() {
        SAdSDK.getImpl().showAd(MiSplashHorizonActivity.this, AdType.SPLASH_HORIZON, new AdCallback() {
            @Override
            public void onDismissed() {
                startActivity(new Intent(MiSplashHorizonActivity.this, MiMainActivity.class));
            }

            @Override
            public void onNoAd(Error error) {
                startActivity(new Intent(MiSplashHorizonActivity.this, MiMainActivity.class));
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
     * 开屏页一定要禁止用户对返回按钮的控制，否则将可能导致用户手动退出了App而广告无法正常曝光和计费。
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
