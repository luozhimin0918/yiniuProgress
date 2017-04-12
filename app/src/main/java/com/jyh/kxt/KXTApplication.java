package com.jyh.kxt;

import android.app.Application;

import com.jyh.kxt.base.utils.UmengShareTool;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/12.
 */

public class KXTApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        UmengShareTool.initUmeng();
    }
}
