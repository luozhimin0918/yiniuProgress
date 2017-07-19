package com.jyh.kxt.market.kline.bean;

import java.util.List;

/**
 * Created by Mr'Dai on 2017/7/17.
 */

public class KLineParse {

    private List<MarketTrendBean> KLineList;

    public void setKLineList(List<MarketTrendBean> KLineList) {
        this.KLineList = KLineList;
    }

    public List<MarketTrendBean> getKLineList() {
        return KLineList;
    }

    public float getBaseValue() {
        MarketTrendBean marketTrendBean = KLineList.get(KLineList.size() - 1);
        return (float) marketTrendBean.getClose();
    }
}
