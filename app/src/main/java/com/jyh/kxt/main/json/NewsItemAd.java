package com.jyh.kxt.main.json;

import android.os.Parcel;
import android.os.Parcelable;

import com.jyh.kxt.base.json.AdTitleIconBean;
import com.jyh.kxt.base.json.AdTitleItemBean;

import java.util.List;

/**
 * 项目名:KxtProfessional
 * 类描述:要闻列表特殊广告
 * 创建人:苟蒙蒙
 * 创建日期:2017/11/7.
 */

public class NewsItemAd implements Parcelable {
    private List<AdTitleItemBean> ad;
    private AdTitleIconBean icon;

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.ad);
        dest.writeParcelable(this.icon, flags);
    }

    public NewsItemAd() {
    }

    protected NewsItemAd(Parcel in) {
        this.ad = in.createTypedArrayList(AdTitleItemBean.CREATOR);
        this.icon = in.readParcelable(AdTitleIconBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<NewsItemAd> CREATOR = new Parcelable.Creator<NewsItemAd>() {
        @Override
        public NewsItemAd createFromParcel(Parcel source) {
            return new NewsItemAd(source);
        }

        @Override
        public NewsItemAd[] newArray(int size) {
            return new NewsItemAd[size];
        }
    };
}
