package com.jyh.kxt.search.json;

import com.jyh.kxt.trading.json.ViewPointTradeBean;

import java.util.List;

/**
 * 项目名:KxtProfessional
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/8/8.
 */

public class ViewpointJson {
    private String type;
    private List<ViewPointTradeBean> data;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<ViewPointTradeBean> getData() {
        return data;
    }

    public void setData(List<ViewPointTradeBean> data) {
        this.data = data;
    }
}
