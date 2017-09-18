package com.jyh.kxt.index.json;

/**
 * 项目名:KxtProfessional
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/9/18.
 */

public class WebShareJson {
    private String href;
    private String title;
    private String description;
    private String share_pic;

    public WebShareJson() {
    }

    public WebShareJson(String href, String title, String description, String share_pic) {

        this.href = href;
        this.title = title;
        this.description = description;
        this.share_pic = share_pic;
    }

    public String getHref() {

        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShare_pic() {
        return share_pic;
    }

    public void setShare_pic(String share_pic) {
        this.share_pic = share_pic;
    }
}
