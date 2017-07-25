package com.jyh.kxt.base.json;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 项目名:Kxt
 * 类描述:标题广告
 * 创建人:苟蒙蒙
 * 创建日期:2017/7/25.
 */

public class AdTitleItemBean implements Parcelable {
    /**
     * title : • 数据直播赢丰财富
     * href : http://521bm.com/6001
     * datetime : 1500862440
     * position : 0
     * author :
     * o_class :
     * o_action :
     * o_id : 15
     * type : title_ad
     * font_size : 26px
     * font_family : PingFang
     * day_color : #136aa4
     * night_color : #1c9cf2
     */

    private String title;
    private String href;
    private String datetime;
    private String position;
    private String author;
    private String o_class;
    private String o_action;
    private String o_id;
    private String type;
    private String font_size;
    private String font_family;
    private String day_color;
    private String night_color;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getO_id() {
        return o_id;
    }

    public void setO_id(String o_id) {
        this.o_id = o_id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getO_class() {
        return o_class;
    }

    public void setO_class(String o_class) {
        this.o_class = o_class;
    }

    public String getO_action() {
        return o_action;
    }

    public void setO_action(String o_action) {
        this.o_action = o_action;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFont_size() {
        return font_size;
    }

    public void setFont_size(String font_size) {
        this.font_size = font_size;
    }

    public String getFont_family() {
        return font_family;
    }

    public void setFont_family(String font_family) {
        this.font_family = font_family;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.href);
        dest.writeString(this.datetime);
        dest.writeString(this.position);
        dest.writeString(this.author);
        dest.writeString(this.o_class);
        dest.writeString(this.o_action);
        dest.writeString(this.o_id);
        dest.writeString(this.type);
        dest.writeString(this.font_size);
        dest.writeString(this.font_family);
        dest.writeString(this.day_color);
        dest.writeString(this.night_color);
    }

    public AdTitleItemBean() {
    }

    protected AdTitleItemBean(Parcel in) {
        this.title = in.readString();
        this.href = in.readString();
        this.datetime = in.readString();
        this.position = in.readString();
        this.author = in.readString();
        this.o_class = in.readString();
        this.o_action = in.readString();
        this.o_id = in.readString();
        this.type = in.readString();
        this.font_size = in.readString();
        this.font_family = in.readString();
        this.day_color = in.readString();
        this.night_color = in.readString();
    }

    public static final Parcelable.Creator<AdTitleItemBean> CREATOR = new Parcelable.Creator<AdTitleItemBean>() {
        @Override
        public AdTitleItemBean createFromParcel(Parcel source) {
            return new AdTitleItemBean(source);
        }

        @Override
        public AdTitleItemBean[] newArray(int size) {
            return new AdTitleItemBean[size];
        }
    };
}