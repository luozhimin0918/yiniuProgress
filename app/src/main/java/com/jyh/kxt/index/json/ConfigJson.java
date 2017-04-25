package com.jyh.kxt.index.json;

import com.jyh.kxt.main.json.SlideJson;

/**
 * 项目名:Kxt
 * 类描述:配置信息
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/20.
 */

public class ConfigJson {
    private String icon;
    private SlideJson index_ad;
    private LoadADJson load_ad;
    private String url_kx_share;//分享地址

    public ConfigJson() {
    }

    public ConfigJson(String icon, SlideJson index_ad, LoadADJson load_ad, String url_kx_share) {
        this.icon = icon;
        this.index_ad = index_ad;
        this.load_ad = load_ad;
        this.url_kx_share = url_kx_share;
    }

    public String getUrl_kx_share() {

        return url_kx_share;
    }

    public void setUrl_kx_share(String url_kx_share) {
        this.url_kx_share = url_kx_share;
    }

    public String getIcon() {

        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public SlideJson getIndex_ad() {
        return index_ad;
    }

    public void setIndex_ad(SlideJson index_ad) {
        this.index_ad = index_ad;
    }

    public LoadADJson getLoad_ad() {
        return load_ad;
    }

    public void setLoad_ad(LoadADJson load_ad) {
        this.load_ad = load_ad;
    }
}
