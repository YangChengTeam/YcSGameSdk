package com.ymm.miui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.yc.adsdk.core.AdCallback;
import com.yc.adsdk.core.AdType;
import com.yc.adsdk.core.Error;
import com.yc.adsdk.core.InitCallback;
import com.yc.adsdk.core.SAdSDK;
import com.yc.adsdk.ui.BasePermissionActivity;

public class MiSplashActivity extends BasePermissionActivity {

    private static final String TAG = "MiSplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_splash);

    }

    @Override
    protected void onRequestPermissionSuccess() {
        initAd();
    }

    private void initAd() {
        SAdSDK.getImpl().init(this, new InitCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: ");
                SAdSDK.getImpl().showAd(MiSplashActivity.this, AdType.SPLASH_VERTICAL, new AdCallback() {
                    @Override
                    public void onDismissed() {
                        startActivity(new Intent(MiSplashActivity.this, MiMainActivity.class));
                        finish();
                    }

                    @Override
                    public void onNoAd(Error error) {
                        startActivity(new Intent(MiSplashActivity.this, MiMainActivity.class));
                        finish();
                    }

                    @Override
                    public void onPresent() {

                    }

                    @Override
                    public void onClick() {

                    }
                });
            }

            @Override
            public void onFailure(Error error) {
                Log.d(TAG, "onFailure: ");
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