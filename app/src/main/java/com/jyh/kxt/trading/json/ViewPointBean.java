package com.jyh.kxt.trading.json;

import java.util.List;

/**
 * Created by Mr'Dai on 2017/8/1.
 */

public class ViewPointBean {
    private List<ViewPointHotBean> hot;
    private List<ViewPointTradeBean> trade;

    public List<ViewPointHotBean> getHot() {
        return hot;
    }

    public void setHot(List<ViewPointHotBean> hot) {
        this.hot = hot;
    }

    public List<ViewPointTradeBean> getTrade() {
        return trade;
    }

    public void setTrade(List<ViewPointTradeBean> trade) {
        this.trade = trade;
    }
}
