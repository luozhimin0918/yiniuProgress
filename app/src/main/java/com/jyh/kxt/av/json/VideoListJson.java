package com.jyh.kxt.av.json;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 项目名:Kxt
 * 类描述:视听列表
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/18.
 */

@Entity(nameInDb = "VIDEO_BEAN")
public class VideoListJson implements Parcelable {
    @JSONField(name = "id")
    private String uid;
    private String category_id;
    private String title;
    private String picture;
    private String num_comment;
    private String num_good;
    private String num_play;
    private String create_time;

    /**
     * 数据库存储类型
     * DB_TYPE_BROWER 代表浏览记录
     * DB_TYPE_COLLECT_LOCAL 未登录本地收藏
     * DB_TYPE_COLLECT_NETTOLOCAL 登录之后本地收藏
     */
    private int dataType;

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public String getId() {
        return uid;
    }

    public void setId(String id) {
        this.uid = id;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
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

    public String getNum_comment() {
        return num_comment;
    }

    public void setNum_comment(String num_comment) {
        this.num_comment = num_comment;
    }

    public String getNum_good() {
        return num_good;
    }

    public void setNum_good(String num_good) {
        this.num_good = num_good;
    }

    public String getNum_play() {
        return num_play;
    }

    public void setNum_play(String num_play) {
        this.num_play = num_play;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uid);
        dest.writeString(this.category_id);
        dest.writeString(this.title);
        dest.writeString(this.picture);
        dest.writeString(this.num_comment);
        dest.writeString(this.num_good);
        dest.writeString(this.num_play);
        dest.writeString(this.create_time);
        dest.writeInt(this.dataType);
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public VideoListJson() {
    }

    protected VideoListJson(Parcel in) {
        this.uid = in.readString();
        this.category_id = in.readString();
        this.title = in.readString();
        this.picture = in.readString();
        this.num_comment = in.readString();
        this.num_good = in.readString();
        this.num_play = in.readString();
        this.create_time = in.readString();
        this.dataType = in.readInt();
    }

    @Generated(hash = 1293184428)
    public VideoListJson(String uid, String category_id, String title, String picture,
            String num_comment, String num_good, String num_play, String create_time,
            int dataType) {
        this.uid = uid;
        this.category_id = category_id;
        this.title = title;
        this.picture = picture;
        this.num_comment = num_comment;
        this.num_good = num_good;
        this.num_play = num_play;
        this.create_time = create_time;
        this.dataType = dataType;
    }

    public static final Creator<VideoListJson> CREATOR = new Creator<VideoListJson>() {
        @Override
        public VideoListJson createFromParcel(Parcel source) {
            return new VideoListJson(source);
        }

        @Override
        public VideoListJson[] newArray(int size) {
            return new VideoListJson[size];
        }
    };
}
