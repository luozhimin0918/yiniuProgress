package com.jyh.kxt.base.json;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/7/24.
 */

public class AdItemJson extends JumpJson implements Parcelable {
    private String author;
    private String datetime;
    private String href;
    private String picture;
    private int position;
    private String title;
    private String type;
    private int imageHeight;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.author);
        dest.writeString(this.datetime);
        dest.writeString(this.href);
        dest.writeString(this.picture);
        dest.writeInt(this.position);
        dest.writeString(this.title);
        dest.writeString(this.type);
        dest.writeInt(this.imageHeight);
    }

    public AdItemJson() {
    }

    protected AdItemJson(Parcel in) {
        this.author = in.readString();
        this.datetime = in.readString();
        this.href = in.readString();
        this.picture = in.readString();
        this.position = in.readInt();
        this.title = in.readString();
        this.type = in.readString();
        this.imageHeight = in.readInt();
    }

    public static final Parcelable.Creator<AdItemJson> CREATOR = new Parcelable.Creator<AdItemJson>() {
        @Override
        public AdItemJson createFromParcel(Parcel source) {
            return new AdItemJson(source);
        }

        @Override
        public AdItemJson[] newArray(int size) {
            return new AdItemJson[size];
        }
    };
}