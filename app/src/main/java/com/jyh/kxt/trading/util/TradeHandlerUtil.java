package com.jyh.kxt.trading.util;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.trading.json.ViewPointTradeBean;
import com.jyh.kxt.user.json.UserJson;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;
import com.library.util.NetUtils;
import com.library.util.SPUtils;
import com.library.widget.window.ToastView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Mr'Dai on 2017/8/4.
 * 交易圈子点赞的记录
 */

public class TradeHandlerUtil {

    /**
     * Sp 文件名称
     */
    private final static String TradeHandlerFileName = "tradehandler";

    /**
     * 存储的点赞,收藏的Key 值
     */
    private final static String tradeKey = "trade";
    /**
     * 存储的收藏的对象
     */
    private final static String collectBeanKey = "collectbean";

    static TradeHandlerUtil tradeHandlerUtil;

    public static TradeHandlerUtil getInstance() {
        if (tradeHandlerUtil == null) {
            tradeHandlerUtil = new TradeHandlerUtil();
        }
        return tradeHandlerUtil;
    }

    private List<TradeHandlerBean> tradeHandlerList = new ArrayList<>();

    public void initTradeHandler(Context mContext) {
        tradeHandlerList.clear();
        String tradeJson = SPUtils.getString2(mContext, TradeHandlerFileName, tradeKey);
        if (tradeJson != null && !tradeJson.isEmpty()) {
            tradeHandlerList.addAll(JSONArray.parseArray(tradeJson, TradeHandlerBean.class));
        }
    }

    public void listCheckState(List<ViewPointTradeBean> checkList) {
        if (checkList == null) return;
        for (ViewPointTradeBean viewPointTradeBean : checkList) {
            TradeHandlerBean tradeHandlerBean = checkHandlerState(viewPointTradeBean.o_id);
            if (tradeHandlerBean != null) {
                viewPointTradeBean.isFavour = tradeHandlerBean.isFavour;
                viewPointTradeBean.isCollect = tradeHandlerBean.isCollect;
            }
        }
    }

    /**
     * 可能为空
     *
     * @param id
     * @return
     */
    public TradeHandlerBean checkHandlerState(String id) {
        return contains(id);
    }

    public void updateCollect(Context mContext, String id, boolean isCollect) {
        for (TradeHandlerBean handlerBean : tradeHandlerList) {
            if (id.equals(handlerBean.tradeId)) {
                handlerBean.isCollect = isCollect;
            }
        }
        SPUtils.save2(mContext, TradeHandlerFileName, tradeKey, JSONObject.toJSONString(tradeHandlerList));
    }


    public boolean saveState(Context mContext, String id, int type, boolean bool) {
        ViewPointTradeBean viewPointTradeBean = new ViewPointTradeBean();
        viewPointTradeBean.o_id = id;
        return saveState(mContext, viewPointTradeBean, type, bool);
    }

    /**
     * @param mContext
     * @param type     1 交易圈列表的赞  2 收藏 3
     * @param bool
     * @return
     */
    //isSaveSuccess
    public boolean saveState(Context mContext, ViewPointTradeBean viewPointTradeBean, int type, boolean bool) {
        try {
            String id = viewPointTradeBean.o_id;

            if (!NetUtils.isNetworkAvailable(mContext)) {
                ToastView.makeText3(mContext, "暂无网络,点赞失败");
                return false;
            }

//            UserJson userInfo = LoginUtils.getUserInfo(mContext);
//            if (type == 2) {
//                if (userInfo == null) {
//                    mContext.startActivity(new Intent(mContext, LoginOrRegisterActivity.class));
//                    return false;
//                }
//            }

            TradeHandlerBean historyTradeHandlerBean = checkHandlerState(id);

            if (historyTradeHandlerBean == null) {
                historyTradeHandlerBean = new TradeHandlerBean();
                historyTradeHandlerBean.tradeId = id;
                if (type == 1 || type == 3) {
                    historyTradeHandlerBean.isFavour = bool;
                } else if (type == 2) {
                    historyTradeHandlerBean.isCollect = bool;
                }
                tradeHandlerList.add(historyTradeHandlerBean);
            } else {
                switch (type) {
                    case 1:
                    case 3:
                        historyTradeHandlerBean.isFavour = bool;
                        break;
                    case 2:
                        historyTradeHandlerBean.isCollect = bool;
                        break;
                }
            }
            SPUtils.save2(mContext, TradeHandlerFileName, tradeKey, JSONObject.toJSONString(tradeHandlerList));

            if (type == 1) {  //发起网络请求
                requestFavour(mContext, id);
            } else if (type == 2) {
                saveCollectBean(mContext, viewPointTradeBean, historyTradeHandlerBean.isCollect);  //同时保存一份收藏到Sp本地文件中

                UserJson userInfo = LoginUtils.getUserInfo(mContext);
                if (userInfo == null) {
                    requestCollect(mContext, id, userInfo, historyTradeHandlerBean.isCollect);
                }

            } else if (type == 3) {
                requestFavour2(mContext, id);
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

    /**
     * 收藏
     *
     * @param mContext
     * @param id
     * @param userInfo
     * @param isCollect
     */
    public void requestCollect(Context mContext, String id, UserJson userInfo, boolean isCollect) {
        IBaseView iBaseView = (IBaseView) mContext;
        VolleyRequest mVolleyRequest = new VolleyRequest(mContext, iBaseView.getQueue());
        mVolleyRequest.setTag(getClass().getName());

        JSONObject mainParam = mVolleyRequest.getJsonParam();

        mainParam.put("uid", userInfo.getUid());
        mainParam.put("accessToken", userInfo.getToken());
        mainParam.put("id", id);
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

            }
        });
    }

    private TradeHandlerBean contains(String id) {
        Iterator<TradeHandlerBean> it = tradeHandlerList.iterator();
        if (id != null) {
            while (it.hasNext()) {
                TradeHandlerBean next = it.next();
                if (id.equals(next.tradeId)) {
                    return next;
                }
            }
        }
        return null;
    }

    public void clearCollectBean(Context mContext) {
        SPUtils.save2(mContext, TradeHandlerFileName, collectBeanKey, "");
    }

    /**
     * 保存收藏的列表
     *
     * @param viewPointTradeList
     */
    public void saveCollectList(Context mContext, List<ViewPointTradeBean> viewPointTradeList) {
        SPUtils.save2(mContext, TradeHandlerFileName, collectBeanKey, JSON.toJSONString(viewPointTradeList));
    }

    /**
     * 保存收藏的Bean
     *
     * @param viewPointTradeBean
     * @param isCollect
     */
    public void saveCollectBean(Context mContext, ViewPointTradeBean viewPointTradeBean, boolean isCollect) {
        String collectJson = SPUtils.getString2(mContext, TradeHandlerFileName, collectBeanKey);

        List<ViewPointTradeBean> viewPointTradeList;
        if (!TextUtils.isEmpty(collectJson)) {
            viewPointTradeList = JSONArray.parseArray(collectJson, ViewPointTradeBean.class);
        } else {
            viewPointTradeList = new ArrayList<>();
        }

        if (viewPointTradeList.size() > 100) {
            viewPointTradeList.remove(0);
        }
        if (isCollect) {
            viewPointTradeList.add(viewPointTradeBean);
        } else {
            Iterator<ViewPointTradeBean> iterator = viewPointTradeList.iterator();
            while (iterator.hasNext()) {
                ViewPointTradeBean next = iterator.next();
                if (viewPointTradeBean.o_id.contains(next.o_id)) {
                    iterator.remove();
                }
            }
        }
        SPUtils.save2(mContext, TradeHandlerFileName, collectBeanKey, JSON.toJSONString(viewPointTradeList));
    }

    /**
     * 得到本地的收藏
     *
     * @param mContext
     * @return
     */
    public List<ViewPointTradeBean> getLocalityCollectList(Context mContext, int start, int digit) {
        String collectJson = SPUtils.getString2(mContext, TradeHandlerFileName, collectBeanKey);
        List<ViewPointTradeBean> viewPointTradeList;
        if (!TextUtils.isEmpty(collectJson)) {
            viewPointTradeList = JSONArray.parseArray(collectJson, ViewPointTradeBean.class);
        } else {
            viewPointTradeList = new ArrayList<>();
        }


        if (Integer.MAX_VALUE == digit) {
            return viewPointTradeList;
        }
        if (start >= viewPointTradeList.size()) {
            return new ArrayList<>();
        }

        List<ViewPointTradeBean> subTrade;
        try {
            subTrade = viewPointTradeList.subList(start, start + digit);
        } catch (Exception e) {
            subTrade = viewPointTradeList.subList(start, viewPointTradeList.size());
        }
        return subTrade;
    }


    public static class TradeHandlerBean {
        public String tradeId;
        public boolean isFavour = false; //是否赞
        public boolean isCollect = false; //是否收藏
    }

    public static class EventHandlerBean {
        public String tradeId;
        public int favourState = 0;// 1 赞   0 取消赞
        public int collectState = 0; // 1 收藏   0 取消收藏
        public int commentState = 0; // 1 评论   0 没评论

        public EventHandlerBean(String tradeId) {
            this.tradeId = tradeId;
        }
    }
}
