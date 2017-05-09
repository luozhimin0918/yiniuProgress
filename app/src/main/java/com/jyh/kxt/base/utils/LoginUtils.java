package com.jyh.kxt.base.utils;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.deser.impl.NullProvider;
import com.jyh.kxt.base.constant.SpConstant;
import com.library.bean.EventBusClass;
import com.library.util.SPUtils;
import com.jyh.kxt.user.json.UserJson;

import org.greenrobot.eventbus.EventBus;

/**
 * 项目名:Kxt
 * 类描述:登录
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/2.
 */

public class LoginUtils {

    /**
     * 登录
     *
     * @param context
     * @param userInfo
     */
    public static void login(Context context, UserJson userInfo) {
        SPUtils.save(context, SpConstant.USERINFO, JSON.toJSONString(userInfo));
        EventBus.getDefault().post(new EventBusClass(EventBusClass.EVENT_LOGIN, userInfo));
    }

    /**
     * 更新用户信息
     *
     * @param context
     * @param userInfo
     */
    public static void changeUserInfo(Context context, UserJson userInfo) {
        SPUtils.save(context, SpConstant.USERINFO, userInfo);
    }

    /**
     * 退出账号
     *
     * @param context
     */
    public static void logout(Context context) {
        SPUtils.save(context, SpConstant.USERINFO, "");
        EventBus.getDefault().post(new EventBusClass(EventBusClass.EVENT_LOGOUT, null));
    }

    /**
     * 是否登录
     *
     * @param context
     * @return
     */
    public static boolean isLogined(Context context) {
        String userInfo = SPUtils.getString(context, SpConstant.USERINFO);
        if (TextUtils.isEmpty(userInfo))
            return false;
        else
            return true;
    }

    /**
     * 获取用户信息
     *
     * @param context
     * @return
     */
    public static UserJson getUserInfo(Context context) {
        String userInfoStr = SPUtils.getString(context, SpConstant.USERINFO);
        try {
            return JSON.parseObject(userInfoStr, UserJson.class);
        } catch (Exception e) {
            return null;
        }
    }

}
