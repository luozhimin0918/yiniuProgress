package com.jyh.kxt.chat.json;

/**
 * Created by Mr'Dai on 2017/8/30.
 */

public class ChatRoomJson {
    /**
     * 这里视图类型分为两种   0 对方视图  1 我的视图
     */
    private int viewType;

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }
}
