package com.jyh.kxt.main.json;

import android.os.Parcel;
import android.os.Parcelable;

import com.jyh.kxt.base.json.JumpJson;

/**
 * 项目名:Kxt
 * 类描述:幻灯片、index_ad、首页按钮
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/17.
 */

public class SlideJson extends JumpJson implements Parcelable {

    private String name;     //名称,暂无用到
    private String title;   //标题
    private String picture;  //图片完整地址
    private String href;     //若href不为空，则使用webview打开该链接

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
    public String toString() {
        return "SlideJson{" +
                "name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", picture='" + picture + '\'' +
                ", href='" + href + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(title);
        dest.writeString(picture);
        dest.writeString(href);
        dest.writeString(o_class);
        dest.writeString(o_action);
        dest.writeString(o_id);
    }

    public SlideJson() {
    }


    public SlideJson(Parcel source) {
        name = source.readString();
        title = source.readString();
        picture = source.readString();
        href = source.readString();
        o_class = source.readString();
        o_action = source.readString();
        o_id = source.readString();
    }

    public SlideJson(String name, String title, String picture, String href, String o_class, String o_action, String o_id) {

        this.name = name;
        this.title = title;
        this.picture = picture;
        this.href = href;
        this.o_class = o_class;
        this.o_action = o_action;
        this.o_id = o_id;
    }

    public static final Parcelable.Creator<SlideJson> CREATOR = new Creator<SlideJson>() {
        @Override
        public SlideJson createFromParcel(Parcel source) {
            return new SlideJson(source);
        }

        @Override
        public SlideJson[] newArray(int size) {
            return new SlideJson[size];
        }
    };
}
