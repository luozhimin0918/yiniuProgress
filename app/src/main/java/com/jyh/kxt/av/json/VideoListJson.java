package com.jyh.kxt.av.json;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Transient;

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
    private String num_favor;
    private String create_time;

    @Transient
    private String share_image;
    @Transient
    private String share_sina_title;
    @Transient
    private String introduce;

    private boolean isCollect;
    private boolean isGood;

    private boolean isSel;//是否被选中

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

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isCollect() {
        return isCollect;
    }

    public void setCollect(boolean collect) {
        isCollect = collect;
    }

    public boolean isGood() {
        return isGood;
    }

    public void setGood(boolean good) {
        isGood = good;
    }

    public boolean isSel() {
        return isSel;
    }

    public void setSel(boolean sel) {
        isSel = sel;
    }


    public boolean getIsCollect() {
        return this.isCollect;
    }

    public void setIsCollect(boolean isCollect) {
        this.isCollect = isCollect;
    }

    public boolean getIsGood() {
        return this.isGood;
    }

    public void setIsGood(boolean isGood) {
        this.isGood = isGood;
    }

    public String getNum_favor() {
        return num_favor;
    }

    public void setNum_favor(String num_favor) {
        this.num_favor = num_favor;
    }


    public String getShare_image() {
        return share_image;
    }

    public void setShare_image(String share_image) {
        this.share_image = share_image;
    }

    public String getShare_sina_title() {
        if (share_sina_title == null) {

        }
        return share_sina_title;
    }

    public void setShare_sina_title(String share_sina_title) {
        this.share_sina_title = share_sina_title;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
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
        dest.writeString(this.num_favor);
        dest.writeString(this.create_time);
        dest.writeByte(this.isCollect ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isGood ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isSel ? (byte) 1 : (byte) 0);
        dest.writeInt(this.dataType);
    }

    public boolean getIsSel() {
        return this.isSel;
    }

    public void setIsSel(boolean isSel) {
        this.isSel = isSel;
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
        this.num_favor = in.readString();
        this.create_time = in.readString();
        this.isCollect = in.readByte() != 0;
        this.isGood = in.readByte() != 0;
        this.isSel = in.readByte() != 0;
        this.dataType = in.readInt();
    }

    @Generated(hash = 1710588985)
    public VideoListJson(String uid, String category_id, String title, String picture,
                         String num_comment, String num_good, String num_play, String num_favor, String create_time,
                         boolean isCollect, boolean isGood, boolean isSel, int dataType) {
        this.uid = uid;
        this.category_id = category_id;
        this.title = title;
        this.picture = picture;
        this.num_comment = num_comment;
        this.num_good = num_good;
        this.num_play = num_play;
        this.num_favor = num_favor;
        this.create_time = create_time;
        this.isCollect = isCollect;
        this.isGood = isGood;
        this.isSel = isSel;
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


    //新的广告对象
    @Transient
    private String type; //如果是ad类型

    @Transient
    private String href;

    @Transient
    private String o_action;

    @Transient
    private String o_class;

    @Transient
    private String o_id;

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
}
