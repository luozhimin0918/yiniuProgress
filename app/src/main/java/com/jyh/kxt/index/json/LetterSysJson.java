package com.jyh.kxt.index.json;

/**
 * 项目名:KxtProfessional
 * 类描述:系统消息
 * 创建人:苟蒙蒙
 * 创建日期:2017/8/29.
 */

public class LetterSysJson {

    private String time;
    private String info;
    private String id;

    public LetterSysJson() {
    }

    public LetterSysJson(String time, String info) {

        this.time = time;
        this.info = info;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LetterSysJson(String time, String info, String id) {

        this.time = time;
        this.info = info;
        this.id = id;
    }

    public String getTime() {

        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
