package com.jyh.kxt.main.json;

import android.os.Parcel;
import android.os.Parcelable;

import com.jyh.kxt.base.json.AdTitleIconBean;
import com.jyh.kxt.base.json.AdTitleItemBean;

import java.util.List;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/7/24.
 */

public class MainNewsContentJson implements Parcelable {

    private String type;
    private DataBean data;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean implements Parcelable {

        private AdTitleIconBean icon;
        private List<NewsJson> data;
        private List<AdTitleItemBean> ad;

        public AdTitleIconBean getIcon() {
            return icon;
        }

        public void setIcon(AdTitleIconBean icon) {
            this.icon = icon;
        }

        public List<NewsJson> getData() {
            return data;
        }

        public void setData(List<NewsJson> data) {
            this.data = data;
        }

        public List<AdTitleItemBean> getAd() {
            return ad;
        }

        public void setAd(List<AdTitleItemBean> ad) {
            this.ad = ad;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(this.icon, flags);
            dest.writeTypedList(this.data);
            dest.writeTypedList(this.ad);
        }

        public DataBean() {
        }

        protected DataBean(Parcel in) {
            this.icon = in.readParcelable(AdTitleIconBean.class.getClassLoader());
            this.data = in.createTypedArrayList(NewsJson.CREATOR);
            this.ad = in.createTypedArrayList(AdTitleItemBean.CREATOR);
        }

        public static final Creator<DataBean> CREATOR = new Creator<DataBean>() {
            @Override
            public DataBean createFromParcel(Parcel source) {
                return new DataBean(source);
            }

            @Override
            public DataBean[] newArray(int size) {
                return new DataBean[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.type);
        dest.writeParcelable(this.data, flags);
    }

    public MainNewsContentJson() {
    }

    protected MainNewsContentJson(Parcel in) {
        this.type = in.readString();
        this.data = in.readParcelable(DataBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<MainNewsContentJson> CREATOR = new Parcelable.Creator<MainNewsContentJson>() {
        @Override
        public MainNewsContentJson createFromParcel(Parcel source) {
            return new MainNewsContentJson(source);
        }

        @Override
        public MainNewsContentJson[] newArray(int size) {
            return new MainNewsContentJson[size];
        }
    };
}
