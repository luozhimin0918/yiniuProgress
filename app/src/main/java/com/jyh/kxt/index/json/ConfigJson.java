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
    private SocketJson kuaixun_ws;
    private SocketJson quotes_ws;
    private LoadADJson load_ad;

    public ConfigJson() {
    }

    public ConfigJson(String icon, SlideJson index_ad, SocketJson kuaixun_ws, SocketJson quotes_ws, LoadADJson load_ad) {

        this.icon = icon;
        this.index_ad = index_ad;
        this.kuaixun_ws = kuaixun_ws;
        this.quotes_ws = quotes_ws;
        this.load_ad = load_ad;
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

    public SocketJson getKuaixun_ws() {
        return kuaixun_ws;
    }

    public void setKuaixun_ws(SocketJson kuaixun_ws) {
        this.kuaixun_ws = kuaixun_ws;
    }

    public SocketJson getQuotes_ws() {
        return quotes_ws;
    }

    public void setQuotes_ws(SocketJson quotes_ws) {
        this.quotes_ws = quotes_ws;
    }

    public LoadADJson getLoad_ad() {
        return load_ad;
    }

    public void setLoad_ad(LoadADJson load_ad) {
        this.load_ad = load_ad;
    }
}
