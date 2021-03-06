package com.jyh.kxt.chat.json;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Generated;

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

@Entity(nameInDb = "CHAT_ROOM_BEAN")
public class ChatRoomJson {
    /**
     * 这里视图类型分为两种   0 对方视图  1 我的视图
     */
    @Transient
    private int viewType;

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }


    /**
     * 分割时间,如果超过五分钟则有数据
     */
    @Transient
    private long partitionTime;

    public long getPartitionTime() {
        return partitionTime;
    }

    public void setPartitionTime(long partitionTime) {
        this.partitionTime = partitionTime;
    }

    /**
     * 我是否屏蔽了对方
     */
    @Transient
    private int is_banned_for_receiver;

    public int getIs_banned_for_receiver() {
        return is_banned_for_receiver;
    }

    public void setIs_banned_for_receiver(int is_banned_for_receiver) {
        this.is_banned_for_receiver = is_banned_for_receiver;
    }

    /**
     * 对方是否屏蔽了我?
     */
    @Transient
    private int is_banned_for_sender;

    public int getIs_banned_for_sender() {
        return is_banned_for_sender;
    }

    public void setIs_banned_for_sender(int is_banned_for_sender) {
        this.is_banned_for_sender = is_banned_for_sender;
    }

    /**
     * 消息发送状态 0 无状态  1 发送中  2 发送失败  （结合ID来使用,如果ID为null） 3 因为屏蔽导致的失败
     */
    public int msgSendStatus = 0;

    public int getMsgSendStatus() {
        return msgSendStatus;
    }

    public void setMsgSendStatus(int msgSendStatus) {
        this.msgSendStatus = msgSendStatus;
    }

    /**
     * 来得到本地保存失败的消息位置
     */
    private String foregoingChatId;

    public String getForegoingChatId() {
        return foregoingChatId;
    }

    public void setForegoingChatId(String foregoingChatId) {
        this.foregoingChatId = foregoingChatId;
    }

    private String id;
    private String sender;
    private String receiver;
    private String content;
    private String nickname;
    private String avatar;
    private String datetime;

    @Generated(hash = 1394291781)
    public ChatRoomJson() {
    }

    @Generated(hash = 1077499007)
    public ChatRoomJson(int msgSendStatus, String foregoingChatId, String id, String sender,
                        String receiver, String content, String nickname, String avatar,
                        String datetime) {
        this.msgSendStatus = msgSendStatus;
        this.foregoingChatId = foregoingChatId;
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.nickname = nickname;
        this.avatar = avatar;
        this.datetime = datetime;
    }

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
