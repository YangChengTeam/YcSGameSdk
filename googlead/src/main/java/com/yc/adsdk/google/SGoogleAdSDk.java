package com.yc.adsdk.google;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.mintegral.msdk.MIntegralSDK;
import com.mintegral.msdk.out.MIntegralSDKFactory;
import com.mintegral.msdk.out.MTGRewardVideoHandler;
import com.mintegral.msdk.out.RewardVideoListener;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.commonsdk.statistics.common.DeviceConfig;
import com.yc.adsdk.core.AdCallback;
import com.yc.adsdk.core.AdType;
import com.yc.adsdk.core.AdTypeHind;
import com.yc.adsdk.core.Error;
import com.yc.adsdk.core.ISGameSDK;
import com.yc.adsdk.core.IUserApiCallback;
import com.yc.adsdk.core.InitAdCallback;
import com.yc.adsdk.core.InitUserCallback;
import com.yc.adsdk.utils.LocalJsonResolutionUtils;
import com.yc.adsdk.utils.PhoneIMEIUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by caokun on 2019/8/19 14:08.
 */

public class SGoogleAdSDk implements ISGameSDK {

    private static final String TAG = "GameSdkLog";
    private static SGoogleAdSDk sAdSDK;
    private AdView mBannerAdAdView;
    private String mBannerAdId;
    private String mVideoAdId;
    private RewardedAd mRewardedAd;
    private AdCallback mAdCallback;
    private String mMintegralRewardedVideoUnitId;
    private String mMintegralAppKey;
    private String mMintegralAppId;
    private String mUmengAppKey;
    private String mUmengChannel;
    private MTGRewardVideoHandler mMTGRewardVideoHandler;
    private Context mContext;
    private RewardedVideoAd mRewardedVideoAdInstance;


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
    public static String[] getTestDeviceInfo(Context context){
        String[] deviceInfo = new String[2];
        try {
            if(context != null){
                deviceInfo[0] = DeviceConfig.getDeviceIdForGeneral(context);
                deviceInfo[1] = DeviceConfig.getMac(context);
            }
        } catch (Exception e){
        }
        return deviceInfo;
    }


    @Override
    public void initAd(final Context context, InitAdCallback adCallback) {
        final Activity activity = (Activity) context;
//        AndroidWorkaround.solveNavigationBar(activity.getWindow());
        if (!initConfig(context)) {
            return;
        }
        // Flurry 统计
        new FlurryAgent.Builder()
                .withLogEnabled(true)
                .build(context, "KSXY35D786WYKM8H7GFM");

        Log.d(TAG, "initAd: mUmengAppKey " + mUmengAppKey + " mUmengChannel " + mUmengChannel);
        //umeng海外版
        UMConfigure.init(context, mUmengAppKey, mUmengChannel, UMConfigure.DEVICE_TYPE_PHONE, null);
        // 选用AUTO页面采集模式
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
        UMConfigure.setLogEnabled(true);


        String[] testDeviceInfo = getTestDeviceInfo(context);
        Log.d(TAG, "initAd: testDeviceInfo[0] "+testDeviceInfo[0]);
        Log.d(TAG, "initAd: testDeviceInfo[1] "+testDeviceInfo[1]);




        Log.d(TAG, "initAd:  " + " mBannerAdId " + mBannerAdId + " mVideoAdId " + mVideoAdId);

        initAllAd(context);

        String url = "http://wsjt.zhuoyi51.com/api/index/apkcount";
        getAsyncToApkcount(url, context);

        new Handler(Looper.myLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                loadAllRewardedVideo(context);
                return false;
            }
        }).sendEmptyMessageDelayed(0, 1100);//表示延迟3秒发送任务

        if (adCallback != null) {
            adCallback.onSuccess();
        }
    }

    private void initAllAd(Context context) {
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

        MIntegralSDK sdk = MIntegralSDKFactory.getMIntegralSDK();
        Map<String, String> map = sdk.getMTGConfigurationMap(mMintegralAppId, mMintegralAppKey);
        Log.d(TAG, "initForMintegral: mMintegralAppId " + mMintegralAppId + " mMintegralAppKey " + mMintegralAppKey + " mMintegralRewardedVideoUnitId " + mMintegralRewardedVideoUnitId);
        sdk.init(map, context);
    }

    private void loadAllRewardedVideo(final Context context) {
        loadMintegralRewardedVideo((Activity) context); //MintegralVideo
        loadGoogleRewardedVideo((Activity) context);  //GoogleVideo
    }

    /**
     * 本地进行统计
     */
    private void getAsyncToApkcount(String url, Context context) {
        //1.创建OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        //2.通过new FormBody()调用build方法,创建一个RequestBody,可以用add添加键值对
        String phoneIMEI = PhoneIMEIUtil.getPhoneIMEI(context);
        RequestBody requestBody = new FormBody.Builder().add("imei", phoneIMEI).add("system", "android" + android.os.Build.VERSION.RELEASE).add("model", android.os.Build.BRAND).build();
        //3.创建Request对象，设置URL地址，将RequestBody作为post方法的参数传入
        Request request = new Request.Builder().url(url).post(requestBody).build();
        //4.创建一个call对象,参数就是Request请求对象
        Call call = okHttpClient.newCall(request);
        Log.d(TAG, "getAsyncToApkcount initAd: url :" + url + " phoneIMEI " + phoneIMEI + " system " + android.os.Build.VERSION.RELEASE + " model " + android.os.Build.BRAND);
        //5.请求加入调度,重写回调方法
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "getAsyncToApkcount onFailure: IOException " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //请求成功
                String string = response.body().string();
                Log.d(TAG, "getAsyncToApkcount onResponse: string " + string);
            }
        });

    }

    @Override
    public void showAd(final Context context, AdType type, AdCallback callback, ViewGroup viewGroup) {
        this.mAdCallback = callback;
        switch (type) {
            case BANNER:
                loadBannerAd((Activity) context);
                this.mContext = context;
                mHandler.postDelayed(r, 40000);//延时100毫秒
                break;
            case VIDEO:
//                loadVideoVerticalAd((Activity) context, true);
//                showVideoVerticalAdForMintegral(context);
                showGoogleRewardedVideo();
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


    private void loadMintegralRewardedVideo(final Context context) {
        mMTGRewardVideoHandler = new MTGRewardVideoHandler(context, mMintegralRewardedVideoUnitId);
        mMTGRewardVideoHandler.setRewardVideoListener(new RewardVideoListener() {

            @Override
            public void onLoadSuccess(String unitId) {
                Log.d(TAG, "onLoadSuccess: unitId " + unitId + " -- " + Thread.currentThread());
            }

            @Override
            public void onVideoLoadSuccess(String unitId) {  //加载视频成功，缓存完成
                Log.d(TAG, "onVideoLoadSuccess: " + Thread.currentThread());
                loadRewardedVideoAd();
            }

            @Override
            public void onVideoLoadFail(String errorMsg) { ////加载视频失败，重新缓存
                Log.d(TAG, "onVideoLoadFail errorMsg:" + errorMsg);
                mMTGRewardVideoHandler.isReady();
//                mMTGRewardVideoHandler.load();
                loadRewardedVideoAd();
            }

            @Override
            public void onShowFail(String errorMsg) {
                Log.d(TAG, "onShowFail=" + errorMsg);
                Error error = new Error();
                error.setMessage(errorMsg);
                mAdCallback.onNoAd(error);
            }

            @Override
            public void onAdShow() {
                Log.d(TAG, "onAdShow");
                mAdCallback.onPresent();
            }

            @Override
            public void onAdClose(boolean isCompleteView, String RewardName, float RewardAmout) {
                mMTGRewardVideoHandler.isReady(); //关闭广告时，缓存新的广告
//                mMTGRewardVideoHandler.load();
                Log.d(TAG, "onAdClose rewardinfo :" + "RewardName:" + RewardName + "RewardAmout:" + RewardAmout + " isCompleteView：" + isCompleteView);
                if (isCompleteView) {
                    mAdCallback.onDismissed();
//                    Toast.makeText(context, "onADClose:" + isCompleteView + ",rName:" + RewardName + "，RewardAmout:" + RewardAmout, Toast.LENGTH_SHORT).show();
//                    showDialog(RewardName, RewardAmout);
                } else {
//                    Toast.makeText(context, "onADClose:" + isCompleteView + ",rName:" + RewardName + "，RewardAmout:" + RewardAmout, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onVideoAdClicked(String unitId) {
                Log.d(TAG, "onVideoAdClicked unitId " + unitId);
                mAdCallback.onClick();
            }

            @Override
            public void onVideoComplete(String unitId) {  //播放结束
                Log.d(TAG, "onVideoComplete unitId " + unitId);
            }

            @Override
            public void onEndcardShow(String unitId) {  //播放结束后展示落地页
                Log.d(TAG, "onEndcardShow unitId " + unitId);
            }
        });
        mMTGRewardVideoHandler.load();
    }

    private boolean initConfig(Context context) {
        String idconfig = LocalJsonResolutionUtils.getJson(context, "googleidconfig.json");
        try {
            JSONObject jsonObject = new JSONObject(idconfig);
            JSONObject data = jsonObject.getJSONObject("data");
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
                    Toast.makeText(context, "初始化失败，缺少广告必须参数 mBannerAdId", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            if (data.has("mintegralAppKey")) {
                mMintegralAppKey = data.getString("mintegralAppKey");
                if (TextUtils.isEmpty(mMintegralAppKey)) {
                    Toast.makeText(context, "初始化失败，缺少广告必须参数 mMintegralAppKey", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            if (data.has("mintegralAppId")) {
                mMintegralAppId = data.getString("mintegralAppId");
                if (TextUtils.isEmpty(mMintegralAppId)) {
                    Toast.makeText(context, "初始化失败，缺少广告必须参数 mMintegralAppId", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            if (data.has("mintegralRewardedVideoUnitId")) {
                mMintegralRewardedVideoUnitId = data.getString("mintegralRewardedVideoUnitId");
                if (TextUtils.isEmpty(mMintegralRewardedVideoUnitId)) {
                    Toast.makeText(context, "初始化失败，缺少广告必须参数 mMintegralRewardedVideoUnitId", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            if (data.has("umengAppKey")) {
                mUmengAppKey = data.getString("umengAppKey");
                if (TextUtils.isEmpty(mUmengAppKey)) {
                    Toast.makeText(context, "初始化失败，缺少广告必须参数 mUmengAppKey", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            if (data.has("umengChannel")) {
                mUmengChannel = data.getString("umengChannel");
                if (TextUtils.isEmpty(mUmengChannel)) {
                    Toast.makeText(context, "初始化失败，缺少广告必须参数 mUmengChannel", Toast.LENGTH_SHORT).show();
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


    private void showVideoVerticalAdForMintegral(Context context) {
        if (mMTGRewardVideoHandler == null) {
            Log.d(TAG, "showVideoVerticalAdForMintegral: mMTGRewardVideoHandler == null");
            return;
        }
        if (mMTGRewardVideoHandler.isReady()) {
            mMTGRewardVideoHandler.show("1", mMintegralRewardedVideoUnitId); //默认情况下将rewardId设置为1
            Log.d(TAG, "showVideoVerticalAdForMintegral:isReady true -- show");
        } else {
            Log.d(TAG, "showVideoVerticalAdForMintegral:isReady false");
//            showVideoVerticalAd(context);
            showGoogleRewardedVideo();
        }
    }

    private void showGoogleRewardedVideo() {
        Log.d(TAG, "showGoogleRewardedVideo: 显示google的广告");
        if (mRewardedVideoAdInstance != null && mRewardedVideoAdInstance.isLoaded()) {
            mRewardedVideoAdInstance.show();
        } else {
            Log.d(TAG, "showGoogleRewardedVideo: google广告 没有准备好");
            if (mMTGRewardVideoHandler == null) {
                Log.d(TAG, "showVideoVerticalAdForMintegral: mMTGRewardVideoHandler == null");
                return;
            }
            if (mMTGRewardVideoHandler.isReady()) {
                mMTGRewardVideoHandler.show("1", mMintegralRewardedVideoUnitId); //默认情况下将rewardId设置为1
            } else {
                Log.d(TAG, "showGoogleRewardedVideo: Mintegral广告 没有准备好");
            }
        }
    }

    private void showVideoVerticalAd(Context context) {
        if (mRewardedAd == null) {
            Log.d(TAG, "showVideoVerticalAd: mRewardedAd==null google广告load失败");
            return;
        }
        if (mRewardedAd.isLoaded()) {

        }
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
                Log.d(TAG, "onUserEarnedReward: type " + type + " amount " + amount);
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
    private void loadGoogleRewardedVideo(final Activity context) {

        mRewardedVideoAdInstance = MobileAds.getRewardedVideoAdInstance(context);
        mRewardedVideoAdInstance.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {
                Log.d(TAG, "onRewardedVideoAdLoaded: ");
            }

            @Override
            public void onRewardedVideoAdOpened() {
                Log.d(TAG, "onRewardedVideoAdOpened: ");
            }

            @Override
            public void onRewardedVideoStarted() {
                Log.d(TAG, "onRewardedVideoStarted: ");
            }

            @Override
            public void onRewardedVideoAdClosed() { //关闭广告
                Log.d(TAG, "onRewardedVideoAdClosed: ");
                // Load the next rewarded video ad.
                loadRewardedVideoAd();
            }

            @Override
            public void onRewarded(RewardItem rewardItem) {
                Log.d(TAG, "onRewarded: ");
                Log.d(TAG, "onUserEarnedReward: rewardItem " + rewardItem);
                String type = rewardItem.getType();
                int amount = rewardItem.getAmount();
                Log.d(TAG, "onUserEarnedReward: type " + type + " amount " + amount);
                mAdCallback.onDismissed();
            }

            @Override
            public void onRewardedVideoAdLeftApplication() {
                Log.d(TAG, "onRewardedVideoAdLeftApplication: ");
            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {
                Log.d(TAG, "onRewardedVideoAdFailedToLoad: " + i);
                loadRewardedVideoAd();
            }

            @Override
            public void onRewardedVideoCompleted() {
                Log.d(TAG, "onRewardedVideoCompleted: ");
            }
        });

        loadRewardedVideoAd();

//        final RewardedAd rewardedAd = new RewardedAd(context,
//                mVideoAdId);
//        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
//            @Override
//            public void onRewardedAdLoaded() {
//                Log.d(TAG, "loadVideoVerticalAd onRewardedAdLoaded:  success");
//                // Ad successfully loaded.
//                SGoogleAdSDk.this.mRewardedAd = rewardedAd;
//            }
//
//            @Override
//            public void onRewardedAdFailedToLoad(int errorCode) {
//                Log.d(TAG, "loadVideoVerticalAd onRewardedAdFailedToLoad: Failed errorCode " + errorCode);
//                // Ad failed to load.
//                Error error = new Error();
//                error.setMessage("视频还没准备好，onRewardedAdFailedToLoad");
//                error.setCode(String.valueOf(errorCode));
//                mAdCallback.onNoAd(error);
//            }
//        };
//        AdRequest.Builder builder = new AdRequest.Builder();
////        builder.addTestDevice("232FB6EC6A8890AE46DF2450BDB68D3F");
//        AdRequest adRequest = builder.build();
//        rewardedAd.loadAd(adRequest, adLoadCallback);
    }

    private void loadRewardedVideoAd() {
        mRewardedVideoAdInstance.loadAd(mVideoAdId,
                new AdRequest.Builder().build());
    }

    private int showBannerNum = 1;

    private Handler mHandler = new Handler();
    Runnable r = new Runnable() {
        @Override
        public void run() {
            showBannerNum++;
            Log.d(TAG, "run: postDelayed  定时器执行");
            if (mContext != null) {
                loadBannerAd((Activity) mContext);
                Log.d(TAG, "run: postDelayed  loadBannerAd 刷新广告");
            }
            //do something
            //每隔1s循环执行run方法
            mHandler.postDelayed(this, 38000);
        }
    };


    private ViewManager mWindowManager;


    private void loadBannerAd(final Activity context) {
        hindBannerAd();
        if (showBannerNum <= 5) {
            mBannerAdAdView = null;
        }

        if (mBannerAdAdView == null) {

            mBannerAdAdView = new AdView(context);
            mBannerAdAdView.setAdSize(AdSize.BANNER);
            mBannerAdAdView.setAdUnitId(mBannerAdId);

            AdRequest.Builder builder = new AdRequest.Builder();
//        builder.addTestDevice("232FB6EC6A8890AE46DF2450BDB68D3F");
            AdRequest adRequest = builder.build();
            Log.d(TAG, "loadBannerAd: adRequest " + adRequest);
            mBannerAdAdView.loadAd(adRequest);
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
                    mBannerAdAdView = null;
                    showBannerNum = 3;
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
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
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
            }
        }, 900);
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
}
