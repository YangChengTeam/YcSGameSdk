package com.yc.adsdk.tt;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTBannerAd;
import com.bytedance.sdk.openadsdk.TTDrawFeedAd;
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd;
import com.bytedance.sdk.openadsdk.TTImage;
import com.bytedance.sdk.openadsdk.TTInteractionAd;
import com.bytedance.sdk.openadsdk.TTNativeAd;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.bytedance.sdk.openadsdk.TTSplashAd;
import com.yc.adsdk.R;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mayn on 2019/7/16.
 */

public class STtAdSDk implements ISGameSDK {

    private static final String TAG = "GameSdkLog";

    private static STtAdSDk sTtAdSDk;
    private TTAdNative mTTAdNative;
    private ViewGroup mSplashContainer;

    //开屏广告加载超时时间,建议大于3000,这里为了冷启动第一次加载到广告并且展示,示例设置了3000ms
    private static final int AD_TIME_OUT = 3000;
    private static final int MSG_GO_MAIN = 1;

    //开屏广告是否已经加载
    private boolean mHasLoaded;

    //开屏广告加载发生超时但是SDK没有及时回调结果的时候，做的一层保护。

    private AdCallback mAdCallback;
    private Context mShowAdContext;
    private String mAppId;
    private String mAppName;
    private String mBannerNative;
    private String mBannerDownload;
    private String mBanner;
    private String mVideoNative;
    private String mVideoVertical;
    private String mVideoRewardHorizontal;
    private String mVideoReward;
    private String mVideoHorizontal;
    private String mSplash;
    private String mInsterNormal;
    private String mInsterDownload;


    public static STtAdSDk getImpl() {
        if (sTtAdSDk == null) {
            synchronized (STtAdSDk.class) {
                if (sTtAdSDk == null) {
                    sTtAdSDk = new STtAdSDk();
                }
            }
        }
        return sTtAdSDk;
    }




    @Override
    public void initAd(Context context, InitAdCallback adCallback) {
        if (!initConfig(context)) {
            return;
        }
        TTAdManagerHolder.init(context, mAppId, mAppName,adCallback);
    }

    @Override
    public void initUser(Context context, InitUserCallback userCallback) {

    }

    @Override
    public void showAd(Context context, AdType type, AdCallback callback) {
        this.showAd(context, type, callback, null);
    }

    @Override
    public void showAd(Context context, AdType type, AdCallback callback, ViewGroup viewGroup) {
        mTTAdNative = TTAdManagerHolder.get().createAdNative(context);
        mAdCallback = callback;
        mShowAdContext = context;
        Activity activity;
        switch (type) {
            case BANNER:
                activity = (Activity) context;
                mSplashContainer = activity.getWindow().getDecorView().findViewById(android.R.id.content);
                loadBannerAd(mBanner);
                break;
            case BANNER_DOWNLOAD:
                activity = (Activity) context;
                mSplashContainer = activity.getWindow().getDecorView().findViewById(android.R.id.content);
                loadBannerAd(mBannerDownload);
                break;
            case BANNER_NATIVE:
                activity = (Activity) context;
                mSplashContainer = activity.getWindow().getDecorView().findViewById(android.R.id.content);
                loadNativeBannerAd(mBannerNative);
                break;
            case VIDEO_NATIVE:
                activity = (Activity) context;
                mSplashContainer = activity.getWindow().getDecorView().findViewById(android.R.id.content);
                loadNativeAd(mVideoNative);
                break;
            case VIDEO_REWARD:
                loadRewardVideoAd(mVideoReward, TTAdConstant.VERTICAL);
                break;
            case VIDEO_REWARD_HORIZON:
                loadRewardVideoAd(mVideoRewardHorizontal, TTAdConstant.HORIZONTAL);
                break;
            case VIDEO:
                loadVideoAd(mVideoVertical, TTAdConstant.VERTICAL);
                break;
            case VIDEO_HORIZON:
                loadVideoAd(mVideoHorizontal, TTAdConstant.HORIZONTAL);
                break;
            case INSTER_DOWNLOAD:
                loadInteractionAd(mInsterDownload);
            case INSTER:
                loadInteractionAd(mInsterNormal);
                break;
            case SPLASH:
                //定时，AD_TIME_OUT时间到时执行，如果开屏广告没有加载则跳转到主页面
                mHandler.sendEmptyMessageDelayed(MSG_GO_MAIN, AD_TIME_OUT);
                if (viewGroup != null) {
                    mSplashContainer = viewGroup;
                } else {
                    activity = (Activity) context;
                    mSplashContainer = activity.getWindow().getDecorView().findViewById(android.R.id.content);
                }
                loadSplashAd(mSplash);  // 加载开屏广告
                break;
        }
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

    private boolean initConfig(Context context) {
        String idconfig = LocalJsonResolutionUtils.getJson(context, "ttidconfig.json");
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
            if (data.has("appName")) {
                mAppName = data.getString("appName");
                if (TextUtils.isEmpty(mAppName)) {
                    Toast.makeText(context, "初始化失败，缺少广告必须参数AppId", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            if (data.has("bannerNative")) {
                mBannerNative = data.getString("bannerNative");
                if (TextUtils.isEmpty(mBannerNative)) {
                    Toast.makeText(context, "初始化失败，缺少广告必须参数bannerAdId", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            if (data.has("bannerDownload")) {
                mBannerDownload = data.getString("bannerDownload");
                if (TextUtils.isEmpty(mBannerDownload)) {
                    Toast.makeText(context, "初始化失败，缺少广告必须参数videoAdId", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            if (data.has("banner")) {
                mBanner = data.getString("banner");
                if (TextUtils.isEmpty(mBanner)) {
                    Toast.makeText(context, "初始化失败，缺少广告必须参数banner", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            if (data.has("videoNative")) {
                mVideoNative = data.getString("videoNative");
                if (TextUtils.isEmpty(mVideoNative)) {
                    Toast.makeText(context, "初始化失败，缺少广告必须参数videoNative", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            if (data.has("videoVertical")) {
                mVideoVertical = data.getString("videoVertical");
                if (TextUtils.isEmpty(mVideoVertical)) {
                    Toast.makeText(context, "初始化失败，缺少广告必须参数videoVertical", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            if (data.has("videoRewardHorizontal")) {
                mVideoRewardHorizontal = data.getString("videoRewardHorizontal");
                if (TextUtils.isEmpty(mVideoRewardHorizontal)) {
                    Toast.makeText(context, "初始化失败，缺少广告必须参数videoRewardHorizontal", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            if (data.has("videoReward")) {
                mVideoReward = data.getString("videoReward");
                if (TextUtils.isEmpty(mVideoReward)) {
                    Toast.makeText(context, "初始化失败，缺少广告必须参数videoReward", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            if (data.has("videoHorizontal")) {
                mVideoHorizontal = data.getString("videoHorizontal");
                if (TextUtils.isEmpty(mVideoHorizontal)) {
                    Toast.makeText(context, "初始化失败，缺少广告必须参数videoHorizontal", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            if (data.has("splash")) {
                mSplash = data.getString("splash");
                if (TextUtils.isEmpty(mSplash)) {
                    Toast.makeText(context, "初始化失败，缺少广告必须参数splash", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            if (data.has("insterNormal")) {
                mInsterNormal = data.getString("insterNormal");
                if (TextUtils.isEmpty(mInsterNormal)) {
                    Toast.makeText(context, "初始化失败，缺少广告必须参数videoHorizontal", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            if (data.has("insterDownload")) {
                mInsterDownload = data.getString("insterDownload");
                if (TextUtils.isEmpty(mInsterDownload)) {
                    Toast.makeText(context, "初始化失败，缺少广告必须参数insterDownload", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }


    private Button mCreativeButton;

    /**
     * 加载原生Banner广告
     */
    @SuppressWarnings({"ALL", "SameParameterValue"})
    private void loadNativeBannerAd(String codeId) {
        //step4:创建广告请求参数AdSlot,注意其中的setNativeAdtype方法，具体参数含义参考文档
        final AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(codeId)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(600, 257)
                .setNativeAdType(AdSlot.TYPE_BANNER) //请求原生广告时候，请务必调用该方法，设置参数为TYPE_BANNER或TYPE_INTERACTION_AD
                .setAdCount(1)
                .build();

        //step5:请求广告，对请求回调的广告作渲染处理
        mTTAdNative.loadNativeAd(adSlot, new TTAdNative.NativeAdListener() {
            @Override
            public void onError(int code, String message) {
//                showToast("原生Banner " + "load error : " + code + ", " + message);
                Error error = new Error();
                error.setTripartiteCode(code);
                error.setMessage(message);
                mAdCallback.onNoAd(error);
            }

            @Override
            public void onNativeAdLoad(List<TTNativeAd> ads) {
                if (ads.get(0) == null) {
                    Error error = new Error();
                    error.setCode(String.valueOf(Error.AD_ERROR));
                    error.setMessage("没有广告");
                    mAdCallback.onNoAd(error);
                    return;
                }
                View bannerView = LayoutInflater.from(mShowAdContext).inflate(R.layout.native_ad, mSplashContainer, false);
                if (bannerView == null) {
                    Error error = new Error();
                    error.setCode(String.valueOf(Error.AD_ERROR));
                    error.setMessage("没有广告");
                    mAdCallback.onNoAd(error);
                    return;
                }
                if (mCreativeButton != null) {
                    //防止内存泄漏
                    mCreativeButton = null;
                }
                mSplashContainer.removeAllViews();
                mSplashContainer.addView(bannerView);
                //绑定原生广告的数据
                setAdData(bannerView, ads.get(0));
            }
        });
    }

    @SuppressWarnings("RedundantCast")
    private void setAdData(View nativeView, TTNativeAd nativeAd) {
        ((TextView) nativeView.findViewById(R.id.tv_native_ad_title)).setText(nativeAd.getTitle());
        ((TextView) nativeView.findViewById(R.id.tv_native_ad_desc)).setText(nativeAd.getDescription());
        ImageView imgDislike = nativeView.findViewById(R.id.img_native_dislike);
//        bindDislikeAction(nativeAd, imgDislike);
        if (nativeAd.getImageList() != null && !nativeAd.getImageList().isEmpty()) {
            TTImage image = nativeAd.getImageList().get(0);
            if (image != null && image.isValid()) {
                ImageView im = nativeView.findViewById(R.id.iv_native_image);
//                Glide.with(mShowAdContext).load(image.getImageUrl()).into(im);
            }
        }
        TTImage icon = nativeAd.getIcon();
        if (icon != null && icon.isValid()) {
            ImageView im = nativeView.findViewById(R.id.iv_native_icon);
//            Glide.with(mShowAdContext).load(icon.getImageUrl()).into(im);
        }
        mCreativeButton = (Button) nativeView.findViewById(R.id.btn_native_creative);
        //可根据广告类型，为交互区域设置不同提示信息
        switch (nativeAd.getInteractionType()) {
            case TTAdConstant.INTERACTION_TYPE_DOWNLOAD:
                //如果初始化ttAdManager.createAdNative(getApplicationContext())没有传入activity 则需要在此传activity，否则影响使用Dislike逻辑
                nativeAd.setActivityForDownloadApp((Activity) mShowAdContext);
                mCreativeButton.setVisibility(View.VISIBLE);
                nativeAd.setDownloadListener(mDownloadListener); // 注册下载监听器
                break;
            case TTAdConstant.INTERACTION_TYPE_DIAL:
                mCreativeButton.setVisibility(View.VISIBLE);
                mCreativeButton.setText("立即拨打");
                break;
            case TTAdConstant.INTERACTION_TYPE_LANDING_PAGE:
            case TTAdConstant.INTERACTION_TYPE_BROWSER:
                mCreativeButton.setVisibility(View.VISIBLE);
                mCreativeButton.setText("查看详情");
                break;
            default:
                mCreativeButton.setVisibility(View.GONE);
//                showToast("原生Banner " + "交互类型异常");
        }

        //可以被点击的view, 也可以把nativeView放进来意味整个广告区域可被点击
        List<View> clickViewList = new ArrayList<>();
        clickViewList.add(nativeView);

        //触发创意广告的view（点击下载或拨打电话）
        List<View> creativeViewList = new ArrayList<>();
        //如果需要点击图文区域也能进行下载或者拨打电话动作，请将图文区域的view传入
        //creativeViewList.add(nativeView);
        creativeViewList.add(mCreativeButton);

        //重要! 这个涉及到广告计费，必须正确调用。convertView必须使用ViewGroup。
        nativeAd.registerViewForInteraction((ViewGroup) nativeView, clickViewList, creativeViewList, imgDislike, new TTNativeAd.AdInteractionListener() {
            @Override
            public void onAdClicked(View view, TTNativeAd ad) {
                if (ad != null) {
                    mAdCallback.onClick();
//                    showToast("原生Banner " + "广告" + ad.getTitle() + "被点击");
                }
            }

            @Override
            public void onAdCreativeClick(View view, TTNativeAd ad) {
                if (ad != null) {
                    mAdCallback.onClick();
//                    showToast("原生Banner " + "广告" + ad.getTitle() + "被创意按钮被点击");
                }
            }

            @Override
            public void onAdShow(TTNativeAd ad) {
                if (ad != null) {
                    mAdCallback.onPresent();
//                    showToast("原生Banner " + "广告" + ad.getTitle() + "展示");
                }
            }
        });
    }

    //接入网盟的dislike 逻辑，有助于提示广告精准投放度
//    private void bindDislikeAction(TTNativeAd ad, View dislikeView) {
//        final TTAdDislike ttAdDislike = ad.getDislikeDialog((Activity) mShowAdContext);
//        if (ttAdDislike != null) {
//            ttAdDislike.setDislikeInteractionCallback(new TTAdDislike.DislikeInteractionCallback() {
//                @Override
//                public void onSelected(int position, String value) {
//                    mSplashContainer.removeAllViews();
//                }
//
//                @Override
//                public void onCancel() {
//
//                }
//            });
//        }
//        dislikeView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (ttAdDislike != null)
//                    ttAdDislike.showDislikeDialog();
//            }
//        });
//    }

    private final TTAppDownloadListener mDownloadListener = new TTAppDownloadListener() {
        @Override
        public void onIdle() {
            if (mCreativeButton != null) {
                mCreativeButton.setText("开始下载");
            }
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
            if (mCreativeButton != null) {
                if (totalBytes <= 0L) {
                    mCreativeButton.setText("下载中 percent: 0");
                } else {
                    mCreativeButton.setText("下载中 percent: " + (currBytes * 100 / totalBytes));
                }
            }
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
            if (mCreativeButton != null) {
                mCreativeButton.setText("下载暂停 percent: " + (currBytes * 100 / totalBytes));
            }
        }

        @Override
        public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
            if (mCreativeButton != null) {
                mCreativeButton.setText("重新下载");
            }
        }

        @Override
        public void onInstalled(String fileName, String appName) {
            if (mCreativeButton != null) {
                mCreativeButton.setText("点击打开");
            }
        }

        @Override
        public void onDownloadFinished(long totalBytes, String fileName, String appName) {
            if (mCreativeButton != null) {
                mCreativeButton.setText("点击安装");
            }
        }
    };


    private RelativeLayout mBannerView;
    private ViewManager mWindowManager;

    /**
     * 加载Banner广告
     */
    private void loadBannerAd(String codeId) {
        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(codeId) //广告位id
                .setSupportDeepLink(true)
                .setImageAcceptedSize(600, 257)
                .build();

        mBannerView = new RelativeLayout(mShowAdContext);
        //step5:请求广告，对请求回调的广告作渲染处理
        mTTAdNative.loadBannerAd(adSlot, new TTAdNative.BannerAdListener() {

            @Override
            public void onError(int code, String message) {
//                showToast("Banner展示 " + "load error : " + code + ", " + message);
                Log.d(TAG, "onError: " + "load error : " + code + ", " + message);
//                mSplashContainer.removeAllViews();
                mBannerView.removeAllViews();
                if (mBannerView != null && mBannerView.getParent() != null) {
                    mWindowManager.removeView(mBannerView);
                }
            }

            @Override
            public void onBannerAdLoad(final TTBannerAd ad) {
                if (ad == null) {
                    return;
                }
                View bannerView = ad.getBannerView();
                if (bannerView == null) {
                    return;
                }
                //设置轮播的时间间隔  间隔在30s到120秒之间的值，不设置默认不轮播
                ad.setSlideIntervalTime(30 * 1000);
//                mSplashContainer.removeAllViews();
//                mSplashContainer.addView(bannerView);
                mBannerView.removeAllViews();
                mBannerView.addView(bannerView);

                WindowManager.LayoutParams params = new WindowManager.LayoutParams();
                params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                params.gravity = Gravity.BOTTOM | Gravity.CENTER;
                params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                mWindowManager = (WindowManager) mShowAdContext.getSystemService(mShowAdContext.WINDOW_SERVICE);
                mWindowManager.addView(mBannerView, params);
                //设置广告互动监听回调
                ad.setBannerInteractionListener(new TTBannerAd.AdInteractionListener() {
                    @Override
                    public void onAdClicked(View view, int type) {
//                        showToast("Banner展示 " + "广告被点击");
                    }

                    @Override
                    public void onAdShow(View view, int type) {
//                        showToast("Banner展示 " + "广告展示");
                    }
                });
                //（可选）设置下载类广告的下载监听
                bindDownloadListener(ad);
                //在banner中显示网盟提供的dislike icon，有助于广告投放精准度提升
//                ad.setShowDislikeIcon(new TTAdDislike.DislikeInteractionCallback() {
//                    @Override
//                    public void onSelected(int position, String value) {
////                        showToast("Banner展示 " + "点击 " + value);
//                        //用户选择不喜欢原因后，移除广告展示
////                        mSplashContainer.removeAllViews();
//                        mBannerView.removeAllViews();
//                    }
//
//                    @Override
//                    public void onCancel() {
////                        showToast("Banner展示 " + "点击取消 ");
//                    }
//                });

                //获取网盟dislike dialog，您可以在您应用中本身自定义的dislike icon 按钮中设置 mTTAdDislike.showDislikeDialog();
                /*mTTAdDislike = ad.getDislikeDialog(new TTAdDislike.DislikeInteractionCallback() {
                        @Override
                        public void onSelected(int position, String value) {
                              showToast("Banner展示 "+ "点击 " + value);
                        }

                        @Override
                        public void onCancel() {
                              showToast("Banner展示 "+ "点击取消 ");
                        }
                    });
                if (mTTAdDislike != null) {
                    XXX.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mTTAdDislike.showDislikeDialog();
                        }
                    });
                } */

            }
        });
    }

    private boolean mHasShowDownloadActive = false;

    private void bindDownloadListener(TTBannerAd ad) {
        ad.setDownloadListener(new TTAppDownloadListener() {
            @Override
            public void onIdle() {
//                showToast("Banner展示 " + "点击图片开始下载");
            }

            @Override
            public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                if (!mHasShowDownloadActive) {
                    mHasShowDownloadActive = true;
//                    showToast("Banner展示 " + "下载中，点击图片暂停");
                }
            }

            @Override
            public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
//                showToast("Banner展示 " + "下载暂停，点击图片继续");
            }

            @Override
            public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
//                showToast("Banner展示 " + "下载失败，点击图片重新下载");
            }

            @Override
            public void onInstalled(String fileName, String appName) {
//                showToast("Banner展示 " + "安装完成，点击图片打开");
            }

            @Override
            public void onDownloadFinished(long totalBytes, String fileName, String appName) {
//                showToast("Banner展示 " + "点击图片安装");
            }
        });
    }

    /**
     * 加载开屏视频广告
     */
    private void loadNativeAd(String codeId) {
        //step3:创建广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(codeId)//开发者申请的广告位
                .setSupportDeepLink(true)
                .setImageAcceptedSize(1080, 1920)//符合广告场景的广告尺寸
                .setAdCount(1) //请求广告数量为1到3条
                .build();
        //step4:请求广告,对请求回调的广告作渲染处理
        mTTAdNative.loadDrawFeedAd(adSlot, new TTAdNative.DrawFeedAdListener() {
            @Override
            public void onError(int code, String message) {
                Log.d(TAG, message);
//                showToast(message);
                Error error = new Error();
                error.setTripartiteCode(code);
                error.setMessage(message);
                mAdCallback.onNoAd(error);
            }

            @Override
            public void onDrawFeedAdLoad(List<TTDrawFeedAd> ads) {
                if (ads == null || ads.isEmpty()) {
//                    TToast.show(NativeVerticalVideoActivity.this, " ad is null!");
//                    showToast(" ad is null!");
                    Error error = new Error();
                    error.setMessage("ad is null!");
                    mAdCallback.onNoAd(error);
                    return;
                }
                //为广告设置activity对象，下载相关行为需要该context对象
                for (TTDrawFeedAd ad : ads) {
                    ad.setActivityForDownloadApp((Activity) mShowAdContext);
                }
                //设置广告视频区域是否响应点击行为，控制视频暂停、继续播放，默认不响应；
                ads.get(0).setCanInterruptVideoPlay(true);
                //设置视频暂停的Icon和大小
                ads.get(0).setPauseIcon(BitmapFactory.decodeResource(mShowAdContext.getResources(), R.drawable.dislike_icon), 60);
                //获取广告视频播放的view并放入广告容器中
                mSplashContainer.addView(ads.get(0).getAdView());
                //初始化并绑定广告行为
                initAdViewAndAction(ads.get(0));
            }
        });
    }

    private void initAdViewAndAction(TTDrawFeedAd ad) {
        Button action = new Button(mShowAdContext);
        action.setText(ad.getButtonText());
        Button btTitle = new Button(mShowAdContext);
        btTitle.setText(ad.getTitle());

        int height = (int) dip2Px(mShowAdContext, 50);
        int margin = (int) dip2Px(mShowAdContext, 10);
        //noinspection SuspiciousNameCombination
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(height * 3, height);
        lp.gravity = Gravity.END | Gravity.BOTTOM;
        lp.rightMargin = margin;
        lp.bottomMargin = margin;
        mSplashContainer.addView(action, lp);

        FrameLayout.LayoutParams lp1 = new FrameLayout.LayoutParams(height * 3, height);
        lp1.gravity = Gravity.START | Gravity.BOTTOM;
        lp1.rightMargin = margin;
        lp1.bottomMargin = margin;
        mSplashContainer.addView(btTitle, lp1);

        //响应点击区域的设置，分为普通的区域clickViews和创意区域creativeViews
        //clickViews中的view被点击会尝试打开广告落地页；creativeViews中的view被点击会根据广告类型
        //响应对应行为，如下载类广告直接下载，打开落地页类广告直接打开落地页。
        //注意：ad.getAdView()获取的view请勿放入这两个区域中。
        List<View> clickViews = new ArrayList<>();
        clickViews.add(btTitle);
        List<View> creativeViews = new ArrayList<>();
        creativeViews.add(action);
        ad.registerViewForInteraction(mSplashContainer, clickViews, creativeViews, new TTNativeAd.AdInteractionListener() {
            @Override
            public void onAdClicked(View view, TTNativeAd ad) {
//                showToast("onAdClicked");
            }

            @Override
            public void onAdCreativeClick(View view, TTNativeAd ad) {
//                showToast("onAdCreativeClick");
                mAdCallback.onClick();
            }

            @Override
            public void onAdShow(TTNativeAd ad) {
//                showToast("onAdShow");
                mAdCallback.onPresent();
            }
        });
    }

    private float dip2Px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return dipValue * scale + 0.5f;
    }

    private TTRewardVideoAd mttRewardVideoAd;

    /**
     * 加载激励视频广告
     */
    private void loadRewardVideoAd(String codeId, int orientation) {
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(codeId)
                .setSupportDeepLink(true)
                .setAdCount(2)
                .setImageAcceptedSize(1080, 1920)
                .setRewardName("金币") //奖励的名称
                .setRewardAmount(3)   //奖励的数量
                //必传参数，表来标识应用侧唯一用户；若非服务器回调模式或不需sdk透传
                //可设置为空字符串
                .setUserID("user123")
                .setOrientation(orientation)  //设置期望视频播放的方向，为TTAdConstant.HORIZONTAL或TTAdConstant.VERTICAL
                .setMediaExtra("media_extra") //用户透传的信息，可不传
                .build();
        mTTAdNative.loadRewardVideoAd(adSlot, new TTAdNative.RewardVideoAdListener() {
            @Override
            public void onError(int code, String message) {
                Log.d(TAG, "onError: loadRewardVideoAd onError code " + code + "  message " + message);
//                showToast("激励视频广告 loadRewardVideoAd onError code " + code + "  message " + message);
                Error error = new Error();
                error.setTripartiteCode(code);
                error.setMessage(message);
                mAdCallback.onNoAd(error);
            }

            //视频广告加载后的视频文件资源缓存到本地的回调
            @Override
            public void onRewardVideoCached() {
                Log.d(TAG, "onRewardVideoCached: loadRewardVideoAd 视频广告加载后的视频文件资源缓存到本地的回调");
//                showToast("激励视频广告  " + "rewardVideoAd video cached");
            }

            //视频广告素材加载到，如title,视频url等，不包括视频文件
            @Override
            public void onRewardVideoAdLoad(TTRewardVideoAd ad) {
                Log.d(TAG, "onRewardVideoAdLoad: 激励视频广告 rewardVideoAd loaded 加载完成");
//                showToast("激励视频广告  " + "rewardVideoAd loaded");
                mttRewardVideoAd = ad;
                //mttRewardVideoAd.setShowDownLoadBar(false);
                mttRewardVideoAd.setRewardAdInteractionListener(new TTRewardVideoAd.RewardAdInteractionListener() {

                    @Override
                    public void onAdShow() {
                        Log.d(TAG, "onAdShow: loadRewardVideoAd");
//                        showToast("激励视频广告  " + "rewardVideoAd show");
                        mAdCallback.onPresent();
                    }

                    @Override
                    public void onAdVideoBarClick() { ////广告的下载bar点击回调
                        Log.d(TAG, "onAdVideoBarClick: loadRewardVideoAd 广告的下载bar点击回调");
//                        showToast("激励视频广告  " + "rewardVideoAd bar click");
                        mAdCallback.onClick();
                    }

                    @Override
                    public void onAdClose() { //视频广告关闭回调
                        Log.d(TAG, "onAdClose: loadRewardVideoAd 视频广告关闭回调");
//                        showToast("激励视频广告  " + "rewardVideoAd close");
                        mAdCallback.onDismissed();
                    }

                    @Override
                    public void onVideoComplete() {   //视频广告播放完毕回调
                        Log.d(TAG, "onVideoComplete: loadRewardVideoAd 视频广告播放完毕回调");
//                        showToast("激励视频广告  " + "rewardVideoAd complete");
                        mAdCallback.onDismissed();
                    }

                    /**
                     * 视频广告播完验证奖励有效性回调，参数分别为是否有效，奖励数量，奖励名称
                     */
                    @Override
                    public void onRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName) {
                        Log.d(TAG, "onRewardVerify: loadRewardVideoAd  视频广告播完验证奖励有效性回调，参数分别为是否有效，奖励数量，奖励名称  " +
                                "verify:" + rewardVerify + " amount:" + rewardAmount + " name:" + rewardName);
//                        showToast("激励视频广告  " + "verify:" + rewardVerify + " amount:" + rewardAmount +" name:" + rewardName);
                    }

                    @Override
                    public void onVideoError() {
                        Log.d(TAG, "onVideoError: loadRewardVideoAd");
//                        showToast("激励视频广告 onVideoError onError ");
                        Error error = new Error();
                        error.setMessage("RewardAdInteractionListener_onVideoError");
                        mAdCallback.onNoAd(error);
                    }

                    @Override
                    public void onSkippedVideo() {  //跳过
                        Log.d(TAG, "onSkippedVideo: loadRewardVideoAd ");
//                        showToast("激励视频广告 onSkippedVideo ");
                        mAdCallback.onDismissed();
                    }
                });
                mttRewardVideoAd.setDownloadListener(new TTAppDownloadListener() {
                    @Override
                    public void onIdle() {

                    }

                    @Override
                    public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {

                    }

                    @Override
                    public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {

                    }

                    @Override
                    public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {

                    }

                    @Override
                    public void onDownloadFinished(long totalBytes, String fileName, String appName) {

                    }

                    @Override
                    public void onInstalled(String fileName, String appName) {

                    }
                });
                if (mttRewardVideoAd != null) {
                    //step6:在获取到广告后展示
                    mttRewardVideoAd.showRewardVideoAd((Activity) mShowAdContext);
                    mttRewardVideoAd = null;
                }
            }
        });
    }

    private TTFullScreenVideoAd mttFullVideoAd;

    /**
     * 加载视频广告
     */
    @SuppressWarnings("SameParameterValue")
    private void loadVideoAd(String codeId, int orientation) {
        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(codeId)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(1080, 1920)
                .setOrientation(orientation)//必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL
                .build();
        //step5:请求广告
        mTTAdNative.loadFullScreenVideoAd(adSlot, new TTAdNative.FullScreenVideoAdListener() {
            @Override
            public void onError(int code, String message) {
//                showToast("视频广告  "+message);
                Error error = new Error();
                error.setTripartiteCode(code);
                error.setMessage(message);
                mAdCallback.onNoAd(error);
            }

            @Override
            public void onFullScreenVideoAdLoad(TTFullScreenVideoAd ad) {
//                showToast("视频广告  " + "FullVideoAd loaded");
                mttFullVideoAd = ad;
                mttFullVideoAd.setFullScreenVideoAdInteractionListener(new TTFullScreenVideoAd.FullScreenVideoAdInteractionListener() {

                    @Override
                    public void onAdShow() {
                        mAdCallback.onPresent();
//                        showToast("视频广告  "+"FullVideoAd show");
                    }

                    @Override
                    public void onAdVideoBarClick() {
//                        showToast("视频广告  "+"FullVideoAd bar click");
                        mAdCallback.onClick();
                    }

                    @Override
                    public void onAdClose() {
//                        showToast("视频广告  "+"FullVideoAd close");
                        mAdCallback.onDismissed();
                    }

                    @Override
                    public void onVideoComplete() {
//                        showToast("视频广告  "+"FullVideoAd complete");
                    }

                    @Override
                    public void onSkippedVideo() {
//                        showToast("视频广告  "+"FullVideoAd skipped");

                    }

                });
                if (mttFullVideoAd != null) {
                    //step6:在获取到广告后展示
                    mttFullVideoAd.showFullScreenVideoAd((Activity) mShowAdContext);
                    mttFullVideoAd = null;
                }
            }

            @Override
            public void onFullScreenVideoCached() {
//                showToast("视频广告  " + "FullVideoAd video cached");
            }
        });
    }

    /**
     * 加载插屏广告
     */
    private void loadInteractionAd(String codeId) {
        //step4:创建插屏广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(codeId)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(600, 600) //根据广告平台选择的尺寸，传入同比例尺寸
                .build();
        //step5:请求广告，调用插屏广告异步请求接口
        mTTAdNative.loadInteractionAd(adSlot, new TTAdNative.InteractionAdListener() {
            @Override
            public void onError(int code, String message) {
//                showToast("code: " + code + "  message: " + message);
                Error error = new Error();
                error.setTripartiteCode(code);
                error.setMessage(message);
                mAdCallback.onNoAd(error);
            }

            @Override
            public void onInteractionAdLoad(final TTInteractionAd ttInteractionAd) {
//                showToast("type:  " + ttInteractionAd.getInteractionType());
                ttInteractionAd.setAdInteractionListener(new TTInteractionAd.AdInteractionListener() {
                    @Override
                    public void onAdClicked() {
                        Log.d(TAG, "被点击");
//                        showToast("广告被点击");
                        mAdCallback.onClick();
                    }

                    @Override
                    public void onAdShow() {
                        Log.d(TAG, "被展示");
//                        showToast("广告被展示");
                        mAdCallback.onPresent();
                    }

                    @Override
                    public void onAdDismiss() {
                        Log.d(TAG, "插屏广告消失");
//                        showToast("广告消失");
                        mAdCallback.onDismissed();
                    }
                });
                //如果是下载类型的广告，可以注册下载状态回调监听
                if (ttInteractionAd.getInteractionType() == TTAdConstant.INTERACTION_TYPE_DOWNLOAD) {
                    ttInteractionAd.setDownloadListener(new TTAppDownloadListener() {
                        @Override
                        public void onIdle() {
                            Log.d(TAG, "点击开始下载");
//                            showToast("点击开始下载");
                        }

                        @Override
                        public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                            Log.d(TAG, "下载中");
//                            showToast("下载中");
                        }

                        @Override
                        public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                            Log.d(TAG, "下载暂停");
//                            showToast("下载暂停");
                        }

                        @Override
                        public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                            Log.d(TAG, "下载失败");
//                            showToast("下载失败");
                        }

                        @Override
                        public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                            Log.d(TAG, "下载完成");
//                            showToast("下载完成");
                        }

                        @Override
                        public void onInstalled(String fileName, String appName) {
                            Log.d(TAG, "安装完成");
//                            showToast("安装完成");
                        }
                    });
                }
                // 弹出插屏广告
                ttInteractionAd.showInteractionAd((Activity) mShowAdContext);
            }
        });
    }

    private WeakHandler mHandler;

    public STtAdSDk() {
        mHandler = new WeakHandler(mIHandler);
    }

    private WeakHandler.IHandler mIHandler = new WeakHandler.IHandler() {
        @Override
        public void handleMsg(Message msg) {
            if (msg.what == MSG_GO_MAIN) {
                if (!mHasLoaded) {
                    Error error = new Error();
                    error.setCode(String.valueOf(Error.AD_ERROR));
                    mAdCallback.onNoAd(error);
                }
            }
        }
    };

    /**
     * 加载开屏广告
     */
    private void loadSplashAd(String codeId) {
        //step3:创建开屏广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(codeId)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(1080, 1920)
                .build();
        //step4:请求广告，调用开屏广告异步请求接口，对请求回调的广告作渲染处理
        mTTAdNative.loadSplashAd(adSlot, new TTAdNative.SplashAdListener() {
            @Override
            public void onError(int code, String message) {
                Log.d(TAG, message);
                mHasLoaded = true;
//                showToast(message);
//                goToMainActivity();

                Error error = new Error();
                error.setMessage(message);
                error.setCode(String.valueOf(Error.AD_ERROR));
                error.setTripartiteCode(code);
                mAdCallback.onNoAd(error);
            }

            @Override
            public void onTimeout() {
                mHasLoaded = true;
//                showToast("开屏广告加载超时");
//                goToMainActivity();


                Error error = new Error();
                error.setCode(String.valueOf(Error.AD_ERROR));
                mAdCallback.onNoAd(error);
            }

            @Override
            public void onSplashAdLoad(TTSplashAd ad) {
                Log.d(TAG, "开屏广告请求成功");
                mHasLoaded = true;
                mHandler.removeCallbacksAndMessages(null);
                if (ad == null) {
                    return;
                }
                //获取SplashView
                View view = ad.getSplashView();
                mSplashContainer.removeAllViews();
                //把SplashView 添加到ViewGroup中,注意开屏广告view：width >=70%屏幕宽；height >=50%屏幕宽
                mSplashContainer.addView(view);
                //设置不开启开屏广告倒计时功能以及不显示跳过按钮,如果这么设置，您需要自定义倒计时逻辑
                //ad.setNotAllowSdkCountdown();

                //设置SplashView的交互监听器
                ad.setSplashInteractionListener(new TTSplashAd.AdInteractionListener() {
                    @Override
                    public void onAdClicked(View view, int type) {
                        Log.d(TAG, "onAdClicked");
//                        showToast("开屏广告点击");

                        mAdCallback.onClick();
                    }

                    @Override
                    public void onAdShow(View view, int type) {
                        Log.d(TAG, "onAdShow");
//                        showToast("开屏广告展示");

                        mAdCallback.onPresent();
                    }

                    @Override
                    public void onAdSkip() {
                        Log.d(TAG, "onAdSkip");
//                        showToast("开屏广告跳过");

                        mAdCallback.onDismissed();


                    }

                    @Override
                    public void onAdTimeOver() {
                        Log.d(TAG, "onAdTimeOver");
//                        showToast("开屏广告倒计时结束");

                        mAdCallback.onDismissed();
                    }
                });
            }
        }, AD_TIME_OUT);
    }


}
