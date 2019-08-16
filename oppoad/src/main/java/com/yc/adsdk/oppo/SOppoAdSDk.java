package com.yc.adsdk.oppo;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.WindowManager;
import android.widget.Toast;

import com.oppo.mobad.api.InitParams;
import com.oppo.mobad.api.MobAdManager;
import com.oppo.mobad.api.ad.BannerAd;
import com.oppo.mobad.api.ad.InterstitialAd;
import com.oppo.mobad.api.ad.RewardVideoAd;
import com.oppo.mobad.api.ad.SplashAd;
import com.oppo.mobad.api.listener.IBannerAdListener;
import com.oppo.mobad.api.listener.IInitListener;
import com.oppo.mobad.api.listener.IInterstitialAdListener;
import com.oppo.mobad.api.listener.IRewardVideoAdListener;
import com.oppo.mobad.api.listener.ISplashAdListener;
import com.oppo.mobad.api.params.RewardVideoAdParams;
import com.oppo.mobad.api.params.SplashAdParams;
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
 * Created by caokun on 2019/8/12 19:45.
 */

public class SOppoAdSDk implements ISGameSDK {

    private static final String TAG = "GameSdkLog";
    private static SOppoAdSDk sAdSDK;
    private AdCallback mAdCallback;
//    private Context context;
    private RewardVideoAd mRewardVideoAd;
    private InterstitialAd mInterstitialAd;
    private String mAppId;
    private String mSplashAdId;
    private String mBannerAdId;
    private String mVideoAdId;
    private String mInsterAdId;


    public static SOppoAdSDk getImpl() {
        if (sAdSDK == null) {
            synchronized (SOppoAdSDk.class) {
                if (sAdSDK == null) {
                    sAdSDK = new SOppoAdSDk();
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
        Log.d(TAG, "init: mAppId " + mAppId + "  mSplashAdId :" + mSplashAdId + "  mBannerAdId :" + mBannerAdId + "  mVideoAdId :" + mVideoAdId + "  mInsterAdId :" + mInsterAdId);

        boolean supportedMobile = MobAdManager.getInstance().isSupportedMobile();
        if (!supportedMobile) {
            Toast.makeText(context, "当前手机机型不支持Oppo联盟SDK", Toast.LENGTH_SHORT).show();
            return;
        }

        InitParams initParams = new InitParams.Builder()
                .setDebug(true)//true打开SDK日志，当应用发布Release版本时，必须注释掉这行代码的调用，或者设为false
                .build();
        /**
         * 调用这行代码初始化广告SDK
         */
        MobAdManager.getInstance().init(context, mAppId, initParams, new IInitListener() {
            @Override
            public void onSuccess() {
                if (callback != null) {
                    callback.onSuccess();
                }
            }

            @Override
            public void onFailed(String s) {
                if (callback != null) {
                    Error error = new Error();
                    error.setMessage(s);
                    callback.onFailure(error);
                }
            }
        });
    }

    private boolean initConfig(Context context) {
        String idconfig = LocalJsonResolutionUtils.getJson(context, "oppoidconfig.json");
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
            if (data.has("splashAdId")) {
                mSplashAdId = data.getString("splashAdId");
                if (TextUtils.isEmpty(mSplashAdId)) {
                    Toast.makeText(context, "初始化失败，缺少广告必须参数AppId", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            if (data.has("bannerAdId")) {
                mBannerAdId = data.getString("bannerAdId");
                if (TextUtils.isEmpty(mBannerAdId)) {
                    Toast.makeText(context, "初始化失败，缺少广告必须参数bannerAdId", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            if (data.has("videoAdId")) {
                mVideoAdId = data.getString("videoAdId");
                if (TextUtils.isEmpty(mVideoAdId)) {
                    Toast.makeText(context, "初始化失败，缺少广告必须参数videoAdId", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            if (data.has("insterAdId")) {
                mInsterAdId = data.getString("insterAdId");
                if (TextUtils.isEmpty(mInsterAdId)) {
                    Toast.makeText(context, "初始化失败，缺少广告必须参数insterAdId", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void showAd(Context context, AdType type, AdCallback callback) {
        this.showAd(context, type, callback, null);
    }

    @Override
    public void showAd(Context context, AdType type, AdCallback callback, ViewGroup viewGroup) {
        this.mAdCallback = callback;
        switch (type) {
            case BANNER:
                loadBannerAd((Activity) context);
                break;
            case VIDEO:
                loadVideoAd((Activity) context);
                break;
            case INSTER:
                loadInsertAd((Activity) context);
                break;
            case SPLASH:
                Activity activity = (Activity) context;
                ViewGroup splashView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
                fetchSplashAd(context, splashView);
                break;
        }
    }

    private void loadInsertAd(Activity context) {
        /**
         * 构造 InterstitialAd.
         */
        mInterstitialAd = new InterstitialAd(context, mInsterAdId);
        /**
         * 设置插屏广告行为监听器.
         */
        mInterstitialAd.setAdListener(insertAdListener);
        /**
         * 调用 loadAd() 方法请求广告.
         */
        mInterstitialAd.loadAd();
    }

    private IInterstitialAdListener insertAdListener = new IInterstitialAdListener() {

        @Override
        public void onAdReady() {
            /**
             *请求广告成功
             */
            Log.d(TAG, "IInterstitialAdListener onAdReady");
            /**
             *  调用sowAd方法展示插屏广告
             *  注意：只有请求广告回调onAdReady以后，调用loadAd方法才会展示广告，如果是onAdFailed，则即使调用了showAd，也不会展示广告
             *
             */
            if (mInterstitialAd != null) {
                mInterstitialAd.showAd();
            }
        }

        @Override
        public void onAdShow() {
            /**
             *广告展示
             */
            Log.d(TAG, "IInterstitialAdListener onAdShow");
            mAdCallback.onPresent();
        }

        @Override
        public void onAdFailed(String errMsg) {
            /**
             *请求广告失败
             */
            Log.d(TAG, "IInterstitialAdListener onAdFailed:errMsg=" + (null != errMsg ? errMsg : ""));
            Error error = new Error();
            error.setMessage(errMsg);
            mAdCallback.onNoAd(error);
        }


        @Override
        public void onAdClick() {
            /**
             *广告被点击
             */
            Log.d(TAG, "IInterstitialAdListener onAdClick");
            mAdCallback.onClick();
        }

        @Override
        public void onAdClose() {
            /**
             *广告被关闭
             */
            Log.d(TAG, "IInterstitialAdListener onAdClose");
            mAdCallback.onDismissed();
        }
    };

    private void loadVideoAd(Activity context) {
        mRewardVideoAd = new RewardVideoAd(context, mVideoAdId, rewardVideoAdListener);

        Log.d(TAG, "loadVideoAd: mRewardVideoAd " + mRewardVideoAd);

        /**
         * 调用loadAd方法请求激励视频广告;通过RewardVideoAdParams.setFetchTimeout方法可以设置请求
         * 视频广告最大超时时间，单位毫秒；
         */
        RewardVideoAdParams rewardVideoAdParams = new RewardVideoAdParams.Builder()
                .setFetchTimeout(FETCH_TIME_OUT)
                .build();
        mRewardVideoAd.loadAd(rewardVideoAdParams);
    }

    private void playVideo() {
        /**
         *  在播放广告时候，先调用isReady方法判断当前是否有广告可以播放；如果有、再调用showAd方法播放激励视频广告。
         */
        if (mRewardVideoAd != null && mRewardVideoAd.isReady()) {
            mRewardVideoAd.showAd();
//            printStatusMsg("播放视频广告.");
        }
    }

    private IRewardVideoAdListener rewardVideoAdListener = new IRewardVideoAdListener() {
        @Override
        public void onAdSuccess() { //1.视频准备好
            Log.d(TAG, "IRewardVideoAdListener onAdSuccess: ");
            playVideo();
        }

        @Override
        public void onAdFailed(String s) {
            Log.d(TAG, "IRewardVideoAdListener onAdFailed: " + s);
            Error error = new Error();
            error.setMessage(s);
            mAdCallback.onNoAd(error);
        }

        @Override
        public void onAdClick(long l) {  //9.点击广告
            Log.d(TAG, "IRewardVideoAdListener onAdClick: " + l);
            mAdCallback.onClick();
        }

        @Override
        public void onVideoPlayStart() {  //2.视频开始播放
            Log.d(TAG, "IRewardVideoAdListener onVideoPlayStart: ");
            mAdCallback.onPresent();
        }

        @Override
        public void onVideoPlayComplete() {  //3.倒计时结束，播放完成，到应用下载页
            Log.d(TAG, "IRewardVideoAdListener onVideoPlayComplete: ");
        }

        @Override
        public void onVideoPlayError(String s) {
            Log.d(TAG, "IRewardVideoAdListener onVideoPlayError: " + s);
            Error error = new Error();
            error.setMessage(s);
            mAdCallback.onNoAd(error);
        }

        @Override
        public void onVideoPlayClose(long l) {  //9.手动跳过
            Log.d(TAG, "IRewardVideoAdListener onVideoPlayClose: -------- " + l);
        }

        @Override
        public void onLandingPageOpen() { //4.打开应用安装页
            Log.d(TAG, "IRewardVideoAdListener onLandingPageOpen: ");
        }

        @Override
        public void onLandingPageClose() {  //5.关闭应用安装页
            Log.d(TAG, "IRewardVideoAdListener onLandingPageClose: ");
        }

        /**
         * 给用户发放奖励
         * 注意：只能在收到onReward回调的时候才能给予用户奖励。
         */

        @Override
        public void onReward(Object... objects) {
            Log.d(TAG, "IRewardVideoAdListener onReward: ");
            mAdCallback.onDismissed();
        }
    };

    @Override
    public void hindAd(AdTypeHind type) {
        switch (type) {
            case BANNER:
                hindBannerAd();
                break;
        }
    }

    private void hindBannerAd() {
        if (mWindowManager != null && mBannerAdAdView != null && mBannerAdAdView.getParent() != null) {
            mWindowManager.removeView(mBannerAdAdView);
        }
    }

    private ViewManager mWindowManager;
    private View mBannerAdAdView;

    private void loadBannerAd(Activity context) {
        /**
         * 构造 bannerAd
         */
        BannerAd mBannerAd = new BannerAd(context, mBannerAdId);
        /**
         * 设置Banner广告行为监听器
         */
        mBannerAd.setAdListener(bannerAdmListener);
        /**
         * 获取Banner广告View，将View添加到你的页面上去
         *
         */
        hindBannerAd();  //为防止广告重复展示，加载前先清空
        mBannerAdAdView = mBannerAd.getAdView();
        /**
         * mBannerAd.getAdView()返回可能为空，判断后在添加
         */
        if (null != mBannerAdAdView) {
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

            mWindowManager = (WindowManager) context.getSystemService(context.WINDOW_SERVICE);
            mWindowManager.addView(mBannerAdAdView, params);
        }
        /**
         * 调用loadAd()方法请求广告.
         */
        mBannerAd.loadAd();
    }


    private IBannerAdListener bannerAdmListener = new IBannerAdListener() {
        @Override
        public void onAdReady() {
            Log.d(TAG, "BannerAd onAdReady: ");
        }

        @Override
        public void onAdClose() {
            Log.d(TAG, "BannerAd onAdClose: ");
            mAdCallback.onDismissed();
        }


        @Override
        public void onAdShow() {
            Log.d(TAG, "BannerAd onAdShow: ");
            mAdCallback.onPresent();
        }

        @Override
        public void onAdFailed(String s) {
            Log.d(TAG, "BannerAd onAdFailed: " + s);
            Error error = new Error();
            error.setMessage(s);
            mAdCallback.onNoAd(error);
        }

        @Override
        public void onAdClick() {
            Log.d(TAG, "BannerAd onAdClick: ");
            mAdCallback.onClick();
        }
    };

    /**
     * 从请求广告到广告展示出来最大耗时时间，只能在[3000,5000]ms之内。
     */
    private static final int FETCH_TIME_OUT = 3000;

    /**
     * 闪屏广告是半屏广告，广告下面半屏是: 应用ICON+应用标题+应用描述，
     * 应用标题和应用描述由应用在SplashAd构造函数里传入，
     * 应用标题限制最多不超过个 8 个汉字，应用描述限制不超过 13 个汉字。
     */
    private static final String APP_TITLE = "半屏广告头部";
    private static final String APP_DESC = "半屏广告描述";

    private void fetchSplashAd(Context context, ViewGroup splashView) {
        try {
            /**
             * SplashAd初始化参数、这里可以设置获取广告最大超时时间，
             * setShowPreLoadPage方法可以设置是否启用SDK默认的等待页面；false不启用；true启用；
             * setBottomArea方法用来设置自定义的广告底部LOGO区域视图
             */
//            LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            View bottomArea = inflate.inflate(R.layout.splash_bottom_area, null);
            //
            SplashAdParams splashAdParams = new SplashAdParams.Builder()
                    .setFetchTimeout(FETCH_TIME_OUT)
                    .setShowPreLoadPage(false)
//                    .setDesc(APP_DESC)
//                    .setTitle(APP_TITLE)
//                    .setBottomArea(null)
                    .build();
            /**
             * 构造SplashAd对象
             * 注意：构造函数传入的几个形参都不能为空，否则将抛出NullPointerException异常。
             */

            SplashAd mSplashAd = new SplashAd((Activity) context, mSplashAdId, plashAdListener, splashAdParams);
        } catch (Exception e) {
            Error error = new Error();
            error.setMessage("SplashAd Exception : " + e.toString());
            mAdCallback.onNoAd(error);
            Log.d(TAG, "", e);
        }
    }


    private ISplashAdListener plashAdListener = new ISplashAdListener() {

        private boolean mIsClickSplashAd;

        @Override
        public void onAdDismissed() {
            Log.d(TAG, "SplashAd onAdDismissed: ");
            if (!mIsClickSplashAd) {
                mAdCallback.onDismissed();
            } else {
                mIsClickSplashAd = false;
            }
        }

        @Override
        public void onAdShow() {
            Log.d(TAG, "SplashAd onAdShow: ");
            mAdCallback.onPresent();
        }

        @Override
        public void onAdFailed(String s) {
            Log.d(TAG, "SplashAd onAdFailed: " + s);
            Error error = new Error();
            error.setMessage(s);
            mAdCallback.onNoAd(error);
        }

        @Override
        public void onAdClick() {
            Log.d(TAG, "SplashAd onAdClick: ");
            mAdCallback.onClick();
            mIsClickSplashAd = true;
        }
    };
}
