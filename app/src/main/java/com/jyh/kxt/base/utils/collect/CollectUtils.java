package com.jyh.kxt.base.utils.collect;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.jyh.kxt.av.json.VideoListJson;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.dao.DBManager;
import com.jyh.kxt.base.dao.DaoSession;
import com.jyh.kxt.base.dao.NewsJsonDao;
import com.jyh.kxt.base.dao.VideoListJsonDao;
import com.jyh.kxt.base.utils.AttentionUtils;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.main.json.NewsJson;
import com.jyh.kxt.main.json.flash.FlashJson;
import com.jyh.kxt.user.json.UserJson;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.util.EncryptionUtils;
import com.library.widget.window.ToastView;

import java.util.List;

/**
 * 项目名:Kxt
 * 类描述:收藏工具类
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/25.
 */

public class CollectUtils {

    /**
     * 收藏
     *
     * @param context
     * @param type          COLLECT_TYPE_FLASH 快讯,COLLECT_TYPE_VIDEO 视听,COLLECT_TYPE_ARTICLE 文章
     * @param obj           保存对象
     * @param observerData  收藏监听
     * @param umengObserver umeng面板监听
     */
    public static void collect(Context context, String type, Object obj, ObserverData observerData, final ObserverData<Boolean>
            umengObserver) {
        if (LoginUtils.isLogined(context)) {
            //登录,网路收藏
            if (VarConstant.COLLECT_TYPE_ARTICLE.equals(type)) {
                NewsJson news = (NewsJson) obj;
                String newsType = news.getType();
                if (newsType != null && newsType.equals(VarConstant.COLLECT_TYPE_BLOG)) {
                    AttentionUtils.attention(context, news, observerData);
                } else {
                    CollectNetUtils.collect(context, type, obj, observerData, umengObserver);
                }
            } else
                CollectNetUtils.collect(context, type, obj, observerData, umengObserver);
        } else {
            //未登录,本地收藏
            CollectLocalUtils.collect(context, type, obj, observerData, umengObserver);
        }
        ToastView.makeText3(context,"收藏成功");
    }

    /**
     * 取消收藏
     *
     * @param context
     * @param type          COLLECT_TYPE_FLASH 快讯,COLLECT_TYPE_VIDEO 视听,COLLECT_TYPE_ARTICLE 文章
     * @param obj           保存对象
     * @param observerData  收藏监听
     * @param umengObserver umeng面板监听
     */
    public static void unCollect(Context context, String type, Object obj, ObserverData observerData, final ObserverData<Boolean>
            umengObserver) {
        if (LoginUtils.isLogined(context)) {
            //登录,网路收藏
            if (VarConstant.COLLECT_TYPE_ARTICLE.equals(type)) {
                NewsJson news = (NewsJson) obj;
                String newsType = news.getType();
                if (newsType != null && newsType.equals(VarConstant.COLLECT_TYPE_BLOG)) {
                    AttentionUtils.unAttention(context, news, observerData);
                } else {
                    CollectNetUtils.unCollect(context, type, obj, observerData, umengObserver);
                }
            } else
                CollectNetUtils.unCollect(context, type, obj, observerData, umengObserver);

        } else {
            //未登录,本地收藏
            CollectLocalUtils.unCollect(context, type, obj, observerData, umengObserver);
        }
        ToastView.makeText3(context,"取消成功");
    }

    /**
     * 批量取消收藏
     *
     * @param context
     * @param type
     * @param ids
     * @param observerData
     */
    public static void unCollects(Context context, String type, String newsType, String ids, ObserverData observerData) {
        if (LoginUtils.isLogined(context)) {
            CollectNetUtils.unCollects(context, type, newsType, ids, observerData);
        } else {
            CollectLocalUtils.unCollects(context, type, newsType, ids, observerData);
        }
    }

    /**
     * 获取
     *
     * @param context
     * @param observerData
     */
    public static void getCollectData(Context context, String type, String newsType, ObserverData<List> observerData) {
        CollectLocalUtils.getCollectData(context, type, newsType, observerData);
    }

    /**
     * 是否收藏
     *
     * @param context
     * @param type
     * @param obj
     * @return
     */
    public static boolean isCollect(Context context, String type, Object obj) {
        return CollectLocalUtils.isCollect(context, type, obj);
    }

    /**
     * 本地收藏数据提交到网络收藏库
     *
     * @param context
     * @param type
     */
    public static void localToNetSynchronization(final Context context, String type, String newsType, final ObserverData observerData) {
        VolleyRequest request = new VolleyRequest(context, Volley.newRequestQueue(context));

        DaoSession dbRead = DBManager.getInstance(context).getDaoSessionRead();
        StringBuffer ids = new StringBuffer();
        switch (type) {
            case VarConstant.COLLECT_TYPE_ARTICLE:
                List<NewsJson> newsJsons = dbRead.getNewsJsonDao().queryBuilder().where
                        (NewsJsonDao.Properties.DataType.eq(VarConstant.DB_TYPE_COLLECT_LOCAL + ""), NewsJsonDao.Properties.Type.eq
                                (newsType)).list();
                for (NewsJson newsJson : newsJsons) {
                    String o_id = newsJson.getO_id();
                    if ("".equals(ids.toString())) {
                        ids.append(o_id);
                    } else {
                        ids.append(",").append(o_id);
                    }
                }
                if (newsType.equals(VarConstant.OCLASS_BLOG)) {
                    type = VarConstant.OCLASS_BLOG;
                }
                break;
            case VarConstant.COLLECT_TYPE_VIDEO:
                List<VideoListJson> videoListJsons = dbRead.getVideoListJsonDao().queryBuilder().where
                        (VideoListJsonDao.Properties.DataType.eq(VarConstant.DB_TYPE_COLLECT_LOCAL + "")).list();
                for (VideoListJson videoListJson : videoListJsons) {
                    String id = videoListJson.getId();
                    if ("".equals(ids.toString()))
                        ids.append(id);
                    else
                        ids.append(",").append(id);
                }
                break;
        }

        request.doGet(getUrl(context, type, request, ids.toString()), new HttpListener<Object>() {
            @Override
            protected void onResponse(Object o) {
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
     * 网络收藏数据同步到本地
     *
     * @param context
     * @param type
     * @param data
     */
    public static void netToLocalSynchronization(Context context, String type, List data) {
        switch (type) {
            case VarConstant.COLLECT_TYPE_ARTICLE:
                for (NewsJson obj : (List<NewsJson>) data) {
                    if (isCollect(context, type, obj)) continue;
                    CollectLocalUtils.collect(context, type, obj, null, null);
                }
                break;
            case VarConstant.COLLECT_TYPE_FLASH:
                for (FlashJson obj : (List<FlashJson>) data) {
                    if (isCollect(context, type, obj)) continue;
                    CollectLocalUtils.collect(context, type, obj, null, null);
                }
                break;
            case VarConstant.COLLECT_TYPE_VIDEO:
                for (VideoListJson obj : (List<VideoListJson>) data) {
                    if (isCollect(context, type, obj)) continue;
                    CollectLocalUtils.collect(context, type, obj, null, null);
                }
                break;
        }
    }

    /**
     * @param context
     * @param type
     * @param request
     * @return
     */
    private static String getUrl(Context context, String type, VolleyRequest request, String id) {
        String url = HttpConstant.COLLECT_ADDS;
        JSONObject jsonParam = request.getJsonParam();
        UserJson userInfo = LoginUtils.getUserInfo(context);
        jsonParam.put(VarConstant.HTTP_UID, userInfo.getUid());
        jsonParam.put(VarConstant.HTTP_ACCESS_TOKEN,userInfo.getToken());
        jsonParam.put(VarConstant.HTTP_ID, id);
        if(VarConstant.OCLASS_BLOG.equals(type)){
            try {
                return HttpConstant.EXPLORE_BLOG_ADDFAVORARTICLE+EncryptionUtils.createJWT(VarConstant.KEY,jsonParam.toString());
            } catch (Exception e) {
                e.printStackTrace();
                return url;
            }
        }else{
            jsonParam.put(VarConstant.HTTP_TYPE, type);
        }


        try {
            return url + VarConstant.HTTP_CONTENT + EncryptionUtils.createJWT(VarConstant.KEY, jsonParam.toString());
        } catch (Exception e) {
            return url;
        }
    }

}
