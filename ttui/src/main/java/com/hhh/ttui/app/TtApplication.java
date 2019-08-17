package com.hhh.ttui.app;

import android.app.Application;

import com.yc.adsdk.tt.STtAdSDk;

/**
 * Created by caokun on 2019/8/16 16:23.
 */

public class TtApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        STtAdSDk.getImpl().init(this, null);
    }

}
