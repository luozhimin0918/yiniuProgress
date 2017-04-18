package com.jyh.kxt.main.json;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 项目名:Kxt
 * 类描述:广告
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/18.
 */

public class AdJson implements Parcelable{

    private SlideJson pic_ad;
    private SlideJson[] text_ad;

    public AdJson(SlideJson pic_ad, SlideJson[] text_ad) {
        this.pic_ad = pic_ad;
        this.text_ad = text_ad;
    }

    public AdJson() {

    }

    protected AdJson(Parcel in) {
        pic_ad = in.readParcelable(SlideJson.class.getClassLoader());
        text_ad = in.createTypedArray(SlideJson.CREATOR);
    }

    public static final Creator<AdJson> CREATOR = new Creator<AdJson>() {
        @Override
        public AdJson createFromParcel(Parcel in) {
            return new AdJson(in);
        }

        @Override
        public AdJson[] newArray(int size) {
            return new AdJson[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(pic_ad,flags);
        dest.writeParcelableArray(text_ad, flags);
    }

    public SlideJson[] getText_ad() {
        return text_ad;
    }

    public void setText_ad(SlideJson[] text_ad) {
        this.text_ad = text_ad;
    }

    public SlideJson getPic_ad() {
        return pic_ad;
    }

    public void setPic_ad(SlideJson pic_ad) {
        this.pic_ad = pic_ad;
    }
}
