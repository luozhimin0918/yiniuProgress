package com.jyh.kxt.base.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;

import com.jyh.kxt.base.dao.DBManager;
import com.jyh.kxt.base.dao.DaoSession;
import com.jyh.kxt.base.dao.NewsJsonDao;
import com.jyh.kxt.main.json.NewsJson;
import com.library.base.http.VarConstant;
import com.library.bean.EventBusClass;
import com.library.widget.window.ToastView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.QueryBuilder;

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
            long mCurrentTimeLong = System.currentTimeMillis() / 1000;
            String mCurrentTimeValue = String.valueOf(mCurrentTimeLong);
            news.setCreate_time(mCurrentTimeValue);

            news.setDataType(VarConstant.DB_TYPE_BROWER);
            DBManager instance = DBManager.getInstance(context);
            List<NewsJson> newsJsons = instance.getDaoSessionRead().getNewsJsonDao().queryRaw("WHERE O_ID=? AND DATA_TYPE=?", new
                    String[]{news.getO_id(), "" + VarConstant.DB_TYPE_BROWER});
            if (newsJsons.size() == 0) {
                DaoSession daoSessionWrit = instance.getDaoSessionWrit();
                daoSessionWrit.getNewsJsonDao().insert(news);
            }
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
        try {
            DBManager instance = DBManager.getInstance(context);
            QueryBuilder qb = instance.getDaoSessionRead().getNewsJsonDao().queryBuilder();
            DeleteQuery bd = qb.where(NewsJsonDao.Properties.DataType.eq(VarConstant.DB_TYPE_BROWER)).buildDelete();
            bd.executeDeleteWithoutDetachingEntities();
            EventBus.getDefault().post(new EventBusClass(EventBusClass.EVENT_CLEAR_BROWER, null));
        } catch (Exception e) {
            e.printStackTrace();
            ToastView.makeText3(context, "浏览记录,删除失败");
        }
    }

    /**
     * 获取浏览历史
     *
     * @param context
     * @return
     */
    public static List<NewsJson> getHistory(Context context) {
        return DBManager.getInstance(context).getDaoSessionRead().getNewsJsonDao().queryBuilder().orderDesc(NewsJsonDao
                .Properties.Datetime).where(NewsJsonDao.Properties.DataType.eq(VarConstant.DB_TYPE_BROWER)).list();
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
                .queryRaw("WHERE O_ID = ? AND DATA_TYPE=?", new String[]{newsJson.getO_id(), VarConstant.DB_TYPE_BROWER + ""});

        return newsJsons.size() == 0 ? false : true;
    }

}
