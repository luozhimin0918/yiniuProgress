package com.jyh.kxt.market.bean;

/**
 * Created by Mr'Dai on 2017/4/26.
 */

public class MarketItemBean {

    /**
     * change : +7.19
     * code : SHICOM
     * name : 上证指数
     * price : 3141.76
     * range : +0.23%
     */

    private String change;
    private String code;
    private String name;
    private double price;
    private String range;

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }
}
