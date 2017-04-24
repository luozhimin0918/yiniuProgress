package com.jyh.kxt.datum.bean;

import java.util.List;

/**
 * Created by Mr'Dai on 2017/4/21.
 */

public class CalendarBean {
    private String type;
    private List<String> data;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }
}
