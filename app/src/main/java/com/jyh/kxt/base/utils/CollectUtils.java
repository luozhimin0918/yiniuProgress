package com.jyh.kxt.base.utils;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.jyh.kxt.av.json.VideoListJson;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.dao.DBManager;
import com.jyh.kxt.base.dao.DaoSession;
import com.jyh.kxt.base.dao.FlashJsonDao;
import com.jyh.kxt.base.dao.NewsJsonDao;
import com.jyh.kxt.base.dao.VideoListJsonDao;
import com.jyh.kxt.main.json.NewsJson;
import com.jyh.kxt.main.json.flash.FlashJson;
import com.jyh.kxt.main.json.flash.FlashJsonComparator;
import com.jyh.kxt.main.json.flash.Flash_KX;
import com.jyh.kxt.main.json.flash.Flash_NEWS;
import com.jyh.kxt.main.json.flash.Flash_RL;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.util.EncryptionUtils;

import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 项目名:Kxt
 * 类描述:收藏工具类
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/25.
 */

public class CollectUtils {

    private static final String SELECT_FLASH = "WHERE SOCRE=? AND DATA_TYPE=?";
    private static final String SELECT_NEWS = "WHERE O_ID=? AND DATA_TYPE=?";
    private static final String SELECT_VIDEO = "WHERE UID=? AND DATA_TYPE=?";

    private static final String SELECT_ALL = "WHERE DATA_TYPE=?";

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
            collectNet(context, type, obj, observerData, umengObserver);
        } else {
            //未登录,本地收藏
            collectLocal(context, type, obj, observerData, umengObserver);
        }
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
            unCollectNet(context, type, obj, observerData, umengObserver);
        } else {
            //未登录,本地收藏
            unCollectLocal(context, type, obj, observerData, umengObserver);
        }
    }

    /**
     * 获取
     *
     * @param context
     * @param observerData
     */
    public static void getCollectData(Context context, String type, ObserverData<List> observerData) {
        if (LoginUtils.isLogined(context)) {
            getNetCollectData(context, type, observerData);
        } else {
            getLocalCollectData(context, type, observerData);
        }
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
        if (LoginUtils.isLogined(context)) {
            if (type != VarConstant.COLLECT_TYPE_FLASH)
                return isCollectNet(context, type, obj);
            else return isCollectLoacl(context, type, obj);
        } else {
            return isCollectLoacl(context, type, obj);
        }
    }

    /**
     * 本地收藏
     *
     * @param context
     * @param type
     * @param obj
     * @param observerData
     * @param umengObserver
     */
    private static void collectLocal(Context context, String type, Object obj, ObserverData observerData, ObserverData<Boolean>
            umengObserver) {
        DBManager instance = DBManager.getInstance(context);
        try {
            switch (type) {
                case VarConstant.COLLECT_TYPE_FLASH:
                    FlashJson flash = (FlashJson) obj;
                    flash.setDataType(VarConstant.DB_TYPE_COLLECT_LOCAL);
                    FlashJsonDao flashJsonRead = instance.getDaoSessionRead().getFlashJsonDao();
                    List<FlashJson> flashJsons = flashJsonRead.queryRaw(SELECT_FLASH, new
                            String[]{flash.getSocre(), VarConstant.DB_TYPE_COLLECT_LOCAL + ""});
                    FlashJsonDao flashJsonWrit = instance.getDaoSessionWrit().getFlashJsonDao();
                    if (flashJsons.size() == 0) {
                        flashJsonWrit.insert(flash);
                    } else {
                        QueryBuilder qb = instance.getDaoSessionRead().getFlashJsonDao().queryBuilder();
                        DeleteQuery bd = qb.where(FlashJsonDao.Properties.DataType.eq(VarConstant.DB_TYPE_COLLECT_LOCAL), FlashJsonDao
                                .Properties.Socre.eq(flash.getSocre())).buildDelete();
                        bd.executeDeleteWithoutDetachingEntities();
                        flashJsonWrit.insert(flash);
                    }
                    break;
                case VarConstant.COLLECT_TYPE_ARTICLE:
                    NewsJson news = (NewsJson) obj;
                    news.setDataType(VarConstant.DB_TYPE_COLLECT_LOCAL);
                    NewsJsonDao newsJsonRead = instance.getDaoSessionRead().getNewsJsonDao();
                    List<NewsJson> newsJsons = newsJsonRead.queryRaw(SELECT_NEWS, new
                            String[]{news.getO_id(), VarConstant.DB_TYPE_COLLECT_LOCAL + ""});
                    NewsJsonDao newsJsonWrite = instance.getDaoSessionWrit().getNewsJsonDao();
                    if (newsJsons.size() == 0) {
                        newsJsonWrite.insert(news);
                    } else {
                        QueryBuilder qb = instance.getDaoSessionRead().getNewsJsonDao().queryBuilder();
                        DeleteQuery bd = qb.where(NewsJsonDao.Properties.DataType.eq(VarConstant.DB_TYPE_COLLECT_LOCAL), NewsJsonDao
                                .Properties.O_id.eq(news.getO_id())).buildDelete();
                        bd.executeDeleteWithoutDetachingEntities();
                        newsJsonWrite.insert(news);
                    }
                    break;
                case VarConstant.COLLECT_TYPE_VIDEO:
                    VideoListJson video = (VideoListJson) obj;
                    video.setDataType(VarConstant.DB_TYPE_COLLECT_LOCAL);
                    VideoListJsonDao videoDaoRead = instance.getDaoSessionRead().getVideoListJsonDao();
                    List<VideoListJson> videoJsons = videoDaoRead.queryRaw(SELECT_VIDEO, new
                            String[]{video.getId(), VarConstant.DB_TYPE_COLLECT_LOCAL + ""});
                    VideoListJsonDao videoDaoWrite = instance.getDaoSessionWrit().getVideoListJsonDao();
                    if (videoJsons.size() == 0) {
                        videoDaoWrite.insert(video);
                    } else {
                        QueryBuilder qb = instance.getDaoSessionRead().getVideoListJsonDao().queryBuilder();
                        DeleteQuery bd = qb.where(VideoListJsonDao.Properties.DataType.eq(VarConstant.DB_TYPE_COLLECT_LOCAL),
                                VideoListJsonDao
                                        .Properties.Uid.eq(video.getId())).buildDelete();
                        bd.executeDeleteWithoutDetachingEntities();
                        videoDaoRead.insert(video);
                    }
                    break;
            }
            if (observerData != null) {
                Map<String, Boolean> map = new HashMap<>();
                map.put(VarConstant.FUNCTION_TYPE_COLLECT, true);
                observerData.callback(map);
            }
            if (umengObserver != null) {
                umengObserver.callback(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (observerData != null) {
                Map<String, Boolean> map = new HashMap<>();
                map.put(VarConstant.FUNCTION_TYPE_COLLECT, false);
                observerData.callback(map);
            }
            if (umengObserver != null) {
                umengObserver.callback(false);
            }
        }
    }

    /**
     * 本地取消收藏
     *
     * @param context
     * @param type
     * @param obj
     * @param observerData
     * @param umengObserver
     */
    private static void unCollectLocal(Context context, String type, Object obj, ObserverData observerData, ObserverData<Boolean>
            umengObserver) {
        DBManager instance = DBManager.getInstance(context);
        try {
            switch (type) {
                case VarConstant.COLLECT_TYPE_FLASH:
                    FlashJson flash = (FlashJson) obj;
                    List<FlashJson> flashJsons = instance.getDaoSessionRead().getFlashJsonDao().queryRaw(SELECT_FLASH, new
                            String[]{flash.getSocre(), VarConstant.DB_TYPE_COLLECT_LOCAL + ""});
                    for (FlashJson flashJson : flashJsons) {
                        QueryBuilder qb = instance.getDaoSessionRead().getFlashJsonDao().queryBuilder();
                        DeleteQuery bd = qb.where(FlashJsonDao.Properties.DataType.eq(VarConstant.DB_TYPE_COLLECT_LOCAL), FlashJsonDao
                                .Properties.Socre.eq(flashJson.getSocre())).buildDelete();
                        bd.executeDeleteWithoutDetachingEntities();
                    }
                    break;
                case VarConstant.COLLECT_TYPE_ARTICLE:
                    NewsJson news = (NewsJson) obj;
                    List<NewsJson> newsJsons = instance.getDaoSessionRead().getNewsJsonDao().queryRaw(SELECT_NEWS, new
                            String[]{news.getO_id(), VarConstant.DB_TYPE_COLLECT_LOCAL + ""});
                    for (NewsJson newsJson : newsJsons) {
                        QueryBuilder qb = instance.getDaoSessionRead().getNewsJsonDao().queryBuilder();
                        DeleteQuery bd = qb.where(NewsJsonDao.Properties.DataType.eq(VarConstant.DB_TYPE_COLLECT_LOCAL), NewsJsonDao
                                .Properties.O_id.eq(newsJson.getO_id())).buildDelete();
                        bd.executeDeleteWithoutDetachingEntities();
                    }
                    break;
                case VarConstant.COLLECT_TYPE_VIDEO:
                    VideoListJson video = (VideoListJson) obj;
                    List<VideoListJson> videoListJsons = instance.getDaoSessionRead().getVideoListJsonDao().queryRaw(SELECT_VIDEO, new
                            String[]{video.getId(), VarConstant.DB_TYPE_COLLECT_LOCAL + ""});
                    for (VideoListJson videoListJson : videoListJsons) {
                        QueryBuilder qb = instance.getDaoSessionRead().getVideoListJsonDao().queryBuilder();
                        DeleteQuery bd = qb.where(VideoListJsonDao.Properties.DataType.eq(VarConstant.DB_TYPE_COLLECT_LOCAL),
                                VideoListJsonDao
                                        .Properties.Uid.eq(videoListJson.getId())).buildDelete();
                        bd.executeDeleteWithoutDetachingEntities();
                    }
                    break;
            }
            if (observerData != null) {
                Map<String, Boolean> map = new HashMap<>();
                map.put(VarConstant.FUNCTION_TYPE_COLLECT, false);
                observerData.callback(map);
            }
            if (umengObserver != null) {
                umengObserver.callback(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (observerData != null) {
                Map<String, Boolean> map = new HashMap<>();
                map.put(VarConstant.FUNCTION_TYPE_COLLECT, true);
                observerData.callback(map);
            }
            if (umengObserver != null) {
                umengObserver.callback(true);
            }
        }
    }

    /**
     * 获取本地收藏列表
     *
     * @param context
     * @param type
     * @param observerData
     */
    private static void getLocalCollectData(Context context, String type, ObserverData<List> observerData) {
        try {
            DaoSession daoSessionRead = DBManager.getInstance(context).getDaoSessionRead();
            List list = new ArrayList();
            switch (type) {
                case VarConstant.COLLECT_TYPE_ARTICLE:
                    list = daoSessionRead.getNewsJsonDao().queryBuilder().orderDesc(NewsJsonDao.Properties.Datetime).where(NewsJsonDao
                            .Properties.DataType.eq(VarConstant.DB_TYPE_COLLECT_LOCAL + "")).list();
                    break;
                case VarConstant.COLLECT_TYPE_FLASH:
                    List<FlashJson> flashJsons = daoSessionRead.getFlashJsonDao().queryRaw(SELECT_ALL, VarConstant.DB_TYPE_COLLECT_LOCAL
                            + "");
                    for (FlashJson flashJson : flashJsons) {
                        String time = "";
                        switch (flashJson.getCode()) {
                            case VarConstant.SOCKET_FLASH_KUAIXUN:
                                time = JSON.parseObject(flashJson.getContent(), Flash_KX.class).getTime();
                                break;
                            case VarConstant.SOCKET_FLASH_CJRL:
                                time = JSON.parseObject(flashJson.getContent(), Flash_RL.class).getTime();
                                break;
                            case VarConstant.SOCKET_FLASH_KXTNEWS:
                                time = JSON.parseObject(flashJson.getContent(), Flash_NEWS.class).getTime();
                                break;
                        }
                        flashJson.setTime(time);
                    }
                    Collections.sort(flashJsons, new FlashJsonComparator());
                    list = flashJsons;
                    break;
                case VarConstant.COLLECT_TYPE_VIDEO:
                    list = daoSessionRead.getVideoListJsonDao().queryBuilder().orderDesc(VideoListJsonDao.Properties.Create_time).where
                            (VideoListJsonDao.Properties.DataType.eq(VarConstant.DB_TYPE_COLLECT_LOCAL + "")).list();
                    break;
            }
            observerData.callback(list);
        } catch (Exception e) {
            e.printStackTrace();
            observerData.onError(new VolleyError("数据加载错误"));
        }
    }

    /**
     * 是否本地收藏
     *
     * @param context
     * @param type
     * @param obj
     * @return
     */
    private static boolean isCollectLoacl(Context context, String type, Object obj) {

        try {
            DBManager instance = DBManager.getInstance(context);
            DaoSession daoSessionRead = instance.getDaoSessionRead();
            switch (type) {
                case VarConstant.COLLECT_TYPE_ARTICLE:
                    NewsJson news = (NewsJson) obj;
                    List<NewsJson> newsJsons = daoSessionRead.getNewsJsonDao().queryRaw(SELECT_NEWS, new String[]{news.getO_id
                            (), "" + VarConstant
                            .DB_TYPE_COLLECT_LOCAL});
                    if (newsJsons.size() > 0)
                        return true;
                    else
                        return false;
                case VarConstant.COLLECT_TYPE_FLASH:
                    FlashJson flash = (FlashJson) obj;
                    List<FlashJson> flashJsons = daoSessionRead.getFlashJsonDao().queryRaw(SELECT_FLASH, new String[]{flash
                            .getSocre(), "" + VarConstant
                            .DB_TYPE_COLLECT_LOCAL});
                    if (flashJsons.size() > 0) return true;
                    else return false;
                case VarConstant.COLLECT_TYPE_VIDEO:
                    VideoListJson video = (VideoListJson) obj;
                    List<VideoListJson> videoListJsons = daoSessionRead.getVideoListJsonDao().queryRaw(SELECT_VIDEO, new
                            String[]{video.getId(), "" + VarConstant
                            .DB_TYPE_COLLECT_LOCAL});
                    if (videoListJsons.size() > 0) return true;
                    else return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 网络收藏
     *
     * @param context
     * @param type
     * @param obj
     * @param observerData
     * @param umengObserver
     */
    private static void collectNet(final Context context, final String type, final Object obj, final ObserverData observerData, final
    ObserverData<Boolean>
            umengObserver) {

        VolleyRequest request = new VolleyRequest(context, Volley.newRequestQueue(context));

        String collectType = "";
        String collectId = "";
        switch (type) {
            case VarConstant.COLLECT_TYPE_ARTICLE:
                collectType = VarConstant.COLLECT_TYPE_ARTICLE;
                collectId = ((NewsJson) obj).getO_id();
                break;
            case VarConstant.COLLECT_TYPE_FLASH:
                collectLocal(context, type, obj, observerData, umengObserver);
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
                collectLocal(context, type, obj, null, null);
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
    private static void unCollectNet(final Context context, final String type, final Object obj, final ObserverData observerData, final
    ObserverData<Boolean>
            umengObserver) {
        VolleyRequest request = new VolleyRequest(context, Volley.newRequestQueue(context));

        String collectType = "";
        String collectId = "";
        switch (type) {
            case VarConstant.COLLECT_TYPE_ARTICLE:
                collectType = VarConstant.COLLECT_TYPE_ARTICLE;
                collectId = ((NewsJson) obj).getO_id();
                break;
            case VarConstant.COLLECT_TYPE_FLASH:
                unCollectLocal(context, type, obj, observerData, umengObserver);
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
                unCollectLocal(context, type, obj, null, null);
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
    private static void getNetCollectData(Context context, String type, ObserverData<List> observerData) {

    }

    /**
     * 是否网络收藏
     *
     * @param context
     * @param type
     * @param obj
     * @return
     */
    private static boolean isCollectNet(Context context, String type, Object obj) {
        return false;
    }

//    /**
//     * 收藏
//     *
//     * @param context
//     * @param id
//     * @param type
//     * @param observerData  用以改变list数据
//     * @param umengObserver 用以改变umeng面板按钮状态
//     */
//    public static void collect(Context context, String id, String type, final ObserverData observerData, final ObserverData<Boolean>
//            umengObserver) {
//            VolleyRequest request = new VolleyRequest(context, Volley.newRequestQueue(context));
//            request.doPost(HttpConstant.COLLECT_ADD, getMap(context, request, id, type), new HttpListener<Boolean>() {
//                @Override
//                protected void onResponse(Boolean o) {
//                    Map<String, Boolean> map = new HashMap<>();
//                    map.put(VarConstant.FUNCTION_TYPE_COLLECT, true);
//                    observerData.callback(map);
//                    umengObserver.callback(o);
//                }
//
//                @Override
//                protected void onErrorResponse(VolleyError error) {
//                    super.onErrorResponse(error);
//                    observerData.onError(new VolleyError("收藏失败"));
//                }
//            });
//    }
//
//    /**
//     * 取消收藏
//     *
//     * @param context
//     * @param id
//     * @param type
//     * @param observerData
//     */
//    public static void unCollect(Context context, String id, String type, final ObserverData observerData, final ObserverData<Boolean>
//            umengOb) {
//            VolleyRequest request = new VolleyRequest(context, Volley.newRequestQueue(context));
//
//            request.doPost(HttpConstant.COLLECT_DEL, getMap(context, request, id, type), new HttpListener<Boolean>() {
//                @Override
//                protected void onResponse(Boolean aBoolean) {
//                    umengOb.callback(false);
//                    Map<String, Boolean> map = new HashMap<String, Boolean>();
//                    map.put(VarConstant.FUNCTION_TYPE_COLLECT, false);
//                    observerData.callback(map);
//                }
//
//                @Override
//                protected void onErrorResponse(VolleyError error) {
//                    super.onErrorResponse(error);
//                    observerData.onError(new VolleyError("取消收藏失败"));
//                }
//            });
//
//            Map<String, Boolean> map = new HashMap<>();
//            map.put(VarConstant.FUNCTION_TYPE_COLLECT, false);
//            observerData.callback(map);
//    }
//
//    /**
//     * 取消收藏(收藏中心)
//     *
//     * @param context
//     * @param ids
//     * @param type
//     * @param observerData
//     */
//    public static void unCollectList(Context context, String ids, String type, ObserverData observerData) {
//            VolleyRequest request = new VolleyRequest(context, Volley.newRequestQueue(context));
//            request.doPost(HttpConstant.COLLECT_LIST_DEL, getMapHaveToken(context, request, ids, type), new HttpListener<Object>() {
//                @Override
//                protected void onResponse(Object obj) {
//
//                }
//
//                @Override
//                protected void onErrorResponse(VolleyError error) {
//                    super.onErrorResponse(error);
//                }
//            });
//    }
//
//    /**
//     * 收藏状态
//     *
//     * @param context
//     * @param id
//     * @param type
//     * @param observerData
//     * @return
//     */
//    public static boolean isCollect(Context context, String id, String type, ObserverData observerData) {
//            VolleyRequest request = new VolleyRequest(context, Volley.newRequestQueue(context));
//            return false;
//    }
//
//    /**
//     * 收藏数据同步
//     */
//    public static void localAndNetSynchronization() {
//    }

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
        jsonParam.put(VarConstant.HTTP_TOKEN, LoginUtils.getUserInfo(context).getToken());
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
