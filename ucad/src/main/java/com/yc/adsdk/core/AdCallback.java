package com.yc.adsdk.core;

public interface AdCallback {
    void onDismissed();

    void onNoAd(AdError error);

    void onPresent();

    void onClick();
}
