package com.yc.adsdk.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Process;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by caokun on 2019/9/4 9:14.
 */

public class SAppUtil {

    public static void exitGameProcess(Context var0) {
        try {
            ArrayList var2 = new ArrayList();
            Iterator var3;
            if ((var3 = o_a(var0)) != null) {
                String var1 = var0.getPackageName();

                while (var3.hasNext()) {
                    ActivityManager.RunningAppProcessInfo var4;
                    if ((var4 = (ActivityManager.RunningAppProcessInfo) var3.next()).processName.equals(var1)) {
                        var2.add(var4.pid);
                    }
                }
            }

            Activity var6;
            if (var0 instanceof Activity && !(var6 = (Activity) var0).isFinishing()) {
                var6.finish();
            }

            Iterator var7 = var2.iterator();

            while (var7.hasNext()) {
                int var8;
                if ((var8 = (Integer) var7.next()) != 0) {
                    Process.killProcess(var8);
                }
            }

            System.exit(0);
        } catch (Exception var5) {
        }
    }

    private static Iterator<ActivityManager.RunningAppProcessInfo> o_a(Context var0) {
        Iterator var1 = null;
        List var2;
        if ((var2 = ((ActivityManager) var0.getSystemService("activity")).getRunningAppProcesses()) != null && !var2.isEmpty()) {
            var1 = var2.iterator();
        }

        return var1;
    }
}
