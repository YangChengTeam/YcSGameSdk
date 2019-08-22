package com.ymm.nopui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.yc.adsdk.core.AdCallback;
import com.yc.adsdk.core.AdType;
import com.yc.adsdk.core.AdTypeHind;
import com.yc.adsdk.core.Error;
import com.yc.adsdk.core.IUserApiCallback;
import com.yc.adsdk.core.InitUserCallback;
import com.yc.adsdk.core.SAdSDK;
import com.ymm.nopui.R;

public class NopMainActivity extends Activity implements View.OnClickListener {

    private String TAG = "GameSdkLog_NopMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nop_main);
        initViews();
    }

    private void initViews() {
        Button splash = findViewById(R.id.btn_splash);
        Button banner = findViewById(R.id.btn_banner);
        Button bannerHind = findViewById(R.id.btn_banner_hind);
        Button video = findViewById(R.id.btn_video);
        Button insert = findViewById(R.id.btn_insert);
        Button login = findViewById(R.id.btn_login);
        Button logout = findViewById(R.id.btn_logout);

        splash.setOnClickListener(this);
        banner.setOnClickListener(this);
        bannerHind.setOnClickListener(this);
        video.setOnClickListener(this);
        insert.setOnClickListener(this);
        login.setOnClickListener(this);
        logout.setOnClickListener(this);

        initUser();
    }

    private void initUser() {
        SAdSDK.getImpl().initUser(this, new InitUserCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(NopMainActivity.this, "NopSplashActivity 账户SDK初始化成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Error error) {
                Toast.makeText(NopMainActivity.this, "NopSplashActivity 账户SDK初始化失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_splash:
                startActivity(new Intent(NopMainActivity.this, NopSplashActivity.class));
                break;
            case R.id.btn_banner:
                showBannerAd();
                break;
            case R.id.btn_banner_hind:
                SAdSDK.getImpl().hindAd(AdTypeHind.BANNER);
                break;
            case R.id.btn_video:
                showVideoAd();
                break;
            case R.id.btn_insert:
                showInsertAd();
                break;
            case R.id.btn_login:
                login();
                break;
            case R.id.btn_logout:
                logout();
                break;
        }
    }

    private void showInsertAd() {
        SAdSDK.getImpl().showAd(this, AdType.INSTER, new AdCallback() {
            @Override
            public void onDismissed() {

            }

            @Override
            public void onNoAd(Error error) {

            }

            @Override
            public void onPresent() {
                Toast.makeText(NopMainActivity.this, "插屏广告展示成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onClick() {

            }
        });
    }

    private void showVideoAd() {
        SAdSDK.getImpl().showAd(this, AdType.VIDEO, new AdCallback() {
            @Override
            public void onDismissed() {
                Log.d(TAG, "showVideoAd onDismissed: ");
            }

            @Override
            public void onNoAd(Error error) {
                Log.d(TAG, "showVideoAd onNoAd: " + error.getMessage());
            }

            @Override
            public void onPresent() {
                Log.d(TAG, "showVideoAd onPresent: ");
                Toast.makeText(NopMainActivity.this, "视频广告展示成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onClick() {
                Log.d(TAG, "showVideoAd onClick: ");
            }
        });
    }

    private void showBannerAd() {
        SAdSDK.getImpl().showAd(this, AdType.BANNER, new AdCallback() {
            @Override
            public void onDismissed() {

            }

            @Override
            public void onNoAd(Error error) {
            }

            @Override
            public void onPresent() {
                Toast.makeText(NopMainActivity.this, "banner广告加载成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onClick() {
            }
        });
    }

    private void login() {
        SAdSDK.getImpl().login(this, new IUserApiCallback() {
            @Override
            public void onSuccess(String msg) {
                Toast.makeText(NopMainActivity.this, "登录成功 " + msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String msg, int code) {
                Toast.makeText(NopMainActivity.this, "登录失败 " + code + " " + msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logout() {
        SAdSDK.getImpl().logout(this, new IUserApiCallback() {
            @Override
            public void onSuccess(String msg) {
                Toast.makeText(NopMainActivity.this, "退出成功 " + msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String msg, int code) {
                Toast.makeText(NopMainActivity.this, "退出失败 " + code + " " + msg, Toast.LENGTH_SHORT).show();
            }
        });
    }


}
