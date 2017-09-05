package com.jyh.kxt.chat.json;

/**
 * Created by Mr'Dai on 2017/9/4.
 */
public class ChatPreviewJson {
    private int type; //预览类型, 0 默认网络请求最后一条   1 发送失败内容   2 草稿内容

    private String receiver;
    private String content;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
