package com.jyh.kxt.push;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.alibaba.fastjson.JSON;
import com.jyh.kxt.R;
import com.jyh.kxt.av.ui.VideoDetailActivity;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.main.ui.activity.FlashActivity;
import com.jyh.kxt.main.ui.activity.NewsContentActivity;
import com.jyh.kxt.push.json.CjInfo;
import com.jyh.kxt.push.json.KxItemCJRL;
import com.jyh.kxt.push.json.PushBean;
import com.jyh.kxt.push.json.PushBean1;

import java.util.Random;

//import com.jyh.kxt.ActivityZxInfo;
//import com.jyh.kxt.CjInfoActicity;

/**
 * 通知类
 *
 * @author PC
 */
public class NotificationKXT {

    NotificationManager manager;
    NotificationCompat.Builder notification;
    private PendingIntent contentIntent;
    private static int count = 0;

    public static final int SDK_VERSION_CURR = 14;

    /*@SuppressWarnings("deprecation")
    public static int getSDKVersionNumber() {
        int sdkVersion;
        try {
            sdkVersion = Integer.valueOf(android.os.Build.VERSION.SDK);
        } catch (NumberFormatException e) {
            sdkVersion = 0;
        }
        return sdkVersion;
    }

    public void sendNoification(Context context, CjInfo cInfo, boolean pushsound) {

        boolean PRE_CUPCAKE = getSDKVersionNumber() < SDK_VERSION_CURR ? true
                : false;
        if (PRE_CUPCAKE) {
            NotificationManager nm = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = new Notification();
            notification.icon = R.drawable.ic_launcher;// 图标
            notification.tickerText = cInfo.getNfi();
            notification.when = System.currentTimeMillis();
            notification.defaults = Notification.DEFAULT_VIBRATE;
            if (pushsound) {
                notification.sound = Uri.parse("android.resource://"
                        + context.getPackageName() + "/" + R.raw.kxt_notify);
            }
            RemoteViews contentView = new RemoteViews(context.getPackageName(),
                    R.layout.text_notification);
            findViews(contentView, cInfo, context);
            notification.contentView = contentView;// 通知显示的布局
            // Intent intent = new Intent(context, MainActivity.class); //
            // 跳到MainActivity
            // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
            // Intent.FLAG_ACTIVITY_NEW_TASK);
            // intent.putExtra("data", "data");

            Intent intent = new Intent(context, FlashActivity.class);

            // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
            // | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("cInfo", cInfo);
            intent.putExtra("id", cInfo.getId());
            intent.putExtra("enterpage", "notification");// shou
            intent.putExtra("type", "2");
            PendingIntent contentIntent = PendingIntent.getActivity(context,
                    Integer.valueOf(cInfo.getId()), intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            notification.contentIntent = contentIntent;// 点击的事件
            notification.flags = Notification.FLAG_AUTO_CANCEL;// 点击通知之后自动消失
            nm.notify(Integer.valueOf(cInfo.getId()), notification);
        } else {
            if (manager == null) {
                manager = (NotificationManager) context
                        .getSystemService(Context.NOTIFICATION_SERVICE);
            }
            if (notification == null) {
                notification = new NotificationCompat.Builder(context);
            }
            notification.setContentTitle(cInfo.getNfi());
            notification.setSmallIcon(R.drawable.ic_launcher);
            notification.setWhen(System.currentTimeMillis());
            // 振动
            notification.setDefaults(Notification.DEFAULT_VIBRATE);
            // 响铃
            if (pushsound) {
                notification.setSound(Uri.parse("android.resource://"
                        + context.getPackageName() + "/" + R.raw.kxt_notify));
            }

            Intent notificationIntent = new Intent(context, FlashActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            notificationIntent.putExtra("id", cInfo.getId());
            notificationIntent.putExtra("enterpage", "notification");// shou
            notificationIntent.putExtra("type", "2");
            PendingIntent pendingIntent = PendingIntent.getActivity(context,
                    Integer.valueOf(cInfo.getId()), notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            RemoteViews views = new RemoteViews(context.getPackageName(),
                    R.layout.text_notification);
            findViews(views, cInfo, context);
            notification.setContent(views);
            notification.setAutoCancel(true);
            notification.setContentIntent(pendingIntent);
            manager.notify(Integer.valueOf(cInfo.getId()), notification.build());
        }

    }

    public void findViews(RemoteViews view, CjInfo cInfo, Context context) {
        CjInfo cjrl = cInfo;
        view.setTextViewText(R.id.calendar_item_listview_version2_nfi,
                cjrl.getState() + cjrl.getNfi());
        view.setTextViewText(R.id.calendar_listview_item_tv_title_time,
                cjrl.getPredictTime());
        view.setTextViewText(R.id.calendar_item_listview_version2_before, "前值:"
                + cjrl.getBefore());
        view.setTextViewText(R.id.calendar_item_listview_version2_forecast,
                "预测:" + cjrl.getForecast());
        view.setTextViewText(R.id.calendar_item_listview_version2_gongbu, ""
                + cjrl.getReality());

        String yingString = "";


        if (!TextUtils.isEmpty(cjrl.getEffectMid())) {
            yingString = cjrl.getEffectMid();
        } else {
            if (!TextUtils.isEmpty(cjrl.getEffectGood())) {
                yingString += "利多 " + cjrl.getEffectGood();
            }
            if (!TextUtils.isEmpty(cjrl.getEffectBad())) {

                if (!TextUtils.isEmpty(cjrl.getEffectGood())) {
                    yingString += ",";
                }
                yingString += "利空 " + cjrl.getEffectBad();
            }
        }


        view.setTextViewText(R.id.calendar_item_listview_version2_4main_effect, "影响：" + yingString);
        if (cInfo.getNature().equals("高")) {
            view.setImageViewResource(R.id.calendar_item_nature,
                    R.drawable.nature_high);
        } else if (cInfo.getNature().equals("低")) {
            view.setImageViewResource(R.id.calendar_item_nature,
                    R.drawable.nature_low);
        } else if (cInfo.getNature().equals("中")) {
            view.setImageViewResource(R.id.calendar_item_nature,
                    R.drawable.nature_mid);
        } else {
            view.setImageViewResource(R.id.calendar_item_nature,
                    R.drawable.nature_high);
        }

    }

    // 13:40
    public void send(Context context, int icon, String title, String content,
                     boolean pushsound, int id, NoticBean bean) {
        Intent intent = null;
        // 1 得到通知管理器
        NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        // 2构建通知
        // Notification notification = new Notification(R.drawable.ic_launcher,
        // title, System.currentTimeMillis());
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setDefaults(Notification.DEFAULT_VIBRATE);
        builder.setWhen(System.currentTimeMillis());
        // 振动
        // notification.defaults |= Notification.DEFAULT_VIBRATE;
        // 响铃
        if (pushsound) {
            // notification.sound = Uri.parse("android.resource://"
            // + context.getPackageName() + "/" + R.raw.kxt_notify); //
            // Uri.parse(String

            builder.setSound(Uri.parse("android.resource://"
                    + context.getPackageName() + "/" + R.raw.kxt_notify));
        }
        // 3设置通知的点击事件
        if (null == bean) {
            intent = new Intent(context, FlashActivity.class); // 跳到MainActivity
            intent.putExtra("id", "" + id);
            intent.putExtra("enterpage", "notification");// shou
            intent.putExtra("type", "1");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            contentIntent = PendingIntent.getActivity(context, id, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        } else if (bean.getAction().getType().equals("video")) {
            Log.i("zml", "url=" + bean.getAction().getUrl());
            intent = new Intent(context, SuperPlayerActivity.class);
            String urlString = bean.getAction().getUrl();
            intent.putExtra("id", urlString.substring(urlString.lastIndexOf("/") + 1, urlString.length()));
            intent.putExtra("url", bean.getAction().getUrl());
            intent.putExtra("title", bean.getTitle());
            intent.putExtra("share", bean.getAction().getShareurl());
            intent.putExtra("type", bean.getAction().getType());
            intent.putExtra("type3", "2");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);

            contentIntent = PendingIntent.getActivity(context, count
                            + new Random(100000).nextInt(), intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            count++;
        } else if (bean.getAction().getType().equals("dianping")) {
            intent = new Intent(context, DPWebActivity_new.class); // 跳到AdWebActivity
            String urlString = bean.getAction().getUrl();
            intent.putExtra("id", urlString.substring(urlString.lastIndexOf("/") + 1, urlString.length()));
            intent.putExtra("url", bean.getAction().getUrl());
            intent.putExtra("title", bean.getTitle());
            intent.putExtra("share", bean.getAction().getShareurl());
            intent.putExtra("type", bean.getAction().getType());
            intent.putExtra("type3", "3");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            contentIntent = PendingIntent.getActivity(context, count
                            + new Random(100000).nextInt(), intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            count++;
        } else if (bean.getAction().getType().equals("news")) {
            intent = new Intent(context, DPWebActivity_new.class); // 跳到AdWebActivity
            String urlString = bean.getAction().getUrl();
            intent.putExtra("id", urlString.substring(urlString.lastIndexOf("/") + 1, urlString.length()));
            intent.putExtra("url", bean.getAction().getUrl());
            intent.putExtra("title", bean.getTitle());
            intent.putExtra("share", bean.getAction().getShareurl());
            intent.putExtra("type", bean.getAction().getType());
            intent.putExtra("type3", "1");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            contentIntent = PendingIntent.getActivity(context, count
                            + new Random(100000).nextInt(), intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            count++;
        } else {
            intent = new Intent(context, DPWebActivity.class); // 跳到AdWebActivity
            intent.putExtra("url", bean.getAction().getUrl());
            intent.putExtra("title", bean.getTitle());
            intent.putExtra("share", bean.getAction().getShareurl());
            intent.putExtra("type", bean.getAction().getType());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            contentIntent = PendingIntent.getActivity(context, count
                            + new Random(100000).nextInt(), intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            count++;
        }

        // notification.setLatestEventInfo(context, title, content,
        // contentIntent);

        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setContentIntent(contentIntent);
        builder.setAutoCancel(true);// 点击通知之后自动消失
        // builder.set
        // notification.flags = Notification.FLAG_AUTO_CANCEL;
        // 4发送通知
        nm.notify(count + new Random(100000).nextInt(), builder.build());
    }*/

    /**
     * 友盟推送
     *
     * @param context
     * @param icon
     * @param title
     * @param content
     * @param pushsound
     * @param id
     * @param bean
     */
    public void sendUmenPush(Context context, String icon, String title, String content,
                             boolean pushsound, int id, PushBean bean) {
        Intent intent = null;
        // 1 得到通知管理器
        NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setDefaults(Notification.DEFAULT_VIBRATE);
        builder.setWhen(System.currentTimeMillis());
        // 响铃
        if (pushsound) {

            builder.setSound(Uri.parse("android.resource://"
                    + context.getPackageName() + "/" + R.raw.kxt_notify));
        }
        // 3设置通知的点击事件
        if (bean == null) return;
        String code = bean.code;
        if (code == null) return;
        if (code.equals("KUAIXUN")) {
            intent = new Intent(context, FlashActivity.class); // 跳到快讯
            intent.putExtra(IntentConstant.O_ID, bean.id);
            contentIntent = PendingIntent.getActivity(context, id, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            count++;
        } else if (code.equals("video")) {
            intent = new Intent(context, VideoDetailActivity.class);
            intent.putExtra(IntentConstant.O_ID, bean.id);

            contentIntent = PendingIntent.getActivities(context, count
                            + new Random(100000).nextInt(), new Intent[]{intent},
                    PendingIntent.FLAG_UPDATE_CURRENT);
            count++;
        } else if (code.equals("dianping")) {
            intent = new Intent(context, NewsContentActivity.class); // 跳到AdWebActivity
            intent.putExtra(IntentConstant.O_ID, bean.id);
            contentIntent = PendingIntent.getActivity(context, count
                            + new Random(100000).nextInt(), intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            count++;
        } else if (code.equals("news")) {
            intent = new Intent(context, NewsContentActivity.class); // 跳到AdWebActivity
            intent.putExtra(IntentConstant.O_ID, bean.id);
            contentIntent = PendingIntent.getActivity(context, count
                            + new Random(100000).nextInt(), intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            count++;
        }


        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setContentIntent(contentIntent);
        builder.setAutoCancel(true);// 点击通知之后自动消失
        // builder.set
        // notification.flags = Notification.FLAG_AUTO_CANCEL;
        // 4发送通知
        nm.notify(count + new Random(100000).nextInt(), builder.build());

    }

    public void sendPushUmengNoifiCJRL(Context context, PushBean1 kxHisItemBen, boolean pushsound) {


        KxItemCJRL kxItemCJRL = JSON.parseObject(kxHisItemBen.getContent().toString(), KxItemCJRL.class);

        if (kxItemCJRL == null) {
            return;
        }

        if (manager == null) {
            manager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if (notification == null) {
            notification = new NotificationCompat.Builder(context);
        }
        notification.setContentTitle(kxItemCJRL.getState() + " " + kxItemCJRL.getTitle());
        notification.setSmallIcon(R.mipmap.ic_launcher);
        notification.setWhen(System.currentTimeMillis());
        // 振动
        notification.setDefaults(Notification.DEFAULT_VIBRATE);
        // 响铃
        if (pushsound) {
            notification.setSound(Uri.parse("android.resource://"
                    + context.getPackageName() + "/" + R.raw.kxt_notify));

        }

        Intent notificationIntent = new Intent(context, FlashActivity.class);
        notificationIntent.putExtra(IntentConstant.O_ID, kxHisItemBen.id);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                Integer.valueOf(kxHisItemBen.id), notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.text_notification);
        findViewsNew(views, kxItemCJRL, context);
        notification.setContent(views);
        notification.setAutoCancel(true);
        notification.setContentIntent(pendingIntent);
        manager.notify(Integer.valueOf(kxHisItemBen.id), notification.build());


    }

    public void findViewsNew(RemoteViews view, KxItemCJRL cInfo, Context context) {
        KxItemCJRL cjrl = cInfo;
        view.setTextViewText(R.id.calendar_item_listview_version2_nfi,
                cjrl.getState() + "  " + cjrl.getTitle());
        view.setTextViewText(R.id.calendar_listview_item_tv_title_time,
                cjrl.getTime().substring(11, 16));
        view.setTextViewText(R.id.calendar_item_listview_version2_before, "前值:"
                + cjrl.getBefore());
        view.setTextViewText(R.id.calendar_item_listview_version2_forecast,
                "预测:" + cjrl.getForecast());
        view.setTextViewText(R.id.calendar_item_listview_version2_gongbu, ""
                + cjrl.getReality());

        String yingString = "";

        CjInfo cjInfo = new CjInfo();
        String title = cjrl.getEffect();
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

    /*public void sendPushUmengNoification(Context context, CjInfo cInfo, boolean pushsound) {

		*//*boolean PRE_CUPCAKE = getSDKVersionNumber() < SDK_VERSION_CURR ? true
                : false;
		if (PRE_CUPCAKE) {
			NotificationManager nm = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			Notification notification = new Notification();
			notification.icon = R.drawable.ic_launcher;// 图标
			notification.tickerText = cInfo.getNfi();
			notification.when = System.currentTimeMillis();
			notification.defaults = Notification.DEFAULT_VIBRATE;
			if (pushsound) {
				notification.sound = Uri.parse("android.resource://"
						+ context.getPackageName() + "/" + R.raw.kxt_notify);
			}
			RemoteViews contentView = new RemoteViews(context.getPackageName(),
					R.layout.text_notification);
			findViews(contentView, cInfo, context);
			notification.contentView = contentView;// 通知显示的布局
			// Intent intent = new Intent(context, MainActivity.class); //
			// 跳到MainActivity
			// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
			// Intent.FLAG_ACTIVITY_NEW_TASK);
			// intent.putExtra("data", "data");

			Intent intent = new Intent(context, FlashActivity.class);

			// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
			// | Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("cInfo", cInfo);
			intent.putExtra("id", cInfo.getId());
			intent.putExtra("enterpage", "notification");// shou
			intent.putExtra("type", "2");
			PendingIntent contentIntent = PendingIntent.getActivity(context,
					Integer.valueOf(cInfo.getId()), intent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			notification.contentIntent = contentIntent;// 点击的事件

			notification.flags = Notification.FLAG_AUTO_CANCEL;// 点击通知之后自动消失
			nm.notify(Integer.valueOf(cInfo.getId()), notification);

		} else {*//*
        if (manager == null) {
            manager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if (notification == null) {
            notification = new NotificationCompat.Builder(context);
        }
        notification.setContentTitle(cInfo.getNfi());
        notification.setSmallIcon(R.drawable.ic_launcher);
        notification.setWhen(System.currentTimeMillis());
        // 振动
        notification.setDefaults(Notification.DEFAULT_VIBRATE);
        // 响铃
        if (pushsound) {
            notification.setSound(Uri.parse("android.resource://"
                    + context.getPackageName() + "/" + R.raw.kxt_notify));

        }

        Intent notificationIntent = new Intent(context, FlashActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationIntent.putExtra("id", cInfo.getId());
        notificationIntent.putExtra("enterpage", "notification");// shou
        notificationIntent.putExtra("type", "2");
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                Integer.valueOf(cInfo.getId()), notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.text_notification);
        findViews(views, cInfo, context);
        notification.setContent(views);
        notification.setAutoCancel(true);
        notification.setContentIntent(pendingIntent);
        manager.notify(Integer.valueOf(cInfo.getId()), notification.build());


    }


    public void sendTitle(Context context, int icon, String title,
                          String content, boolean pushsound) {
        Intent intent = null;
        // 1 得到通知管理器
        NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        // 2构建通知
        // Notification notification = new Notification(R.drawable.ic_launcher,
        // title, System.currentTimeMillis());
        // // 振动
        // notification.defaults |= Notification.DEFAULT_VIBRATE;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setDefaults(Notification.DEFAULT_VIBRATE);
        builder.setWhen(System.currentTimeMillis());
        // 响铃
        if (pushsound) {
            // notification.sound = Uri.parse("android.resource://"
            // + context.getPackageName() + "/" + R.raw.kxt_notify); //
            // Uri.parse(String
            builder.setSound(Uri.parse("android.resource://"
                    + context.getPackageName() + "/" + R.raw.kxt_notify));
        }
        intent = new Intent(context, MainActivity.class); // 跳到MainActivity
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        contentIntent = PendingIntent.getActivity(context,
                Integer.valueOf(content), intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        count++;

        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setContentIntent(contentIntent);
        builder.setAutoCancel(true);// 点击通知之后自动消失
        // notification.setLatestEventInfo(context, title, content,
        // contentIntent);
        // // builder.set
        // notification.flags = Notification.FLAG_AUTO_CANCEL;// 点击通知之后自动消失
        // 4发送通知
        nm.notify(count + new Random(100000).nextInt(), builder.build());
    }

    public void custom(Context context, int icon, String title, String content) {
        NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification();
        notification.icon = R.drawable.ic_launcher;// 图标
        notification.tickerText = "拦截到了新的短信";
        RemoteViews contentView = new RemoteViews(context.getPackageName(),
                R.layout.kxt_notification_content);
        notification.contentView = contentView;// 通知显示的布局
        Intent intent = new Intent(context, MainActivity.class); // 跳到MainActivity
        PendingIntent contentIntent = PendingIntent.getActivity(context, 100,
                intent, 0);
        notification.contentIntent = contentIntent;// 点击的事件
        notification.flags = Notification.FLAG_AUTO_CANCEL;// 点击通知之后自动消失
        nm.notify(100, notification);
    }*/

}
