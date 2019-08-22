package com.yc.adsdk.core;

import android.content.Context;
import android.view.ViewGroup;


public interface ISGameSDK {

    void initAd(Context context, InitAdCallback adCallback);

    void initUser(Context context, InitUserCallback userCallback);

    void showAd(Context context, AdType type, AdCallback callback);

    void showAd(Context context, AdType type, AdCallback callback, ViewGroup viewGroup);

    void hindAd(AdTypeHind type);

    void login(Context context, IUserApiCallback iUserApiCallback);

    void logout(Context context, IUserApiCallback iUserApiCallback);
}
