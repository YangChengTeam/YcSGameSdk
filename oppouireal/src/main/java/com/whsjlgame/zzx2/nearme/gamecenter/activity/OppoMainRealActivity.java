package com.whsjlgame.zzx2.nearme.gamecenter.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.whsjlgame.zzx2.nearme.gamecenter.R;
import com.yc.adsdk.core.AdCallback;
import com.yc.adsdk.core.AdType;
import com.yc.adsdk.core.AdTypeHind;
import com.yc.adsdk.core.Error;
import com.yc.adsdk.core.IUserApiCallback;
import com.yc.adsdk.core.InitUserCallback;
import com.yc.adsdk.core.SAdSDK;

public class OppoMainRealActivity extends Activity implements View.OnClickListener {

    private String TAG = "GameSdkLog_OppoMainRealActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oppo_main_real);
        initViews();
        initUser();
    }

    private void initUser() {
        SAdSDK.getImpl().initUser(this, new InitUserCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: ");
            }

            @Override
            public void onFailure(Error error) {
                Log.d(TAG, "onFailure: " + error.getMessage());
            }
        });
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
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_splash) {
            startActivity(new Intent(OppoMainRealActivity.this, OppoSplashRealActivity.class));
        } else if (i == R.id.btn_banner) {
            showBannerAd();
        } else if (i == R.id.btn_banner_hind) {
            SAdSDK.getImpl().hindAd(AdTypeHind.BANNER);
        } else if (i == R.id.btn_video) {
            showVideoAd();
        } else if (i == R.id.btn_insert) {
            showInsertAd();
        } else if (i == R.id.btn_login) {
            login();
        } else if (i == R.id.btn_logout) {
            logout();
        }
    }

    private void login() {
        SAdSDK.getImpl().login(this, new IUserApiCallback() {
            @Override
            public void onSuccess(String msg) {
                Toast.makeText(OppoMainRealActivity.this, "登录成功 " + msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String msg, int code) {
                Toast.makeText(OppoMainRealActivity.this, "登录失败 " + code + " " + msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logout() {
        SAdSDK.getImpl().logout(this, new IUserApiCallback() {
            @Override
            public void onSuccess(String msg) {
                Toast.makeText(OppoMainRealActivity.this, "退出成功 " + msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String msg, int code) {
                Toast.makeText(OppoMainRealActivity.this, "退出失败 " + code + " " + msg, Toast.LENGTH_SHORT).show();
            }
        });
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
            }

            @Override
            public void onClick() {
            }
        });
    }
}

