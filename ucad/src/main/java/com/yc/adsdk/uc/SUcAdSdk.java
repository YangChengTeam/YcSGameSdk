package com.yc.adsdk.uc;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.yc.adsdk.core.AdCallback;
import com.yc.adsdk.core.Error;
import com.yc.adsdk.core.AdType;
import com.yc.adsdk.core.AdTypeHind;
import com.yc.adsdk.core.ISGameSDK;
import com.yc.adsdk.core.IUserApiCallback;
import com.yc.adsdk.core.InitAdCallback;
import com.yc.adsdk.core.InitUserCallback;
import com.yc.adsdk.utils.LocalJsonResolutionUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.sirius.nga.NGASDK;
import cn.sirius.nga.NGASDKFactory;
import cn.sirius.nga.properties.NGABannerController;
import cn.sirius.nga.properties.NGABannerListener;
import cn.sirius.nga.properties.NGABannerProperties;
import cn.sirius.nga.properties.NGAInsertController;
import cn.sirius.nga.properties.NGAInsertListener;
import cn.sirius.nga.properties.NGAInsertProperties;
import cn.sirius.nga.properties.NGAVideoController;
import cn.sirius.nga.properties.NGAVideoListener;
import cn.sirius.nga.properties.NGAVideoProperties;
import cn.sirius.nga.properties.NGAWelcomeListener;
import cn.sirius.nga.properties.NGAWelcomeProperties;
import cn.sirius.nga.properties.NGAdController;

import static cn.sirius.nga.NGASDKFactory.getNGASDK;

/**
 * Created by caokun on 2019/7/23 11:45.
 */

public class SUcAdSdk implements ISGameSDK {

    private String TAG = "GameSdkLog";

    private static SUcAdSdk sUcAdSdk;
    private InitAdCallback mSAdSDKInitCallback;
    //    private String mIdAppId;
    private NGASDK mNgasdk;
    private NGABannerController mNgaBannerController;
    private String mBannerPosId;
    private String mInsertPosId;
    private String mVideoPosId;
    private String mWelcomeId;
    private String mIdAppId;
    private Context mVideoContext;


    public static SUcAdSdk getImpl() {
        if (sUcAdSdk == null) {
            synchronized (SUcAdSdk.class) {
                if (sUcAdSdk == null) {
                    sUcAdSdk = new SUcAdSdk();
                }
            }
        }
        return sUcAdSdk;
    }

    private Activity mActivity;
    private Handler mHandler;

    private AdCallback mAdCallback;


    private NGAVideoController mNGAVideoController;

    @Override
    public void initAd(Context context, InitAdCallback adCallback) {
        this.mSAdSDKInitCallback = adCallback;
        this.mActivity = (Activity) context;
        this.mHandler = new Handler(Looper.getMainLooper());
        if (!initConfig(context)) {
            return;
        }
        Log.d(TAG, "init: ids " + " mIdAppId " + mIdAppId + " bannerPosId " + mBannerPosId + " insertPosId " + mInsertPosId + " videoPosId " + mVideoPosId + " welcomeId " + mWelcomeId);

        initAd(mActivity);
    }

    @Override
    public void initUser(Context context, InitUserCallback userCallback) {
        userCallback.onSuccess();
    }

    private boolean initConfig(Context context) {
        String idconfig = LocalJsonResolutionUtils.getJson(context, "ucidconfig.json");
        if (TextUtils.isEmpty(idconfig)) {
            Toast.makeText(context, "初始化失败，idconfig.json文件配置错误", Toast.LENGTH_SHORT).show();
            return false;
        }
        try {
            JSONObject jsonObject = new JSONObject(idconfig);
            JSONObject data = jsonObject.getJSONObject("data");
            if (data.has("appId")) {
                mIdAppId = data.getString("appId");
                if (TextUtils.isEmpty(mIdAppId)) {
                    Toast.makeText(context, "初始化失败，缺少广告必须参数AppId", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            if (data.has("splashAdId")) {
                mWelcomeId = data.getString("splashAdId");
                if (TextUtils.isEmpty(mWelcomeId)) {
                    Toast.makeText(context, "初始化失败，缺少广告必须参数AppId", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            if (data.has("bannerAdId")) {
                mBannerPosId = data.getString("bannerAdId");
                if (TextUtils.isEmpty(mBannerPosId)) {
                    Toast.makeText(context, "初始化失败，缺少广告必须参数bannerAdId", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            if (data.has("videoAdId")) {
                mVideoPosId = data.getString("videoAdId");
                if (TextUtils.isEmpty(mVideoPosId)) {
                    Toast.makeText(context, "初始化失败，缺少广告必须参数videoAdId", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            if (data.has("insterAdId")) {
                mInsertPosId = data.getString("insterAdId");
                if (TextUtils.isEmpty(mInsertPosId)) {
                    Toast.makeText(context, "初始化失败，缺少广告必须参数insterAdId", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }

    private void initAd(Activity activity) {
        NGASDK ngasdk = NGASDKFactory.getNGASDK();
        Map<String, Object> args = new HashMap<>();
        args.put(NGASDK.APP_ID, mIdAppId);
        ngasdk.init(activity, args, mInitCallback);
    }

    private NGASDK.InitCallback mInitCallback = new NGASDK.InitCallback() {  //初始化
        @Override
        public void success() {
            Log.d(TAG, "success: initAd: success");
            Log.d(TAG, "initSGameSDK initAd: success");
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mSAdSDKInitCallback != null) {
                        mSAdSDKInitCallback.onSuccess();
                    }
                }
            });
            loadVideoAd(mActivity);  //预加载视频广告
        }

        @Override
        public void fail(Throwable throwable) {
            Log.d(TAG, "success: initAd: success");
            Log.d(TAG, "initSGameSDK initAd: fail");
            if (mSAdSDKInitCallback != null) {
                Error error = new Error();
                error.setCode(String.valueOf(Error.AD_INIT_ERROR));
                error.setThrowable(throwable);
                mSAdSDKInitCallback.onFailure(error);
            }
        }
    };


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
                this.mVideoContext = context;
//                loadVideoAd((Activity) context);
                showVideoAd();
                break;
            case INSTER:
                loadInsertAd((Activity) context);
                break;
            case SPLASH:
                Activity activity = (Activity) context;
                viewGroup = activity.getWindow().getDecorView().findViewById(android.R.id.content);
                loadSplashAd(activity, viewGroup);
                break;
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

    /**
     * 插屏广告
     * 部分广点通的插屏是只能竖屏的,广点通那边的问题
     */
    private void loadInsertAd(Activity activity) {  //插屏广告
        Log.d(TAG, "loadBannerAd: mIdAppId " + mIdAppId + " mInsertPosId " + mInsertPosId);
        NGAInsertProperties properties = new NGAInsertProperties(activity, mIdAppId, mInsertPosId, null);
        properties.setListener(mInsertAdListener);
        NGASDK ngasdk = getNGASDK();
        ngasdk.loadAd(properties);


    }

    NGAInsertListener mInsertAdListener = new NGAInsertListener() {

        @Override
        public void onShowAd() {
            Log.d(TAG, "InsertAdListener onShowAd:  ");
        }


        @Override
        public void onRequestAd() {
            Log.d(TAG, "InsertAdListener onRequestAd:  ");
        }

        @Override
        public <T extends NGAdController> void onReadyAd(T controller) {
            NGAInsertController ngaInsertController = (NGAInsertController) controller;
            Log.d(TAG, "InsertAdListener onReadyAd:  ngaInsertController " + ngaInsertController);
            if (ngaInsertController != null) {
                ngaInsertController.showAd();
                if (mAdCallback != null) {
                    mAdCallback.onPresent();
                }
            }
        }

        @Override
        public void onCloseAd() {
            if (mAdCallback != null) {
                mAdCallback.onDismissed();
            }
        }

        @Override
        public void onClickAd() {
            if (mAdCallback != null) {
                mAdCallback.onClick();
            }
        }

        @Override
        public void onErrorAd(int code, String message) {
            Log.d(TAG, "InsertAdListener onErrorAd: code " + code + " message " + message);
            if (mAdCallback != null) {
                Error error = new Error();
                error.setCode(String.valueOf(Error.AD_ERROR));
//                error.setCode(String.valueOf(code));
                error.setMessage(message);
                mAdCallback.onNoAd(error);
            }
        }
    };


    private RelativeLayout mBannerView;
    private ViewManager mWindowManager;

    private void loadBannerAd(Activity activity) {
        if (mBannerView != null && mBannerView.getParent() != null) {
            mWindowManager.removeView(mBannerView);
        }
        mBannerView = new RelativeLayout(activity);

        //此代码是为了显示广告区域，游戏请根据游戏主题背景决定是否要添加
        mBannerView.setBackgroundColor(activity.getResources().getColor(android.R.color.darker_gray));

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM | Gravity.CENTER;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mWindowManager = (WindowManager) activity.getSystemService(activity.WINDOW_SERVICE);
        mWindowManager.addView(mBannerView, params);

        Log.d(TAG, "loadBannerAd: mIdAppId " + mIdAppId + " mBannerPosId " + mBannerPosId);

        NGABannerProperties properties = new NGABannerProperties(activity, mIdAppId, mBannerPosId, mBannerView);
        properties.setListener(mBannerAdListener);
        mNgasdk = NGASDKFactory.getNGASDK();
        mNgasdk.loadAd(properties);
    }

    //注意：请在Activity成员变量保存，使用匿名内部类可能导致回收
    NGABannerListener mBannerAdListener = new NGABannerListener() {
        @Override
        public void onRequestAd() {
//            ToastUtil.show(TAG, "onRequestAd");
        }

        @Override
        public <T extends NGAdController> void onReadyAd(T controller) {
            mNgaBannerController = (NGABannerController) controller;
            if (mNgaBannerController != null) {
                mNgaBannerController.showAd();
            }
        }

        @Override
        public void onShowAd() {
            if (mAdCallback != null) {
                mAdCallback.onPresent();
            }
        }

        @Override
        public void onCloseAd() {
            //广告关闭之后mController置null，鼓励加载广告重新调用loadAd，提高广告填充率
            mNgaBannerController = null;
            if (mAdCallback != null) {
                mAdCallback.onDismissed();
            }
        }

        @Override
        public void onErrorAd(int code, String message) {
            if (mAdCallback != null) {
                Error error = new Error();
                error.setCode(String.valueOf(Error.AD_ERROR));
//                error.setCode(String.valueOf(code));
                error.setMessage(message);
                mAdCallback.onNoAd(error);
            }

        }

        @Override
        public void onClickAd() {
//            ToastUtil.show(TAG, "onClickAd");
            if (mAdCallback != null) {
                mAdCallback.onClick();
            }
        }
    };


    private void loadVideoAd(Activity activity) {
        Log.d(TAG, "loadVideoAd: 加载视频广告 mIdAppId " + mIdAppId + " mVideoPosId " + mVideoPosId);
        NGAVideoProperties properties = new NGAVideoProperties(activity, mIdAppId, mVideoPosId);
        properties.setListener(mVideoAdListener);
        NGASDK ngasdk = NGASDKFactory.getNGASDK();
        ngasdk.loadAd(properties);
    }

    public void loadSplashAd(Activity activity, ViewGroup container) {
        Log.d(TAG, "loadBannerAd: mIdAppId " + mIdAppId + " mWelcomeId " + mWelcomeId);
        NGAWelcomeProperties properties = new NGAWelcomeProperties(activity, mIdAppId, mWelcomeId, container);
        // 支持开发者自定义的跳过按钮。SDK要求skipContainer一定在传入后要处于VISIBLE状态，且其宽高都不得小于3x3dp。
        // 如果需要使用SDK默认的跳过按钮，可以选择上面两个构造方法。
        //properties.setSkipView(skipView);
        properties.setListener(mSplashAdListener);
        NGASDK ngasdk = getNGASDK();
        ngasdk.loadAd(properties);
    }

    private void showVideoAd() {
        isLoadAndShowVideo = false;
        if (mNGAVideoController != null) { //播放视频
            mNGAVideoController.showAd();
            if (mAdCallback != null) {
                mAdCallback.onPresent();
            }
        } else {
            loadVideoAd((Activity) mVideoContext);
            isLoadAndShowVideo = true;
        }
    }

    //
    private boolean isLoadAndShowVideo = false;

    NGAVideoListener mVideoAdListener = new NGAVideoListener() {
        @Override
        public void onRequestAd() {  //01 请求
            Log.d(TAG, "VideoAdListener onRequestAd: 请求视频广告");
        }

        @Override
        public <T extends NGAdController> void onReadyAd(T controller) {  //02 准备完成
            Log.d(TAG, "VideoAdListener onReadyAd: 视频广告加载成功");
            mNGAVideoController = (NGAVideoController) controller;
            if (isLoadAndShowVideo) {
                showVideoAd();
            }
        }

        @Override
        public void onShowAd() { // 03 播放
            Log.d(TAG, "VideoAdListener onShowAd: 展示视频广告");
        }

        @Override
        public void onCompletedAd() {  //04 播放完成
        }


        @Override
        public void onCloseAd() {  //05 关闭
            Log.d(TAG, "VideoAdListener onCloseAd: 03");
            if (mAdCallback != null) {
                mAdCallback.onDismissed();
            }
            mNGAVideoController = null;  //清除上一个视频的Controller
            loadVideoAd((Activity) mVideoContext);
        }

        @Override
        public void onClickAd() {
            if (mAdCallback != null) {
                mAdCallback.onClick();
            }
        }

        @Override
        public void onErrorAd(int code, String message) {
            Log.d(TAG, "VideoAdListener onErrorAd: 04" + " ucsdk_code " + code + "  message " + message);
            if (mAdCallback != null) {
                Error adError = new Error();
                adError.setCode(String.valueOf(Error.AD_ERROR));
//                adError.setCode(String.valueOf(code));
                adError.setMessage(message);
                mAdCallback.onNoAd(adError);
            }
//            loadVideoAd((Activity) mVideoContext);
        }
    };

    //注意：请在Activity成员变量保存，使用匿名内部类可能导致回收
    private NGAWelcomeListener mSplashAdListener = new NGAWelcomeListener() {

        @Override
        public void onClickAd() {
            Log.d(TAG, "SplashAdListener onClickAd: 05");
            if (mAdCallback != null) {
                mAdCallback.onClick();
            }
        }

        @Override
        public void onErrorAd(int code, String message) {
            Log.d(TAG, "SplashAdListener onErrorAd: 06" + " code " + code + "  message " + message); //code 8201  message [5004-没有广告]
            if (mAdCallback != null) {
                Error adError = new Error();
                adError.setCode(String.valueOf(Error.AD_ERROR));
//                adError.setCode(String.valueOf(code));
                adError.setMessage(message);
                mAdCallback.onNoAd(adError);
            }
        }

        @Override
        public void onShowAd() {
            Log.d(TAG, "SplashAdListener onShowAd: 03");
            // 广告展示后一定要把预设的开屏图片隐藏起来
            if (mAdCallback != null) {
                mAdCallback.onPresent();
            }
        }

        @Override
        public void onCloseAd() {
            Log.d(TAG, "SplashAdListener onCloseAd: 04");
            //无论成功展示成功或失败都回调用该接口，所以开屏结束后的操作在该回调中实现
            if (mAdCallback != null) {
                mAdCallback.onDismissed();
            }
        }

        @Override
        public <T extends NGAdController> void onReadyAd(T controller) {  //不回调此方法
            Log.d(TAG, "SplashAdListener onReadyAd: 02");
            // 开屏广告是闪屏过程自动显示不需要NGAdController对象，所以返回controller为null；
//            ToastUtil.show(TAG, "onReadyAd");
        }

        @Override
        public void onRequestAd() {
            Log.d(TAG, "SplashAdListener onRequestAd: 01");
//            ToastUtil.show(TAG, "onRequestAd");
        }


        /**
         * 倒计时回调，返回广告还将被展示的剩余时间。
         * 通过这个接口，开发者可以自行决定是否显示倒计时提示，或者还剩几秒的时候显示倒计时
         *
         * @param millisUntilFinished 剩余毫秒数
         */
        @Override
        public void onTimeTickAd(long millisUntilFinished) {
            Log.d(TAG, "SplashAdListener onTimeTickAd: 07 剩余毫秒数 millisUntilFinished " + millisUntilFinished);
        }
    };


}
