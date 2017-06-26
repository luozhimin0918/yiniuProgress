package com.jyh.kxt.base.utils.collect;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.ObjectArraySerializer;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.jyh.kxt.av.json.VideoListJson;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.utils.AttentionUtils;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.main.json.NewsJson;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.util.EncryptionUtils;
import com.library.util.RegexValidateUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 项目名:Kxt
 * 类描述:网络收藏
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/12.
 */

public class CollectNetUtils {
    /**
     * 网络收藏
     *
     * @param context
     * @param type
     * @param obj
     * @param observerData
     * @param umengObserver
     */
    public static void collect(final Context context, final String type, final Object obj, final ObserverData observerData, final
    ObserverData<Boolean>
            umengObserver) {

        VolleyRequest request = new VolleyRequest(context, Volley.newRequestQueue(context));

        String collectType = "";
        String collectId = "";
        switch (type) {
            case VarConstant.COLLECT_TYPE_ARTICLE:
                collectType = VarConstant.COLLECT_TYPE_ARTICLE;
                collectId = ((NewsJson) obj).getO_id();
                String newsType = ((NewsJson) obj).getType();
                if (VarConstant.COLLECT_TYPE_BLOG.equals(newsType)) {
                    AttentionUtils.attention(context, (NewsJson) obj, observerData);
                    return;
                }
                break;
            case VarConstant.COLLECT_TYPE_FLASH:
                CollectLocalUtils.collect(context, type, obj, observerData, umengObserver);
                return;
            case VarConstant.COLLECT_TYPE_VIDEO:
                collectType = VarConstant.COLLECT_TYPE_VIDEO;
                collectId = ((VideoListJson) obj).getId();
                break;
        }

        request.doPost(HttpConstant.COLLECT_ADD, getMap(context, request, collectId, collectType), new HttpListener<Boolean>() {
            @Override
            protected void onResponse(Boolean o) {
                Map<String, Boolean> map = new HashMap<>();
                map.put(VarConstant.FUNCTION_TYPE_COLLECT, true);
                if (observerData != null) {
                    observerData.callback(map);
                }
                if (umengObserver != null) {
                    umengObserver.callback(o);
                }
                CollectLocalUtils.collect(context, type, obj, null, null);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                if (observerData != null)
                    observerData.onError(new VolleyError("收藏失败"));
                if (umengObserver != null) {
                    umengObserver.onError(new VolleyError("收藏失败"));
                }
            }
        });

    }

    /**
     * 网络取消收藏
     *
     * @param context
     * @param type
     * @param obj
     * @param observerData
     * @param umengObserver
     */
    public static void unCollect(final Context context, final String type, final Object obj, final ObserverData observerData, final
    ObserverData<Boolean>
            umengObserver) {
        VolleyRequest request = new VolleyRequest(context, Volley.newRequestQueue(context));

        String collectType = "";
        String collectId = "";
        switch (type) {
            case VarConstant.COLLECT_TYPE_ARTICLE:
                collectType = VarConstant.COLLECT_TYPE_ARTICLE;
                collectId = ((NewsJson) obj).getO_id();

                String newsType = ((NewsJson) obj).getType();
                if (VarConstant.COLLECT_TYPE_BLOG.equals(newsType)) {
                    AttentionUtils.unAttention(context, (NewsJson) obj, observerData);
                    return;
                }

                break;
            case VarConstant.COLLECT_TYPE_FLASH:
                CollectLocalUtils.unCollect(context, type, obj, observerData, umengObserver);
                return;
            case VarConstant.COLLECT_TYPE_VIDEO:
                collectType = VarConstant.COLLECT_TYPE_VIDEO;
                collectId = ((VideoListJson) obj).getId();
                break;
        }

        request.doPost(HttpConstant.COLLECT_DEL, getMap(context, request, collectId, collectType), new HttpListener<Boolean>() {
            @Override
            protected void onResponse(Boolean aBoolean) {
                Map<String, Boolean> map = new HashMap<>();
                map.put(VarConstant.FUNCTION_TYPE_COLLECT, false);
                if (observerData != null)
                    observerData.callback(map);
                if (umengObserver != null)
                    umengObserver.callback(false);
                CollectLocalUtils.unCollect(context, type, obj, null, null);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                if (observerData != null)
                    observerData.onError(new VolleyError("取消收藏失败"));
                if (umengObserver != null)
                    umengObserver.onError(new VolleyError("取消收藏失败"));
            }
        });

    }

    /**
     * 获取网络收藏列表
     *
     * @param context
     * @param type
     * @param observerData
     */
    public static void getCollectData(Context context, String type, ObserverData<List> observerData) {
        VolleyRequest request = new VolleyRequest(context, Volley.newRequestQueue(context));
    }

    /**
     * 网络批量取消收藏
     *
     * @param context
     * @param type
     * @param ids
     * @param observerData
     */
    public static void unCollects(final Context context, final String type, final String newsType, final String ids, final ObserverData
            observerData) {
        VolleyRequest request = new VolleyRequest(context, Volley.newRequestQueue(context));

        String collectType = "";
        String collectId = "";
        switch (type) {
            case VarConstant.COLLECT_TYPE_ARTICLE:
                collectType = "1";
                collectId = ids;
                break;
            case VarConstant.COLLECT_TYPE_FLASH:
                CollectLocalUtils.unCollects(context, type, newsType, ids, observerData);
                return;
            case VarConstant.COLLECT_TYPE_VIDEO:
                collectType = "2";
                collectId = ids;
                break;
        }

        String url = HttpConstant.COLLECT_DELS;
        if (!RegexValidateUtil.isEmpty(newsType) && newsType.equals(VarConstant.OCLASS_BLOG)) {
            url=HttpConstant.EXPLORE_BLOG_DELETEFAVORARTICLE;
        }

        request.doPost(url, getMap(context, request, collectId, collectType), new HttpListener<Object>() {
            @Override
            protected void onResponse(Object aBoolean) {
                if (observerData != null)
                    observerData.callback(null);
                CollectLocalUtils.unCollects(context, type, newsType, ids, null);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                if (observerData != null)
                    observerData.onError(new VolleyError("取消收藏失败"));
            }
        });
    }

    private static Map<String, String> getMap(Context context, VolleyRequest request, String id, String type) {
        Map<String, String> map = new HashMap<>();
        JSONObject jsonParam = request.getJsonParam();
        jsonParam.put(VarConstant.HTTP_UID, LoginUtils.getUserInfo(context).getUid());
        jsonParam.put(VarConstant.HTTP_ACCESS_TOKEN, LoginUtils.getUserInfo(context).getToken());
        jsonParam.put(VarConstant.HTTP_ID, id);
        jsonParam.put(VarConstant.HTTP_TYPE, type);

        try {
            map.put(VarConstant.HTTP_CONTENT2, EncryptionUtils.createJWT(VarConstant.KEY, jsonParam.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

}
