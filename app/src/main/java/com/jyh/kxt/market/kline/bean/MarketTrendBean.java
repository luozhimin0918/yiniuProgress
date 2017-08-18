package com.jyh.kxt.market.kline.bean;

/**
 * Created by Mr'Dai on 2017/7/13.
 */

/**
 * close : 268.75
 * high : 268.82
 * low : 268.75
 * open : 268.8
 * quotetime : 2017-07-13 15:25:00
 * start : 1499930700
 * volume : 92
 */
public class MarketTrendBean {
    private double close;
    private double high;
    private double low;
    private double open;
    private String quotetime;
    private int start;
//    private int volume;

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public String getQuotetime() {
        return quotetime;
    }

    public void setQuotetime(String quotetime) {
        this.quotetime = quotetime;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }
//    public int getVolume() {
//        return volume;
//    }
//
//    public void setVolume(int volume) {
//        this.volume = volume;
//    }
}
