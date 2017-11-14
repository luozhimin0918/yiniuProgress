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
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.base.utils.bean.LoginBean;
import com.jyh.kxt.base.widget.night.ThemeUtil;
import com.jyh.kxt.index.json.MainInitJson;
import com.jyh.kxt.user.json.UserJson;
import com.jyh.kxt.user.ui.BindActivity;
import com.jyh.kxt.user.ui.LoginActivity;
import com.library.base.http.HttpListener;
import com.library.base.http.PreCacheHttpResponse;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.bean.EventBusClass;
import com.library.util.RegexValidateUtil;
import com.library.util.SPUtils;
import com.library.widget.window.ToastView;

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

    /**
     * 请求验证码
     *
     * @param context
     * @param type
     * @param user
     * @param tag
     * @param observerData
     */
    public static void requestCode(final BasePresenter context, String type, String user, String tag, final ObserverData
            observerData) {
        VolleyRequest volleyRequest = new VolleyRequest(context.mContext, context.mQueue);
        volleyRequest.setTag(tag);
        JSONObject jsonParam = volleyRequest.getJsonParam();

        if (RegexValidateUtil.checkCellphone(user)) {
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
                ToastView.makeText(context.mContext, error == null || error.getMessage() == null ? "验证码获取失败" : error.getMessage());
                observerData.onError(error);
            }
        });
    }

    /**
     * 验证验证码
     *
     * @param context
     * @param type
     * @param user
     * @param code
     * @param tag
     * @param observerData
     */
    public static void verifyCode(BasePresenter context, String type, String user, String code, String tag, final
    ObserverData observerData) {
        VolleyRequest volleyRequest = new VolleyRequest(context.mContext, context.mQueue);
        volleyRequest.setTag(tag);
        JSONObject jsonParam = volleyRequest.getJsonParam();

        if (RegexValidateUtil.checkCellphone(user)) {
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

    /**
     * 有密码时 手机或邮箱绑定
     *
     * @param context
     * @param user
     * @param code
     * @param tag
     * @param observerData
     */
    public static void bindPwd(BasePresenter context, String user, String code, String tag, final
    ObserverData observerData) {
        VolleyRequest volleyRequest = new VolleyRequest(context.mContext, context.mQueue);
        volleyRequest.setTag(tag);
        JSONObject jsonParam = volleyRequest.getJsonParam();

        if (RegexValidateUtil.checkCellphone(user)) {
            jsonParam.put("phone", user);
        } else {
            jsonParam.put("email", user);
        }
        jsonParam.put("type", "bind");
        jsonParam.put("code", code);
        jsonParam.put("uid", getUserInfo(context.mContext).getUid());

        volleyRequest.doPost(HttpConstant.USER_BIND_PHONE_EMAIL, jsonParam, new HttpListener<Object>() {
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

    /**
     * 绑定并设置密码(无密码绑定最后一步)
     *
     * @param presenter
     * @param user
     * @param pwd
     * @param tag
     * @param observerData
     */
    public static void bindNoPwd(BasePresenter presenter, String user, String pwd, String tag, final ObserverData observerData) {
        VolleyRequest request = new VolleyRequest(presenter.mContext, presenter.mQueue);
        request.setTag(presenter.getClass().getName());
        JSONObject jsonParam = request.getJsonParam();

        jsonParam.put(VarConstant.HTTP_UID, getUserInfo(presenter.mContext).getUid());
        jsonParam.put(VarConstant.HTTP_PWD, pwd);
        if (RegexValidateUtil.checkCellphone(user)) {
            jsonParam.put(VarConstant.HTTP_PHONE, user);
        } else {
            jsonParam.put(VarConstant.HTTP_EMAIL, user);
        }

        request.doPost(HttpConstant.USER_SET_BIND_WITH_PWD, jsonParam, new HttpListener<Object>() {
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


    /**
     * 设置、修改、忘记密码
     *
     * @param presenter
     * @param user
     * @param oldPwd
     * @param newPwd
     * @param tag
     * @param observerData
     */
    public static void changePwd(BasePresenter presenter, String user, String oldPwd, String newPwd, String
            tag, final
                                 ObserverData observerData) {
        VolleyRequest volleyRequest = new VolleyRequest(presenter.mContext, presenter.mQueue);
        volleyRequest.setTag(tag);
        JSONObject jsonParam = volleyRequest.getJsonParam();

        if (!RegexValidateUtil.isEmpty(user)) {
            if (RegexValidateUtil.checkCellphone(user)) {
                jsonParam.put(VarConstant.HTTP_PHONE, user);
            } else {
                jsonParam.put(VarConstant.HTTP_EMAIL, user);
            }
        }

        UserJson userInfo = getUserInfo(presenter.mContext);
        if (userInfo != null) {
            jsonParam.put(VarConstant.HTTP_UID, userInfo.getUid());
        }
        jsonParam.put(VarConstant.HTTP_PWD_NEW, newPwd);
        jsonParam.put(VarConstant.HTTP_PWD_OLD, oldPwd);
        volleyRequest.doPost(HttpConstant.USER_SET_PASSWORD, jsonParam, new HttpListener<Object>() {
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

    /**
     * 登录
     */
    public static void requestLogin(final BasePresenter presenter, String user, String pwd, String type, String tag,
                                    final ObserverData<UserJson> observerData) {
        VolleyRequest request = new VolleyRequest(presenter.mContext, presenter.mQueue);
        request.setTag(tag);
        JSONObject jsonParam = request.getJsonParam();
        jsonParam.put(VarConstant.HTTP_TYPE, type);
        if (type.equals(VarConstant.LOGIN_TYPE_DEFAULT)) {
            jsonParam.put(VarConstant.HTTP_USERNAME, user);
            jsonParam.put(VarConstant.HTTP_PWD, pwd);
        } else {
            jsonParam.put(VarConstant.HTTP_PHONE, user);
            jsonParam.put(VarConstant.HTTP_CODE, pwd);
        }

        request.doPost(HttpConstant.USER_LOGIN2, jsonParam, new HttpListener<UserJson>() {
            @Override
            protected void onResponse(UserJson userJson) {
                observerData.callback(userJson);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                observerData.onError(error);
            }
        });
    }

//    /**
//     * 三方登录
//     */
//    public static void requestLogin2(final BaseActivity presenter, LoginBean loginBean, final ObserverData<UserJson> observerData) {
//        VolleyRequest request = new VolleyRequest(presenter, presenter.getQueue());
//        JSONObject jsonParam = request.getJsonParam();
//        jsonParam.put(VarConstant.HTTP_USERNAME, loginBean.getUsername());
//        jsonParam.put(VarConstant.HTTP_TYPE, loginBean.getType());
//        jsonParam.put(VarConstant.HTTP_CITY, loginBean.getCity());
//        jsonParam.put(VarConstant.HTTP_AVATAR, loginBean.getAvatar());
//        jsonParam.put(VarConstant.HTTP_PROVINCE, loginBean.getProvince());
//        jsonParam.put(VarConstant.HTTP_SEX, loginBean.getSex());
//        jsonParam.put(VarConstant.HTTP_ACCESS_TOKEN2, loginBean.getAccess_token2());
//        jsonParam.put(VarConstant.HTTP_OPENID, loginBean.getOpenid());
//        jsonParam.put(VarConstant.HTTP_UNIONID, loginBean.getUnionid());
//        request.doPost(HttpConstant.USER_LOGIN2, jsonParam, new HttpListener<UserJson>() {
//            @Override
//            protected void onResponse(UserJson userJson) {
//                login(presenter, userJson);
//                observerData.callback(userJson);
//            }
//
//            @Override
//            protected void onErrorResponse(VolleyError error) {
//                super.onErrorResponse(error);
//                observerData.onError(error);
//            }
//        });
//    }

    /**
     * 默认允许进行评论等操作
     *
     * @param mContext
     * @return
     */
    public static boolean isToBindPhoneInfo(final Context mContext) {
        try {
            UserJson userInfo = getUserInfo(mContext);
            if (userInfo == null) {
                mContext.startActivity(new Intent(mContext, LoginActivity.class));
                return true;
            }

            String configStr = SPUtils.getString(mContext, SpConstant.INIT_LOAD_APP_CONFIG);
            MainInitJson config = JSON.parseObject(configStr, MainInitJson.class);
            if (config.getIs_bind() == 0) {
                return true;
            } else {
                if (!userInfo.isSetPhone()) {
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean isUnReadAction(Context mContext) {
        String mHtMoreNewestId = SPUtils.getString(mContext, SpConstant.HT_MORE_NEWEST_ID);
        String mMoreNewestId = SPUtils.getString(mContext, SpConstant.MORE_NEWEST_ID);

        if (mHtMoreNewestId.equals(mMoreNewestId) || "0".equals(mHtMoreNewestId)) {
            return false;
        }
        return true;
    }
}
