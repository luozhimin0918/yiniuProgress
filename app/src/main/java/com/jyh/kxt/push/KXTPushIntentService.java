package com.jyh.kxt.push;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import com.jyh.kxt.R;
import com.jyh.kxt.index.ui.MainActivity;
import com.umeng.message.UmengMessageService;
import com.umeng.message.entity.UMessage;

import org.android.agoo.common.AgooConstants;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 项目名:Kxt
 * 类描述:推送
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/13.
 */

public class KXTPushIntentService extends UmengMessageService {


    @Override
    public void onMessage(Context context, Intent intent) {
        try {
            String message = intent.getStringExtra(AgooConstants.MESSAGE_BODY);

            JSONObject data = new JSONObject(message);
            UMessage uMessage = new UMessage(data);

            showNotification(context, uMessage, new Intent[]{new Intent(context, MainActivity.class)});



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showNotification(Context context, UMessage msg, final Intent[] intent) {
        try {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);


            mBuilder.setContentTitle(msg.title)//设置通知栏标题
                    .setContentText(msg.text);

            PendingIntent pendingIntent = PendingIntent.getActivities(context, 0,
                    intent, PendingIntent.FLAG_ONE_SHOT);//不是Intent

            mBuilder.setContentIntent(pendingIntent) //设置通知栏点击意图
                    .setTicker(msg.title) //通知首次出现在通知栏，带上升动画效果的
                    .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
                    .setOngoing(false)
                    .setSmallIcon(R.mipmap.ic_launcher);//设置通知小ICON

            int id = (int) System.currentTimeMillis();

            Notification build = mBuilder.build();
            build.flags = Notification.FLAG_AUTO_CANCEL;
            mNotificationManager.notify(id, build);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
