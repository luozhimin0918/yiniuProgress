package com.jyh.kxt.main.json.flash;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

import java.util.Comparator;

/**
 * 项目名:Kxt
 * 类描述:快讯
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/21.
 */

@Entity(nameInDb = "FLASH_BEAN")
public class FlashJson implements Parcelable {
    @JSONField(name = "id") private String uid;
    private String socre;//真正用到的id
    private String code;//类型 ["CJRL" 日历,"KUAIXUN" 快讯,"KXTNEWS" 要闻]
    private String content;
    private boolean isColloct;//是否收藏
    private boolean isShowMore;//是否显示更多
    private String time;

    private boolean isSel;

    public boolean isSel() {
        return isSel;
    }

    public void setSel(boolean sel) {
        isSel = sel;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    /**
     * 数据库存储类型
     * DB_TYPE_BROWER 代表浏览记录
     * DB_TYPE_COLLECT_LOCAL 未登录本地收藏
     * DB_TYPE_COLLECT_NETTOLOCAL 登录之后本地收藏
     */
    private int dataType;

    public boolean isShowMore() {
        return isShowMore;
    }

    public void setShowMore(boolean showMore) {
        isShowMore = showMore;
    }

    public boolean isColloct() {
        return isColloct;
    }

    public void setColloct(boolean colloct) {
        isColloct = colloct;
    }

    public FlashJson() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public String getSocre() {
        return socre;
    }

    public void setSocre(String socre) {
        this.socre = socre;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uid);
        dest.writeString(this.socre);
        dest.writeString(this.code);
        dest.writeString(this.content);
        dest.writeByte(this.isColloct ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isShowMore ? (byte) 1 : (byte) 0);
        dest.writeInt(this.dataType);
    }

    public boolean getIsColloct() {
        return this.isColloct;
    }

    public void setIsColloct(boolean isColloct) {
        this.isColloct = isColloct;
    }

    public boolean getIsShowMore() {
        return this.isShowMore;
    }

    public void setIsShowMore(boolean isShowMore) {
        this.isShowMore = isShowMore;
    }

    public boolean getIsSel() {
        return this.isSel;
    }

    public void setIsSel(boolean isSel) {
        this.isSel = isSel;
    }

    protected FlashJson(Parcel in) {
        this.uid = in.readString();
        this.socre = in.readString();
        this.code = in.readString();
        this.content = in.readString();
        this.isColloct = in.readByte() != 0;
        this.isShowMore = in.readByte() != 0;
        this.dataType = in.readInt();
    }

    @Generated(hash = 595772489)
    public FlashJson(String uid, String socre, String code, String content, boolean isColloct,
                     boolean isShowMore, String time, boolean isSel, int dataType) {
        this.uid = uid;
        this.socre = socre;
        this.code = code;
        this.content = content;
        this.isColloct = isColloct;
        this.isShowMore = isShowMore;
        this.time = time;
        this.isSel = isSel;
        this.dataType = dataType;
    }

    public static final Creator<FlashJson> CREATOR = new Creator<FlashJson>() {
        @Override
        public FlashJson createFromParcel(Parcel source) {
            return new FlashJson(source);
        }

        @Override
        public FlashJson[] newArray(int size) {
            return new FlashJson[size];
        }
    };
}
