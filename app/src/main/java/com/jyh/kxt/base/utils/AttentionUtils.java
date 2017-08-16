package com.jyh.kxt.base.utils;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.base.utils.collect.CollectLocalUtils;
import com.jyh.kxt.main.json.NewsJson;
import com.jyh.kxt.user.json.UserJson;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.util.EncryptionUtils;
import com.library.util.SPUtils;
import com.library.widget.window.ToastView;

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

        ToastView.makeText3(context, "关注成功");
    }

    /**
     * 关注文章
     *
     * @param context
     * @param newsJson
     * @param observerData
     */
    public static void attention(final Context context, final NewsJson newsJson, final ObserverData<Boolean> observerData) {
        String url = HttpConstant.EXPLORE_BLOG_ADDFAVORARTICLE;

        UserJson userInfo = LoginUtils.getUserInfo(context);
        VolleyRequest request = new VolleyRequest(context, Volley.newRequestQueue(context));
        JSONObject jsonParam = request.getJsonParam();
        jsonParam.put(VarConstant.HTTP_UID, userInfo.getUid());
        jsonParam.put(VarConstant.HTTP_ACCESS_TOKEN, userInfo.getToken());
        jsonParam.put(VarConstant.HTTP_ID, newsJson.getO_id());
        try {
            url += EncryptionUtils.createJWT(VarConstant.KEY, jsonParam.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        request.doGet(url, new HttpListener<Object>() {
            @Override
            protected void onResponse(Object o) {
                CollectLocalUtils.collect(context, VarConstant.COLLECT_TYPE_ARTICLE, newsJson, null, null);
                observerData.callback(null);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                observerData.onError(null);
            }
        });

        ToastView.makeText3(context, "关注成功");
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
        ToastView.makeText3(context, "取消成功");
    }

    /**
     * 取消关注文章
     *
     * @param context
     * @param news
     * @param observerData
     */
    public static void unAttention(final Context context, final NewsJson news, final ObserverData<Boolean> observerData) {
        String url = HttpConstant.EXPLORE_BLOG_DELETEFAVORARTICLE;
        UserJson userInfo = LoginUtils.getUserInfo(context);
        VolleyRequest request = new VolleyRequest(context, Volley.newRequestQueue(context));
        JSONObject jsonParam = request.getJsonParam();
        jsonParam.put(VarConstant.HTTP_UID, userInfo.getUid());
        jsonParam.put(VarConstant.HTTP_ACCESS_TOKEN, userInfo.getToken());
        jsonParam.put(VarConstant.HTTP_ID, news.getO_id());
        try {
            url += EncryptionUtils.createJWT(VarConstant.KEY, jsonParam.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        request.doGet(url, new HttpListener<Object>() {
            @Override
            protected void onResponse(Object o) {
                CollectLocalUtils.unCollect(context, VarConstant.COLLECT_TYPE_ARTICLE, news, null, null);
                observerData.callback(null);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                observerData.onError(null);
            }
        });
        ToastView.makeText3(context, "取消成功");
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
