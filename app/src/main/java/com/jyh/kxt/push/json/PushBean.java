package com.jyh.kxt.push.json;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/6/6.
 */

public class PushBean implements Parcelable {
    public String id;
    public String title;
    public String tvContent;
    public String code;

    public PushBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.tvContent);
        dest.writeString(this.code);
    }

    protected PushBean(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.tvContent = in.readString();
        this.code = in.readString();
    }

    public static final Creator<PushBean> CREATOR = new Creator<PushBean>() {
        @Override
        public PushBean createFromParcel(Parcel source) {
            return new PushBean(source);
        }

        @Override
        public PushBean[] newArray(int size) {
            return new PushBean[size];
        }
    };
}
