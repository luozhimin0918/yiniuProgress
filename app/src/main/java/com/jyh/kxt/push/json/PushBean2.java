package com.jyh.kxt.push.json;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 项目名:Kxt
 * 类描述:推送实体类2
 * 创建人:苟蒙蒙
 * 创建日期:2017/6/6.
 */

public class PushBean2 extends  PushBean implements Parcelable {
    private String url;
    private String share_url;
    private String time;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getShare_url() {
        return share_url;
    }

    public void setShare_url(String share_url) {
        this.share_url = share_url;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeString(this.share_url);
        dest.writeString(this.time);
    }

    public PushBean2() {
    }

    protected PushBean2(Parcel in) {
        this.url = in.readString();
        this.share_url = in.readString();
        this.time = in.readString();
    }

    public static final Parcelable.Creator<PushBean2> CREATOR = new Parcelable.Creator<PushBean2>() {
        @Override
        public PushBean2 createFromParcel(Parcel source) {
            return new PushBean2(source);
        }

        @Override
        public PushBean2[] newArray(int size) {
            return new PushBean2[size];
        }
    };
}
