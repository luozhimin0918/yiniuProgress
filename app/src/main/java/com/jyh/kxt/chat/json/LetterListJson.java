package com.jyh.kxt.chat.json;

/**
 * 项目名:KxtProfessional
 * 类描述:私信列表bean
 * 创建人:苟蒙蒙
 * 创建日期:2017/8/28.
 */

public class LetterListJson {
    private String sender;   //uid消息发送者
    private String receiver;   //消息接收者
    private String last_content;   //最后一条消息
    private String num_unread;  //未读数量
    private String is_banned;   //是否屏蔽
    private String nickname;  //昵称
    private String avatar;   //头像
    private String datetime;

    public LetterListJson() {
    }

    public LetterListJson(String sender, String receiver, String last_content, String num_unread, String is_banned, String nickname,
                          String avatar, String datetime) {

        this.sender = sender;
        this.receiver = receiver;
        this.last_content = last_content;
        this.num_unread = num_unread;
        this.is_banned = is_banned;
        this.nickname = nickname;
        this.avatar = avatar;
        this.datetime = datetime;
    }

    public String getSender() {

        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getLast_content() {
        return last_content;
    }

    public void setLast_content(String last_content) {
        this.last_content = last_content;
    }

    public String getNum_unread() {
        return num_unread;
    }

    public void setNum_unread(String num_unread) {
        this.num_unread = num_unread;
    }

    public String getIs_banned() {
        return is_banned;
    }

    public void setIs_banned(String is_banned) {
        this.is_banned = is_banned;
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

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}
