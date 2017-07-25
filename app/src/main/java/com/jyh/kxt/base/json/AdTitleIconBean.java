package com.jyh.kxt.base.json;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/7/25.
 */

public class AdTitleIconBean implements Parcelable {
    /**
     * day_icon : http://img.kxt.com/Uploads/Picture/2017-07-24/5975707c9b7ea.png
     * night_icon : http://img.kxt.com/Uploads/Picture/2017-07-24/5975706fdf3cd.png
     */

    private String day_icon;
    private String night_icon;

    public String getDay_icon() {
        return day_icon;
    }

    public void setDay_icon(String day_icon) {
        this.day_icon = day_icon;
    }

    public String getNight_icon() {
        return night_icon;
    }

    public void setNight_icon(String night_icon) {
        this.night_icon = night_icon;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.day_icon);
        dest.writeString(this.night_icon);
    }

    public AdTitleIconBean() {
    }

    protected AdTitleIconBean(Parcel in) {
        this.day_icon = in.readString();
        this.night_icon = in.readString();
    }

    public static final Parcelable.Creator<AdTitleIconBean> CREATOR = new Parcelable.Creator<AdTitleIconBean>() {
        @Override
        public AdTitleIconBean createFromParcel(Parcel source) {
            return new AdTitleIconBean(source);
        }

        @Override
        public AdTitleIconBean[] newArray(int size) {
            return new AdTitleIconBean[size];
        }
    };
}