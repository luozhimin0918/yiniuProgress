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
    @Bindable private String change = "--";
    @Bindable private String code;
    @Bindable private String name;
    @Bindable private String price = "--";
    @Bindable private String range = "--";
    //本地变
    @Bindable private String switchTarget;

    //行情来源,0 默认来源 1 第一次的数据 >1 之后数据更新
    @Bindable private int marketFromSource;

    //前一次价格
    private String aborPrice = "0";

    @Bindable private int bgItemColor;

    public void setChange(String change) {
        this.change = change;
        notifyPropertyChanged(BR.change);
    }

    public String getChange() {
        if (change == null) {
            change = "--";
        }
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
        if (name == null) {
            name = "";
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    public String getPrice() {
        if (price == null) {
            price = "--";
        }
        return price;
    }

    public void setPrice(String price) {
        setAborPrice(this.price);

        this.price = price;
        notifyPropertyChanged(BR.price);
    }

    public String getRange() {
        if (range == null) {
            range = "--";
        }
        return range;
    }

    public void setRange(String range) {
        this.range = range;
        notifyPropertyChanged(BR.range);
    }

    public String getSwitchTarget() {
        return switchTarget;
    }

    public void setSwitchTarget(String switchTarget) {
        this.switchTarget = switchTarget;
        notifyPropertyChanged(BR.switchTarget);
    }

    public int getMarketFromSource() {
        return marketFromSource;
    }

    public void setMarketFromSource(int marketFromSource) {
        this.marketFromSource = marketFromSource;
        notifyPropertyChanged(BR.marketFromSource);
    }

    public String getAborPrice() {
        if (aborPrice == null) {
            aborPrice = "0";
        }
        return aborPrice;
    }

    public void setAborPrice(String aborPrice) {
        this.aborPrice = aborPrice;
    }

    public int getBgItemColor() {
        return bgItemColor;
    }

    public void setBgItemColor(int bgItemColor) {
        this.bgItemColor = bgItemColor;
        notifyPropertyChanged(BR.bgItemColor);
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
        dest.writeString(this.switchTarget);
        dest.writeInt(this.marketFromSource);
    }

    public MarketItemBean() {
    }

    protected MarketItemBean(Parcel in) {
        this.change = in.readString();
        this.code = in.readString();
        this.name = in.readString();
        this.price = in.readString();
        this.range = in.readString();
        this.switchTarget = in.readString();
        this.marketFromSource = in.readInt();
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
