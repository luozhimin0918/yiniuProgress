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
    private String font_size;
    private String day_color;
    private String night_color;
    private AdTitleIconBean icon;

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

    public String getFont_size() {
        return font_size;
    }

    public void setFont_size(String font_size) {
        this.font_size = font_size;
    }

    public String getDay_color() {
        return day_color;
    }

    public void setDay_color(String day_color) {
        this.day_color = day_color;
    }

    public String getNight_color() {
        return night_color;
    }

    public void setNight_color(String night_color) {
        this.night_color = night_color;
    }

    public AdTitleIconBean getIcon() {
        return icon;
    }

    public void setIcon(AdTitleIconBean icon) {
        this.icon = icon;
    }

    public AdItemJson(String author, String datetime, String href, String picture, int position, String title, String type, int
            imageHeight, String font_size, String day_color, String night_color, AdTitleIconBean icon) {

        this.author = author;
        this.datetime = datetime;
        this.href = href;
        this.picture = picture;
        this.position = position;
        this.title = title;
        this.type = type;
        this.imageHeight = imageHeight;
        this.font_size = font_size;
        this.day_color = day_color;
        this.night_color = night_color;
        this.icon = icon;
    }

    public AdItemJson(String o_action, String o_class, String o_id, String author, String datetime, String href, String picture, int
            position, String title, String type, int imageHeight, String font_size, String day_color, String night_color, AdTitleIconBean
            icon) {
        super(o_action, o_class, o_id);
        this.author = author;
        this.datetime = datetime;
        this.href = href;
        this.picture = picture;
        this.position = position;
        this.title = title;
        this.type = type;
        this.imageHeight = imageHeight;
        this.font_size = font_size;
        this.day_color = day_color;
        this.night_color = night_color;
        this.icon = icon;
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
        dest.writeString(this.font_size);
        dest.writeString(this.day_color);
        dest.writeString(this.night_color);
        dest.writeParcelable(this.icon, flags);
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
        this.font_size = in.readString();
        this.day_color = in.readString();
        this.night_color = in.readString();
        this.icon = in.readParcelable(AdTitleIconBean.class.getClassLoader());
    }

    public static final Creator<AdItemJson> CREATOR = new Creator<AdItemJson>() {
        @Override
        public AdItemJson createFromParcel(Parcel source) {
            return new AdItemJson(source);
        }

        @Override
        public AdItemJson[] newArray(int size) {
            return new AdItemJson[size];
        }
    };

    public AdItemJson() {
    }
}