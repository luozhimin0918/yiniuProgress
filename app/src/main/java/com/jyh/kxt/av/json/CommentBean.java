package com.jyh.kxt.av.json;

import java.util.List;

/**
 * Created by Mr'Dai on 2017/5/4.
 */

public class CommentBean {

//    {
//        "content": "讲的很好",
//            "create_time": 1495420009,
//            "id": 39603,
//            "is_good": false,
//            "member_id": 38717,
//            "member_nickname": "daoyao",
//            "member_picture": "http://img.kuaixun360.com/Public/Home/images/default_head_pic.jpg",
//            "num_good": 0,
//            "num_reply": 0,
//            "object_id": 541,
//            "object_title": "《指标秘籍》宣传片",
//            "parent_content": 0,
//            "parent_create_time": 0,
//            "parent_id": 0,
//            "parent_is_good": false,
//            "parent_member_id": 0,
//            "parent_member_nickname": 0,
//            "parent_member_picture": 0,
//            "parent_num_good": 0,
//            "parent_num_reply": 0,
//            "status": 0,
//            "type": "video"
//    }
    private String content;
    private long create_time;
    private int id;
    private boolean is_good;
    private int member_id;
    private String member_nickname;
    private String member_picture;
    private int num_good;
    private int num_reply;
    private String parent_content;
    private long parent_create_time;

    private int parent_id;
    private boolean parent_is_good;
    private int parent_member_id;
    private String parent_member_nickname;
    private String parent_member_picture;
    private int parent_num_good;
    private int parent_num_reply;
    private int status;
    private String type;

    private String object_id;
    private String object_title;

    //我的评论才有
    private List<CommentBean> reply;

    /**
     * 本地变量,判断是否需要在攒上+1，本地内存的
     */
    private boolean temporaryClickFavour = false;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean is_good() {
        return is_good;
    }

    public void setIs_good(boolean is_good) {
        this.is_good = is_good;
    }

    public int getMember_id() {
        return member_id;
    }

    public void setMember_id(int member_id) {
        this.member_id = member_id;
    }

    public String getMember_nickname() {
        return member_nickname;
    }

    public void setMember_nickname(String member_nickname) {
        this.member_nickname = member_nickname;
    }

    public String getMember_picture() {
        return member_picture;
    }

    public void setMember_picture(String member_picture) {
        this.member_picture = member_picture;
    }

    public int getNum_good() {
        return num_good;
    }

    public void setNum_good(int num_good) {
        this.num_good = num_good;
    }

    public int getNum_reply() {
        return num_reply;
    }

    public void setNum_reply(int num_reply) {
        this.num_reply = num_reply;
    }

    public String getParent_content() {
        return parent_content;
    }

    public void setParent_content(String parent_content) {
        this.parent_content = parent_content;
    }

    public long getParent_create_time() {
        return parent_create_time;
    }

    public void setParent_create_time(long parent_create_time) {
        this.parent_create_time = parent_create_time;
    }

    public int getParent_id() {
        return parent_id;
    }

    public void setParent_id(int parent_id) {
        this.parent_id = parent_id;
    }

    public boolean isParent_is_good() {
        return parent_is_good;
    }

    public void setParent_is_good(boolean parent_is_good) {
        this.parent_is_good = parent_is_good;
    }

    public int getParent_member_id() {
        return parent_member_id;
    }

    public void setParent_member_id(int parent_member_id) {
        this.parent_member_id = parent_member_id;
    }

    public String getParent_member_nickname() {
        return parent_member_nickname;
    }

    public void setParent_member_nickname(String parent_member_nickname) {
        this.parent_member_nickname = parent_member_nickname;
    }

    public String getParent_member_picture() {
        return parent_member_picture;
    }

    public void setParent_member_picture(String parent_member_picture) {
        this.parent_member_picture = parent_member_picture;
    }

    public int getParent_num_good() {
        return parent_num_good;
    }

    public void setParent_num_good(int parent_num_good) {
        this.parent_num_good = parent_num_good;
    }

    public int getParent_num_reply() {
        return parent_num_reply;
    }

    public void setParent_num_reply(int parent_num_reply) {
        this.parent_num_reply = parent_num_reply;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isTemporaryClickFavour() {
        return temporaryClickFavour;
    }

    public void setTemporaryClickFavour(boolean temporaryClickFavour) {
        this.temporaryClickFavour = temporaryClickFavour;
    }

    public String getObject_id() {
        return object_id;
    }

    public void setObject_id(String object_id) {
        this.object_id = object_id;
    }

    public String getObject_title() {
        return object_title;
    }

    public void setObject_title(String object_title) {
        this.object_title = object_title;
    }

    public List<CommentBean> getReply() {
        return reply;
    }

    public void setReply(List<CommentBean> reply) {
        this.reply = reply;
    }
}
