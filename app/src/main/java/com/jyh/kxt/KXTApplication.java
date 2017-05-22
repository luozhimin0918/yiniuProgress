package com.jyh.kxt;

import android.app.Application;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.jyh.kxt.base.utils.CrashHandler;
import com.jyh.kxt.base.utils.UmengShareTool;
import com.jyh.kxt.index.ui.MainActivity;
import com.jyh.kxt.push.KXTPushIntentService;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.MsgConstant;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.common.UmLog;
import com.umeng.message.entity.UMessage;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/12.
 */

public class KXTApplication extends Application {

    private String TAG="KXTApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化umeng分享
        UmengShareTool.initUmeng(this);
        initPush();
    }

    private void initPush() {

        final Handler handler = new Handler();
        PushAgent mPushAgent = PushAgent.getInstance(this);
        //sdk开启通知声音
        //mPushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SDK_ENABLE);
        // sdk关闭通知声音
        //mPushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SDK_DISABLE);
        //通知声音由服务端控制
        mPushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SERVER);

        //注册推送服务 每次调用register都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {
                UmLog.i(TAG, "device token: " + deviceToken);
            }

            @Override
            public void onFailure(String s, String s1) {
                UmLog.i(TAG, "register failed: " + s + " " +s1);
            }
        });

        CrashHandler crashHandler = new CrashHandler();
        crashHandler.init(this);

        mPushAgent.setPushIntentServiceClass(KXTPushIntentService.class);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
