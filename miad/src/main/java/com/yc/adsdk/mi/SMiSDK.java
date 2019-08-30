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
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.miui.zeus.mimo.sdk.MimoSdk;
import com.miui.zeus.mimo.sdk.ad.AdWorkerFactory;
import com.miui.zeus.mimo.sdk.ad.IAdWorker;
import com.miui.zeus.mimo.sdk.ad.IRewardVideoAdWorker;
import com.miui.zeus.mimo.sdk.api.IMimoSdkListener;
import com.miui.zeus.mimo.sdk.listener.MimoAdListener;
import com.miui.zeus.mimo.sdk.listener.MimoRewardVideoListener;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.yc.adsdk.core.AdCallback;
import com.yc.adsdk.core.AdType;
import com.yc.adsdk.core.AdTypeHind;
import com.yc.adsdk.core.Error;
import com.yc.adsdk.core.ISGameSDK;
import com.yc.adsdk.core.IUserApiCallback;
import com.yc.adsdk.core.InitAdCallback;
import com.yc.adsdk.core.InitUserCallback;
import com.yc.adsdk.utils.LocalJsonResolutionUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by caokun on 2019/8/16 17:47.
 */

public class SMiSDK implements ISGameSDK {

    private String TAG = "GameSdkLog_SMiSDK";

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
    private String mUmengAppKey;

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
    public void initAd(Context context, final InitAdCallback adCallback) {
        if (!initConfig(context)) {
            return;
        }

        Log.d(TAG, "initAd: mAppId " + mAppId + " mAppKey " + mAppKey + " mBannerAdId " + mBannerAdId + " mInsterHorizonAdId " + mInsterHorizonAdId
                + " mInsterVerticalAdId " + mInsterVerticalAdId + " mAppToken " + mAppToken + " mSplashHorizonAdId " + mSplashHorizonAdId + " mSplashVerticalAdId " + mSplashVerticalAdId
                + " mVideoHorizonAdId " + mVideoHorizonAdId + " mVideoHorizonAdId " + mVideoHorizonAdId+" mUmengAppKey "+mUmengAppKey);

        UMConfigure.init(context, mUmengAppKey, "Umeng", UMConfigure.DEVICE_TYPE_PHONE, null);
        // 选用AUTO页面采集模式
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);

        Log.d(TAG, "init: MimoSdk.isSdkReady() " + MimoSdk.isSdkReady());
        if (!MimoSdk.isSdkReady()) {
            // 正式上线时候务必关闭stage和debug
            MimoSdk.setDebugOn();
//            MimoSdk.setStageOn();

            // 如需要在本地预置插件,请在assets目录下添加mimo_asset.apk;
            MimoSdk.init(context, mAppId, mAppKey, mAppToken, new IMimoSdkListener() {
                @Override
                public void onSdkInitSuccess() {
                    Log.d(TAG, "onSdkInitSuccess: 小米广告SDK初始化成功 ");
                    adCallback.onSuccess();
                }

                @Override
                public void onSdkInitFailed() {
                    Log.d(TAG, "onSdkInitFailed: 小米广告SDK初始化失败 ");
                    Error error = new Error();
                    error.setMessage("小米广告SDK初始化失败");
                    adCallback.onFailure(error);
                }
            });
        } else {
            Log.d(TAG, "onSdkInitSuccess: isSdkReady true");
            adCallback.onSuccess();
        }
    }

    @Override
    public void initUser(Context context, InitUserCallback userCallback) {
        Log.d(TAG, "initUser: ");
        userCallback.onSuccess();
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
            if (data.has("umengAppKey")) {
                mUmengAppKey = data.getString("umengAppKey");
                if (TextUtils.isEmpty(mUmengAppKey)) {
                    Toast.makeText(context, "初始化失败，缺少广告必须参数 userAppId", Toast.LENGTH_SHORT).show();
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
                Log.d(TAG, "showAd: BANNER");
                loadBannerAd((Activity) context);
                break;
            case SPLASH:
                Log.d(TAG, "showAd: SPLASH");
                loadSplashVerticalAd((Activity) context, mSplashVerticalAdId);
                break;
            case SPLASH_HORIZON:
                Log.d(TAG, "showAd: SPLASH_HORIZON ");
                loadSplashVerticalAd((Activity) context, mSplashHorizonAdId);
                break;
            case VIDEO:
                Log.d(TAG, "showAd: case VIDEO: VIDEO_VERTICAL");
                loadVideoVerticalAd((Activity) context, mVideoVerticalAdId);
                break;
            case VIDEO_HORIZON:
                Log.d(TAG, "showAd: case VIDEO: VIDEO_HORIZON");
                loadVideoVerticalAd((Activity) context, mVideoHorizonAdId);
                break;
            case INSTER:
                Log.d(TAG, "showAd: INSTER");
                loadInsterHorizonAd((Activity) context, mInsterVerticalAdId);
                break;
            case INSTER_HORIZON:
                Log.d(TAG, "showAd: INSTER_HORIZON");
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
            Log.d(TAG, "loadVideoVerticalAd: mVideoAdWorker.isReady() " + mVideoAdWorker.isReady());
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
            Log.d(TAG, "RewardVideoListener onVideoStart " + " onVideoStart status = " + mAdWorker.getVideoStatus());

        }

        @Override
        public void onVideoPause() {
            Log.d(TAG, "RewardVideoListener onVideoPause" + " onVideoStart status = " + mAdWorker.getVideoStatus());
        }

        @Override
        public void onVideoComplete() {
            Log.d(TAG, "RewardVideoListener onVideoComplete" + " onVideoStart status = " + mAdWorker.getVideoStatus());
        }

        @Override
        public void onAdPresent() {
            Log.d(TAG, "RewardVideoListener onAdPresent" + " onVideoStart status = " + mAdWorker.getVideoStatus() + " onAdPresent isReady = " + mAdWorker.isReady());
            mAdCallback.onPresent();
        }

        @Override
        public void onAdClick() {
            Log.d(TAG, "RewardVideoListener onAdClick" + mAdWorker.getVideoStatus() + " onAdPresent isReady = " + mAdWorker.isReady());
            mAdCallback.onClick();
        }

        @Override
        public void onAdDismissed() {
            Log.d(TAG, "RewardVideoListener onAdDismissed" + mAdWorker.getVideoStatus() + " onAdPresent isReady = " + mAdWorker.isReady());
            mAdCallback.onDismissed();
        }

        @Override
        public void onAdFailed(String message) {
            Log.e(TAG, "RewardVideoListener onAdFailed : " + message + " " + mAdWorker.getVideoStatus() + " onAdPresent isReady = " + mAdWorker.isReady());
            Error error = new Error();
            error.setMessage(message);
            mAdCallback.onNoAd(error);
        }

        @Override
        public void onAdLoaded(int size) {
            Log.d(TAG, "RewardVideoListener onAdLoaded : " + size + " " + mAdWorker.getVideoStatus() + " onAdPresent isReady = " + mAdWorker.isReady());
            try {
                mVideoAdWorker.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onStimulateSuccess() {
            Log.d(TAG, "RewardVideoListener onStimulateSuccess" + mAdWorker.getVideoStatus() + " onAdPresent isReady = " + mAdWorker.isReady());
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
            Log.d(TAG, "loadInsterHorizonAd: mInsterAdWorker.isReady() " + mInsterAdWorker.isReady());
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
    private FrameLayout mBannerView;

    private void loadBannerAd(Activity activity) {
        if (mBannerView != null && mBannerView.getParent() != null && mWindowManager != null) {
            mWindowManager.removeView(mBannerView);
        }
        mBannerView = new FrameLayout(activity);

        FrameLayout.LayoutParams layoutParams1 = new FrameLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        mBannerView.setLayoutParams(layoutParams1);

        //此代码是为了显示广告区域，游戏请根据游戏主题背景决定是否要添加
//        mBannerView.setBackgroundColor(activity.getResources().getColor(android.R.color.darker_gray));


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
                    mAdCallback.onPresent();
                }

                @Override
                public void onAdClick() {
                    Log.e(TAG, "loadBannerAd onAdClick");
                    mAdCallback.onClick();
                }

                @Override
                public void onAdDismissed() {
                    Log.d(TAG, "loadBannerAd onAdDismissed: ");
                    mAdCallback.onDismissed();
                }

                @Override
                public void onAdFailed(String s) {
                    Log.d(TAG, "loadBannerAd onAdFailed: " + s);
                    Error error = new Error();
                    error.setMessage(s);
                    mAdCallback.onNoAd(error);
                }

                @Override
                public void onAdLoaded(int size) {
                    Log.d(TAG, "loadBannerAd onAdLoaded: size " + size);
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
        switch (type) {
            case BANNER:
                if (mBannerView != null && mBannerView.getVisibility() != View.GONE) {
                    mBannerView.setVisibility(View.GONE); //// 若需要默认横幅广告不展示
                }
                break;
        }
    }

    @Override
    public void login(Context context, IUserApiCallback iUserApiCallback) {

    }

    @Override
    public void logout(Context context, IUserApiCallback iUserApiCallback) {

    }

     /*APP_KEY和APP_TOKEN则直接传入"fake_app_key","fake_app_token",传空会报错。
    {
      "message": "小米广告SDK参数-测试id",
      "data": {
        "appId": "2882303761517411490",
        "appKey": "appKey",
        "appToken": "appToken",
        "splashVerticalAdId": "b373ee903da0c6fc9c9da202df95a500",
        "splashHorizonAdId": "94f4805a2d50ba6e853340f9035fda18",
        "videoVerticalAdId": "92d90db71791e6b9f7caaf46e4a997ec",
        "videoHorizonAdId": "17853953c5adafd100f24cd747edd6b7",
        "bannerAdId": "802e356f1726f9ff39c69308bfd6f06a",
        "insterVerticalAdId": "67b05e7cc9533510d4b8d9d4d78d0ae9",
        "insterHorizonAdId": "1d576761b7701d436f5a9253e7cf9572"
      },
      "code": "0"
    }*/


    /*
     real
     {
       "message": "小米广告SDK参数-部分正式",
       "data": {
         "appId": "2882303761518131182",
         "appKey": "appKey",
         "appToken": "appToken",
         "splashVerticalAdId": "686225be57c8fed008db8f274d08a387",
         "splashHorizonAdId": "94f4805a2d50ba6e853340f9035fda18",
         "videoVerticalAdId": "56da83da2e1515513641ff30e43c0a4d",
         "videoHorizonAdId": "17853953c5adafd100f24cd747edd6b7",
         "bannerAdId": "eca82947865af6e3d746beec68b9ebf4",
         "insterVerticalAdId": "9e44cb3c76bf41da115864dcc738e98a",
         "insterHorizonAdId": "1d576761b7701d436f5a9253e7cf9572"
       },
       "code": "0"
     }*/


    /*
     跳跃吧邦尼
     "data": {
        "appId": "2882303761518131182",
        "appKey": "appKey",
        "appToken": "appToken",
        "splashVerticalAdId": "686225be57c8fed008db8f274d08a387",
        "splashHorizonAdId": "94f4805a2d50ba6e853340f9035fda18",
        "videoVerticalAdId": "56da83da2e1515513641ff30e43c0a4d",
        "videoHorizonAdId": "17853953c5adafd100f24cd747edd6b7",
        "bannerAdId": "eca82947865af6e3d746beec68b9ebf4",
        "insterVerticalAdId": "9e44cb3c76bf41da115864dcc738e98a",
        "insterHorizonAdId": "1d576761b7701d436f5a9253e7cf9572",
        "umengAppKey": "5d65e98a4ca357f9b8000b6a"
      }
    */

   /*
  {
        "message": "小米广告SDK参数-部分正式-疯狂飞刀之僵尸农场",
            "data": {
      "appId": "2882303761518141674",
      "appKey": "appKey",
      "appToken": "appToken",
      "splashVerticalAdId": "686225be57c8fed008db8f274d08a387",
      "splashHorizonAdId": "94f4805a2d50ba6e853340f9035fda18",
      "videoVerticalAdId": "7b6419fb7345fe748ce80cfd4f357efb",
      "videoHorizonAdId": "17853953c5adafd100f24cd747edd6b7",
      "bannerAdId": "1e96d5ee1374835dbd1c2c9e7f201b00",
      "insterVerticalAdId": "9e44cb3c76bf41da115864dcc738e98a",
      "insterHorizonAdId": "1d576761b7701d436f5a9253e7cf9572",
      "umengAppKey": "5d65e94d4ca357f9b8000b3c"
      },
        "code": "0"
    }*/
/*
    {
        "message": "小米广告SDK参数-部分正式-末日打僵尸",
            "data": {
        "appId": "2882303761518141670",
                "appKey": "appKey",
                "appToken": "appToken",
                "splashVerticalAdId": "686225be57c8fed008db8f274d08a387",
                "splashHorizonAdId": "94f4805a2d50ba6e853340f9035fda18",
                "videoVerticalAdId": "11b3a8ba152fbb16ced1edfee2cf5a5b",
                "videoHorizonAdId": "17853953c5adafd100f24cd747edd6b7",
                "bannerAdId": "9278041c3fe77af66665f863e1f73e4c",
                "insterVerticalAdId": "9e44cb3c76bf41da115864dcc738e98a",
                "insterHorizonAdId": "1d576761b7701d436f5a9253e7cf9572",
                "umengAppKey": "5d65e94d4ca357f9b8000b3c"
    },
        "code": "0"
    }*/


}
