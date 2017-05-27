package com.jyh.kxt.explore.json;

import com.alibaba.fastjson.annotation.JSONField;
import com.jyh.kxt.main.json.NewsJson;

import java.util.List;

/**
 * 项目名:Kxt
 * 类描述:作者详情
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/8.
 */

public class AuthorDetailsJson {

//    [id] => 1000
//    [name] => 老铁说股
//    [picture] => http://img.kuaixun360.com/Member/37119/avatar/58fff6f044a43.jpg
//    [introduce] => 拦尽股市风云
//    [num_fans] => 27
//    [article_num] => 2

    private String id;//作者id
    private String name;//作者名
    private String picture;//作者头像
    private String introduce;//简介
    private String num_fans;//粉丝数
    private String article_num;//文章数
    private String is_follow;//是否关注

    @JSONField(name = "article")
    private List<AuthorNewsJson> list;

    public String getIs_follow() {
        return is_follow;
    }

    public void setIs_follow(String is_follow) {
        this.is_follow = is_follow;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getNum_fans() {
        return num_fans;
    }

    public void setNum_fans(String num_fans) {
        this.num_fans = num_fans;
    }

    public String getArticle_num() {
        return article_num;
    }

    public void setArticle_num(String article_num) {
        this.article_num = article_num;
    }

    public List<AuthorNewsJson> getList() {
        return list;
    }

    public void setList(List<AuthorNewsJson> list) {
        this.list = list;
    }
}
