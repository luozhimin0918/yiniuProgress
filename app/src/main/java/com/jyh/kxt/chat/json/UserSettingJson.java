package com.jyh.kxt.chat.json;

/**
 * 项目名:KxtProfessional
 * 类描述:信息设置
 * 创建人:苟蒙蒙
 * 创建日期:2017/9/1.
 */

public class UserSettingJson {
    private String receiver;  //消息接收者 ps:如果is_writer为1 通过此id跳转到作者页，否者不跳转
    private String is_writer;
    private String is_banned;
    private String nickname;    //昵称
    private String avatar;    //头像

    public UserSettingJson() {
    }

    public UserSettingJson(String receiver, String is_writer, String is_banned, String nickname, String avatar) {

        this.receiver = receiver;
        this.is_writer = is_writer;
        this.is_banned = is_banned;
        this.nickname = nickname;
        this.avatar = avatar;
    }

    public String getReceiver() {

        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getIs_writer() {
        return is_writer;
    }

    public void setIs_writer(String is_writer) {
        this.is_writer = is_writer;
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
}
