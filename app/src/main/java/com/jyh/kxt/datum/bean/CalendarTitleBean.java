package com.jyh.kxt.datum.bean;

import com.jyh.kxt.base.json.AdTitleIconBean;
import com.jyh.kxt.base.json.AdTitleItemBean;

import java.util.List;

/**
 * Created by Mr'Dai on 2017/4/21.
 * 标题的Bean
 */
public class CalendarTitleBean extends CalendarType {
    private String name;
    private int spaceType;//填充类型  0不填充  1需要填充  2已经填充
    private List<AdTitleItemBean> ads;
    private AdTitleIconBean icon;
    private boolean isShowAd = false;

    public AdTitleIconBean getIcon() {
        return icon;
    }

    public void setIcon(AdTitleIconBean icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSpaceType() {
        return spaceType;
    }

    public void setSpaceType(int spaceType) {
        this.spaceType = spaceType;
    }

    public List<AdTitleItemBean> getAds() {
        return ads;
    }

    public void setAds(List<AdTitleItemBean> ads) {
        this.ads = ads;
    }

    public boolean isShowAd() {
        return isShowAd;
    }

    public void setShowAd(boolean showAd) {
        isShowAd = showAd;
    }
}
