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
    private String image;

    public Flash_KX(String title, String time, String importance, String autoid, String image) {
        this.title = title;
        this.time = time;
        this.importance = importance;
        this.autoid = autoid;
        this.image = image;
    }

    public Flash_KX() {
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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
