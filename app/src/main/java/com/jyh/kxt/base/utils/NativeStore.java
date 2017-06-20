package com.jyh.kxt.base.utils;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.SpConstant;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.util.SPUtils;

import org.android.spdy.SpdyVersion;

import java.text.BreakIterator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 点赞
 */

public class NativeStore {

    /**
     * 点赞
     *
     * @param mContext
     * @param type
     * @param id
     */
    public static void addThumbID(final Context mContext, String type, final String id, final ObserverData
            observerData, final ObserverData observerData2) {


        String spName = "";
        String url = "";

        switch (type) {
            case VarConstant.GOOD_TYPE_NEWS:
                spName = SpConstant.GOOD_NEWS;
                url = HttpConstant.GOOD_NEWS;
                break;
            case VarConstant.GOOD_TYPE_VIDEO:
                spName = SpConstant.GOOD_VIDEO;
                url = HttpConstant.GOOD_VIDEO;
                break;
            case VarConstant.GOOD_TYPE_COMMENT_NEWS:
                spName = SpConstant.GOOD_COMMENT_NEWS;
                url = HttpConstant.GOOD_COMMENT;
                break;
            case VarConstant.GOOD_TYPE_COMMENT_VIDEO:
                spName = SpConstant.GOOD_COMMENT_VIDEO;
                url = HttpConstant.GOOD_COMMENT;
                break;
            case VarConstant.GOOD_TYPE_COMMENT_BLOG:
                spName = SpConstant.GOOD_COMMENT_BLOG;
                url = HttpConstant.GOOD_COMMENT;
                break;
        }
        VolleyRequest request = new VolleyRequest(mContext, Volley.newRequestQueue(mContext));
        final String finalSpName = spName;
        request.doGet(url, getParam(request, type, id), new HttpListener<Object>() {
            @Override
            protected void onResponse(Object o) {
                good(mContext, id, observerData, observerData2, finalSpName);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                if (observerData != null) {
                    observerData.onError(new VolleyError("点赞失败"));
                }
                if (observerData2 != null) {
                    observerData2.onError(null);
                }
            }
        });
    }

    private static JSONObject getParam(VolleyRequest request, String type, String id) {
        JSONObject jsonParam = request.getJsonParam();
        switch (type) {
            case VarConstant.GOOD_TYPE_NEWS:
                jsonParam.put(VarConstant.HTTP_ID, id);
                break;
            case VarConstant.GOOD_TYPE_VIDEO:
                jsonParam.put(VarConstant.HTTP_ID, id);
                break;
            case VarConstant.GOOD_TYPE_COMMENT_NEWS:
                jsonParam.put(VarConstant.HTTP_ID, id);
                jsonParam.put(VarConstant.HTTP_TYPE, VarConstant.ARTICLE);
                break;
            case VarConstant.GOOD_TYPE_COMMENT_VIDEO:
                jsonParam.put(VarConstant.HTTP_ID, id);
                jsonParam.put(VarConstant.HTTP_TYPE, VarConstant.VIDEO);
                break;
            case VarConstant.GOOD_TYPE_COMMENT_BLOG:
                jsonParam.put(VarConstant.HTTP_ID, id);
                jsonParam.put(VarConstant.HTTP_TYPE, VarConstant.BLOG_ARTICLE);
                break;
        }
        return jsonParam;
    }

    private static void good(Context mContext, String id, ObserverData observerData, ObserverData observerData2,
                             String spName) {
        try {
            Set<String> set = SPUtils.getStringSet(mContext, spName);
            if (set == null) set = new HashSet<>();
            set.add(id);
            SPUtils.save(mContext, spName, set);
            if (observerData != null) {
                Map<String, Boolean> map = new HashMap<>();
                map.put(VarConstant.FUNCTION_TYPE_GOOD, true);
                observerData.callback(map);
            }
            if (observerData2 != null) {
                observerData2.callback(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (observerData != null) {
                observerData.onError(new VolleyError("点赞失败"));
            }
            if (observerData2 != null) {
                observerData2.onError(null);
            }
        }
    }

    /**
     * 取消点赞
     *
     * @param mContext
     * @param type
     * @param id
     */
    public static void removeThumbId(Context mContext, String type, String id) {
        String spName = "";
        switch (type) {
            case VarConstant.GOOD_TYPE_NEWS:
                spName = SpConstant.GOOD_NEWS;
                break;
            case VarConstant.GOOD_TYPE_VIDEO:
                spName = SpConstant.GOOD_VIDEO;
                break;
            case VarConstant.GOOD_TYPE_COMMENT_NEWS:
                spName = SpConstant.GOOD_COMMENT_NEWS;
                break;
            case VarConstant.GOOD_TYPE_COMMENT_VIDEO:
                spName = SpConstant.GOOD_COMMENT_VIDEO;
                break;
            case VarConstant.GOOD_TYPE_COMMENT_BLOG:
                spName = SpConstant.GOOD_COMMENT_BLOG;
                break;
        }
        Set<String> set = SPUtils.getStringSet(mContext, spName);
        if (set == null) set = new HashSet<>();
        set.remove(id);
        SPUtils.save(mContext, spName, set);
    }

    /**
     * 是否点赞
     *
     * @param id
     * @param type
     * @return
     */
    public static boolean isThumbSucceed(Context mContext, String type, String id) {
        String spName = "";
        switch (type) {
            case VarConstant.GOOD_TYPE_NEWS:
                spName = SpConstant.GOOD_NEWS;
                break;
            case VarConstant.GOOD_TYPE_VIDEO:
                spName = SpConstant.GOOD_VIDEO;
                break;
            case VarConstant.GOOD_TYPE_COMMENT_NEWS:
                spName = SpConstant.GOOD_COMMENT_NEWS;
                break;
            case VarConstant.GOOD_TYPE_COMMENT_VIDEO:
                spName = SpConstant.GOOD_COMMENT_VIDEO;
                break;
            case VarConstant.GOOD_TYPE_COMMENT_BLOG:
                spName = SpConstant.GOOD_COMMENT_BLOG;
                break;
        }
        Set<String> set = SPUtils.getStringSet(mContext, spName);
        if (set == null) {
            return false;
        } else {
            return set.contains(id);
        }
    }

}
