package com.ymm.nopui.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.widget.Toast;

import com.yc.adsdk.core.AdCallback;
import com.yc.adsdk.core.Error;
import com.yc.adsdk.core.AdType;
import com.yc.adsdk.core.InitAdCallback;
import com.yc.adsdk.core.SAdSDK;
import com.yc.adsdk.ui.BasePermissionActivity;
import com.ymm.nopui.R;

public class NopSplashActivity extends BasePermissionActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nop_splash);
    }

    @Override
    protected void onRequestPermissionSuccess() {
        SAdSDK.getImpl().initAd(this, new InitAdCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(NopSplashActivity.this, "NopSplashActivity 广告SDK初始化成功", Toast.LENGTH_SHORT).show();
                showSplashAd();
            }

            @Override
            public void onFailure(Error error) {
                Toast.makeText(NopSplashActivity.this, "NopSplashActivity 广告SDK初始化失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showSplashAd() {
        SAdSDK.getImpl().showAd(NopSplashActivity.this, AdType.SPLASH, new AdCallback() {
            @Override
            public void onDismissed() {

            }

            @Override
            public void onNoAd(Error error) {

            }

            @Override
            public void onPresent() {
                Toast.makeText(NopSplashActivity.this, "NopSplashActivity 闪屏广告展示成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onClick() {

            }
        });
        new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                //实现页面跳转
                startNext();
                return false;
            }
        }).sendEmptyMessageDelayed(0, 5200);//表示延迟3秒发送任务
    }

    private void startNext() {
        startActivity(new Intent(getApplicationContext(), NopMainActivity.class));
        finish();
    }
}
