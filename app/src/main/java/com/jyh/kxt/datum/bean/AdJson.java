package com.jyh.kxt.datum.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.jyh.kxt.base.json.AdItemJson;

import java.util.List;

/**
 * 项目名:Kxt
 * 类描述:广告
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/18.
 */

public class AdJson extends CalendarType {
    private AdItemJson pic_ad;
    private List<AdItemJson> text_ad;

    public AdItemJson getPic_ad() {
        return pic_ad;
    }

    public void setPic_ad(AdItemJson pic_ad) {
        this.pic_ad = pic_ad;
    }

    public List<AdItemJson> getText_ad() {
        return text_ad;
    }

    public void setText_ad(List<AdItemJson> text_ad) {
        this.text_ad = text_ad;
    }
    public AdJson() {
    }

    public AdJson(AdItemJson pic_ad, List<AdItemJson> text_ad) {
        this.pic_ad = pic_ad;
        this.text_ad = text_ad;
    }
}
