package com.jyh.kxt.user.json;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 项目名:Kxt
 * 类描述:用户信息
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/27.
 */

public class UserJson {
//    uid:1,
//    nickname:"",
//    picture:"",
//    token:""   // accessToken
//    sex:1 ,   //性别：0：保密 1：男 2：女
//    email:"",
//    address:"", //province-city
//    work:"",
//    birthday:""

    @JSONField(name = "accessToken")
    private String token;
    private String nickname;
    private String picture;
    private String uid;
    private int sex;
    private String email;
    private String address;
    private String work;
    private String birthday;

    private String writer_id;
    private String writer_name;

    public UserJson() {
    }

    public UserJson(String token, String nickname, String picture, String uid, int sex, String email, String address, String work, String
            birthday, String writer_id, String writer_name) {
        this.token = token;
        this.nickname = nickname;
        this.picture = picture;
        this.uid = uid;
        this.sex = sex;
        this.email = email;
        this.address = address;
        this.work = work;
        this.birthday = birthday;
        this.writer_id = writer_id;
        this.writer_name = writer_name;
    }

    public String getWriter_id() {
        return writer_id;
    }

    public void setWriter_id(String writer_id) {
        this.writer_id = writer_id;
    }

    public String getWriter_name() {
        return writer_name;
    }

    public void setWriter_name(String writer_name) {
        this.writer_name = writer_name;
    }

    public String getToken() {

        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

}
