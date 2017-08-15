package com.jyh.kxt.trading.util;

import android.content.Context;
import android.database.Cursor;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.dao.DBManager;
import com.jyh.kxt.base.dao.DaoSession;
import com.jyh.kxt.base.dao.MarkBean;
import com.jyh.kxt.base.dao.MarkBeanDao;
import com.jyh.kxt.base.dao.ViewPointTradeBeanDao;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.trading.json.ViewPointTradeBean;
import com.jyh.kxt.user.json.UserJson;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.util.NetUtils;
import com.library.widget.window.ToastView;

import org.greenrobot.greendao.database.Database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Mr'Dai on 2017/8/4.
 * 交易圈子点赞的记录
 */

public class TradeHandlerUtil {

    static TradeHandlerUtil tradeHandlerUtil;

    public static TradeHandlerUtil getInstance() {
        if (tradeHandlerUtil == null) {
            tradeHandlerUtil = new TradeHandlerUtil();
        }
        return tradeHandlerUtil;
    }

    /**
     * 查询一个数据集合中的收藏状态
     *
     * @param mContext
     * @param oId
     */
    public MarkBean entityCheckState(Context mContext, String oId) {
        if (oId == null) return null;

        DBManager mDBManager = DBManager.getInstance(mContext);
        MarkBeanDao markBeanDao = mDBManager.getDaoSessionRead().getMarkBeanDao();
        MarkBean markBean = markBeanDao.queryBuilder().where(MarkBeanDao.Properties.OId.eq(oId)).unique();
        if (markBean == null) {
            markBean = new MarkBean();
            markBean.setUserId(oId);
        }
        return markBean;
    }

    public void listCheckState(Context mContext, List<ViewPointTradeBean> checkList) {
        listCheckState(mContext, checkList, null);
    }

    /**
     * 查询一个数据集合中的收藏状态
     *
     * @param mContext
     * @param checkList
     */
    public void listCheckState(Context mContext, List<ViewPointTradeBean> checkList, String sql) {
        if (checkList == null || checkList.size() == 0) return;

        StringBuffer objectIds = new StringBuffer();
        //组织Map List
        Map<String, ViewPointTradeBean> pointMap = new HashMap<>();
        for (ViewPointTradeBean viewPointTradeBean : checkList) {
            pointMap.put(viewPointTradeBean.getO_id(), viewPointTradeBean);
            objectIds.append(viewPointTradeBean.getO_id() + ",");
        }
        objectIds.deleteCharAt(objectIds.length() - 1);

        DBManager mDBManager = DBManager.getInstance(mContext);
        Database mDataBase = mDBManager.getDaoSessionRead().getDatabase();
        Cursor cursor;
        if (sql == null) {
            cursor = mDataBase.rawQuery("SELECT * FROM MARK_BEAN WHERE O_ID in (" + objectIds.toString() + ")", new String[]{});
        } else {
            cursor = mDataBase.rawQuery("SELECT * FROM MARK_BEAN WHERE O_ID in (" + objectIds.toString() + ") " + sql, new String[]{});
        }

        while (cursor.moveToNext()) {
            String oId = cursor.getString(cursor.getColumnIndex("O_ID"));
            int favourState = cursor.getInt(cursor.getColumnIndex("FAVOUR_STATE"));
            int collectState = cursor.getInt(cursor.getColumnIndex("COLLECT_STATE"));

            ViewPointTradeBean viewPointTradeBean = pointMap.get(oId);
            if (viewPointTradeBean != null) {
                viewPointTradeBean.isFavour = favourState != 0;
                viewPointTradeBean.isCollect = collectState != 0;
            }
        }
        cursor.close();
    }

    public void updateCollect(Context mContext, String id, boolean isCollect) {
        int collectState = isCollect ? 1 : 0;
        DBManager mDBManager = DBManager.getInstance(mContext);
        Database mDataBase = mDBManager.getDaoSessionWrit().getDatabase();
        mDataBase.execSQL("UPDATE MARK_BEAN SET COLLECT_STATE = ? WHERE O_ID=?", new String[]{String.valueOf(collectState), id});
    }


    public boolean saveState(Context mContext, String id, int type, boolean bool) {
        ViewPointTradeBean viewPointTradeBean = new ViewPointTradeBean();
        viewPointTradeBean.o_id = id;
        return saveState(mContext, viewPointTradeBean, type, bool);
    }

    /**
     * @param mContext
     * @param type     1 交易圈列表的赞  2 收藏
     * @param bool
     * @return
     */
    //isSaveSuccess
    public boolean saveState(Context mContext, ViewPointTradeBean viewPointTradeBean, int type, boolean bool) {
        try {

            if (!NetUtils.isNetworkAvailable(mContext)) {
                ToastView.makeText3(mContext, "暂无网络,点赞失败");
                return false;
            }

            UserJson userInfo = LoginUtils.getUserInfo(mContext);

            /**
             * 对于标记的ID的DAO 如果存在则替换否则增加
             */

            DBManager mDBManager = DBManager.getInstance(mContext);
            MarkBeanDao markBeanDao = mDBManager.getDaoSessionWrit().getMarkBeanDao();

            MarkBean markBean = markBeanDao.queryBuilder().where(MarkBeanDao.Properties.OId.eq(viewPointTradeBean.o_id)).unique();
            if (markBean == null) {
                markBean = new MarkBean();
            }
            markBean.setOId(viewPointTradeBean.o_id);

            if (userInfo != null) {
                markBean.setUserId(userInfo.getUid());
            }
            if (type == 1 || type == 3) {
                viewPointTradeBean.isFavour = bool;
                markBean.setFavourState(bool ? 1 : 0);
            } else if (type == 2) {
                viewPointTradeBean.isCollect = bool;
                markBean.setCollectState(bool ? 1 : 0);
            }
            markBeanDao.insertOrReplace(markBean);

            /**
             * 发起网络请求
             */
            if (type == 1 || type == 3) {  //发起网络请求
                requestFavour(mContext, viewPointTradeBean.o_id);
            } else if (type == 2) {
                if (userInfo != null) {
                    requestCollect(mContext, userInfo, viewPointTradeBean, bool);
                } else {
                    ViewPointTradeBeanDao viewPointTradeBeanDao = mDBManager.getDaoSessionWrit().getViewPointTradeBeanDao();
                    viewPointTradeBeanDao.insertOrReplace(viewPointTradeBean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    /**
     * 点赞
     *
     * @param mContext
     * @param id
     */
    private void requestFavour(Context mContext, String id) {
        IBaseView iBaseView = (IBaseView) mContext;
        VolleyRequest mVolleyRequest = new VolleyRequest(mContext, iBaseView.getQueue());
        mVolleyRequest.setTag(getClass().getName());

        JSONObject mainParam = mVolleyRequest.getJsonParam();
        mainParam.put("id", id);
        mVolleyRequest.doGet(HttpConstant.VIEW_POINT_ADDGOOD, mainParam, new HttpListener<String>() {
            @Override
            protected void onResponse(String s) {

            }
        });
    }

    /**
     * 交易圈评论点赞
     *
     * @param mContext
     * @param id
     */
    private void requestFavour2(Context mContext, String id) {
        IBaseView iBaseView = (IBaseView) mContext;
        VolleyRequest mVolleyRequest = new VolleyRequest(mContext, iBaseView.getQueue());
        mVolleyRequest.setTag(getClass().getName());

        JSONObject mainParam = mVolleyRequest.getJsonParam();
        mainParam.put("id", id);
        mainParam.put("type", "point");
        mVolleyRequest.doGet(HttpConstant.VP_COMMENT_ADDGOOD, mainParam, new HttpListener<String>() {
            @Override
            protected void onResponse(String s) {

            }
        });
    }

    public boolean getCollectState(Context mContext, String id) {
        DBManager mDBManager = DBManager.getInstance(mContext);
        MarkBeanDao markBeanDao = mDBManager.getDaoSessionWrit().getMarkBeanDao();
        MarkBean markBean = markBeanDao.queryBuilder().where(MarkBeanDao.Properties.OId.eq(id)).unique();
        if (markBean == null) {
            return false;
        } else {
            return markBean.getCollectState() == 1;
        }
    }

    /**
     * 收藏
     *
     * @param mContext
     * @param userInfo
     * @param viewPointTradeBean
     * @param isCollect
     */
    public void requestCollect(final Context mContext,
                               UserJson userInfo,
                               final ViewPointTradeBean viewPointTradeBean,
                               final boolean isCollect) {

        IBaseView iBaseView = (IBaseView) mContext;
        VolleyRequest mVolleyRequest = new VolleyRequest(mContext, iBaseView.getQueue());
        mVolleyRequest.setTag(getClass().getName());

        JSONObject mainParam = mVolleyRequest.getJsonParam();

        mainParam.put("uid", userInfo.getUid());
        mainParam.put("accessToken", userInfo.getToken());
        mainParam.put("id", viewPointTradeBean.o_id);
        mainParam.put("type", "point");

        String collectUrl;
        if (isCollect) {
            collectUrl = HttpConstant.COLLECT_ADD;
        } else {
            collectUrl = HttpConstant.COLLECT_DEL;
        }

        mVolleyRequest.doPost(collectUrl, mainParam, new HttpListener<String>() {
            @Override
            protected void onResponse(String s) {
                DBManager mDBManager = DBManager.getInstance(mContext);
                ViewPointTradeBeanDao viewPointTradeBeanDao = mDBManager.getDaoSessionWrit().getViewPointTradeBeanDao();
                viewPointTradeBeanDao.insertOrReplace(viewPointTradeBean);
            }
        });
    }

    /**
     * 保存收藏的列表
     *
     * @param viewPointTradeList
     */
    public void saveCollectList(Context mContext, List<ViewPointTradeBean> viewPointTradeList) {
        DBManager mDBManager = DBManager.getInstance(mContext);
        DaoSession daoSessionWrit = mDBManager.getDaoSessionWrit();
        //保存到本地收藏
        ViewPointTradeBeanDao viewPointTradeBeanDao = daoSessionWrit.getViewPointTradeBeanDao();
        viewPointTradeBeanDao.insertOrReplaceInTx(viewPointTradeList);

        //同时保存一份到MarkBean中
        MarkBeanDao markBeanDao = daoSessionWrit.getMarkBeanDao();

        List<MarkBean> markList = new ArrayList<>();
        for (ViewPointTradeBean viewPointTradeBean : viewPointTradeList) {
            MarkBean markBean = new MarkBean();
            markBean.setOId(viewPointTradeBean.o_id);
            markBean.setCollectState(viewPointTradeBean.isCollect ? 1 : 0);
            markBean.setFavourState(viewPointTradeBean.isFavour ? 1 : 0);
            markList.add(markBean);
        }
        markBeanDao.insertOrReplaceInTx(markList);
    }


    /**
     * 得到本地的收藏
     *
     * @param mContext
     * @return
     */
    public List<ViewPointTradeBean> getLocalityCollectList(Context mContext, int start, int digit) {
        DBManager mDBManager = DBManager.getInstance(mContext);
        ViewPointTradeBeanDao viewPointTradeBeanDao = mDBManager.getDaoSessionRead().getViewPointTradeBeanDao();

        List<ViewPointTradeBean> subTrade =
                viewPointTradeBeanDao
                        .queryBuilder().limit(digit).offset(start).list();

        listCheckState(mContext, subTrade);

        //查询出来收藏已经取消的 则删除
        Iterator<ViewPointTradeBean> iterator = subTrade.iterator();
        while (iterator.hasNext()) {
            ViewPointTradeBean next = iterator.next();
            if (!next.isCollect) {
                iterator.remove();
            }
        }

        return subTrade;
    }

    /**
     * 将本地收藏提交到网络
     *
     * @param observerData
     * @param localityCount
     */
    public void updata(Context mContext, final ObserverData<Boolean> observerData, int localityCount) {
        IBaseView iBaseView = (IBaseView) mContext;
        VolleyRequest mVolleyRequest = new VolleyRequest(mContext, iBaseView.getQueue());
        mVolleyRequest.setTag(getClass().getName());
        JSONObject jsonParam = mVolleyRequest.getJsonParam();
        UserJson userJson = LoginUtils.getUserInfo(mContext);
        jsonParam.put(VarConstant.HTTP_UID, userJson.getUid());
        jsonParam.put(VarConstant.HTTP_ACCESS_TOKEN, userJson.getToken());

        List<ViewPointTradeBean> localityCollectList = getLocalityCollectList(mContext, localityCount, VarConstant.LIST_MAX_SIZE);

        Iterator<ViewPointTradeBean> iterator = localityCollectList.iterator();
        String ids = "";
        while (iterator.hasNext()) {
            if (ids.equals("")) {
                ids += iterator.next().o_id;
            } else {
                ids += "," + iterator.next().o_id;
            }
        }
        jsonParam.put(VarConstant.HTTP_ID, ids);
        jsonParam.put(VarConstant.HTTP_TYPE, "point");
        mVolleyRequest.doPost(HttpConstant.COLLECT_ADDS, jsonParam, new HttpListener<Object>() {
            @Override
            protected void onResponse(Object o) {
                observerData.callback(true);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                observerData.callback(false);
            }
        });
    }

    /**
     * 网络批量删除收藏
     *
     * @param mContext
     * @param observerData
     * @param ids
     */
    public void dels(Context mContext, final ObserverData<Boolean> observerData, String ids) {
        IBaseView iBaseView = (IBaseView) mContext;
        VolleyRequest mVolleyRequest = new VolleyRequest(mContext, iBaseView.getQueue());
        mVolleyRequest.setTag(getClass().getName());
        JSONObject jsonParam = mVolleyRequest.getJsonParam();
        UserJson userJson = LoginUtils.getUserInfo(mContext);
        jsonParam.put(VarConstant.HTTP_UID, userJson.getUid());
        jsonParam.put(VarConstant.HTTP_ACCESS_TOKEN, userJson.getToken());
        jsonParam.put(VarConstant.HTTP_ID, ids);
        mVolleyRequest.doPost(HttpConstant.COLLECT_DELS_POINT, jsonParam, new HttpListener<Object>() {
            @Override
            protected void onResponse(Object o) {
                observerData.callback(true);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                observerData.callback(false);
            }
        });
    }

    public static class EventHandlerBean {
        public String tradeId;//
        public int favourState = 0;// 1 赞   0 取消赞
        public int collectState = 0; // 1 收藏   0 取消收藏
        public int commentState = 0; // 1 评论   0 没评论

        public EventHandlerBean(String tradeId) {
            this.tradeId = tradeId;
        }
    }
}
