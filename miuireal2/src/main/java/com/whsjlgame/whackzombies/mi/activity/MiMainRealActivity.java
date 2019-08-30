package com.whsjlgame.whackzombies.mi.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.whsjlgame.whackzombies.mi.R;
import com.yc.adsdk.core.AdCallback;
import com.yc.adsdk.core.AdType;
import com.yc.adsdk.core.AdTypeHind;
import com.yc.adsdk.core.Error;
import com.yc.adsdk.core.SAdSDK;

public class MiMainRealActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_main_real);
        Button splashVertical = findViewById(R.id.btn_splash_v);
        Button insertVertical = findViewById(R.id.btn_insert_v);
        Button videoVertical = findViewById(R.id.btn_video_v);
        Button banner = findViewById(R.id.btn_banner);
        Button bannerHind = findViewById(R.id.btn_banner_hind);


        splashVertical.setOnClickListener(this);
        videoVertical.setOnClickListener(this);
        insertVertical.setOnClickListener(this);
        banner.setOnClickListener(this);
        bannerHind.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_splash_v:
                startActivity(new Intent(MiMainRealActivity.this, MiSplashRealActivity.class));
                break;
            case R.id.btn_video_v:
                SAdSDK.getImpl().showAd(MiMainRealActivity.this, AdType.VIDEO, new AdCallback() {
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
                break;
            case R.id.btn_insert_v:
                SAdSDK.getImpl().showAd(MiMainRealActivity.this, AdType.INSTER, new AdCallback() {
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
                break;
            case R.id.btn_banner:
                SAdSDK.getImpl().showAd(MiMainRealActivity.this, AdType.BANNER, new AdCallback() {
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
                break;
            case R.id.btn_banner_hind:
                SAdSDK.getImpl().hindAd(AdTypeHind.BANNER);
                break;
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
