package com.ymm.nopui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.yc.adsdk.core.AdCallback;
import com.yc.adsdk.core.AdError;
import com.yc.adsdk.core.AdType;
import com.yc.adsdk.core.InitCallback;
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
        SAdSDK.getImpl().init(this, new InitCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(NopSplashActivity.this, "SDK初始化成功", Toast.LENGTH_SHORT).show();

                SAdSDK.getImpl().showAd(NopSplashActivity.this, AdType.BANNER, new AdCallback() {
                    @Override
                    public void onDismissed() {

                    }

                    @Override
                    public void onNoAd(AdError error) {

                    }

                    @Override
                    public void onPresent() {
                        Toast.makeText(NopSplashActivity.this, "展示广告成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onClick() {

                    }
                });
            }

            @Override
            public void onFailure(AdError error) {
                Toast.makeText(NopSplashActivity.this, "SDK初始化失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
