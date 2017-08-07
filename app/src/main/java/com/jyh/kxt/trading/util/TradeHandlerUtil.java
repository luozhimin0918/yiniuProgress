package com.jyh.kxt.trading.util;

import android.content.Context;
import android.content.Intent;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.trading.json.ViewPointTradeBean;
import com.jyh.kxt.user.json.UserJson;
import com.jyh.kxt.user.ui.LoginOrRegisterActivity;
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
     * 存储的点赞的Key 值
     */
    private final static String tradeKey = "trade";

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

    //isSaveSuccess
    public boolean saveState(Context mContext, String id, int type, boolean bool) {

        if (!NetUtils.isNetworkAvailable(mContext)) {
            ToastView.makeText3(mContext, "暂无网络,点赞失败");
            return false;
        }

        UserJson userInfo = LoginUtils.getUserInfo(mContext);
        if (type == 2) {
            if (userInfo == null) {
                mContext.startActivity(new Intent(mContext, LoginOrRegisterActivity.class));
                return false;
            }
        }

        TradeHandlerBean historyTradeHandlerBean = checkHandlerState(id);

        if (historyTradeHandlerBean == null) {
            historyTradeHandlerBean = new TradeHandlerBean();
            historyTradeHandlerBean.tradeId = id;
            if (type == 1) {
                historyTradeHandlerBean.isFavour = bool;
            } else if (type == 2) {
                historyTradeHandlerBean.isCollect = bool;
            }
            tradeHandlerList.add(historyTradeHandlerBean);
        } else {
            switch (type) {
                case 1:
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
            requestCollect(mContext, id, userInfo, historyTradeHandlerBean.isCollect);
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
     * 收藏
     *
     * @param mContext
     * @param id
     * @param userInfo
     * @param isCollect
     */
    private void requestCollect(Context mContext, String id, UserJson userInfo, boolean isCollect) {
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


    public static class TradeHandlerBean {
        public String tradeId;
        public boolean isFavour = false; //是否赞
        public boolean isCollect = false; //是否收藏
    }
}
