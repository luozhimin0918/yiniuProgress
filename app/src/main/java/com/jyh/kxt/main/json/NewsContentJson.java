package com.jyh.kxt.main.json;

import java.util.List;

/**
 * 项目名:Kxt
 * 类描述:要闻详情
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/2.
 */

public class NewsContentJson {
    private String id;
    private String title;
    private String picture;
    private String content;
    private String create_time;
    private String num_good;        //点赞数
    private String num_comment;     //评论数
    private String url_share;       //分享地址
    private String author_id;       //作者
    private String author_name;
    private String author_image;
    private String author_profile;
    private String is_good;         // 1:Y | 0:N 点赞状态
    private String is_favor;        // 1:Y | 0:N 收藏状态
    private String source;          //文章来源
//    private String comment:[],    //评论列表  格式和评论接口单条格式一致
    private List<NewsJson> article; //相关文章 格式和list格式一致

    public NewsContentJson() {
    }

    public NewsContentJson(String id, String title, String picture, String content, String create_time, String num_good, String
            num_comment, String url_share, String author_id, String author_name, String author_image, String author_profile, String
            is_good, String is_favor, String source, List<NewsJson> article) {

        this.id = id;
        this.title = title;
        this.picture = picture;
        this.content = content;
        this.create_time = create_time;
        this.num_good = num_good;
        this.num_comment = num_comment;
        this.url_share = url_share;
        this.author_id = author_id;
        this.author_name = author_name;
        this.author_image = author_image;
        this.author_profile = author_profile;
        this.is_good = is_good;
        this.is_favor = is_favor;
        this.source = source;
        this.article = article;
    }

    public String getId() {

        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getNum_good() {
        return num_good;
    }

    public void setNum_good(String num_good) {
        this.num_good = num_good;
    }

    public String getNum_comment() {
        return num_comment;
    }

    public void setNum_comment(String num_comment) {
        this.num_comment = num_comment;
    }

    public String getUrl_share() {
        return url_share;
    }

    public void setUrl_share(String url_share) {
        this.url_share = url_share;
    }

    public String getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(String author_id) {
        this.author_id = author_id;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public String getAuthor_image() {
        return author_image;
    }

    public void setAuthor_image(String author_image) {
        this.author_image = author_image;
    }

    public String getAuthor_profile() {
        return author_profile;
    }

    public void setAuthor_profile(String author_profile) {
        this.author_profile = author_profile;
    }

    public String getIs_good() {
        return is_good;
    }

    public void setIs_good(String is_good) {
        this.is_good = is_good;
    }

    public String getIs_favor() {
        return is_favor;
    }

    public void setIs_favor(String is_favor) {
        this.is_favor = is_favor;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<NewsJson> getArticle() {
        return article;
    }

    public void setArticle(List<NewsJson> article) {
        this.article = article;
    }
}
