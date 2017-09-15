package com.jyh.kxt.index.json;

import com.jyh.kxt.base.json.JumpJson;
import com.library.base.http.VarConstant;

/**
 * Created by Mr'Dai on 2017/9/15.
 */

public class HomeAdJson extends JumpJson {
    private String href;
    private String picture;
    private int showTime;
    private String title;
    private String type;
    private String icon;
    private int left_screen_size;
    private int show;//0不主动显示   1 主动显示


    public String getHref() {
        String connector = "?";
        if (href.contains("?"))
            connector = "&";
        href = href + connector + VarConstant.HTTP_VERSION + "=" + VarConstant.HTTP_VERSION_VALUE
                + "&" + VarConstant.HTTP_SYSTEM + "=" + VarConstant.HTTP_SYSTEM_VALUE;
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public int getShowTime() {
        return showTime;
    }

    public void setShowTime(int showTime) {
        this.showTime = showTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getLeft_screen_size() {
        return left_screen_size;
    }

    public void setLeft_screen_size(int left_screen_size) {
        this.left_screen_size = left_screen_size;
    }

    public int getShow() {
        return show;
    }

    public void setShow(int show) {
        this.show = show;
    }
}
