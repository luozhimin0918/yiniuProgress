package com.jyh.kxt.base.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Process;
import android.util.Log;

import com.jyh.kxt.base.widget.night.ThemeUtil;
import com.jyh.kxt.index.ui.ErrorActivity;
import com.library.manager.ActivityManager;


public class CrashHandler implements Thread.UncaughtExceptionHandler {
    public Context context;

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        this.context = context;
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        Log.e("uncaughtException", "uncaughtException: " + ex.getMessage().toString());

        Intent intent = new Intent(context, ErrorActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        String errorInfo = ex.getMessage() + "\n";
        try {
            StackTraceElement[] stackTrace = ex.getStackTrace();

            for (int i = 0; i < stackTrace.length; i++) {
                errorInfo += stackTrace[i].toString() + "\n";
            }
        } catch (Exception e) {

        }
        intent.putExtra(ErrorActivity.ERROR_MSG, errorInfo);
        context.startActivity(intent);
        ThemeUtil.removeAllCache();
        ActivityManager.getInstance().finishAllActivity();
        //然后重启进程
        Process.killProcess(Process.myPid());
    }
}