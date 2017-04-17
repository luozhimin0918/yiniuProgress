package com.jyh.kxt.main.json;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 项目名:Kxt
 * 类描述:要闻
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/17.
 */

public class NewsJson implements Parcelable {
    private String title;    //标题
    private String picture;  //图片
    private String author;   //作者
    private String datetime; //时间
    private String type;     //类型
    private String href;     //用于webview直接打开的链接
    private String o_class;  //目标栏目
    private String o_action; //index|list|detail
    private String o_id;      //目标id

    @Override
    public String toString() {
        return "NewsJson{" +
                "title='" + title + '\'' +
                ", picture='" + picture + '\'' +
                ", author='" + author + '\'' +
                ", datetime='" + datetime + '\'' +
                ", type='" + type + '\'' +
                ", href='" + href + '\'' +
                ", o_class='" + o_class + '\'' +
                ", o_action='" + o_action + '\'' +
                ", o_id='" + o_id + '\'' +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
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

    public String getO_id() {
        return o_id;
    }

    public void setO_id(String o_id) {
        this.o_id = o_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(picture);
        dest.writeString(author);
        dest.writeString(datetime);
        dest.writeString(type);
        dest.writeString(href);
        dest.writeString(o_class);
        dest.writeString(o_action);
        dest.writeString(o_id);
    }

    private NewsJson(Parcel parcel) {
        title = parcel.readString();
        picture = parcel.readString();
        author = parcel.readString();
        datetime = parcel.readString();
        type = parcel.readString();
        href = parcel.readString();
        o_class = parcel.readString();
        o_action = parcel.readString();
        o_id = parcel.readString();
    }

    public NewsJson() {
    }

    public NewsJson(String title, String picture, String author, String datetime, String type, String href, String o_class, String
            o_action, String o_id) {
        this.title = title;
        this.picture = picture;
        this.author = author;
        this.datetime = datetime;
        this.type = type;
        this.href = href;
        this.o_class = o_class;
        this.o_action = o_action;
        this.o_id = o_id;
    }

    public static final Parcelable.Creator<NewsJson> CREATOR = new Parcelable.Creator<NewsJson>() {

        @Override
        public NewsJson createFromParcel(Parcel source) {
            return new NewsJson(source);
        }

        @Override
        public NewsJson[] newArray(int size) {
            return new NewsJson[size];
        }
    };

}
