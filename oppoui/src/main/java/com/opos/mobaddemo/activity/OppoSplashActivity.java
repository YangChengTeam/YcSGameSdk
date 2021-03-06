package com.opos.mobaddemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.opos.mobaddemo.R;
import com.yc.adsdk.core.AdCallback;
import com.yc.adsdk.core.AdType;
import com.yc.adsdk.core.Error;
import com.yc.adsdk.core.InitAdCallback;
import com.yc.adsdk.core.InitUserCallback;
import com.yc.adsdk.core.SAdSDK;
import com.yc.adsdk.ui.BasePermissionActivity;


public class OppoSplashActivity extends BasePermissionActivity {

    private String TAG = "GameSdkLog_OppoSplashActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oppo_splash);
    }

    @Override
    protected void onRequestPermissionSuccess() {

        SAdSDK.getImpl().initAd(this, new InitAdCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "OppoSplashActivity  init ad onSuccess: ");
                showSplashAd();
            }

            @Override
            public void onFailure(Error error) {
                Log.d(TAG, "OppoSplashActivity  init ad onFailure: ");
                startNext();
            }
        });
    }

    private boolean mIsClickAd;

    private void showSplashAd() {
        SAdSDK.getImpl().showAd(this, AdType.SPLASH, new AdCallback() {
            @Override
            public void onDismissed() {
                startNext();
            }

            @Override
            public void onNoAd(Error error) {
                startNext();
            }

            @Override
            public void onPresent() {
                Log.d(TAG, "showSplashAd onPresent: ");
            }

            @Override
            public void onClick() {
                mIsClickAd = true; //记录广告的点击事件
            }
        });
    }

    /**
     * 重新回到闪屏页面时，当用户已经点击过广告了，跳转页面关闭广告
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (mIsClickAd) {
            startNext();
        }
    }

    private void startNext() {
        startActivity(new Intent(OppoSplashActivity.this, OppoMainActivity.class));
        finish();
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
