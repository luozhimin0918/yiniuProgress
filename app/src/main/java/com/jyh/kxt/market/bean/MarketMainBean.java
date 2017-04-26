package com.jyh.kxt.market.bean;

import java.util.List;

/**
 * Created by Mr'Dai on 2017/4/26.
 */

public class MarketMainBean {
    private List<MarketItemBean> data;
    private String type;

    public List<MarketItemBean> getData() {
        return data;
    }

    public void setData(List<MarketItemBean> data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
