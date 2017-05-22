package com.jyh.kxt.main.json.flash;

import android.os.Parcel;
import android.os.Parcelable;

import com.jyh.kxt.main.json.AdJson;
import com.jyh.kxt.main.json.NewsJson;

import java.util.List;

/**
 * 项目名:Kxt
 * 类描述:快讯详情
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/16.
 */

public class FlashContentJson implements Parcelable {

    private FlashJson kuaixun;
    private AdJson ad;
    private List<NewsJson> article;//推荐列表

    public FlashJson getKuaixun() {
        return kuaixun;
    }

    public void setKuaixun(FlashJson kuaixun) {
        this.kuaixun = kuaixun;
    }

    public AdJson getAd() {
        return ad;
    }

    public void setAd(AdJson ad) {
        this.ad = ad;
    }

    public List<NewsJson> getArticle() {
        return article;
    }

    public void setArticle(List<NewsJson> article) {
        this.article = article;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.kuaixun, flags);
        dest.writeParcelable(this.ad, flags);
        dest.writeTypedList(this.article);
    }

    public FlashContentJson() {
    }

    protected FlashContentJson(Parcel in) {
        this.kuaixun = in.readParcelable(FlashJson.class.getClassLoader());
        this.ad = in.readParcelable(AdJson.class.getClassLoader());
        this.article = in.createTypedArrayList(NewsJson.CREATOR);
    }

    public static final Parcelable.Creator<FlashContentJson> CREATOR = new Parcelable.Creator<FlashContentJson>() {
        @Override
        public FlashContentJson createFromParcel(Parcel source) {
            return new FlashContentJson(source);
        }

        @Override
        public FlashContentJson[] newArray(int size) {
            return new FlashContentJson[size];
        }
    };
}