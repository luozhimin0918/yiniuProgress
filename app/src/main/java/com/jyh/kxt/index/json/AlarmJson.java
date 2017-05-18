package com.jyh.kxt.index.json;

/**
 * Created by Mr'Dai on 2016/12/27.
 * 提醒的数据
 */
public class AlarmJson {
    private String code; //这个ID对应 code
    private String time; //这个时间存储的是 时分

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
