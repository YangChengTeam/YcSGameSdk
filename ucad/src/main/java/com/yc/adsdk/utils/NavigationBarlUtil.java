package com.yc.adsdk.utils;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;

/**
 * 创建于 2018/5/17 3:38 PM
 *
 * @author spirits
 * 类说明：android手机底部导航栏控制工具类
 * 所属公司：贵州中测
 */
public class NavigationBarlUtil {

    /**
     * 是否显示底部导航栏
     *
     * @return
     */
    public static boolean isNavigationBarShow(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            Point realSize = new Point();
            display.getSize(size);
            display.getRealSize(realSize);
            return realSize.y != size.y;
        } else {
            boolean menu = ViewConfiguration.get(activity).hasPermanentMenuKey();
            boolean back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            if (menu || back) {
                return false;
            } else {
                return true;
            }
        }
    }

    /**
     * 获取底部导航栏高度
     *
     * @param activity
     * @return
     */
    public static int getNavigationBarHeight(Activity activity) {
        if (!isNavigationBarShow(activity)) {
            return 0;
        }
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height",
                "dimen", "android");
        //获取NavigationBar的高度
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    /**
     * 获取屏幕高度
     *
     * @param activity
     * @return
     */
    public static int getSceenHeight(Activity activity) {
        return activity.getWindowManager().getDefaultDisplay().getHeight() + getNavigationBarHeight(activity);
    }
}

