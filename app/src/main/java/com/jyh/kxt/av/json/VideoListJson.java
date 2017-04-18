package com.jyh.kxt.av.json;

/**
 * 项目名:Kxt
 * 类描述:视听列表
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/18.
 */

public class VideoListJson{
    private String id;
    private String category_id;
    private String title;
    private String picture;
    private String num_comment;
    private String num_good;
    private String num_play;
    private String create_time;

    public VideoListJson(String id, String category_id, String title, String picture, String num_comment, String num_good, String
            num_play, String create_time) {
        this.id = id;
        this.category_id = category_id;
        this.title = title;
        this.picture = picture;
        this.num_comment = num_comment;
        this.num_good = num_good;
        this.num_play = num_play;
        this.create_time = create_time;
    }

    public VideoListJson() {

    }

    @Override
    public String toString() {
        return "VideoListJson{" +
                "id='" + id + '\'' +
                ", category_id='" + category_id + '\'' +
                ", title='" + title + '\'' +
                ", picture='" + picture + '\'' +
                ", num_comment='" + num_comment + '\'' +
                ", num_good='" + num_good + '\'' +
                ", num_play='" + num_play + '\'' +
                ", create_time='" + create_time + '\'' +
                '}';
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

    public String getNum_comment() {
        return num_comment;
    }

    public void setNum_comment(String num_comment) {
        this.num_comment = num_comment;
    }

    public String getNum_good() {
        return num_good;
    }

    public void setNum_good(String num_good) {
        this.num_good = num_good;
    }

    public String getNum_play() {
        return num_play;
    }

    public void setNum_play(String num_play) {
        this.num_play = num_play;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }
}
