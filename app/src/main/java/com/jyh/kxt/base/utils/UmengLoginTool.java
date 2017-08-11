package com.jyh.kxt.base.utils;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.user.json.UserJson;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.util.EncryptionUtils;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.HashMap;
import java.util.Map;

/**
 * 项目名:Kxt
 * 类描述:登录工具类
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/12.
 */

public class UmengLoginTool {

    public static void umenglogin(final BaseActivity context, SHARE_MEDIA share_media) {

        UMShareAPI umShareAPI = UMShareAPI.get(context);
        UMAuthListener umAuthListener = new UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA platform) {
                //授权开始的回调
                context.showWaitDialog("登录中");
            }

            @Override
            public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
                UmengLoginTool.onComplete(context, platform, action, data);
            }

            @Override
            public void onError(SHARE_MEDIA platform, int action, Throwable t) {
                context.dismissWaitDialog();
            }

            @Override
            public void onCancel(SHARE_MEDIA platform, int action) {
                context.dismissWaitDialog();
            }
        };
        umShareAPI.getPlatformInfo(context, share_media, umAuthListener);
    }

    private static void onComplete(BaseActivity context, SHARE_MEDIA platform, int action, Map<String, String> data) {
        login(context, platform, data);
    }

    private static void login(final BaseActivity context, SHARE_MEDIA platform, Map data) {
        RequestQueue queue = context.getQueue();
        VolleyRequest request = new VolleyRequest(context, queue);
        Map map = getMap(request, platform, data);
        request.doPost(HttpConstant.USER_LOGIN, map, new HttpListener<UserJson>() {
            @Override
            protected void onResponse(UserJson user) {
                LoginUtils.login(context, user);
//                EventBus.getDefault().post(new EventBusClass(EventBusClass.EVENT_LOGIN, user));//上面会发送一个登录广播
                context.dismissWaitDialog();
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                context.dismissWaitDialog();
            }
        });
    }

    public static void logout(Activity context){
        UMShareAPI.get(context).deleteOauth(context,SHARE_MEDIA.QQ,null);
        UMShareAPI.get(context).deleteOauth(context,SHARE_MEDIA.SINA,null);
        UMShareAPI.get(context).deleteOauth(context,SHARE_MEDIA.WEIXIN,null);
    }

    @NonNull
    private static Map getMap(VolleyRequest request, SHARE_MEDIA platform, Map<String, String> data) {
        Map map = new HashMap();
        JSONObject jsonParam = request.getJsonParam();

        String name = "";
        String uid = "";
        String accesstoken = "";
        String sex = "0";////1男 ，2 女，0保密
        String city = "";
        String prvinice = "";
        String openid = "";
        String img = "";
        String type = "";// 1:QQ|2:微信|3:微博
        String unionid = "";

        switch (platform) {
            case QQ:
//                name：name（6.2以前用screen_name）
//                用户id：uid
//                accesstoken: accessToken （6.2以前用access_token）
//                过期时间：expiration （6.2以前用expires_in）
//                性别：gender
//                头像：iconurl（6.2以前用profile_image_url）
//                城市：city
//                省份：province

                type = "1";
                name = data.get("name");
                accesstoken = data.get("accessToken");
                String qqGender = data.get("gender");
                if (qqGender != null)
                    switch (qqGender) {
                        case "男":
                            sex = "1";
                            break;
                        case "女":
                            sex = "2";
                            break;
                        default:
                            sex = "0";
                            break;
                    }
                openid=data.get("openid");
                uid=data.get("unionid");
                img = data.get("iconurl");
                city = data.get("city");
                prvinice = data.get("province");
                break;
            case SINA:
//                用户id：uid
//                accesstoken: accessToken （6.2以前用access_token）
//                refreshtoken: （6.2以前用refresh_token）
//                过期时间：expiration （6.2以前用expires_in）
//                用户名：name（6.2以前用screen_name）
//                头像：iconurl（6.2以前用profile_image_url）
//                性别：gender

                type = "3";
                name = data.get("name");
                openid = data.get("id");
                unionid = data.get("id");
                accesstoken = data.get("accessToken");
                String sinaGender = data.get("gender");
                if (sinaGender != null)
                    switch (sinaGender) {
                        case "男":
                            sex = "1";
                            break;
                        case "女":
                            sex = "2";
                            break;
                        default:
                            sex = "0";
                            break;
                    }
                img = data.get("iconurl");

                break;
            case WEIXIN:
//                openid:openid
//                unionid:（6.2以前用unionid）用户id
//                accesstoken: accessToken （6.2以前用access_token）
//                refreshtoken: refreshtoken: （6.2以前用refresh_token）
//                过期时间：expiration （6.2以前用expires_in）
//                name：name（6.2以前用screen_name）
//                城市：city
//                省份：prvinice
//                性别：gender
//                头像：iconurl（6.2以前用profile_image_url）

                type = "2";
                name = data.get("name");
                openid = data.get("openid");
                unionid = data.get("unionid");
                accesstoken = data.get("access_token");
                String wxGender = data.get("gender");
                if (wxGender != null)
                    switch (wxGender) {
                        case "男":
                            sex = "1";
                            break;
                        case "女":
                            sex = "2";
                            break;
                        default:
                            sex = "0";
                            break;
                    }
                img = data.get("iconurl");
                city = data.get("city");
                prvinice = data.get("province");

                break;
        }


        jsonParam.put(VarConstant.HTTP_USERNAME, name);
        jsonParam.put(VarConstant.HTTP_TYPE, type);
        jsonParam.put(VarConstant.HTTP_CITY, city);
        jsonParam.put(VarConstant.HTTP_AVATAR, img);
        jsonParam.put(VarConstant.HTTP_PROVINCE, prvinice);
        jsonParam.put(VarConstant.HTTP_SEX, sex);
        jsonParam.put(VarConstant.HTTP_ACCESS_TOKEN2, accesstoken);
        jsonParam.put(VarConstant.HTTP_OPENID, openid);
        jsonParam.put(VarConstant.HTTP_UNIONID, unionid);

        try {
            map.put(VarConstant.HTTP_CONTENT2, EncryptionUtils.createJWT(VarConstant.KEY, jsonParam.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }
}
