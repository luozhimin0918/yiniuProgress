package com.jyh.kxt.main.json;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

import java.util.Objects;

/**
 * 项目名:Kxt
 * 类描述:要闻
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/17.
 */
@Entity(nameInDb = "NEWS_BEAN")
public class NewsJson implements Parcelable {

    private String title;    //标题
    private String picture;  //图片
    private String author;   //作者
    private String datetime; //时间
    private String type;     //类型
    private String href;     //用于webview直接打开的链接
    private String o_action;
    private String o_class;
    private String o_id;

    /**
     * 数据库存储类型
     * DB_TYPE_BROWER 代表浏览记录
     * DB_TYPE_COLLECT_LOCAL 未登录本地收藏
     * DB_TYPE_COLLECT_NETTOLOCAL 登录之后本地收藏
     */
    private int dataType;

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

    public String getO_action() {
        return o_action;
    }

    public void setO_action(String o_action) {
        this.o_action = o_action;
    }

    public String getO_class() {
        return o_class;
    }

    public void setO_class(String o_class) {
        this.o_class = o_class;
    }

    public String getO_id() {
        return o_id;
    }

    public void setO_id(String o_id) {
        this.o_id = o_id;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
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

    @Generated(hash = 196233574)
    public NewsJson(String title, String picture, String author, String datetime, String type, String href, String o_action, String o_class,
            String o_id, int dataType) {
        this.title = title;
        this.picture = picture;
        this.author = author;
        this.datetime = datetime;
        this.type = type;
        this.href = href;
        this.o_action = o_action;
        this.o_class = o_class;
        this.o_id = o_id;
        this.dataType = dataType;
    }

    @Generated(hash = 1974929583)
    public NewsJson() {
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof NewsJson) {
            NewsJson newsJson = (NewsJson) obj;
            return Objects.equals(title, newsJson.title) && Objects.equals(picture, newsJson.getPicture()) && Objects.equals(author,
                    newsJson.getAuthor()) && Objects.equals(datetime, newsJson.getDatetime()) && Objects.equals(type, newsJson.getType())
                    && Objects.equals(href, newsJson.getHref()) && Objects.equals(o_class, newsJson.getO_class()) && Objects.equals
                    (o_action, newsJson.getO_action()) && Objects.equals(o_id, newsJson.getO_id());
        } else
            return false;
    }
}
