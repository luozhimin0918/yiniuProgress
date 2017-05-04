package com.jyh.kxt.main.json.flash;

import java.io.Serializable;

/**
 * 项目名:Kxt
 * 类描述:快讯
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/21.
 */

public class FlashJson implements Serializable {
    private String id;
    private String socre;//真正用到的id
    private String code;//类型 ["CJRL" 日历,"KUAIXUN" 快讯,"KXTNEWS" 要闻]
    private Object content;
    private boolean isColloct;//是否收藏
    private boolean isShowMore;//是否显示更多

    public boolean isShowMore() {
        return isShowMore;
    }

    public void setShowMore(boolean showMore) {
        isShowMore = showMore;
    }

    public boolean isColloct() {
        return isColloct;
    }

    public void setColloct(boolean colloct) {
        isColloct = colloct;
    }

    public FlashJson() {
    }

    public FlashJson(String id, String socre, String code, String content) {

        this.id = id;
        this.socre = socre;
        this.code = code;
        this.content = content;
    }

    public String getId() {

        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSocre() {
        return socre;
    }

    public void setSocre(String socre) {
        this.socre = socre;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }
}
