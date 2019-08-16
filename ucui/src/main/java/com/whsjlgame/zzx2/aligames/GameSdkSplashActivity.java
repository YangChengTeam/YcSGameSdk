package com.whsjlgame.zzx2.aligames;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.yc.adsdk.core.AdCallback;
import com.yc.adsdk.core.AdError;
import com.yc.adsdk.core.AdType;
import com.yc.adsdk.core.InitCallback;
import com.yc.adsdk.core.SAdSDK;
import com.yc.adsdk.ui.BasePermissionActivity;

public class GameSdkSplashActivity extends BasePermissionActivity {

    private String TAG = "GameSdkLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_sdk_splash);
    }

    @Override
    protected void onRequestPermissionSuccess() {
        Log.d(TAG, "onRequestPermissionSuccess: ");

        initAdSdk();
    }

    private void initAdSdk() {
        SAdSDK.getImpl().init(this, new InitCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: 初始化广告SDK onSuccess");
                showSplash();
            }

            @Override
            public void onFailure(AdError error) {
                String code = error.getCode();
                String message = error.getMessage();
                Throwable throwable = error.getThrowable();
                Log.d(TAG, "onSuccess: 初始化广告SDK onFailure " + "  code " + code + "  message " + message + "  throwable " + throwable);
                startNext();
            }
        });
    }

    private void showSplash() {
        SAdSDK.getImpl().showAd(this, AdType.SPLASH, new AdCallback() {
            @Override
            public void onDismissed() { //广告关闭
                startNext();
            }

            @Override
            public void onNoAd(AdError error) { //没有广告或广告错误
                startNext();
            }

            @Override
            public void onPresent() { //展示广告

            }

            @Override
            public void onClick() { //点击广告

            }
        });
    }

    private void startNext() {
        if (!GameSdkSplashActivity.this.isFinishing()) {
            startActivity(new Intent(GameSdkSplashActivity.this, GameSdkMainActivity.class));
            GameSdkSplashActivity.this.finish();
        }
    }
}
