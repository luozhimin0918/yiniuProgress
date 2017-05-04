package com.jyh.kxt.market.bean;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.jyh.kxt.BR;

/**
 * Created by Mr'Dai on 2017/4/26.
 */

public class MarketItemBean extends BaseObservable {

    /**
     * change : +7.19
     * code : SHICOM
     * name : 上证指数
     * price : 3141.76
     * range : +0.23%
     */
    @Bindable
    private String change;
    @Bindable
    private String code;
    @Bindable
    private String name;
    @Bindable
    private String price;
    @Bindable
    private String range;

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
        notifyPropertyChanged(BR.change);
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
}
