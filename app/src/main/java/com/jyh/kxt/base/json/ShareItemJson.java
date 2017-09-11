package com.jyh.kxt.base.json;

/**
 * Created by Mr'Dai on 2017/9/8.
 */

public class ShareItemJson {
    /**
     * 如果这个值存在  则表示默认操作  例如复制连接  关闭PopupWindow
     */
    public int tagId;

    public int icon;
    public String title;
    public boolean isSelectedView = false;

    public ShareItemJson(int icon, String title) {
        this.icon = icon;
        this.title = title;
    }


    public ShareItemJson(int icon, String title, boolean isSelectedView) {
        this.icon = icon;
        this.title = title;
        this.isSelectedView = isSelectedView;
    }

    public ShareItemJson(int tagId, int icon, String title) {
        this.tagId = tagId;
        this.icon = icon;
        this.title = title;
    }
}
