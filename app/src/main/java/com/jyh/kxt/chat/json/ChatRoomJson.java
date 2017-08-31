package com.jyh.kxt.chat.json;

/**
 * Created by Mr'Dai on 2017/8/30.
 */

//    id: 2;
//    sender: 59706;    //uid消息发送者
//    receiver: 69313   //消息接收者
//    content: '测试发消息1'    //消息
//    nickname: '思金融雅'    //sender的昵称
//    avatar: 'http://img.kxt.com/Member/69313/avatar/5982c05f52a72.jpg'  //sender的头像
//    datetime: 1502780986
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


    private String id;
    private String sender;
    private String receiver;
    private String content;
    private String nickname;
    private String avatar;
    private String datetime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
