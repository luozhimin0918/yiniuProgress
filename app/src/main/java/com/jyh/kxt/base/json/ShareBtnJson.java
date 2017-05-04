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
    private boolean status;

    public ShareBtnJson(int id, String text, boolean status) {
        this.id = id;
        this.text = text;
        this.status = status;
    }

    public ShareBtnJson(int id, String text) {
        this.id = id;
        this.text = text;
        status = false;
    }

    public boolean isStatus() {

        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public ShareBtnJson() {
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
