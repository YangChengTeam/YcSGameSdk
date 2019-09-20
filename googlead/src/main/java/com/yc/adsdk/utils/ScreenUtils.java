package com.yc.adsdk.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

/**
 * Created by caokun on 2019/9/17 17:55.
 */

public class ScreenUtils {
    /**
     * 获取手机屏幕高度
     */
    public static int getHeight(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    /**
     * 获取屏幕真实高度（包括虚拟键盘）
     */
    public static int getRealHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealMetrics(dm);
        } else {
            display.getMetrics(dm);
        }
        int realHeight = dm.heightPixels;
        return realHeight;
    }

    public interface NavigationListener {
        void show();

        void hide();
    }

    //虚拟导航栏显示/隐藏
    public static void setNavigationListener(final View rootView, final NavigationListener navigationListener, final Context context) {
        if (rootView == null || navigationListener == null) {
            return;
        }
        if (getRealHeight(context) != getHeight(context)) {
            rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                int rootViewHeight;

                @Override
                public void onGlobalLayout() {
                    int viewHeight = rootView.getHeight();
                    if (rootViewHeight != viewHeight) {
                        rootViewHeight = viewHeight;
                        if (viewHeight == getRealHeight(context)) {
                            Log.d("GameSdkLog", "onGlobalLayout: 隐藏虚拟按键 viewHeight == getRealHeight(context)");
                            //隐藏虚拟按键
                            if (navigationListener != null) {
                                navigationListener.hide();
                            }
                        } else {
                            Log.d("GameSdkLog", "onGlobalLayout:  显示虚拟按键 viewHeight !!!!!!!!!!!!!!!!!! getRealHeight(context)");
                            Log.d("GameSdkLog", "onGlobalLayout: navigationListener navigationListener"+navigationListener);
                            //显示虚拟按键
                            if (navigationListener != null) {
                                navigationListener.show();
                            }
                        }
                    }
                }
            });
        }
    }
}
