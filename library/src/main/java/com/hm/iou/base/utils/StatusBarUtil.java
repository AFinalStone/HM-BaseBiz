package com.hm.iou.base.utils;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by syl on 2018/8/3.
 */

public class StatusBarUtil {

    /**
     * 设置状态栏上面的字体颜色
     *
     * @param activity
     * @param isDarkFont true-表示黑色，false-表示白色
     */
    public static void setStatusBarDarkFont(Activity activity, boolean isDarkFont) {
        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (StatusBarUtil.setMeiZuStatusBarDarkFont(isDarkFont, activity.getWindow())) {
                return;
            }
            if (StatusBarUtil.setXiaoMiStatusBarDarkFont(isDarkFont, activity)) {

            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (isDarkFont) {
                    activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                }
            }
        }
    }


    /**
     * 设置小米手机状态栏
     *
     * @param isDarkFont
     * @param activity
     */
    public static boolean setXiaoMiStatusBarDarkFont(boolean isDarkFont, Activity activity) {
        boolean result = false;
        Class<? extends Window> clazz = activity.getWindow().getClass();
        try {
            int darkModeFlag = 0;
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(activity.getWindow(), isDarkFont ? darkModeFlag : 0, darkModeFlag);
            result = true;
        } catch (Exception e) {
//            Logger.e("XiaoMi", "setStatusBarDarkIcon: failed");
        }
        return result;
    }

    /**
     * 设置魅族手机状态栏
     *
     * @param isDarkFont
     * @param window
     * @return
     */
    public static boolean setMeiZuStatusBarDarkFont(boolean isDarkFont, Window window) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (isDarkFont) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {
//                Logger.e("MeiZu", "setStatusBarDarkIcon: failed");
            }
        }
        return result;
    }
}
