package com.jyh.kxt.main.json;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 项目名:Kxt
 * 类描述:行情
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/17.
 */

public class QuotesJson implements Parcelable{
    private String name;   //名称
    private String code;   //代码
    private String price;  //价格
    private String change; //涨跌
    private String range;  //涨跌幅

    private QuotesJson(Parcel source) {
        name=source.readString();
        code=source.readString();
        price=source.readString();
        change=source.readString();
        range=source.readString();
    }

    public QuotesJson(String name, String code, String price, String change, String range) {
        this.name = name;
        this.code = code;
        this.price = price;
        this.change = change;
        this.range = range;
    }

    public QuotesJson() {

    }

    @Override
    public String toString() {
        return "QuotesJson{" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", price='" + price + '\'' +
                ", change='" + change + '\'' +
                ", range='" + range + '\'' +
                '}';
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(code);
        dest.writeString(price);
        dest.writeString(change);
        dest.writeString(range);
    }

    public static final Parcelable.Creator<QuotesJson> CREATOR=new Creator<QuotesJson>() {
        @Override
        public QuotesJson createFromParcel(Parcel source) {
            return new QuotesJson(source);
        }

        @Override
        public QuotesJson[] newArray(int size) {
            return new QuotesJson[size];
        }
    };
}
