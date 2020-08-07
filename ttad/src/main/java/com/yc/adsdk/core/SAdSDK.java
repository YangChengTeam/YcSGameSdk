package com.yc.adsdk.core;

import android.content.Context;
import android.os.Build;
import android.view.ViewGroup;

import com.yc.adsdk.tt.STtAdSDk;
import com.yc.adsdk.tt.TTAdManagerHolder;


public class SAdSDK implements ISGameSDK {
    private static SAdSDK sAdSDK;
    private String TAG = "GameSdkLog";

    public static SAdSDK getImpl() {
        if (sAdSDK == null) {
            synchronized (SAdSDK.class) {
                if (sAdSDK == null) {
                    sAdSDK = new SAdSDK();
                }
            }
        }
        return sAdSDK;
    }


    @Override
    public void initAd(Context context, InitAdCallback adCallback) {
        STtAdSDk.getImpl().initAd(context, adCallback);
    }

    @Override
    public void initUser(Context context, InitUserCallback userCallback) {
        STtAdSDk.getImpl().initUser(context, userCallback);
    }

    /**
     * @param context  上下文对象
     * @param type     广告类型
     * @param callback 广告状态
     */
    @Override
    public void showAd(Context context, AdType type, AdCallback callback) {
        this.showAd(context, type, callback, null);
    }

    @Override
    public void showAd(Context context, AdType type, AdCallback callback, ViewGroup viewGroup) {
        if (type != AdType.SPLASH && type != AdType.VIDEO_REWARD_HORIZON && type != AdType.VIDEO_REWARD && Build.VERSION.SDK_INT >= 23) {  //闪屏类没有下载类广告，不需要用户授权动态权限
            //:申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
            TTAdManagerHolder.get().requestPermissionIfNecessary(context);
        }
        STtAdSDk.getImpl().showAd(context, type, callback, viewGroup);
    }

    @Override
    public void hindAd(AdTypeHind type) {

    }

    @Override
    public void login(Context context, IUserApiCallback iUserApiCallback) {

    }

    @Override
    public void logout(Context context, IUserApiCallback iUserApiCallback) {

    }
}
