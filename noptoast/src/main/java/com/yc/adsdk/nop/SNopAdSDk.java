package com.yc.adsdk.nop;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import com.yc.adsdk.core.AdCallback;
import com.yc.adsdk.core.AdType;
import com.yc.adsdk.core.AdTypeHind;
import com.yc.adsdk.core.Error;
import com.yc.adsdk.core.ISGameSDK;
import com.yc.adsdk.core.IUserApiCallback;
import com.yc.adsdk.core.InitAdCallback;
import com.yc.adsdk.core.InitUserCallback;

/**
 * Created by caokun on 2019/8/19 14:08.
 */

public class SNopAdSDk implements ISGameSDK {

    private static final String TAG = "GameSdkLog";
    private static SNopAdSDk sAdSDK;

    public static SNopAdSDk getImpl() {
        if (sAdSDK == null) {
            synchronized (SNopAdSDk.class) {
                if (sAdSDK == null) {
                    sAdSDK = new SNopAdSDk();
                }
            }
        }
        return sAdSDK;
    }

    @Override
    public void initAd(Context context, InitAdCallback adCallback) {
        final Error error = new Error();
        error.setMessage("回调不存在");
        if (adCallback != null) {
            adCallback.onSuccess();
            Log.d(TAG, "SNopAdSDk init: 初始化广告成功");
        } else {
            adCallback.onFailure(error);
            Log.d(TAG, "SNopAdSDk init: 初始化广告失败");
        }
    }

    @Override
    public void initUser(Context context, InitUserCallback userCallback) {
        final Error error = new Error();
        error.setMessage("回调不存在");
        if (userCallback != null) {
            userCallback.onSuccess();
            Log.d(TAG, "SNopAdSDk init: 初始化账户成功");
        } else {
            userCallback.onFailure(error);
            Log.d(TAG, "SNopAdSDk init: 初始化账户失败");
        }
    }


    @Override
    public void showAd(Context context, AdType type, AdCallback callback) {
        this.showAd(context, type, callback, null);
    }

    @Override
    public void showAd(Context context, AdType type, AdCallback callback, ViewGroup viewGroup) {
        //        this.mAdCallback = callback;
        switch (type) {
            case BANNER:
                Toast.makeText(context, "广告还未准备好", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "SNopAdSDk showAd: banner广告加载成功");
                callback.onPresent();
                break;
            case VIDEO:
                Toast.makeText(context, "广告还未准备好", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "SNopAdSDk showAd: 视频广告展示成功");
                callback.onPresent();
                break;
            case INSTER:
                Toast.makeText(context, "广告还未准备好", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "SNopAdSDk showAd: 插屏广告展示成功");
                callback.onPresent();
                break;
            case SPLASH:
                Toast.makeText(context, "广告还未准备好", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "SNopAdSDk showAd: 闪屏广告展示成功");
                callback.onPresent();
                break;
        }
        callback.onPresent();
        Log.d(TAG, "SNopAdSDk  showAd: AdType ： " + type);
    }

    @Override
    public void hindAd(AdTypeHind type) {
        Log.d(TAG, "SNopAdSDk  hindAd: ");
    }

    @Override
    public void login(Context context, IUserApiCallback iUserApiCallback) {
        iUserApiCallback.onSuccess("登录成功");
    }

    @Override
    public void logout(Context context, IUserApiCallback iUserApiCallback) {
        iUserApiCallback.onSuccess("退出成功");
    }
}
