package com.jyh.kxt.base.utils;

import android.content.Context;
import android.databinding.tool.util.L;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.constant.HttpConstant;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.util.EncryptionUtils;

import java.util.HashMap;
import java.util.Map;

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
    public static void addGood(Context context, String id, String type, final ObserverData observerData, ObserverData<Boolean> umengOb) {
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
                Map<String, Boolean> map = new HashMap<>();
                map.put(VarConstant.FUNCTION_TYPE_GOOD, true);
                observerData.callback(map);
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

        return false;
    }

    /**
     * 取消点赞
     *
     * @param context
     * @param id
     * @param type
     * @param observerData
     */
    public static void delGood(Context context, String id, String type, ObserverData observerData) {
        Map<String, Boolean> map = new HashMap<>();
        map.put(VarConstant.FUNCTION_TYPE_GOOD, false);
        observerData.callback(map);
    }
}
