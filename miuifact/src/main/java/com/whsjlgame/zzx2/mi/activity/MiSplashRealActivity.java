package com.whsjlgame.zzx2.mi.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.whsjlgame.zzx2.mi.R;
import com.yc.adsdk.core.AdCallback;
import com.yc.adsdk.core.AdType;
import com.yc.adsdk.core.Error;
import com.yc.adsdk.core.InitAdCallback;
import com.yc.adsdk.core.SAdSDK;
import com.yc.adsdk.ui.BasePermissionActivity;

public class MiSplashRealActivity extends BasePermissionActivity {
    private final String TAG = "GameSdkLog_MiSplashRealActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_splash_real);
    }

    @Override
    protected void onRequestPermissionSuccess() {
        initAd();
    }

    private void initAd() {
        SAdSDK.getImpl().initAd(this, new InitAdCallback() {
            @Override
            public void onSuccess() {
                SAdSDK.getImpl().showAd(MiSplashRealActivity.this, AdType.SPLASH, new AdCallback() {
                    @Override
                    public void onDismissed() {
                        startActivity(new Intent(MiSplashRealActivity.this, MiMainRealActivity.class));
                        finish();
                    }

                    @Override
                    public void onNoAd(Error error) {
                        startActivity(new Intent(MiSplashRealActivity.this, MiMainRealActivity.class));
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
