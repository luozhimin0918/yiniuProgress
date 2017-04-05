package com.superplayer.library.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

/**
 * @author Super南仔
 * @time 2016-9-19
 */
public class SuperPlayerUtils {
    /**
     * 得到屏幕宽度
     *
     * @return 宽度
     */
    public static int getScreenWidth(Activity activity) {
        return getScreenDisplay(activity).x;
    }

    /**
     * 得到屏幕高度
     *
     * @return 高度
     */
    public static int getScreenHeight(Activity activity) {
        return getScreenDisplay(activity).y;
    }


    /**
     * 屏幕 - 宽高
     *
     * @return
     */
    public static Point getScreenDisplay(Activity activity) {
        WindowManager windowManager = activity.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        return point;
    }




}
