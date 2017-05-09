package com.jyh.kxt.base.utils;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.main.json.flash.FlashJson;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.util.EncryptionUtils;
import com.library.util.SPUtils;
import com.library.widget.window.ToastView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 项目名:Kxt
 * 类描述:收藏工具类
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/25.
 */

public class CollectUtils {

    /**
     * 快讯 收藏
     *
     * @param context
     * @param str
     * @param observable
     */
    public static void flashCollect(Context context, String str, ObserverData observable) {

        if (LoginUtils.isLogined(context))
            try {
                Set<String> set = SPUtils.getStringSet(context, SpConstant.COLLECT_FLASH);
                if (set == null)
                    set = new HashSet<>();
                set.add(str);
                SPUtils.save(context, SpConstant.COLLECT_FLASH, set);
                observable.callback(null);
            } catch (Exception e) {
                e.printStackTrace();
                observable.onError(null);
            }
        else
            ToastView.makeText3(context, "请先登录");
    }

    /**
     * 快讯 取消收藏
     *
     * @param context
     * @param str
     * @param observable
     */
    public static void flashUnCollect(Context context, String str, ObserverData observable) {

        if (LoginUtils.isLogined(context))

            try {
                Set<String> set = SPUtils.getStringSet(context, SpConstant.COLLECT_FLASH);
                if (set == null) {
                    ToastView.makeText3(context, "未收藏");
                    return;
                }
                set.remove(str);
                SPUtils.save(context, SpConstant.COLLECT_FLASH, set);
                observable.callback(null);
            } catch (Exception e) {
                e.printStackTrace();
                observable.onError(null);
            }
        else
            ToastView.makeText3(context, "请先登录");
    }

    /**
     * 快讯 是否收藏
     *
     * @param context
     * @param str
     * @return
     */
    public static boolean flashIsCollect(Context context, String str) {

        if (LoginUtils.isLogined(context)) {

            Set<String> set = SPUtils.getStringSet(context, SpConstant.COLLECT_FLASH);
            if (set == null)
                return false;
            Iterator<String> iterator = set.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().equals(str)) {
                    return true;
                }
            }
            return false;
        } else
            return false;
    }

    /**
     * 获取快讯收藏内容
     *
     * @param context
     * @return
     * @throws Exception
     */
    public static void getCollectData(Context context, ObserverData<List<FlashJson>> observable) {
        try {
            List<FlashJson> flashJsons = new ArrayList<>();
            Set<String> set = SPUtils.getStringSet(context, SpConstant.COLLECT_FLASH);
            if (set == null || set.size() == 0) {
                observable.callback(null);
            } else {
                Iterator<String> iterator = set.iterator();
                while (iterator.hasNext()) {
                    flashJsons.add(JSON.parseObject(iterator.next(), FlashJson.class));
                }
                observable.callback(flashJsons);
            }
        } catch (Exception e) {
            e.printStackTrace();
            observable.onError(null);
        }
    }

    /**
     * 收藏
     *
     * @param context
     * @param id
     * @param type
     * @param observerData  用以改变list数据
     * @param umengObserver 用以改变umeng面板按钮状态
     */
    public static void collect(Context context, String id, String type, final ObserverData observerData, final ObserverData<Boolean>
            umengObserver) {
        if (LoginUtils.isLogined(context)) {
            VolleyRequest request = new VolleyRequest(context, Volley.newRequestQueue(context));
            request.doPost(HttpConstant.COLLECT_ADD, getMap(context, request, id, type), new HttpListener<Boolean>() {
                @Override
                protected void onResponse(Boolean o) {
                    Map<String, Boolean> map = new HashMap<>();
                    map.put(VarConstant.FUNCTION_TYPE_COLLECT, true);
                    observerData.callback(map);
                    umengObserver.callback(o);
                }

                @Override
                protected void onErrorResponse(VolleyError error) {
                    super.onErrorResponse(error);
                    observerData.onError(new VolleyError("收藏失败"));
                }
            });
        } else {
            ToastView.makeText3(context, "请先登录");
        }
    }

    /**
     * 取消收藏
     *
     * @param context
     * @param id
     * @param type
     * @param observerData
     */
    public static void unCollect(Context context, String id, String type, final ObserverData observerData, final ObserverData<Boolean>
            umengOb) {
        if (LoginUtils.isLogined(context)) {
            VolleyRequest request = new VolleyRequest(context, Volley.newRequestQueue(context));

            request.doPost(HttpConstant.COLLECT_DEL, getMap(context, request, id, type), new HttpListener<Boolean>() {
                @Override
                protected void onResponse(Boolean aBoolean) {
                    umengOb.callback(false);
                    Map<String, Boolean> map = new HashMap<String, Boolean>();
                    map.put(VarConstant.FUNCTION_TYPE_COLLECT, false);
                    observerData.callback(map);
                }

                @Override
                protected void onErrorResponse(VolleyError error) {
                    super.onErrorResponse(error);
                    observerData.onError(new VolleyError("取消收藏失败"));
                }
            });

            Map<String, Boolean> map = new HashMap<>();
            map.put(VarConstant.FUNCTION_TYPE_COLLECT, false);
            observerData.callback(map);
        } else {
            ToastView.makeText3(context, "请先登录");

        }
    }

    /**
     * 取消收藏(收藏中心)
     *
     * @param context
     * @param ids
     * @param type
     * @param observerData
     */
    public static void unCollectList(Context context, String ids, String type, ObserverData observerData) {
        if (LoginUtils.isLogined(context)) {
            VolleyRequest request = new VolleyRequest(context, Volley.newRequestQueue(context));
            request.doPost(HttpConstant.COLLECT_LIST_DEL, getMapHaveToken(context, request, ids, type), new HttpListener<Object>() {
                @Override
                protected void onResponse(Object obj) {

                }

                @Override
                protected void onErrorResponse(VolleyError error) {
                    super.onErrorResponse(error);
                }
            });
        } else {
            ToastView.makeText3(context, "请先登录");
        }
    }

    /**
     * 收藏状态
     *
     * @param context
     * @param id
     * @param type
     * @param observerData
     * @return
     */
    public static boolean isCollect(Context context, String id, String type, ObserverData observerData) {
        if (LoginUtils.isLogined(context)) {
            VolleyRequest request = new VolleyRequest(context, Volley.newRequestQueue(context));
            return false;
        } else {
            return false;
        }
    }

    /**
     * 收藏数据同步
     */
    public static void localAndNetSynchronization() {

    }

    private static Map<String, String> getMap(Context context, VolleyRequest request, String id, String type) {
        Map<String, String> map = new HashMap<>();
        JSONObject jsonParam = request.getJsonParam();
        jsonParam.put(VarConstant.HTTP_UID, LoginUtils.getUserInfo(context).getUid());
        jsonParam.put(VarConstant.HTTP_ID, id);
        jsonParam.put(VarConstant.HTTP_TYPE, type);

        try {
            map.put(VarConstant.HTTP_CONTENT2, EncryptionUtils.createJWT(VarConstant.KEY, jsonParam.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    private static Map<String, String> getMapHaveToken(Context context, VolleyRequest request, String ids, String type) {
        Map<String, String> map = new HashMap<>();
        JSONObject jsonParam = request.getJsonParam();
        jsonParam.put(VarConstant.HTTP_UID, LoginUtils.getUserInfo(context).getUid());
        jsonParam.put(VarConstant.HTTP_TOKEN, LoginUtils.getUserInfo(context).getAccessToken());
        jsonParam.put(VarConstant.HTTP_ID, ids);
        jsonParam.put(VarConstant.HTTP_TYPE, type);

        try {
            map.put(VarConstant.HTTP_CONTENT2, EncryptionUtils.createJWT(VarConstant.KEY, jsonParam.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }
}
