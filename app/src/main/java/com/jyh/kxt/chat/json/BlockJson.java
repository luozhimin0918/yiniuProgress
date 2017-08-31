package com.jyh.kxt.chat.json;

/**
 * Created by Mr'Dai on 2017/8/30.
 */

public class BlockJson {
    private String receiver; //消息接收者
    private String nickname;//昵称
    private String avatar;
    private boolean isBan = true;

    public BlockJson() {
    }

    public BlockJson(String receiver, String nickname, String avatar) {

        this.receiver = receiver;
        this.nickname = nickname;
        this.avatar = avatar;
    }

    public boolean isBan() {
        return isBan;
    }

    public void setBan(boolean ban) {
        isBan = ban;
    }

    public String getReceiver() {

        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
