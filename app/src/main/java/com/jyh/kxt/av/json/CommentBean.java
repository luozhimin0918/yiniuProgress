package com.jyh.kxt.av.json;

/**
 * Created by Mr'Dai on 2017/5/4.
 */

public class CommentBean {

    /**
     * content : 有呀
     * create_time : 1492257667
     * id : 37381
     * is_good : false
     * member_id : 54988
     * member_nickname : 越努力越幸运
     * member_picture : http://img.kuaixun360.com/Uploads/Picture/2017-04-09/58e90bdb50ea9.png
     * num_good : 0
     * num_reply : 0
     * parent_content : 怎么没有黄金，白银，原油的分析？
     * parent_create_time : 1492244524
     * parent_id : 37378
     * parent_is_good : false
     * parent_member_id : 21313
     * parent_member_nickname : 博大精深
     * parent_member_picture : http://img.kuaixun360.com/Uploads/Picture/2016-10-11/57fbec59b056f.png
     * parent_num_good : 0
     * parent_num_reply : 1
     * status : 1
     * type : 2
     */

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
}
