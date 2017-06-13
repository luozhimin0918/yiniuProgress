package com.jyh.kxt.base.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.annotation.NetEvent;
import com.library.util.NetUtils;

/**
 * 项目名:Kxt
 * 类描述:网络监听
 * 创建人:苟蒙蒙
 * 创建日期:2017/6/7.
 */

public class NetBroadcastReceiver extends BroadcastReceiver {
    public NetEvent event = BaseActivity.netEvent;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        // 如果相等的话就说明网络状态发生了变化
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            int netWorkState = NetUtils.getNetConnectState(context);
            // 接口回调传过去状态的类型
            event.onNetChange(netWorkState);
        }
    }
}