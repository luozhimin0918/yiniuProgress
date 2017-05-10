package com.jyh.kxt.base.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;

import com.jyh.kxt.base.dao.DBManager;
import com.jyh.kxt.base.dao.DaoSession;
import com.jyh.kxt.main.json.NewsJson;
import com.library.base.http.VarConstant;

import java.util.List;

/**
 * 项目名:Kxt
 * 类描述:浏览记录工具类
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/5.
 */

public class BrowerHistoryUtils {

    /**
     * 保存浏览记录
     *
     * @param context
     * @param news
     */
    public static void save(Context context, NewsJson news) {
        try {
            news.setDataType(VarConstant.DB_NEWS_TYPE_BROWER);
            DaoSession daoSessionWrit = DBManager.getInstance(context).getDaoSessionWrit();
            daoSessionWrit.getNewsJsonDao().insertOrReplace(news);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 清空浏览历史
     *
     * @param context
     */
    public static void clear(Context context) {
        DBManager.getInstance(context).getDaoSessionWrit().getNewsJsonDao().deleteAll();
    }

    /**
     * 获取浏览历史
     *
     * @param context
     * @return
     */
    public static List<NewsJson> getHistory(Context context) {
        List<NewsJson> list = DBManager.getInstance(context).getDaoSessionRead().getNewsJsonDao().loadAll();
        return list;
    }

    /**
     * 获取浏览状态
     *
     * @param context
     * @param newsJson
     * @return
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean isBrowered(Context context, NewsJson newsJson) {

        List<NewsJson> newsJsons = DBManager.getInstance(context)
                .getDaoSessionRead()
                .getNewsJsonDao()
                .queryRaw("WHERE O_ID = ?", newsJson.getO_id());

        return newsJsons.size() == 0 ? false : true;
    }

}
