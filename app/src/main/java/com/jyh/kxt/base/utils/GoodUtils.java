package com.jyh.kxt.base.utils;

import android.content.Context;
import android.databinding.tool.util.L;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.SpConstant;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.util.EncryptionUtils;
import com.library.util.SPUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 项目名:Kxt
 * 类描述:点评工具类
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/3.
 */

public class GoodUtils {

    /**
     * 点赞
     *
     * @param context
     * @param id
     * @param type
     * @param observerData 用以改变list数据
     * @param umengOb      用以改变umeng面板按钮状态
     */
    public static void addGood(final Context context, final String id, final String type, final ObserverData observerData, final
    ObserverData<Boolean> umengOb) {
        String url = "";
        switch (type) {
            case VarConstant.GOOD_TYPE_NEWS:
                url = HttpConstant.GOOD_NEWS;
                break;
            case VarConstant.GOOD_TYPE_VIDEO:
                url = HttpConstant.GOOD_VIDEO;
                break;
        }
        VolleyRequest request = new VolleyRequest(context, Volley.newRequestQueue(context));
        request.doGet(getUrl(request, url, id), new HttpListener<Object>() {
            @Override
            protected void onResponse(Object o) {

                if (observerData != null) {
                    Map<String, Boolean> map = new HashMap<>();
                    map.put(VarConstant.FUNCTION_TYPE_GOOD, true);
                    observerData.callback(map);
                }
                if (umengOb != null) {
                    umengOb.callback(true);
                }

                Set<String> set;
                switch (type) {
                    case VarConstant.GOOD_TYPE_NEWS:
                        set = SPUtils.getStringSet(context, SpConstant.GOOD_NEWS);
                        if (set == null)
                            set = new HashSet<String>();
                        set.add(id);
                        SPUtils.save(context, SpConstant.GOOD_NEWS, set);
                        break;
                    case VarConstant.GOOD_TYPE_VIDEO:
                        set = SPUtils.getStringSet(context, SpConstant.GOOD_VIDEO);
                        if (set == null)
                            set = new HashSet<String>();
                        set.add(id);
                        SPUtils.save(context, SpConstant.GOOD_VIDEO, set);
                        break;
                }


            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                observerData.onError(new VolleyError("点赞失败"));
            }
        });
    }

    private static String getUrl(VolleyRequest request, String url, String id) {
        JSONObject jsonParam = request.getJsonParam();
        jsonParam.put(VarConstant.HTTP_ID, id);
        try {
            return url + VarConstant.HTTP_CONTENT + EncryptionUtils.createJWT(VarConstant.KEY, jsonParam.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return url;
        }
    }

    /**
     * 点赞状态
     *
     * @param context
     * @param id
     * @param type
     * @return
     */
    public static boolean isGood(Context context, String id, String type) {
        Set<String> set = new HashSet<>();
        switch (type) {
            case VarConstant.GOOD_TYPE_NEWS:
                set = SPUtils.getStringSet(context, SpConstant.GOOD_NEWS);
                break;
            case VarConstant.GOOD_TYPE_VIDEO:
                set = SPUtils.getStringSet(context, SpConstant.GOOD_VIDEO);
                break;
        }

        if (set == null) return false;
        for (String s : set) {
            if (s.equals(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 取消点赞
     *
     * @param context
     * @param id
     * @param type
     * @param data
     * @param umeng
     */
    public static void delGood(Context context, String id, String type, ObserverData data, ObserverData umeng) {
    }
}
