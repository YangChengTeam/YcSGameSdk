package com.yc.adsdk.core;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.ViewGroup;
import android.widget.Toast;

import com.yc.adsdk.google.SGoogleAdSDk;
import com.yc.adsdk.utils.PermissionHelpUtils;


public class SAdSDK implements ISGameSDK {
    private String TAG = "GameSdkLog";

    private static SAdSDK sAdSDK;

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
        SGoogleAdSDk.getImpl().initAd(context, adCallback);
    }

    @Override
    public void initUser(Context context, InitUserCallback userCallback) {
        SGoogleAdSDk.getImpl().initUser(context, userCallback);
    }

    @Override
    public void showAd(Context context, AdType type, AdCallback callback) {
        this.showAd(context, type, callback, null);
    }

    @Override
    public void showAd(Context context, AdType type, AdCallback callback, ViewGroup viewGroup) {
//        if (Build.VERSION.SDK_INT >= 23) {
//            if (!PermissionHelpUtils.checkMustPermissions(context)) {
////                openSettingGivePermission(context);
//                return;
//            }
//        }
        SGoogleAdSDk.getImpl().showAd(context, type, callback);
    }

    @Override
    public void hindAd(AdTypeHind type) {
        SGoogleAdSDk.getImpl().hindAd(type);
    }

    @Override
    public void login(Context context, IUserApiCallback iUserApiCallback) {
        SGoogleAdSDk.getImpl().login(context, iUserApiCallback);
    }

    @Override
    public void logout(Context context, IUserApiCallback iUserApiCallback) {
        SGoogleAdSDk.getImpl().logout(context, iUserApiCallback);
    }

    private void openSettingGivePermission(Context context) {
        // 如果用户没有授权，那么应该说明意图，引导用户去设置里面授权。
        Toast.makeText(context, "应用缺少必要的权限！请点击\"权限\"，打开所需要的权限。", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
    }
}
