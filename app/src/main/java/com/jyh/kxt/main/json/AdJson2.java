package com.jyh.kxt.main.json;

import android.os.Parcel;
import android.os.Parcelable;

import com.jyh.kxt.base.json.AdTitleIconBean;
import com.jyh.kxt.base.json.AdTitleItemBean;

import java.util.List;

/**
 * 项目名:KxtProfessional
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/10/16.
 */

public class AdJson2 implements Parcelable {
    private List<AdTitleItemBean> ad;
    private AdTitleIconBean icon;

    public AdJson2() {
    }

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

    public AdJson2(List<AdTitleItemBean> ad, AdTitleIconBean icon) {

        this.ad = ad;
        this.icon = icon;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.ad);
        dest.writeParcelable(this.icon, flags);
    }

    protected AdJson2(Parcel in) {
        this.ad = in.createTypedArrayList(AdTitleItemBean.CREATOR);
        this.icon = in.readParcelable(AdTitleIconBean.class.getClassLoader());
    }

    public static final Creator<AdJson2> CREATOR = new Creator<AdJson2>() {
        @Override
        public AdJson2 createFromParcel(Parcel source) {
            return new AdJson2(source);
        }

        @Override
        public AdJson2[] newArray(int size) {
            return new AdJson2[size];
        }
    };
}
