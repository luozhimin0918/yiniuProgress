package com.jyh.kxt.base.utils.collect;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.android.volley.VolleyError;
import com.jyh.kxt.av.json.VideoListJson;
import com.jyh.kxt.base.annotation.ObserverData;
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
import com.library.base.http.VarConstant;

import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 项目名:Kxt
 * 类描述:本地收藏
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/12.
 */

public class CollectLocalUtils {

    /**
     * 本地收藏
     *
     * @param context
     * @param type
     * @param obj
     * @param observerData
     * @param umengObserver
     */
    public static void collect(Context context, String type, Object obj, ObserverData observerData, ObserverData<Boolean>
            umengObserver) {
        DBManager instance = DBManager.getInstance(context);
        try {
            switch (type) {
                case VarConstant.COLLECT_TYPE_FLASH:
                    FlashJson flash = (FlashJson) obj;
                    flash.setDataType(VarConstant.DB_TYPE_COLLECT_LOCAL);
                    FlashJsonDao flashJsonRead = instance.getDaoSessionRead().getFlashJsonDao();
                    List<FlashJson> flashJsons = flashJsonRead.queryRaw(VarConstant.SELECT_FLASH, new
                            String[]{flash.getSocre(), VarConstant.DB_TYPE_COLLECT_LOCAL + ""});
                    FlashJsonDao flashJsonWrit = instance.getDaoSessionWrit().getFlashJsonDao();
                    if (flashJsons.size() == 0) {
                        flashJsonWrit.insert(flash);
                    } else {
                        QueryBuilder qb = instance.getDaoSessionRead().getFlashJsonDao().queryBuilder();
                        DeleteQuery bd = qb.where(FlashJsonDao.Properties.DataType.eq(VarConstant.DB_TYPE_COLLECT_LOCAL + ""), FlashJsonDao
                                .Properties.Socre.eq(flash.getSocre())).buildDelete();
                        bd.executeDeleteWithoutDetachingEntities();
                        flashJsonWrit.insert(flash);
                    }
                    break;
                case VarConstant.COLLECT_TYPE_ARTICLE:
                    NewsJson news = (NewsJson) obj;
                    news.setDataType(VarConstant.DB_TYPE_COLLECT_LOCAL);
                    NewsJsonDao newsJsonRead = instance.getDaoSessionRead().getNewsJsonDao();
                    List<NewsJson> newsJsons = newsJsonRead.queryBuilder()
                            .where(NewsJsonDao.Properties.DataType.eq(VarConstant.DB_TYPE_COLLECT_LOCAL + "")
                                    , NewsJsonDao.Properties.Type.eq(news.getType())
                                    , NewsJsonDao.Properties.O_id.eq(news.getO_id())).list();
                    NewsJsonDao newsJsonWrite = instance.getDaoSessionWrit().getNewsJsonDao();
                    if (newsJsons.size() == 0) {
                        newsJsonWrite.insert(news);
                    } else {
                        QueryBuilder qb = instance.getDaoSessionRead().getNewsJsonDao().queryBuilder();
                        DeleteQuery bd = qb.where(NewsJsonDao.Properties.DataType.eq(VarConstant.DB_TYPE_COLLECT_LOCAL + ""), NewsJsonDao
                                .Properties.O_id.eq(news.getO_id())).buildDelete();
                        bd.executeDeleteWithoutDetachingEntities();
                        newsJsonWrite.insert(news);
                    }
                    break;
                case VarConstant.COLLECT_TYPE_VIDEO:
                    VideoListJson video = (VideoListJson) obj;
                    video.setDataType(VarConstant.DB_TYPE_COLLECT_LOCAL);
                    VideoListJsonDao videoDaoRead = instance.getDaoSessionRead().getVideoListJsonDao();
                    List<VideoListJson> videoJsons = videoDaoRead.queryRaw(VarConstant.SELECT_VIDEO, new
                            String[]{video.getId(), VarConstant.DB_TYPE_COLLECT_LOCAL + ""});
                    VideoListJsonDao videoDaoWrite = instance.getDaoSessionWrit().getVideoListJsonDao();
                    if (videoJsons.size() == 0) {
                        videoDaoWrite.insert(video);
                    } else {
                        QueryBuilder qb = instance.getDaoSessionRead().getVideoListJsonDao().queryBuilder();
                        DeleteQuery bd = qb.where(VideoListJsonDao.Properties.DataType.eq(VarConstant.DB_TYPE_COLLECT_LOCAL + ""),
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
    public static void unCollect(Context context, String type, Object obj, ObserverData observerData, ObserverData<Boolean>
            umengObserver) {
        DBManager instance = DBManager.getInstance(context);
        try {
            switch (type) {
                case VarConstant.COLLECT_TYPE_FLASH:
                    FlashJson flash = (FlashJson) obj;
                    List<FlashJson> flashJsons = instance.getDaoSessionRead().getFlashJsonDao().queryRaw(VarConstant.SELECT_FLASH, new
                            String[]{flash.getSocre(), VarConstant.DB_TYPE_COLLECT_LOCAL + ""});
                    for (FlashJson flashJson : flashJsons) {
                        QueryBuilder qb = instance.getDaoSessionRead().getFlashJsonDao().queryBuilder();
                        DeleteQuery bd = qb.where(FlashJsonDao.Properties.DataType.eq(VarConstant.DB_TYPE_COLLECT_LOCAL + ""), FlashJsonDao
                                .Properties.Socre.eq(flashJson.getSocre())).buildDelete();
                        bd.executeDeleteWithoutDetachingEntities();
                    }
                    break;
                case VarConstant.COLLECT_TYPE_ARTICLE:
                    NewsJson news = (NewsJson) obj;
                    List<NewsJson> newsJsons = instance.getDaoSessionRead().getNewsJsonDao().queryBuilder().where(NewsJsonDao.Properties
                                    .DataType.eq(VarConstant.DB_TYPE_COLLECT_LOCAL + "")
                            , NewsJsonDao.Properties.O_id.eq(news.getO_id())
                            , NewsJsonDao.Properties.Type.eq(news.getType())).list();
                    for (NewsJson newsJson : newsJsons) {
                        QueryBuilder qb = instance.getDaoSessionRead().getNewsJsonDao().queryBuilder();
                        DeleteQuery bd = qb.where(NewsJsonDao.Properties.DataType.eq(VarConstant.DB_TYPE_COLLECT_LOCAL + ""), NewsJsonDao
                                .Properties.O_id.eq(newsJson.getO_id()), NewsJsonDao.Properties.Type.eq(newsJson.getType())).buildDelete();
                        bd.executeDeleteWithoutDetachingEntities();
                    }
                    break;
                case VarConstant.COLLECT_TYPE_VIDEO:
                    VideoListJson video = (VideoListJson) obj;
                    List<VideoListJson> videoListJsons = instance.getDaoSessionRead().getVideoListJsonDao().queryRaw(VarConstant
                            .SELECT_VIDEO, new
                            String[]{video.getId(), VarConstant.DB_TYPE_COLLECT_LOCAL + ""});
                    for (VideoListJson videoListJson : videoListJsons) {
                        QueryBuilder qb = instance.getDaoSessionRead().getVideoListJsonDao().queryBuilder();
                        DeleteQuery bd = qb.where(VideoListJsonDao.Properties.DataType.eq(VarConstant.DB_TYPE_COLLECT_LOCAL + ""),
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
    public static void getCollectData(Context context, String type, String newsType, ObserverData<List> observerData) {
        try {
            DaoSession daoSessionRead = DBManager.getInstance(context).getDaoSessionRead();
            List list = new ArrayList();
            switch (type) {
                case VarConstant.COLLECT_TYPE_ARTICLE:
                    if (VarConstant.OCLASS_BLOG.equals(newsType)) {
                        list = daoSessionRead.getNewsJsonDao().queryBuilder().orderDesc(NewsJsonDao.Properties.Datetime)
                                .where(NewsJsonDao.Properties.DataType.eq(VarConstant.DB_TYPE_COLLECT_LOCAL + "")
                                        , NewsJsonDao.Properties.Type.eq(newsType)).list();
                    } else {
                        QueryBuilder<NewsJson> newsJsonQueryBuilder = daoSessionRead.getNewsJsonDao().queryBuilder().orderDesc
                                (NewsJsonDao.Properties.Datetime);
                        newsJsonQueryBuilder
                                .where(NewsJsonDao.Properties.DataType.eq(VarConstant.DB_TYPE_COLLECT_LOCAL + "")
                                        , newsJsonQueryBuilder.or(NewsJsonDao.Properties.Type.eq(VarConstant.OCLASS_NEWS),
                                                NewsJsonDao.Properties.Type.eq(VarConstant.OCLASS_DIANPING)));
                        list = newsJsonQueryBuilder.list();
                    }
                    break;
                case VarConstant.COLLECT_TYPE_FLASH:
                    List<FlashJson> flashJsons = daoSessionRead.getFlashJsonDao().queryRaw(VarConstant.SELECT_ALL, VarConstant
                            .DB_TYPE_COLLECT_LOCAL
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
    public static boolean isCollect(Context context, String type, Object obj) {

        try {
            DBManager instance = DBManager.getInstance(context);
            DaoSession daoSessionRead = instance.getDaoSessionRead();
            switch (type) {
                case VarConstant.COLLECT_TYPE_ARTICLE:
                    NewsJson news = (NewsJson) obj;
                    List<NewsJson> newsJsons = daoSessionRead.getNewsJsonDao().queryBuilder().
                            where(NewsJsonDao.Properties.DataType.eq(VarConstant.DB_TYPE_COLLECT_LOCAL + "")
                                    , NewsJsonDao.Properties.O_id.eq(news.getO_id())
                                    , NewsJsonDao.Properties.Type.eq(news.getType())).list();
                    if (newsJsons.size() > 0)
                        return true;
                    else
                        return false;
                case VarConstant.COLLECT_TYPE_FLASH:
                    FlashJson flash = (FlashJson) obj;
                    List<FlashJson> flashJsons = daoSessionRead.getFlashJsonDao().queryRaw(VarConstant.SELECT_FLASH, new String[]{flash
                            .getSocre(), "" + VarConstant
                            .DB_TYPE_COLLECT_LOCAL});
                    if (flashJsons.size() > 0) return true;
                    else return false;
                case VarConstant.COLLECT_TYPE_VIDEO:
                    VideoListJson video = (VideoListJson) obj;
                    List<VideoListJson> videoListJsons = daoSessionRead.getVideoListJsonDao().queryRaw(VarConstant.SELECT_VIDEO, new
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
     * 是否本地收藏
     *
     * @param context
     * @param type
     * @param id
     * @return
     */
    public static boolean isCollect(Context context, String type, String newsType, String id) {

        try {
            DBManager instance = DBManager.getInstance(context);
            DaoSession daoSessionRead = instance.getDaoSessionRead();
            switch (type) {
                case VarConstant.COLLECT_TYPE_ARTICLE:
                    List<NewsJson> newsJsons = daoSessionRead.getNewsJsonDao().queryBuilder()
                            .where(NewsJsonDao.Properties.DataType.eq(VarConstant.DB_TYPE_COLLECT_LOCAL + "")
                                    , NewsJsonDao.Properties.O_id.eq(id)
                                    , NewsJsonDao.Properties.Type.eq(newsType)
                            ).list();
                    if (newsJsons.size() > 0)
                        return true;
                    else
                        return false;
                case VarConstant.COLLECT_TYPE_FLASH:
                    List<FlashJson> flashJsons = daoSessionRead.getFlashJsonDao().queryRaw(VarConstant.SELECT_FLASH, new String[]{id, ""
                            + VarConstant
                            .DB_TYPE_COLLECT_LOCAL});
                    if (flashJsons.size() > 0) return true;
                    else return false;
                case VarConstant.COLLECT_TYPE_VIDEO:
                    List<VideoListJson> videoListJsons = daoSessionRead.getVideoListJsonDao().queryRaw(VarConstant.SELECT_VIDEO, new
                            String[]{id, "" + VarConstant
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
     * 本地批量取消收藏
     *
     * @param context
     * @param type
     * @param ids
     * @param observerData
     */
    public static void unCollects(Context context, String type, String newsType, String ids, ObserverData observerData) {
        DBManager instance = DBManager.getInstance(context);
        String[] idsSplit = ids.split(",");
        List<String> idList = Arrays.asList(idsSplit);
        try {
            switch (type) {
                case VarConstant.COLLECT_TYPE_FLASH:
                    for (String id : idList) {
                        QueryBuilder qb = instance.getDaoSessionRead().getFlashJsonDao().queryBuilder();
                        DeleteQuery bd = qb.where(FlashJsonDao.Properties.DataType.eq(VarConstant.DB_TYPE_COLLECT_LOCAL + ""), FlashJsonDao
                                .Properties.Socre.eq(id)).buildDelete();
                        bd.executeDeleteWithoutDetachingEntities();
                    }
                    break;
                case VarConstant.COLLECT_TYPE_ARTICLE:
                    for (String id : idList) {
                        QueryBuilder qb = instance.getDaoSessionRead().getNewsJsonDao().queryBuilder();
                        DeleteQuery bd;
                        if (newsType.equals(VarConstant.OCLASS_BLOG)) {
                            bd = qb.where(NewsJsonDao.Properties.DataType.eq(VarConstant.DB_TYPE_COLLECT_LOCAL + ""), NewsJsonDao
                                    .Properties.O_id.eq(id), NewsJsonDao.Properties.Type.eq(newsType)).buildDelete();
                        } else {
                            qb.where(NewsJsonDao.Properties.O_id.eq(id), NewsJsonDao.Properties.DataType.eq(VarConstant
                                    .DB_TYPE_COLLECT_LOCAL + ""), qb.or(NewsJsonDao.Properties.Type.eq(VarConstant.OCLASS_NEWS), NewsJsonDao
                                    .Properties.O_id.eq(VarConstant.OCLASS_DIANPING)));
                            bd = qb.buildDelete();
                        }
                        bd.executeDeleteWithoutDetachingEntities();
                    }
                    break;
                case VarConstant.COLLECT_TYPE_VIDEO:
                    for (String id : idList) {
                        QueryBuilder qb = instance.getDaoSessionRead().getVideoListJsonDao().queryBuilder();
                        DeleteQuery bd = qb.where(VideoListJsonDao.Properties.DataType.eq(VarConstant.DB_TYPE_COLLECT_LOCAL + ""),
                                VideoListJsonDao
                                        .Properties.Uid.eq(id)).buildDelete();
                        bd.executeDeleteWithoutDetachingEntities();
                    }
                    break;
            }
            if (observerData != null)
                observerData.callback(null);
        } catch (Exception e) {
            e.printStackTrace();
            if (observerData != null)
                observerData.onError(null);
        }
    }

}
