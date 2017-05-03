package com.jyh.kxt.user.json;

/**
 * 项目名:Kxt
 * 类描述:用户信息
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/27.
 */

public class UserJson {
    //{"accessToken":"3ab5a1f1025af3b996031360c28388eb",
    // "nickname":"Win-Win",
    // "picture":"http://q.qlogo.cn/qqapp/1101487761/34C8BB98F38FA67C3BEC0F1BA1C7FBCB/100",
    // "uid":55887}

    private String accessToken;
    private String nickname;
    private String picture;
    private String uid;

    public UserJson() {
    }

    public UserJson(String accessToken, String nickname, String picture, String uid) {

        this.accessToken = accessToken;
        this.nickname = nickname;
        this.picture = picture;
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "{" +
                "\"accessToken\":\"" + accessToken +
                "\",\"nickname\":\"" + nickname +
                "\",\"picture\":\"" + picture +
                "\",\"uid\":\"" + uid + "\"}";
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
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
}
