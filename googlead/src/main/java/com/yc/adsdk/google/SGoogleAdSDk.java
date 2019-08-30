package com.yc.adsdk.google;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
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

import java.util.Map;

import static com.google.android.gms.ads.AdRequest.DEVICE_ID_EMULATOR;

/**
 * Created by caokun on 2019/8/19 14:08.
 */

public class SGoogleAdSDk implements ISGameSDK {

    private static final String TAG = "GameSdkLog";
    private static SGoogleAdSDk sAdSDK;
    private String mUmengAppKey;
    private AdView mBannerAdAdView;
    private String mBannerAdId;
    private String mVideoAdId;
    private RewardedAd mRewardedAd;
    private AdCallback mAdCallback;

    public static SGoogleAdSDk getImpl() {
        if (sAdSDK == null) {
            synchronized (SGoogleAdSDk.class) {
                if (sAdSDK == null) {
                    sAdSDK = new SGoogleAdSDk();
                }
            }
        }
        return sAdSDK;
    }

    @Override
    public void initAd(final Context context, InitAdCallback adCallback) {
        if (!initConfig(context)) {
            return;
        }

        Log.d(TAG, "initAd: mUmengAppKey "+mUmengAppKey+" mBannerAdId "+mBannerAdId+" mVideoAdId "+mVideoAdId);

        UMConfigure.init(context, mUmengAppKey, "Umeng", UMConfigure.DEVICE_TYPE_PHONE, null);
        // 选用AUTO页面采集模式
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);

        Log.d(TAG, "initAd: onInitializationComplete 555555555555555555555555555555");
        MobileAds.initialize(context, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                /**
                 *  onInitializationComplete: initializationStatus com.google.android.gms.internal.ads.zzafi@720f34
                 *  onInitializationComplete: adapterStatusMap key com.google.android.gms.ads.MobileAds v com.google.android.gms.internal.ads.zzaff@76a815d
                 *  onInitializationComplete: description Timeout. latency 10007
                 */
                Log.d(TAG, "onInitializationComplete: initializationStatus " + initializationStatus);
                Map<String, AdapterStatus> adapterStatusMap = initializationStatus.getAdapterStatusMap();
                for (String key : adapterStatusMap.keySet()
                ) {
                    Log.d(TAG, "onInitializationComplete: adapterStatusMap key " + key + " v " + adapterStatusMap.get(key));
                    AdapterStatus adapterStatus = adapterStatusMap.get(key);
                    String description = adapterStatus.getDescription();
                    int latency = adapterStatus.getLatency();
                    Log.d(TAG, "onInitializationComplete: description " + description + " latency " + latency);
                }
            }
        });
    }

    private boolean initConfig(Context context) {
        String idconfig = LocalJsonResolutionUtils.getJson(context, "googleidconfig.json");
        try {
            JSONObject jsonObject = new JSONObject(idconfig);
            JSONObject data = jsonObject.getJSONObject("data");
            if (data.has("umengAppKey")) {
                mUmengAppKey = data.getString("umengAppKey");
                if (TextUtils.isEmpty(mUmengAppKey)) {
                    Toast.makeText(context, "初始化失败，缺少广告必须参数 userAppId", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            if (data.has("videoAdId")) {
                mVideoAdId = data.getString("videoAdId");
                if (TextUtils.isEmpty(mVideoAdId)) {
                    Toast.makeText(context, "初始化失败，缺少广告必须参数 userAppId", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            if (data.has("bannerAdId")) {
                mBannerAdId = data.getString("bannerAdId");
                if (TextUtils.isEmpty(mBannerAdId)) {
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
        this.mAdCallback = callback;
        switch (type) {
            case BANNER:
                loadBannerAd((Activity) context);
                break;
            case VIDEO:
//                showVideoVerticalAd(context);
                loadVideoVerticalAd((Activity) context, true);
                break;
            case INSTER:
                Log.d(TAG, "SNopAdSDk showAd: 插屏广告展示成功");
                callback.onPresent();
                break;
            case SPLASH:
                Log.d(TAG, "SNopAdSDk showAd: 闪屏广告展示成功");
                callback.onPresent();
                break;
        }
        callback.onPresent();
        Log.d(TAG, "SNopAdSDk  showAd: AdType ： " + type);
    }

    private void showVideoVerticalAd(Context context) {
        RewardedAdCallback adCallback = new RewardedAdCallback() {
            public void onRewardedAdOpened() {  //开始播放
                // Ad opened.
                Log.d(TAG, "onRewardedAdOpened: ");
                mAdCallback.onPresent();
            }

            public void onRewardedAdClosed() {  //视频关闭
                // Ad closed.
                Log.d(TAG, "onRewardedAdClosed: ");
            }

            @Override
            public void onUserEarnedReward(@NonNull com.google.android.gms.ads.rewarded.RewardItem rewardItem) {  //获取奖励
                Log.d(TAG, "onUserEarnedReward: rewardItem " + rewardItem);
                String type = rewardItem.getType();
                int amount = rewardItem.getAmount();
                Log.d(TAG, "onUserEarnedReward: type "+type+" amount "+amount);
                mAdCallback.onDismissed();
            }

            public void onRewardedAdFailedToShow(int errorCode) {
                // Ad failed to display
                Log.d(TAG, "onRewardedAdFailedToShow: errorCode " + errorCode);
                Error error = new Error();
                error.setMessage("初始化视频环境失败");
                error.setCode(String.valueOf(errorCode));
                mAdCallback.onNoAd(error);
            }
        };
        mRewardedAd.show((Activity) context, adCallback);
    }

    /**
     * 初始化成功就提前加载视频广告
     *
     * @param context
     */
    private void loadVideoVerticalAd(final Activity context, final boolean isShow) {
        final RewardedAd rewardedAd = new RewardedAd(context,
                mVideoAdId);
        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                Log.d(TAG, "loadVideoVerticalAd onRewardedAdLoaded:  success");
                // Ad successfully loaded.
                SGoogleAdSDk.this.mRewardedAd = rewardedAd;
                if (isShow) {
                    showVideoVerticalAd(context);
                }
            }

            @Override
            public void onRewardedAdFailedToLoad(int errorCode) {
                Log.d(TAG, "loadVideoVerticalAd onRewardedAdFailedToLoad: Failed errorCode " + errorCode);
                // Ad failed to load.
                Error error = new Error();
                error.setMessage("视频还没准备好，onRewardedAdFailedToLoad");
                error.setCode(String.valueOf(errorCode));
                mAdCallback.onNoAd(error);
            }
        };
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
    }

    private ViewManager mWindowManager;

    private void loadBannerAd(Activity context) {
        mBannerAdAdView = new AdView(context);
        mBannerAdAdView.setAdSize(AdSize.BANNER);
        mBannerAdAdView.setAdUnitId(mBannerAdId);

        AdRequest adRequest = new AdRequest.Builder().build();
        mBannerAdAdView.loadAd(adRequest);

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


        mBannerAdAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {  //展示
                // Code to be executed when an ad finishes loading.
                Log.d(TAG, "onAdLoaded: ");
                mAdCallback.onPresent();
            }

            /**
             *    public static final int ERROR_CODE_INTERNAL_ERROR = 0;
             *     public static final int ERROR_CODE_INVALID_REQUEST = 1;
             *     public static final int ERROR_CODE_NETWORK_ERROR = 2;
             *     public static final int ERROR_CODE_NO_FILL = 3;
             *     public static final int GENDER_UNKNOWN = 0;
             *     public static final int GENDER_MALE = 1;
             *     public static final int GENDER_FEMALE = 2;
             */
            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                Log.d(TAG, "onAdFailedToLoad: " + errorCode);
//                DEVICE_ID_EMULATOR
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Log.d(TAG, "onAdOpened: ");
                mAdCallback.onClick();
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                Log.d(TAG, "onAdClicked: ");
                mAdCallback.onClick();
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                Log.d(TAG, "onAdLeftApplication: ");
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
                Log.d(TAG, "onAdClosed: ");
            }
        });
    }

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

    @Override
    public void login(Context context, IUserApiCallback iUserApiCallback) {
        iUserApiCallback.onSuccess("登录成功");
    }

    @Override
    public void logout(Context context, IUserApiCallback iUserApiCallback) {
        iUserApiCallback.onSuccess("退出成功");
    }

    /*{
        "message": "google广告SDK参数",
            "data": {
        "umengAppKey": "5d65e98a4ca357f9b8000b6a",
                "videoAdId": "ca-app-pub-5223248037916302/6885409760",
                "bannerAdId": "ca-app-pub-5223248037916302/5608481958"
    },
        "code": "0"
    }*/

    /*{
        "message": "google广告SDK参数-测试",
            "data": {
        "umengAppKey": "5d65e98a4ca357f9b8000b6a",
                "videoAdId": "ca-app-pub-3940256099942544/5224354917",
                "bannerAdId": "ca-app-pub-3940256099942544/6300978111"
    },
        "code": "0"
    }*/
}
