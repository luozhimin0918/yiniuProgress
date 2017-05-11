package com.jyh.kxt.main.json;

import com.jyh.kxt.av.json.CommentBean;

import java.util.List;

/**
 * 项目名:Kxt
 * 类描述:要闻详情
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/2.
 */

public class NewsContentJson {

//            "font_big":"<style> body p {font-size: 16px;}</style>",
//            "font_mid":"<style> body p {font-size: 14px;}</style>",
//            "font_small":"<style> body p {font-size: 12px;}</style>",
//            "night_style":"<head><style> body p {color:#575757!important;} body {background:rgba(0,0,0,.7);}</style></head>",

    private String id;
    private String category_id;
    private String type;//类型
    private String typeName;//类型名
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
    private List<CommentBean> comment;    //评论列表  格式和评论接口单条格式一致
    private List<NewsJson> article; //相关文章 格式和list格式一致
    private String is_follow;

    /**
     * 内容样式js
     */
    private String font_big;//最大字体
    private String font_mid;//中等字体
    private String font_small;//最小字体
    private String night_style;//夜间模式

    public NewsContentJson() {
    }

    public NewsContentJson(String id, String category_id, String type, String typeName, String title, String picture, String content,
                           String create_time, String num_good, String num_comment, String url_share, String author_id, String
                                   author_name, String author_image, String author_profile, String is_good, String is_favor, String
                                   source, List<CommentBean> comment, List<NewsJson> article, String is_follow, String font_big, String
                                   font_mid, String font_small, String night_style) {

        this.id = id;
        this.category_id = category_id;
        this.type = type;
        this.typeName = typeName;
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
        this.comment = comment;
        this.article = article;
        this.is_follow = is_follow;
        this.font_big = font_big;
        this.font_mid = font_mid;
        this.font_small = font_small;
        this.night_style = night_style;
    }

    public String getId() {

        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
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

    public List<CommentBean> getComment() {
        return comment;
    }

    public void setComment(List<CommentBean> comment) {
        this.comment = comment;
    }

    public List<NewsJson> getArticle() {
        return article;
    }

    public void setArticle(List<NewsJson> article) {
        this.article = article;
    }

    public String getIs_follow() {
        return is_follow;
    }

    public void setIs_follow(String is_follow) {
        this.is_follow = is_follow;
    }

    public String getFont_big() {
        return font_big;
    }

    public void setFont_big(String font_big) {
        this.font_big = font_big;
    }

    public String getFont_mid() {
        return font_mid;
    }

    public void setFont_mid(String font_mid) {
        this.font_mid = font_mid;
    }

    public String getFont_small() {
        return font_small;
    }

    public void setFont_small(String font_small) {
        this.font_small = font_small;
    }

    public String getNight_style() {
        return night_style;
    }

    public void setNight_style(String night_style) {
        this.night_style = night_style;
    }
}
