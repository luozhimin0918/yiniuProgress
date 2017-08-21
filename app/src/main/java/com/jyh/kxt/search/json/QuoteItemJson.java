package com.jyh.kxt.search.json;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 项目名:KxtProfessional
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/8/8.
 */

public class QuoteItemJson implements Parcelable {

    /**
     * id : 188
     * name : 美燃油11
     * code : HONX
     */

    private String id;
    private String name;
    private String code;
    private String letter;
    private String pinyin;

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.code);
        dest.writeString(this.letter);
        dest.writeString(this.pinyin);
    }

    public QuoteItemJson() {
    }

    public QuoteItemJson(String name, String code) {
        this.name = name;
        this.code = code;
    }

    protected QuoteItemJson(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.code = in.readString();
        this.letter = in.readString();
        this.pinyin = in.readString();
    }

    public static final Parcelable.Creator<QuoteItemJson> CREATOR = new Parcelable.Creator<QuoteItemJson>() {
        @Override
        public QuoteItemJson createFromParcel(Parcel source) {
            return new QuoteItemJson(source);
        }

        @Override
        public QuoteItemJson[] newArray(int size) {
            return new QuoteItemJson[size];
        }
    };
}
