package com.jyh.kxt.base.widget.night;

import android.app.Activity;
import android.content.Context;

import com.jyh.kxt.base.constant.SpConstant;
import com.library.util.SPUtils;

import java.util.HashMap;

/**
 * 项目名:FirstGold 2.1.3
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/3/16.
 */

public class ThemeUtil {
    /**
     * true 代表夜间模式
     * false 代表白天模式
     */
    private static HashMap cacheHashMap;

    public static void addActivityToThemeCache(Activity mCurrentActivity) {
        if (cacheHashMap == null) {
            cacheHashMap = new HashMap<>();
        }
        boolean localTheme = SPUtils.getBoolean(mCurrentActivity, SpConstant.SETTING_DAY_NIGHT);
        cacheHashMap.put(mCurrentActivity, localTheme);
    }

    public static void removeActivityFromThemeCache(Activity mCurrentActivity) {
        if (cacheHashMap != null) {
            cacheHashMap.remove(mCurrentActivity);
        }
    }

    public static void removeAllCache() {
        if (cacheHashMap != null) {
            cacheHashMap.clear();
        }
    }

    /**
     * 真的改变主题,
     *
     * @param mCurrentActivity
     * @return
     */
    public static boolean isChangeCurrentActionTheme(Activity mCurrentActivity) {
        if (cacheHashMap != null) {
            boolean queryActivityBeing = cacheHashMap.containsKey(mCurrentActivity);

            if (queryActivityBeing) {
                boolean localTheme = SPUtils.getBoolean(mCurrentActivity, SpConstant.SETTING_DAY_NIGHT);
                boolean activityTheme = (boolean) cacheHashMap.get(mCurrentActivity);

                if (localTheme != activityTheme) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void changeCacheActionTheme(Activity mCurrentActivity) {
        boolean localTheme = SPUtils.getBoolean(mCurrentActivity, SpConstant.SETTING_DAY_NIGHT);
        cacheHashMap.remove(mCurrentActivity);
        cacheHashMap.put(mCurrentActivity, localTheme);
    }

    public static int getAlertTheme(Context mContext) {
        int theme;
        boolean localTheme = SPUtils.getBoolean(mContext, SpConstant.SETTING_DAY_NIGHT);
        if (localTheme) {
            theme = android.support.v7.appcompat.R.style.Theme_AppCompat_DayNight_Dialog_Alert ;
        } else {
            theme = android.support.v7.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert;
        }
        return theme;
    }
}
