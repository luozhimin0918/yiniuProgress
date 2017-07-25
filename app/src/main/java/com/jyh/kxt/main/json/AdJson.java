package com.jyh.kxt.main.json;

import android.os.Parcel;
import android.os.Parcelable;

import com.jyh.kxt.base.json.AdItemJson;
import com.jyh.kxt.base.json.JumpJson;

import java.util.List;

/**
 * 项目名:Kxt
 * 类描述:广告
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/18.
 */

public class AdJson implements Parcelable {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.pic_ad, flags);
        dest.writeTypedList(this.text_ad);
    }

    public AdJson() {
    }

    protected AdJson(Parcel in) {
        this.pic_ad = in.readParcelable(AdItemJson.class.getClassLoader());
        this.text_ad = in.createTypedArrayList(AdItemJson.CREATOR);
    }

    public static final Creator<AdJson> CREATOR = new Creator<AdJson>() {
        @Override
        public AdJson createFromParcel(Parcel source) {
            return new AdJson(source);
        }

        @Override
        public AdJson[] newArray(int size) {
            return new AdJson[size];
        }
    };
}
