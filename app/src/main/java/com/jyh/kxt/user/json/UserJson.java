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
    private String email;//已绑定的邮箱，未绑定时无该参数或者值为空
    private String address;
    private String work;
    private String birthday;

    private String writer_id;
    private String writer_name;
    private String writer_avatar;

    private int is_unread_msg;

    private String login_type;//qq/sina/wx/password/message//登录方式
    private Boolean is_set_password;//是否设置密码
    private Boolean is_set_phone;//是否绑定手机
    private Boolean is_set_email;//是否绑定邮箱
    private String phone;   //已绑定的手机号，未绑定时无该参数或者值为空

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

    public String getWriter_avatar() {
        return writer_avatar;
    }

    public void setWriter_avatar(String writer_avatar) {
        this.writer_avatar = writer_avatar;
    }

    public int getIs_unread_msg() {
        return is_unread_msg;
    }

    public void setIs_unread_msg(int is_unread_msg) {
        this.is_unread_msg = is_unread_msg;
    }

    public String getLogin_type() {
        return login_type;
    }

    public void setLogin_type(String login_type) {
        this.login_type = login_type;
    }

    public Boolean getIs_set_password() {
        return is_set_password==null?false:is_set_password;
    }

    public void setIs_set_password(Boolean is_set_password) {
        this.is_set_password = is_set_password;
    }

    public Boolean getIs_set_phone() {
        return is_set_phone==null?false:is_set_phone;
    }

    public void setIs_set_phone(Boolean is_set_phone) {
        this.is_set_phone = is_set_phone;
    }

    public Boolean getIs_set_email() {
        return is_set_email==null?false:is_set_email;
    }

    public void setIs_set_email(Boolean is_set_email) {
        this.is_set_email = is_set_email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
