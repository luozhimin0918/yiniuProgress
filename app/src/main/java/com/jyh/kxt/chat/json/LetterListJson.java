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

    private String local_content;//本地消息，发送失败或者草稿数据

    private String num_unread = "0";  //未读数量
    private String is_banned;   //是否屏蔽
    private String nickname;  //昵称
    private String avatar;   //头像
    private String datetime;
    private String last_id;

    public String getLast_id() {
        return last_id;
    }

    public void setLast_id(String last_id) {
        this.last_id = last_id;
    }

    private int contentType; // 0 默认网络数据  1 发送失败的  2草稿数据

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

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public String getLocal_content() {
        return local_content;
    }

    public void setLocal_content(String local_content) {
        this.local_content = local_content;
    }
}
