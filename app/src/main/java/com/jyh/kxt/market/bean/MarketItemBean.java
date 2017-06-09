package com.jyh.kxt.market.bean;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;

import com.jyh.kxt.BR;


/**
 * Created by Mr'Dai on 2017/4/26.
 */

public class MarketItemBean extends BaseObservable implements Parcelable {

    /**
     * change : +7.19
     * code : SHICOM
     * name : 上证指数
     * price : 3141.76
     * range : +0.23%
     */
    @Bindable private String change;
    @Bindable private String code;
    @Bindable private String name;
    @Bindable private String price;
    @Bindable private String range;
    //本地变
    @Bindable private int bgGlint = 0;
    @Bindable private String switchTarget;
    private int fromSource = 0;//0来自网络 1来自本地
    @Bindable  private  int updateFontSize = 0;//0 默认文字大小  1 表示放大文字

    public void setChange(String change) {
        this.change = change;
        notifyPropertyChanged(BR.change);
    }

    public String getChange() {
        return change;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
        notifyPropertyChanged(BR.code);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
        notifyPropertyChanged(BR.price);
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
        notifyPropertyChanged(BR.range);
    }

    public int getBgGlint() {
        return bgGlint;
    }

    public void setBgGlint(int bgGlint) {
        this.bgGlint = bgGlint;
        notifyPropertyChanged(BR.bgGlint);
    }

    public String getSwitchTarget() {
        return switchTarget;
    }

    public void setSwitchTarget(String switchTarget) {
        this.switchTarget = switchTarget;
        notifyPropertyChanged(BR.switchTarget);
    }

    public int getFromSource() {
        return fromSource;
    }

    public void setFromSource(int fromSource) {
        this.fromSource = fromSource;
    }

    public int getUpdateFontSize() {
        return updateFontSize;
    }

    public void setUpdateFontSize(int updateFontSize) {
        this.updateFontSize = updateFontSize;
        notifyPropertyChanged(BR.updateFontSize);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.change);
        dest.writeString(this.code);
        dest.writeString(this.name);
        dest.writeString(this.price);
        dest.writeString(this.range);
        dest.writeInt(this.bgGlint);
        dest.writeString(this.switchTarget);
        dest.writeInt(this.fromSource);
        dest.writeInt(this.updateFontSize);
    }

    public MarketItemBean() {
    }

    protected MarketItemBean(Parcel in) {
        this.change = in.readString();
        this.code = in.readString();
        this.name = in.readString();
        this.price = in.readString();
        this.range = in.readString();
        this.bgGlint = in.readInt();
        this.switchTarget = in.readString();
        this.fromSource = in.readInt();
        this.updateFontSize = in.readInt();
    }

    public static final Creator<MarketItemBean> CREATOR = new Creator<MarketItemBean>() {
        @Override
        public MarketItemBean createFromParcel(Parcel source) {
            return new MarketItemBean(source);
        }

        @Override
        public MarketItemBean[] newArray(int size) {
            return new MarketItemBean[size];
        }
    };
}
