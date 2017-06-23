package com.jyh.kxt.push;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.index.ui.MainActivity;
import com.jyh.kxt.push.json.KxItemKuaiXun;
import com.jyh.kxt.push.json.PushBean1;
import com.jyh.kxt.push.json.PushBean2;
import com.library.util.RegexValidateUtil;
import com.library.util.SPUtils;
import com.umeng.message.UmengMessageService;
import com.umeng.message.entity.UMessage;

import org.android.agoo.common.AgooConstants;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

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
        Boolean push_sound = SPUtils.getBooleanTrue(KXTPushIntentService.this, SpConstant.SETTING_SOUND);
        Boolean push = SPUtils.getBooleanTrue(KXTPushIntentService.this, SpConstant.SETTING_PUSH);

        try {
            String after_open = msg.after_open;
            switch (after_open) {
                case UMessage.NOTIFICATION_GO_CUSTOM:
                    //自定义
                    SendNotificationCompOK(msg, push, push_sound);
                    break;
                case UMessage.NOTIFICATION_GO_ACTIVITY:
                    //打开特定的activity
                    break;
                case UMessage.NOTIFICATION_GO_APP:
                    //打开应用
                    break;
                case UMessage.NOTIFICATION_GO_APPURL:
                    break;
                case UMessage.NOTIFICATION_GO_URL:
                    //跳转到URL
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void SendNotificationCompOK(UMessage msg, boolean push, boolean push_sound) {

        if (null != msg.custom && !msg.custom.equals("") && msg.after_open.equals("go_custom")) {
            try {
                JSONObject object = new JSONObject(msg.custom.toString());
                String code = object.optString("code");
                if (code.equals("KUAIXUN")) {
                    //快讯
                    PushBean1 kxunInfo = JSON.parseObject(object.toString(), PushBean1.class);
                    kxunInfo.title = msg.title;
                    kxunInfo.tvContent = msg.text;
                    if (push && kxunInfo != null && !TextUtils.isEmpty(kxunInfo.getContent().toString())) {
                        KxItemKuaiXun itemKuaiXun = JSON.parseObject(kxunInfo.getContent().toString(), KxItemKuaiXun.class);
                        if (itemKuaiXun != null) {
                            Random randow = new Random();
                            NotificationKXT notification = new NotificationKXT();
                            notification.sendUmenPush(
                                    KXTPushIntentService.this,
                                    msg.icon,
                                    "快讯通财经【快讯】",
                                    itemKuaiXun.getTitle(),
                                    push_sound,
                                    randow.nextInt(100000), kxunInfo);
                        }
                    }
                } else if (code.equals("CJRL")) {
                    // 财经
                    PushBean1 cjInfo = JSON.parseObject(object.toString(), PushBean1.class);
                    cjInfo.title = msg.title;
                    cjInfo.tvContent = msg.text;
                    if (push && cjInfo != null) {
                        NotificationKXT notification = new NotificationKXT();
                        notification.sendPushUmengNoifiCJRL(
                                KXTPushIntentService.this,
                                cjInfo, push_sound);
                    }
                } else if (code.equals("news") || code.equals("dianping")) {
                    // 文章
                    PushBean2 news = JSON.parseObject(object.toString(), PushBean2.class);
                    if (push && news != null && news.getUrl() != null) {
                        String url = news.getUrl();
                        String id = url.substring(url.indexOf("/id") + 3);
                        news.title = msg.title;
                        news.tvContent = msg.text;
                        if (!RegexValidateUtil.isEmpty(id)) {
                            news.id = id;
                            Random randow = new Random();
                            NotificationKXT notification = new NotificationKXT();
                            notification.sendUmenPush(
                                    KXTPushIntentService.this,
                                    msg.icon,
                                    "快讯通财经【要闻】",
                                    object.getString("title"),
                                    push_sound,
                                    randow.nextInt(100000), news);
                        }
                    }

                } else if (code.equals("video")) {

                    PushBean2 video = JSON.parseObject(object.toString(), PushBean2.class);
                    if (push && video != null && video.getUrl() != null) {
                        video.title = msg.title;
                        video.tvContent = msg.text;
                        String url = video.getUrl();
                        String id = url.substring(url.indexOf("/id") + 3);
                        if (!RegexValidateUtil.isEmpty(id)) {
                            video.id = id;
                            Random randow = new Random();
                            NotificationKXT notification = new NotificationKXT();
                            notification.sendUmenPush(
                                    KXTPushIntentService.this,
                                    msg.icon,
                                    "快讯通财经【视听】",
                                    object.getString("title"),
                                    push_sound,
                                    randow.nextInt(1000000), video);
                        }
                    }
                } else {
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}
