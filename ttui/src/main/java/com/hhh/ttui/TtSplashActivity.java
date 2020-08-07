package com.hhh.ttui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.yc.adsdk.core.AdCallback;
import com.yc.adsdk.core.AdType;
import com.yc.adsdk.core.Error;
import com.yc.adsdk.core.InitAdCallback;
import com.yc.adsdk.core.SAdSDK;

public class TtSplashActivity extends Activity {
    private String TAG="GameSdkLog_TtSplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tt_splash);


        SAdSDK.getImpl().initAd(this, new InitAdCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "initAd onSuccess: ");
                showSplash();
            }

            @Override
            public void onFailure(Error error) {

            }
        });
        /*new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                //实现页面跳转
                showSplash();
                return false;
            }
        }).sendEmptyMessageDelayed(0, 2000);//表示延迟3秒发送任务*/
    }

    private void showSplash() {
        SAdSDK.getImpl().showAd(TtSplashActivity.this, AdType.SPLASH, new AdCallback() {
            @Override
            public void onDismissed() {
                Log.d(TAG, "showSplash onDismissed: ");
                goToMainActivity();
            }

            @Override
            public void onNoAd(Error error) {
                Log.d(TAG, "showSplash  onNoAd: ");
                goToMainActivity();
            }

            @Override
            public void onPresent() {
                Log.d(TAG, "showSplash onPresent: ");
            }

            @Override
            public void onClick() {
                Log.d(TAG, "showSplash onClick: ");
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
