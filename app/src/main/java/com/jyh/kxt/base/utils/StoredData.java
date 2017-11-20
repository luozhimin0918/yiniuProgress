package com.jyh.kxt.base.utils;

/**
 * 项目名称：com.jyh.tool
 * 类描述：引导页判断
 * 创建人：Administrator
 * 创建时间：2016/7/2215:33
 * 修改人：Administrator
 * 修改时间：2016/7/2215:33
 * 修改备注：
 */

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

public class StoredData {
    public static final int LMODE_NEW_INSTALL = 1; // 启动-模式,首次安装-首次启动、覆盖安装-首次启动、已安装-二次启动
    public static final int LMODE_UPDATE = 2;
    public static final int LMODE_AGAIN = 3;

    private boolean isOpenMarked = false;
    private static int launchMode = LMODE_AGAIN; // 启动-模式

    private static StoredData instance;

    private SharedPreferences share; // 一般信息

    public static StoredData getThis() {
        if (instance == null)
            instance = new StoredData();

        return instance;
    }

    // -------启动状态------------------------------------------------------------

    // 标记-打开app,用于产生-是否首次打开
    public void markOpenApp(Context context) {
        // 防止-重复调用
        share = context.getSharedPreferences("yd", Context.MODE_PRIVATE);
        if (isOpenMarked)
            return;
        isOpenMarked = true;

        String lastVersion = share.getString("lastVersion", "");
        String thisVersion = getAppVersion(context);

        // 首次启动
        if (TextUtils.isEmpty(lastVersion)) {
            launchMode = LMODE_NEW_INSTALL;
            share.edit().putString("lastVersion", thisVersion).commit();
        }else if (!thisVersion.equals(lastVersion)) {// 更新
            launchMode = LMODE_UPDATE;
            share.edit().putString("lastVersion", thisVersion).commit();
        } else{ // 二次启动(版本未变)
            launchMode = LMODE_AGAIN;
        }

    }

    public static int getLaunchMode() {
        return launchMode;
    }

    // 首次打开,新安装、覆盖(版本号不同)
    public boolean isFirstOpen() {
        return launchMode != LMODE_AGAIN;
    }

    // -------------------------
    // 软件-版本
    public static String getAppVersion(Context context) {

        String versionName = "";
        Application app = (Application) context.getApplicationContext();
        try {
            PackageManager pkgMng = app.getPackageManager();
            PackageInfo pkgInfo = pkgMng
                    .getPackageInfo(app.getPackageName(), 0);
            versionName = pkgInfo.versionName;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        return versionName;
    }
}