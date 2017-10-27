package com.jyh.kxt.base.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.base.widget.night.ThemeUtil;
import com.jyh.kxt.user.json.UserJson;
import com.jyh.kxt.user.ui.BindActivity;
import com.jyh.kxt.user.ui.LoginActivity;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;
import com.library.bean.EventBusClass;
import com.library.util.SPUtils;

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

//        setPushTag(context, userInfo);
    }

    /**
     * 更新用户信息
     *
     * @param context
     * @param userInfo
     */
    public static void changeUserInfo(Context context, UserJson userInfo) {
        SPUtils.save(context, SpConstant.USERINFO, JSON.toJSONString(userInfo));

//        setPushTag(context, userInfo);
    }

//    private static void setPushTag(Context context, UserJson userInfo) {
//        try {
//            //登录之后绑定用户信息
//            String pushUid = SPUtils.getString(context, SpConstant.PUSH_AGENT_TAG);
//            if (TextUtils.isEmpty(pushUid) || !userInfo.getUid().equals(pushUid)) {
//                SPUtils.save(context, SpConstant.PUSH_AGENT_TAG, userInfo.getUid());
//
//                PushAgent mPushAgent = PushAgent.getInstance(SampleApplicationContext.context);
//                mPushAgent.addAlias(userInfo.getUid(), "uid", new UTrack.ICallBack() {
//                    @Override
//                    public void onMessage(boolean isSuccess, String message) {
//                    }
//                });
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 退出账号
     *
     * @param context
     */
    public static void logout(Context context) {
        SPUtils.save(context, SpConstant.USERINFO, "");
        UmengLoginTool.logout((Activity) context);
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
        if (TextUtils.isEmpty(userInfo)) {
            return false;
        } else {
            return true;
        }
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

    public static void requestCode(BasePresenter context, String type, boolean isPhone, String user, String tag, final ObserverData observerData) {
        VolleyRequest volleyRequest = new VolleyRequest(context.mContext, context.mQueue);
        volleyRequest.setTag(tag);
        JSONObject jsonParam = volleyRequest.getJsonParam();

        if (isPhone) {
            jsonParam.put("phone", user);
        } else {
            jsonParam.put("email", user);
        }
        jsonParam.put("type", type);

        volleyRequest.doPost(HttpConstant.USER_CODE_REQUEST, jsonParam, new HttpListener<Object>() {
            @Override
            protected void onResponse(Object o) {
                observerData.callback(o);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                observerData.onError(error);
            }
        });
    }

    public static void verifyCode(BasePresenter context, String type, boolean isPhone, String user, String code, String tag, final ObserverData observerData) {
        VolleyRequest volleyRequest = new VolleyRequest(context.mContext, context.mQueue);
        volleyRequest.setTag(tag);
        JSONObject jsonParam = volleyRequest.getJsonParam();

        if (isPhone) {
            jsonParam.put("phone", user);
        } else {
            jsonParam.put("email", user);
        }
        jsonParam.put("type", type);
        jsonParam.put("code", code);

        volleyRequest.doPost(HttpConstant.USER_CODE_VERIFY, jsonParam, new HttpListener<Object>() {
            @Override
            protected void onResponse(Object o) {
                observerData.callback(o);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                observerData.onError(error);
            }
        });
    }

    public static boolean isToBindPhoneInfo(final Context mContext) {
        // FixKxt: 提出人:Mr'Dai-> 判断手机号码是否已经绑定过了
        UserJson userInfo = getUserInfo(mContext);
        if (userInfo == null) {
            mContext.startActivity(new Intent(mContext, LoginActivity.class));
            return true;
        }
        if (userInfo.getIs_set_phone() == null ||
                !userInfo.getIs_set_phone()) {

            new AlertDialog.Builder(mContext, ThemeUtil.getAlertTheme(mContext))
                    .setPositiveButton("取消",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                    .setNegativeButton("绑定手机",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent bindIntent = new Intent(mContext, BindActivity.class);
                                    bindIntent.putExtra(BindActivity.TYPE, BindActivity.TYPE_BIND_PHONE);
                                    mContext.startActivity(bindIntent);
                                }
                            })
                    .setTitle("提示")
                    .setMessage("根据网络安全法对互联网实名制的要求,请您尽快完成手机号验证")
                    .show();
            return true;
        }
        return false;
    }

}
