package com.jyh.kxt.base.utils;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.user.json.UserJson;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.util.EncryptionUtils;
import com.library.util.SPUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * 项目名:Kxt
 * 类描述:关注工具类
 * 创建人:苟蒙蒙
 * 创建日期:2017/6/6.
 */

public class AttentionUtils {

    public static final String TYPE_NEWS = "文章";
    public static final String TYPE_AUTHOR = "作者";

    /**
     * 关注
     *
     * @param context
     * @param type
     * @param id
     */
    public static void attention(final Context context, String type, final String id, final ObserverData<Boolean> observerData) {
        String url = "";
        String spType = "";
        switch (type) {
            case TYPE_AUTHOR:
                spType = SpConstant.ATTENTION_AUTHOR;
                url = HttpConstant.EXPLORE_BLOG_ADDFAVOR;
                break;
            case TYPE_NEWS:
                spType = SpConstant.ATTENTION_NEWS;
                url = HttpConstant.EXPLORE_BLOG_ADDFAVORARTICLE;
                break;
        }
        UserJson userInfo = LoginUtils.getUserInfo(context);
        VolleyRequest request = new VolleyRequest(context, Volley.newRequestQueue(context));
        JSONObject jsonParam = request.getJsonParam();
        jsonParam.put(VarConstant.HTTP_UID, userInfo.getUid());
        jsonParam.put(VarConstant.HTTP_ACCESS_TOKEN, userInfo.getToken());
        jsonParam.put(VarConstant.HTTP_ID, id);
        try {
            url += EncryptionUtils.createJWT(VarConstant.KEY, jsonParam.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        final String finalSpType = spType;
        request.doGet(url, new HttpListener<Object>() {
            @Override
            protected void onResponse(Object o) {
                Set<String> set = SPUtils.getStringSet(context, finalSpType);
                if (set == null)
                    set = new HashSet<>();
                set.add(id);
                SPUtils.save(context, SpConstant.ATTENTION_AUTHOR, set);
                observerData.callback(null);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                observerData.onError(null);
            }
        });

    }

    /**
     * 取消关注
     *
     * @param context
     * @param type
     * @param id
     */
    public static void unAttention(final Context context, String type, final String id, final ObserverData<Boolean> observerData) {
        String url = "";
        String spType = "";
        switch (type) {
            case TYPE_AUTHOR:
                spType = SpConstant.ATTENTION_AUTHOR;
                url = HttpConstant.EXPLORE_BLOG_DELETEFAVOR;
                break;
            case TYPE_NEWS:
                spType = SpConstant.ATTENTION_NEWS;
                url = HttpConstant.EXPLORE_BLOG_DELETEFAVORARTICLE;
                break;
        }
        UserJson userInfo = LoginUtils.getUserInfo(context);
        VolleyRequest request = new VolleyRequest(context, Volley.newRequestQueue(context));
        JSONObject jsonParam = request.getJsonParam();
        jsonParam.put(VarConstant.HTTP_UID, userInfo.getUid());
        jsonParam.put(VarConstant.HTTP_ACCESS_TOKEN, userInfo.getToken());
        jsonParam.put(VarConstant.HTTP_ID, id);
        try {
            url += EncryptionUtils.createJWT(VarConstant.KEY, jsonParam.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        final String finalSpType = spType;
        request.doGet(url, new HttpListener<Object>() {
            @Override
            protected void onResponse(Object o) {

                Set<String> set = SPUtils.getStringSet(context, finalSpType);
                if (set == null)
                    set = new HashSet<>();
                set.remove(id);
                SPUtils.save(context, SpConstant.ATTENTION_AUTHOR, set);

                observerData.callback(null);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                observerData.onError(null);
            }
        });
    }

    /**
     * 是否关注
     *
     * @param context
     * @param type
     * @param id
     * @return
     */
    public static boolean isAttention(Context context, String type, String id) {
        String spType = "";
        switch (type) {
            case TYPE_AUTHOR:
                spType = SpConstant.ATTENTION_AUTHOR;
                break;
            case TYPE_NEWS:
                spType = SpConstant.ATTENTION_NEWS;
                break;
        }
        Set<String> set = SPUtils.getStringSet(context, spType);
        if (set == null)
            return false;
        return set.contains(id);
    }

}
