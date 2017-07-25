package com.jyh.kxt.market.bean;

import com.jyh.kxt.base.json.AdItemJson;
import com.jyh.kxt.base.json.AdTitleIconBean;
import com.jyh.kxt.base.json.AdTitleItemBean;

import java.util.List;

/**
 * Created by Mr'Dai on 2017/4/26.
 */

public class MarketHotBean {


    /**
     * type : hot
     * data : {"ad":[],"data":[]}
     */

    private String type;
    private DataBean data;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private List<AdTitleItemBean> ad;
        private List<MarketItemBean> data;
        private AdTitleIconBean icon;

        public List<AdTitleItemBean> getAd() {
            return ad;
        }

        public void setAd(List<AdTitleItemBean> ad) {
            this.ad = ad;
        }

        public AdTitleIconBean getIcon() {
            return icon;
        }

        public void setIcon(AdTitleIconBean icon) {
            this.icon = icon;
        }

        public List<MarketItemBean> getData() {
            return data;
        }

        public void setData(List<MarketItemBean> data) {
            this.data = data;
        }
    }

}
