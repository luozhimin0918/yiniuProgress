package com.jyh.kxt.index.json;

import java.io.Serializable;

/**
 * 项目名:Kxt
 * 类描述:欢迎页加载广告
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/20.
 */

public class LoadADJson implements Serializable {

    private String href;
    private String o_action;
    private String o_class;
    private String o_id;
    private String picture;
    private String showTime;
    private String title;

    public LoadADJson() {
    }

    public LoadADJson(String href, String o_action, String o_class, String o_id, String picture, String showTime, String title) {

        this.href = href;
        this.o_action = o_action;
        this.o_class = o_class;
        this.o_id = o_id;
        this.picture = picture;
        this.showTime = showTime;
        this.title = title;
    }

    public String getHref() {

        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getO_action() {
        return o_action;
    }

    public void setO_action(String o_action) {
        this.o_action = o_action;
    }

    public String getO_class() {
        return o_class;
    }

    public void setO_class(String o_class) {
        this.o_class = o_class;
    }

    public String getO_id() {
        return o_id;
    }

    public void setO_id(String o_id) {
        this.o_id = o_id;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getShowTime() {
        return showTime;
    }

    public void setShowTime(String showTime) {
        this.showTime = showTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
