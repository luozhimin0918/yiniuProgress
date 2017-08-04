package com.jyh.kxt.trading.util;

import android.content.Context;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jyh.kxt.trading.json.ViewPointTradeBean;
import com.library.util.SPUtils;

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

    public void saveState(Context mContext, String id, int type) {

        TradeHandlerBean historyTradeHandlerBean = checkHandlerState(id);

        if (historyTradeHandlerBean == null) {
            TradeHandlerBean tradeHandlerBean = new TradeHandlerBean();
            tradeHandlerBean.tradeId = id;
            tradeHandlerBean.isFavour = type == 1;
            tradeHandlerBean.isCollect = type == 2;

            tradeHandlerList.add(tradeHandlerBean);
        } else {
            switch (type) {
                case 1:
                    historyTradeHandlerBean.isFavour = true;
                    break;
                case 2:
                    historyTradeHandlerBean.isCollect = true;
                    break;
            }
        }
        SPUtils.save2(mContext, TradeHandlerFileName, tradeKey, JSONObject.toJSONString(tradeHandlerList));
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
        public boolean isFavour; //是否赞
        public boolean isCollect; //是否收藏
    }
}
