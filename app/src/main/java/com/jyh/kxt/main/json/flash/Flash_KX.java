package com.jyh.kxt.main.json.flash;

/**
 * 项目名:Kxt
 * 类描述:快讯-快讯
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/21.
 */

public class Flash_KX {
    private String title;
    private String time;
    private String importance;//重要性
    private String autoid;

    public Flash_KX() {
    }

    public Flash_KX(String title, String time, String importance, String autoid) {

        this.title = title;
        this.time = time;
        this.importance = importance;
        this.autoid = autoid;
    }

    @Override
    public String toString() {
        return "Flash_KX{" +
                "title='" + title + '\'' +
                ", time='" + time + '\'' +
                ", importance='" + importance + '\'' +
                ", autoid='" + autoid + '\'' +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImportance() {
        return importance;
    }

    public void setImportance(String importance) {
        this.importance = importance;
    }

    public String getAutoid() {
        return autoid;
    }

    public void setAutoid(String autoid) {
        this.autoid = autoid;
    }
}
