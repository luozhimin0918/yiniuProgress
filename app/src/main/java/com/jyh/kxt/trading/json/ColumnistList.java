package com.jyh.kxt.trading.json;

import java.util.List;

/**
 * 项目名:KxtProfessional
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/8/4.
 */

public class ColumnistList {
    private String current_page;
    private List<ColumnistListJson> data;

    public String getCurrent_page() {
        return current_page;
    }

    public void setCurrent_page(String current_page) {
        this.current_page = current_page;
    }

    public List<ColumnistListJson> getData() {
        return data;
    }

    public void setData(List<ColumnistListJson> data) {
        this.data = data;
    }
}
