package com.jyh.kxt.main.json.flash;

import android.os.Parcel;
import android.os.Parcelable;

import com.jyh.kxt.main.json.NewsJson;
import com.jyh.kxt.main.json.SlideJson;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名:Kxt
 * 类描述:快讯详情
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/16.
 */

public class FlashContentJson implements Parcelable {

    private FlashJson kuaixun;
    private List<SlideJson> ad;
    private List<NewsJson> article;//推荐列表

    private String share_sina_title;

    public FlashJson getKuaixun() {
        return kuaixun;
    }

    public void setKuaixun(FlashJson kuaixun) {
        this.kuaixun = kuaixun;
    }

    public List<SlideJson> getAd() {
        if (ad == null) {
            ad = new ArrayList<>();
        }
        return ad;
    }

    public void setAd(List<SlideJson> ad) {
        this.ad = ad;
    }

    public List<NewsJson> getArticle() {
        return article;
    }

    public void setArticle(List<NewsJson> article) {
        this.article = article;
    }

    public String getShare_sina_title() {
        return share_sina_title;
    }

    public void setShare_sina_title(String share_sina_title) {
        this.share_sina_title = share_sina_title;
    }

    public FlashContentJson() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.kuaixun, flags);
        dest.writeTypedList(this.ad);
        dest.writeTypedList(this.article);
        dest.writeString(this.share_sina_title);
    }

    protected FlashContentJson(Parcel in) {
        this.kuaixun = in.readParcelable(FlashJson.class.getClassLoader());
        this.ad = in.createTypedArrayList(SlideJson.CREATOR);
        this.article = in.createTypedArrayList(NewsJson.CREATOR);
        this.share_sina_title = in.readString();
    }

    public static final Creator<FlashContentJson> CREATOR = new Creator<FlashContentJson>() {
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
