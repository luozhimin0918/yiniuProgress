package com.jyh.kxt.push;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.jyh.kxt.av.ui.VideoDetailActivity;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.index.ui.MainActivity;
import com.jyh.kxt.index.ui.WebActivity;
import com.jyh.kxt.main.ui.activity.FlashActivity;
import com.jyh.kxt.main.ui.activity.NewsContentActivity;
import com.jyh.kxt.push.json.PushBean;
import com.library.manager.ActivityManager;
import com.library.util.SPUtils;

/**
 * Created by Mr'Dai on 2017/10/12.
 */

public class PushJsonHandle {
    private static PushJsonHandle pushJsonHandle;

    static final String KUAI_XUN = "KUAIXUN";
    static final String VIDEO = "video";
    static final String NEWS = "news";
    static final String DIAN_PING = "dianping";
    static final String CJRL = "CJRL";

    public static PushJsonHandle getInstance() {
        if (pushJsonHandle == null) {
            pushJsonHandle = new PushJsonHandle();
        }
        return pushJsonHandle;
    }

    public Intent getSkipIntent(Context mContext, PushBean bean) {
        String code = bean.code;
        Intent intent = null;

        if (PushJsonHandle.KUAI_XUN.equals(code) || PushJsonHandle.CJRL.equals(code)) {
            intent = new Intent(mContext, FlashActivity.class); // 跳到快讯
            intent.putExtra(IntentConstant.O_ID, bean.id);
        } else if (PushJsonHandle.VIDEO.equals(code)) {
            intent = new Intent(mContext, VideoDetailActivity.class); //跳转视听
            intent.putExtra(IntentConstant.O_ID, bean.id);
        } else if (PushJsonHandle.NEWS.equals(code) || DIAN_PING.equals(code)) {
            intent = new Intent(mContext, NewsContentActivity.class); // 跳转要闻
            intent.putExtra(IntentConstant.O_ID, bean.id);
        }else{
            if (bean.url != null){
                intent = new Intent(mContext, WebActivity.class); // 跳转要闻
                intent.putExtra(IntentConstant.WEBURL, bean.url);
            }
        }
        return intent;
    }

    /**
     * 华为通知的显示逻辑
     * @param mContext
     * @param notifyMessage
     */
    public void notificationHuaWeiDisplay(Context mContext, String notifyMessage) {
        try {
            if (TextUtils.isEmpty(notifyMessage)) {
                return;
            }

            Boolean pushStatus = SPUtils.getBooleanTrue(mContext, SpConstant.SETTING_PUSH);
            PushBean pushBean = JSONObject.parseObject(notifyMessage, PushBean.class);

            if (!pushStatus || pushBean == null) {
                return;
            }

            boolean existActivity = ActivityManager.getInstance().isExistActivity(MainActivity.class);
            if (existActivity) {
                Intent skipIntent = getSkipIntent(mContext, pushBean);
                mContext.startActivity(skipIntent);
            } else {
                Intent mainIntent = new Intent(mContext, MainActivity.class);

                Intent skipIntent = getSkipIntent(mContext, pushBean);
                mContext.startActivities(new Intent[]{mainIntent, skipIntent});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
