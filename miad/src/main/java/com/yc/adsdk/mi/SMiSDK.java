package com.yc.adsdk.mi;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.miui.zeus.mimo.sdk.MimoSdk;
import com.miui.zeus.mimo.sdk.ad.AdWorkerFactory;
import com.miui.zeus.mimo.sdk.ad.IAdWorker;
import com.miui.zeus.mimo.sdk.ad.IRewardVideoAdWorker;
import com.miui.zeus.mimo.sdk.api.IMimoSdkListener;
import com.miui.zeus.mimo.sdk.listener.MimoAdListener;
import com.miui.zeus.mimo.sdk.listener.MimoRewardVideoListener;
import com.yc.adsdk.core.AdCallback;
import com.yc.adsdk.core.AdType;
import com.yc.adsdk.core.AdTypeHind;
import com.yc.adsdk.core.Error;
import com.yc.adsdk.core.ISGameSDK;
import com.yc.adsdk.core.InitCallback;
import com.yc.adsdk.utils.LocalJsonResolutionUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by caokun on 2019/8/16 17:47.
 */

public class SMiSDK implements ISGameSDK {

    private String TAG = "GameSdkLog";

    private static SMiSDK sAdSDK;
    private String mAppId;
    private String mAppKey;
    private String mAppToken;
    private String mSplashVerticalAdId;
    private String mSplashHorizonAdId;
    private String mVideoVerticalAdId;
    private String mVideoHorizonAdId;
    private String mBannerAdId;
    private String mInsterVerticalAdId;
    private String mInsterHorizonAdId;
    private AdCallback mAdCallback;
    private IRewardVideoAdWorker mVideoAdWorker;

    public static SMiSDK getImpl() {
        if (sAdSDK == null) {
            synchronized (SMiSDK.class) {
                if (sAdSDK == null) {
                    sAdSDK = new SMiSDK();
                }
            }
        }
        return sAdSDK;
    }

    @Override
    public void init(Context context, final InitCallback callback) {
        if (!initConfig(context)) {
            return;
        }

        // 正式上线时候务必关闭stage和debug
        MimoSdk.setDebugOn();
        MimoSdk.setStageOn();

        // 如需要在本地预置插件,请在assets目录下添加mimo_asset.apk;
        MimoSdk.init(context, mAppId, mAppKey, mAppToken, new IMimoSdkListener() {
            @Override
            public void onSdkInitSuccess() {
                Log.d(TAG, "onSdkInitSuccess: ");
                callback.onSuccess();
            }

            @Override
            public void onSdkInitFailed() {
                Log.d(TAG, "onSdkInitFailed: ");
                Error error = new Error();
                error.setMessage("小米游戏SDK初始化失败");
                callback.onFailure(error);
            }
        });
    }

    private boolean initConfig(Context context) {
        String idconfig = LocalJsonResolutionUtils.getJson(context, "miidconfig.json");
        try {
            JSONObject jsonObject = new JSONObject(idconfig);
            JSONObject data = jsonObject.getJSONObject("data");
            if (data.has("appId")) {
                mAppId = data.getString("appId");
                if (TextUtils.isEmpty(mAppId)) {
                    Toast.makeText(context, "初始化失败，缺少广告必须参数AppId", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            if (data.has("appKey")) {
                mAppKey = data.getString("appKey");
                if (TextUtils.isEmpty(mAppKey)) {
                    Toast.makeText(context, "初始化失败，缺少广告必须参数 appKey", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            if (data.has("appToken")) {
                mAppToken = data.getString("appToken");
                if (TextUtils.isEmpty(mAppToken)) {
                    Toast.makeText(context, "初始化失败，缺少广告必须参数 appToken", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            if (data.has("splashVerticalAdId")) {
                mSplashVerticalAdId = data.getString("splashVerticalAdId");
                if (TextUtils.isEmpty(mSplashVerticalAdId)) {
                    Toast.makeText(context, "初始化失败，缺少广告必须参数 splashVerticalAdId", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            if (data.has("splashHorizonAdId")) {
                mSplashHorizonAdId = data.getString("splashHorizonAdId");
                if (TextUtils.isEmpty(mSplashHorizonAdId)) {
                    Toast.makeText(context, "初始化失败，缺少广告必须参数 splashHorizonAdId", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            if (data.has("videoVerticalAdId")) {
                mVideoVerticalAdId = data.getString("videoVerticalAdId");
                if (TextUtils.isEmpty(mVideoVerticalAdId)) {
                    Toast.makeText(context, "初始化失败，缺少广告必须参数 videoVerticalAdId", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            if (data.has("videoHorizonAdId")) {
                mVideoHorizonAdId = data.getString("videoHorizonAdId");
                if (TextUtils.isEmpty(mVideoHorizonAdId)) {
                    Toast.makeText(context, "初始化失败，缺少广告必须参数 videoHorizonAdId", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            if (data.has("bannerAdId")) {
                mBannerAdId = data.getString("bannerAdId");
                if (TextUtils.isEmpty(mBannerAdId)) {
                    Toast.makeText(context, "初始化失败，缺少广告必须参数 bannerAdId", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            if (data.has("insterVerticalAdId")) {
                mInsterVerticalAdId = data.getString("insterVerticalAdId");
                if (TextUtils.isEmpty(mInsterVerticalAdId)) {
                    Toast.makeText(context, "初始化失败，缺少广告必须参数 insterVerticalAdId", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            if (data.has("insterHorizonAdId")) {
                mInsterHorizonAdId = data.getString("insterHorizonAdId");
                if (TextUtils.isEmpty(mInsterHorizonAdId)) {
                    Toast.makeText(context, "初始化失败，缺少广告必须参数 insterHorizonAdId", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "initConfig: JSONException " + e.toString());
        }
        return true;
    }

    @Override
    public void showAd(Context context, AdType type, AdCallback callback) {
        this.showAd(context, type, callback, null);
    }

    @Override
    public void showAd(Context context, AdType type, AdCallback callback, ViewGroup viewGroup) {
        mAdCallback = callback;
        switch (type) {
            case BANNER:
                loadBannerAd((Activity) context);
                break;
            case SPLASH_VERTICAL:
                loadSplashVerticalAd((Activity) context, mSplashVerticalAdId);
                break;
            case SPLASH_HORIZON:
                loadSplashVerticalAd((Activity) context, mSplashHorizonAdId);
                break;
            case VIDEO_VERTICAL:
                Log.d(TAG, "showAd: VIDEO_VERTICAL");
                loadVideoVerticalAd((Activity) context, mVideoVerticalAdId);
                break;
            case VIDEO_HORIZON:
                Log.d(TAG, "showAd: VIDEO_HORIZON");
                loadVideoVerticalAd((Activity) context, mVideoHorizonAdId);
                break;
            case INSTER_VERTICAL:
                loadInsterHorizonAd((Activity) context, mInsterVerticalAdId);
                break;
            case INSTER_HORIZON:
                loadInsterHorizonAd((Activity) context, mInsterHorizonAdId);
                break;
        }
    }

    private void loadVideoVerticalAd(Activity context, String adId) {
        try {
            mVideoAdWorker = AdWorkerFactory
                    .getRewardVideoAdWorker(context, adId, com.xiaomi.ad.common.pojo.AdType.AD_REWARDED_VIDEO);
            mVideoAdWorker.setListener(new RewardVideoListener(mVideoAdWorker));
            mVideoAdWorker.recycle();
            if (!mVideoAdWorker.isReady()) {
                mVideoAdWorker.load();
            }

        } catch (Exception e) {
            Log.e(TAG, "Video Ad Worker e : ", e);
        }
    }

    private class RewardVideoListener implements MimoRewardVideoListener {
        private IRewardVideoAdWorker mAdWorker;

        public RewardVideoListener(IRewardVideoAdWorker adWorker) {
            mAdWorker = adWorker;
        }

        @Override
        public void onVideoStart() {
            Log.d(TAG, "onVideoStart " + " onVideoStart status = " + mAdWorker.getVideoStatus());
        }

        @Override
        public void onVideoPause() {
            Log.d(TAG, "onVideoPause" + " onVideoStart status = " + mAdWorker.getVideoStatus());
        }

        @Override
        public void onVideoComplete() {
            Log.d(TAG, "onVideoComplete" + " onVideoStart status = " + mAdWorker.getVideoStatus());
        }

        @Override
        public void onAdPresent() {
            Log.d(TAG, "onAdPresent" + " onVideoStart status = " + mAdWorker.getVideoStatus() + " onAdPresent isReady = " + mAdWorker.isReady());
        }

        @Override
        public void onAdClick() {
            Log.d(TAG, "onAdClick" + mAdWorker.getVideoStatus() + " onAdPresent isReady = " + mAdWorker.isReady());
        }

        @Override
        public void onAdDismissed() {
            Log.d(TAG, "onAdDismissed" + mAdWorker.getVideoStatus() + " onAdPresent isReady = " + mAdWorker.isReady());
        }

        @Override
        public void onAdFailed(String message) {
            Log.e(TAG, "onAdFailed : " + message + " " + mAdWorker.getVideoStatus() + " onAdPresent isReady = " + mAdWorker.isReady());
        }

        @Override
        public void onAdLoaded(int size) {
            Log.d(TAG, "onAdLoaded : " + size + " " + mAdWorker.getVideoStatus() + " onAdPresent isReady = " + mAdWorker.isReady());
            try {
                mVideoAdWorker.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onStimulateSuccess() {
            Log.d(TAG, "onStimulateSuccess" + mAdWorker.getVideoStatus() + " onAdPresent isReady = " + mAdWorker.isReady());
        }
    }


    private MimoAdListener mMimoAdListener = new MimoAdListener() {
        @Override
        public void onAdPresent() {
            // 开屏广告展示
            Log.d(TAG, "onAdPresent");
            mAdCallback.onPresent();
        }

        @Override
        public void onAdClick() {
            //用户点击了开屏广告
            Log.d(TAG, "onAdClick");
            mAdCallback.onClick();
        }

        @Override
        public void onAdDismissed() {
            //这个方法被调用时，表示从开屏广告消失。
            Log.d(TAG, "onAdDismissed");
            mAdCallback.onDismissed();
        }

        @Override
        public void onAdFailed(String s) {
            Log.e(TAG, "ad fail message : " + s);
            Error error = new Error();
            error.setMessage(s);
            mAdCallback.onNoAd(error);
        }

        @Override
        public void onAdLoaded(int size) {
            Log.d(TAG, "onAdLoaded: " + size);
            //do nothing
        }

        @Override
        public void onStimulateSuccess() {
        }
    };

    private void loadSplashVerticalAd(Activity context, String adId) {
        ViewGroup mSplashContainer = context.getWindow().getDecorView().findViewById(android.R.id.content);
        try {
            IAdWorker mWorker = AdWorkerFactory.getAdWorker(context, mSplashContainer, mMimoAdListener, com.xiaomi.ad.common.pojo.AdType.AD_SPLASH);

            mWorker.loadAndShow(adId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private IAdWorker mInsterAdWorker;

    private void loadInsterHorizonAd(Activity context, String adId) {
        //在这里,mPlayBtn是作为一个锚点传入的，可以换成任意其他的view，比如getWindow().getDecorView()
        try {
            mInsterAdWorker = AdWorkerFactory.getAdWorker(context, (ViewGroup) context.getWindow().getDecorView(), mInsterAdListener, com.xiaomi.ad.common.pojo.AdType.AD_INTERSTITIAL);

            mInsterAdWorker.load(adId);
            if (!mInsterAdWorker.isReady()) {
                mInsterAdWorker.load(adId);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private MimoAdListener mInsterAdListener = new MimoAdListener() {
        @Override
        public void onAdPresent() {
            // 开屏广告展示
            Log.d(TAG, "onAdPresent");
            mAdCallback.onPresent();
        }

        @Override
        public void onAdClick() {
            //用户点击了开屏广告
            Log.d(TAG, "onAdClick");
            mAdCallback.onClick();
        }

        @Override
        public void onAdDismissed() {
            //这个方法被调用时，表示从开屏广告消失。
            Log.d(TAG, "onAdDismissed");
            mAdCallback.onDismissed();
        }

        @Override
        public void onAdFailed(String s) {
            Log.e(TAG, "ad fail message : " + s);
            Error error = new Error();
            error.setMessage(s);
            mAdCallback.onNoAd(error);
        }

        @Override
        public void onAdLoaded(int size) {
            Log.d(TAG, "onAdLoaded: " + size);
            //do nothing
            try {
                mInsterAdWorker.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onStimulateSuccess() {
        }
    };


    private ViewManager mWindowManager;
    private RelativeLayout mBannerView;

    private void loadBannerAd(Activity activity) {
        if (mBannerView != null && mBannerView.getParent() != null && mWindowManager != null) {
            mWindowManager.removeView(mBannerView);
        }
        mBannerView = new RelativeLayout(activity);

        mBannerView.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

        if (null != mBannerView) {
            /**
             * 这里addView是可以自己指定Banner广告的放置位置【一般是页面顶部或者底部】
             */
//            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.gravity = Gravity.BOTTOM | Gravity.CENTER;
            params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

            mWindowManager = (WindowManager) activity.getSystemService(activity.WINDOW_SERVICE);
            mWindowManager.addView(mBannerView, params);
        }

        try {
            final IAdWorker mBannerAd = AdWorkerFactory.getAdWorker(activity, mBannerView, new MimoAdListener() {
                @Override
                public void onAdPresent() {
                    Log.e(TAG, "loadBannerAd onAdPresent");
                }

                @Override
                public void onAdClick() {
                    Log.e(TAG, "loadBannerAd onAdClick");
                }

                @Override
                public void onAdDismissed() {
                    Log.d(TAG, "loadBannerAd onAdDismissed: ");
                }

                @Override
                public void onAdFailed(String s) {
                    Log.d(TAG, "loadBannerAd onAdFailed: " + s);
                }

                @Override
                public void onAdLoaded(int size) {
                    Log.d(TAG, "loadBannerAd onAdLoaded: " + size);
                }

                @Override
                public void onStimulateSuccess() {
                    Log.d(TAG, "loadBannerAd onStimulateSuccess: ");
                }
            }, com.xiaomi.ad.common.pojo.AdType.AD_BANNER);
            mBannerAd.loadAndShow(mBannerAdId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void hindAd(AdTypeHind type) {

    }
}
