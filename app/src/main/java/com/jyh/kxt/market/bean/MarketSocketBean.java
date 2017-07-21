package com.jyh.kxt.market.bean;

/**
 * Created by Mr'Dai on 2017/7/21.
 */

public class MarketSocketBean {
    public String price, change, range, zuoshou, jinkai, zuigao, zuidi, lastTime;
    private String jsonData;

    public MarketSocketBean() {
        price  = change =range = zuoshou = jinkai = zuigao = zuidi = lastTime = "";
    }

    public MarketSocketBean(String price,
                            String change,
                            String range,
                            String zuoshou,
                            String jinkai,
                            String zuigao,
                            String zuidi,
                            String lastTime) {
        this.price = price;
        this.change = change;
        this.range = range;
        this.zuoshou = zuoshou;
        this.jinkai = jinkai;
        this.zuigao = zuigao;
        this.zuidi = zuidi;
        this.lastTime = lastTime;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    public String getJsonData() {
        return jsonData;
    }
}
