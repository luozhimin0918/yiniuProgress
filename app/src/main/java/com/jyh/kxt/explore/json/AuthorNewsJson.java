package com.jyh.kxt.explore.json;

/**
 * 项目名:Kxt
 * 类描述:作者文章
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/27.
 */

public class AuthorNewsJson {

    private String picture;
    private String title;
    private String create_time;
    private String name;
    private String type;
    private String href;
    private String o_class;
    private String o_action;
    private String o_id;

    public AuthorNewsJson() {
    }

    public AuthorNewsJson(String picture, String title, String create_time, String name, String type, String href, String o_class, String
            o_action, String o_id) {

        this.picture = picture;
        this.title = title;
        this.create_time = create_time;
        this.name = name;
        this.type = type;
        this.href = href;
        this.o_class = o_class;
        this.o_action = o_action;
        this.o_id = o_id;
    }

    public String getPicture() {

        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getO_class() {
        return o_class;
    }

    public void setO_class(String o_class) {
        this.o_class = o_class;
    }

    public String getO_action() {
        return o_action;
    }

    public void setO_action(String o_action) {
        this.o_action = o_action;
    }

    public String getO_id() {
        return o_id;
    }

    public void setO_id(String o_id) {
        this.o_id = o_id;
    }
}
