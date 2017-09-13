package com.jyh.kxt.base.json;

/**
 * Created by Mr'Dai on 2017/9/8.
 */

public class UmengShareBean {
    /**
     * 跳转到分享的来源
     */
    private int fromSource;

    private String title;
    private String detail;
    private String webUrl;
    private String imageUrl;

    //分享微博需要的
    private String sinaTitle;

    public int getFromSource() {
        return fromSource;
    }

    public void setFromSource(int fromSource) {
        this.fromSource = fromSource;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSinaTitle() {
        return sinaTitle;
    }

    public void setSinaTitle(String sinaTitle) {
        this.sinaTitle = sinaTitle;
    }
}
