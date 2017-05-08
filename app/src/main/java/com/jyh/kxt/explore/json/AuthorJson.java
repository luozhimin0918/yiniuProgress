package com.jyh.kxt.explore.json;

/**
 * 项目名:Kxt
 * 类描述:作者
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/5.
 */

public class AuthorJson {
    private String article_num;//文章数
    private String id;//作者id
    private String name;//作者名
    private String num_fans;//粉丝数
    private String picture;//作者头像

    @Override
    public String toString() {
        return "AuthorJson{" +
                "article_num='" + article_num + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", num_fans='" + num_fans + '\'' +
                ", picture='" + picture + '\'' +
                '}';
    }

    public AuthorJson() {
    }

    public AuthorJson(String article_num, String id, String name, String num_fans, String picture) {
        this.article_num = article_num;
        this.id = id;
        this.name = name;
        this.num_fans = num_fans;
        this.picture = picture;
    }

    public String getArticle_num() {

        return article_num;
    }

    public void setArticle_num(String article_num) {
        this.article_num = article_num;
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

    public String getNum_fans() {
        return num_fans;
    }

    public void setNum_fans(String num_fans) {
        this.num_fans = num_fans;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
