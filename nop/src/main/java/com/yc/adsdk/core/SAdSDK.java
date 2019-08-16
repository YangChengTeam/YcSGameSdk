package com.yc.adsdk.core;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

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
    public void init(Context context, InitCallback callback) {
        Log.d(TAG, "init: ");
    }


    @Override
    public void showAd(Context context, AdType type, AdCallback callback) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!PermissionHelpUtils.checkMustPermissions(context)) {
                openSettingGivePermission(context);
                return;
            }
        }
        Log.d(TAG, "showAd: ");
    }

    @Override
    public void hindAd(AdTypeHind type) {
        Log.d(TAG, "hindAd: ");
    }

    private void openSettingGivePermission(Context context) {
        // 如果用户没有授权，那么应该说明意图，引导用户去设置里面授权。
        Toast.makeText(context, "应用缺少必要的权限！请点击\"权限\"，打开所需要的权限。", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
    }
}
