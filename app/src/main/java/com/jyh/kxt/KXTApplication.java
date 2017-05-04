package com.jyh.kxt;

import android.os.Handler;
import android.support.multidex.MultiDexApplication;

import com.jyh.kxt.base.utils.UmengShareTool;
import com.jyh.kxt.push.KXTPushIntentService;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.MsgConstant;
import com.umeng.message.PushAgent;
import com.umeng.message.common.UmLog;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/12.
 */

public class KXTApplication extends MultiDexApplication {

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

        mPushAgent.setPushIntentServiceClass(KXTPushIntentService.class);
    }
}
