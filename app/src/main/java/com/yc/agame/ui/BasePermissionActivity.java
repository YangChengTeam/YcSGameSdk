package com.yc.agame.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;


import com.yc.agame.utils.PermissionHelpUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public abstract class BasePermissionActivity extends Activity {

    private String TAG = "GameSdkLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        onBeforeRequestPermission(savedInstanceState);
        // 如果targetSDKVersion >= 23，就要申请好权限。如果您的App没有适配到Android6.0（即targetSDKVersion < 23），那么只需要在这里直接调用fetchSplashAD接口。
        if (Build.VERSION.SDK_INT >= 23) {
            checkAndRequestPermission();
        } else {
            onRequestPermissionSuccess();
        }
    }


    public void backPressed(View v) {
        onBackPressed();
    }

    /**
     * 手动切换横竖屏
     */
    public void switchOrientation() {
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }


    protected List<String> getNecessaryPermissions() {
        return Arrays.asList(Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION
        );
    }

    /**
     * READ_PHONE_STATE、WRITE_EXTERNAL_STORAGE 两个权限是必须权限，没有这两个权限SDK无法正常获得广告
     * WRITE_CALENDAR、ACCESS_FINE_LOCATION 是两个可选权限；没有不影响SDK获取广告；但是如果应用申请到该权限，会显著提升应用的广告收益
     */
   /* protected List<String> getMustPermissions() {
        return Arrays.asList(Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }*/

  /*  protected List<String> getNotMustPermissions() {
        return Arrays.asList(Manifest.permission.ACCESS_FINE_LOCATION);
    }*/

    /**
     * 校验必须权限是否授权
     */
    /*@TargetApi(23)
    private boolean checkMustPermissions() {
        List<String> lackedPermission = new ArrayList<>();
        *//**
     * READ_PHONE_STATE、WRITE_EXTERNAL_STORAGE 两个权限是必须权限，没有这两个权限SDK无法正常获得广告。
     *//*
        for (String permissions : getMustPermissions()) {
            if (!(checkSelfPermission(permissions) == PackageManager.PERMISSION_GRANTED)) {
                lackedPermission.add(permissions);
            }
        }
        if (0 == lackedPermission.size()) {
            return true;
        }
        return false;
    }*/

    /**
     * 校验非必须权限是否授权
     */
    /*@TargetApi(23)
    private boolean checkNotMustPermissions() {
        List<String> lackedPermission = new ArrayList<>();
        *//**
     * READ_PHONE_STATE、WRITE_EXTERNAL_STORAGE 两个权限是必须权限，没有这两个权限SDK无法正常获得广告。
     *//*
        for (String permissions : getNotMustPermissions()) {
            if (!(checkSelfPermission(permissions) == PackageManager.PERMISSION_GRANTED)) {
                lackedPermission.add(permissions);
            }
        }
        if (0 == lackedPermission.size()) {
            return true;
        }
        return false;
    }*/

    /**
     * ----------非常重要----------
     * <p>
     * Android6.0以上的权限适配简单示例：
     * <p>
     * 如果targetSDKVersion >= 23，那么必须要申请到所需要的权限，再调用广点通SDK，否则广点通SDK不会工作。
     * <p>
     * Demo代码里是一个基本的权限申请示例，请开发者根据自己的场景合理地编写这部分代码来实现权限申请。
     * 注意：下面的`checkSelfPermission`和`requestPermissions`方法都是在Android6.0的SDK中增加的API，如果您的App还没有适配到Android6.0以上，则不需要调用这些方法，直接调用广点通SDK即可。
     */
    @TargetApi(23)
    private void checkAndRequestPermission() {

        boolean obtainMust = PermissionHelpUtils.checkMustPermissions(this);
//        boolean obtainNotMust = checkNotMustPermissions();

        if (obtainMust) {
            onRequestPermissionSuccess();
        } else {
            List<String> allPermissions = new ArrayList<>();
            List<String> mustPermissions = PermissionHelpUtils.getMustPermissions();
            List<String> notMustPermissions = PermissionHelpUtils.getNotMustPermissions();
            allPermissions.addAll(mustPermissions);
            allPermissions.addAll(notMustPermissions);
            // 请求所缺少的权限，在onRequestPermissionsResult中再看是否获得权限，如果获得权限就可以调用SDK，否则不要调用SDK。
            String[] requestPermissions = new String[allPermissions.size()];
            allPermissions.toArray(requestPermissions);
            requestPermissions(requestPermissions, 1024);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1024 && PermissionHelpUtils.checkMustPermissions(this)) {
//        if (requestCode == 1024 && hasAllPermissionsGranted(grantResults)) {
            onRequestPermissionSuccess();
        } else {
            onRequestPermissionError();
            // 如果用户没有授权，那么应该说明意图，引导用户去设置里面授权。
            Toast.makeText(this, "应用缺少必要的权限！请点击\"权限\"，打开所需要的权限。", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
            finish();
        }
    }


    /**
     * 校验时候所有权限都已授权
     *
     * @param grantResults 系统返回的是否授权权限数组
     */
    private boolean hasAllPermissionsGranted(int[] grantResults) {
        int count = 0;
        for (int grantResult : grantResults) {
            Log.d(TAG, "hasAllPermissionsGranted: count " + count++);
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 权限请求失败的回调函数
     */
    protected void onRequestPermissionError() {

    }

    /**
     * 权限请求成功的回调函数
     */
    protected abstract void onRequestPermissionSuccess();


    private void cheakOverlay() {
        if (Build.VERSION.SDK_INT >= 23) {
            //检查是否已经授予权限
            if (!Settings.canDrawOverlays(this)) {
                //若未授权则请求权限,请求悬浮窗权限
                SharedPreferences sPreferences = getSharedPreferences("oppo_sdk_config", MODE_PRIVATE);
                boolean isRequestOverlay = sPreferences.getBoolean("isRequestOverlay", false);
                if (!isRequestOverlay) {
                    requestOverlay();
                } else {
                    SharedPreferences.Editor editor = sPreferences.edit();
                    editor.putBoolean("isRequestOverlay", true);
                    //切记最后要使用commit方法将数据写入文件
                    editor.commit();
                }
            }
        }
    }

    @TargetApi(23)
    private void requestOverlay() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, 0);
    }
}
