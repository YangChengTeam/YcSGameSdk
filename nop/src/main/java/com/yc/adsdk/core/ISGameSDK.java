package com.yc.adsdk.core;

import android.content.Context;


public interface ISGameSDK {
    void init(Context context, InitCallback callback);


    void showAd(Context context, AdType type, AdCallback callback);

    void hindAd(AdTypeHind type);

}
