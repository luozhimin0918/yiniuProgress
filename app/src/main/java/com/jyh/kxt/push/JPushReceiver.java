package com.jyh.kxt.push;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jyh.kxt.R;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.main.ui.activity.FlashActivity;
import com.jyh.kxt.push.json.CjInfo;
import com.jyh.kxt.push.json.KxItemCJRL;
import com.jyh.kxt.push.json.PushBean;
import com.library.util.RegexValidateUtil;
import com.library.util.SPUtils;

import java.util.Random;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class JPushReceiver extends BroadcastReceiver {
    private static final String TAG = "JIGUANG-Example";

    private NotificationManager manager;
    private NotificationCompat.Builder notification;
    private PendingIntent contentIntent;

    @Override
    public void onReceive(Context mContext, Intent intent) {
        try {

            Bundle bundle = intent.getExtras();

            if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {

            } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {

                //接受到推送下来的自定义消息
                String myNotifyMessage = bundle.getString(JPushInterface.EXTRA_MESSAGE);

                if (myNotifyMessage == null) {
                    return;
                }
                Boolean pushStatus = SPUtils.getBooleanTrue(mContext, SpConstant.SETTING_PUSH);
                PushBean pushBean = JSONObject.parseObject(myNotifyMessage, PushBean.class);

                if (!pushStatus || pushBean == null) {
                    return;
                }
                switch (pushBean.code) {
                    case PushJsonHandle.KUAI_XUN:
                        if (!TextUtils.isEmpty(pushBean.content)) {
                            sendUmengPush(mContext, pushBean);
                        }
                        break;
                    case PushJsonHandle.CJRL:
                        sendPushUmengNoifiyCJRL(mContext, pushBean);
                        break;
                    case PushJsonHandle.NEWS:
                    case PushJsonHandle.DIAN_PING:
                        if (pushBean.url != null) {
                            String id;
                            if (!TextUtils.isEmpty(pushBean.url)) {
                                id = pushBean.url.substring(pushBean.url.indexOf("/id") + 3);
                            } else {
                                id = pushBean.id;
                            }
                            if (!RegexValidateUtil.isEmpty(id)) {
                                pushBean.id = id;
                                sendUmengPush(mContext, pushBean);
                            }
                        }

                        break;
                    case PushJsonHandle.VIDEO:
                        if (!RegexValidateUtil.isEmpty(pushBean.id)) {
                            sendUmengPush(mContext, pushBean);
                        }
                        break;
                    default:
                        break;
                }
            } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
                // 接受到推送下来的通知
            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
                // 用户点击打开了通知
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 友盟推送
     *
     * @param mContext
     * @param bean
     */
    public void sendUmengPush(Context mContext, PushBean bean) {
        Boolean pushSound = SPUtils.getBooleanTrue(mContext, SpConstant.SETTING_SOUND);

        // 1 得到通知管理器
        NotificationManager nm = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                mContext);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setDefaults(Notification.DEFAULT_VIBRATE);
        builder.setWhen(System.currentTimeMillis());
        // 响铃
        if (pushSound) {

            builder.setSound(Uri.parse("android.resource://"
                    + mContext.getPackageName() + "/" + R.raw.kxt_notify));
        }
        // 3设置通知的点击事件
        if (bean == null) {
            return;
        }
        String code = bean.code;
        if (code == null) {
            return;
        }

        Intent skipIntent = PushJsonHandle.getInstance().getSkipIntent(mContext, bean);
        contentIntent = PendingIntent.getActivity(
                mContext,
                100,
                skipIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentTitle(bean.title);
        builder.setContentText(bean.content);
        builder.setContentIntent(contentIntent);
        builder.setAutoCancel(true);// 点击通知之后自动消失
        // builder.set
        // notification.flags = Notification.FLAG_AUTO_CANCEL;
        // 4发送通知
        nm.notify(new Random().nextInt(100000), builder.build());

    }

    public void sendPushUmengNoifiyCJRL(Context mContext, PushBean pushBean) {

        Boolean pushSound = SPUtils.getBooleanTrue(mContext, SpConstant.SETTING_SOUND);

        KxItemCJRL kxItemCJRL = JSON.parseObject(pushBean.content, KxItemCJRL.class);
        if (kxItemCJRL == null) {
            return;
        }

        if (manager == null) {
            manager = (NotificationManager) mContext
                    .getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if (notification == null) {
            notification = new NotificationCompat.Builder(mContext);
        }
        notification.setContentTitle(kxItemCJRL.getState() + " " + kxItemCJRL.getTitle());
        notification.setSmallIcon(R.mipmap.ic_launcher);
        notification.setWhen(System.currentTimeMillis());
        // 振动
        notification.setDefaults(Notification.DEFAULT_VIBRATE);
        // 响铃
        if (pushSound) {
            notification.setSound(
                    Uri.parse("android.resource://"
                            + mContext.getPackageName()
                            + "/" + R.raw.kxt_notify));
        }

        Intent notificationIntent = new Intent(mContext, FlashActivity.class);
        notificationIntent.putExtra(IntentConstant.O_ID, pushBean.id);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext,
                Integer.valueOf(pushBean.id), notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.text_notification);
        findCJRLViews(views, kxItemCJRL);
        notification.setContent(views);
        notification.setAutoCancel(true);
        notification.setContentIntent(pendingIntent);
        manager.notify(Integer.valueOf(pushBean.id), notification.build());
    }


    /**
     * 自定义财经数据视图
     *
     * @param view
     * @param cInfo
     */
    public void findCJRLViews(RemoteViews view, KxItemCJRL cInfo) {
        view.setTextViewText(R.id.calendar_item_listview_version2_nfi, cInfo.getState() + "  " + cInfo.getTitle());
        view.setTextViewText(R.id.calendar_listview_item_tv_title_time, cInfo.getTime().substring(11, 16));
        view.setTextViewText(R.id.calendar_item_listview_version2_before, "前值:" + cInfo.getBefore());
        view.setTextViewText(R.id.calendar_item_listview_version2_forecast, "预测:" + cInfo.getForecast());
        view.setTextViewText(R.id.calendar_item_listview_version2_gongbu, "" + cInfo.getReality());

        String yingString = "";

        CjInfo cjInfo = new CjInfo();
        String title = cInfo.getEffect();
        String[] titles = title.split("\\|");
        if (titles.length > 0 && titles.length == 1) {
            cjInfo.setEffectGood(titles[0]);// 利空
        } else if (titles.length > 0
                && titles.length == 2) {
            cjInfo.setEffectBad(titles[1]);// 利多
            cjInfo.setEffectGood(titles[0]);// 利空
        } else {
            cjInfo.setEffectMid("影响较小");
        }

        if (!TextUtils.isEmpty(cjInfo.getEffectMid())) {
            yingString = cjInfo.getEffectMid();
        } else {
            if (!TextUtils.isEmpty(cjInfo.getEffectGood())) {
                yingString += "利多 " + cjInfo.getEffectGood();
            }
            if (!TextUtils.isEmpty(cjInfo.getEffectBad())) {

                if (!TextUtils.isEmpty(cjInfo.getEffectGood())) {
                    yingString += ",";
                }
                yingString += "利空 " + cjInfo.getEffectBad();
            }
        }

        view.setTextViewText(R.id.calendar_item_listview_version2_4main_effect, "影响：" + yingString);
        if (cInfo.getImportance().equals("高")) {
            view.setImageViewResource(R.id.calendar_item_nature,
                    R.mipmap.nature_high_bt);
        } else if (cInfo.getImportance().equals("低")) {
            view.setImageViewResource(R.id.calendar_item_nature,
                    R.mipmap.nature_low_bt);
        } else if (cInfo.getImportance().equals("中")) {
            view.setImageViewResource(R.id.calendar_item_nature,
                    R.mipmap.nature_mid_bt);
        } else {
            view.setImageViewResource(R.id.calendar_item_nature,
                    R.mipmap.nature_high_bt);
        }
    }
}
