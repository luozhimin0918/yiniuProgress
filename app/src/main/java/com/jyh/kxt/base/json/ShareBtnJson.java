package com.jyh.kxt.base.json;

/**
 * 项目名:Kxt
 * 类描述:分享按钮
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/2.
 */

public class ShareBtnJson {

    private int id;
    private String text;

    public ShareBtnJson() {
    }

    public ShareBtnJson(int id, String text) {

        this.id = id;
        this.text = text;
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
