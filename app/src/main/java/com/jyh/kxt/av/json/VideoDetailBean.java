package com.jyh.kxt.av.json;

import java.util.List;

/**
 * Created by Mr'Dai on 2017/5/4.
 * category_id	139
 * comment	Array
 * create_time	1492149420
 * id	549
 * num_comment	30
 * num_good	10
 * num_play	11466
 * picture	/Uploads/Picture/2017-04-14/58f066270c96d.jpg
 * title	美欧复活节小憩  外汇市场如何研判
 * url	http://media.kxt.com/video/cjgc/20170414.mp4
 * video	Array
 */

public class VideoDetailBean {
    private String category_id;
    private List<VideoDetailCommentBean> comment;
    private String create_time;
    private String id;
    private String num_comment;
    private String num_good;
    private String num_play;
    private String picture;
    private String title;
    private String url;
    private List<VideoDetailVideoBean> video;

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public List<VideoDetailCommentBean> getComment() {
        return comment;
    }

    public void setComment(List<VideoDetailCommentBean> comment) {
        this.comment = comment;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<VideoDetailVideoBean> getVideo() {
        return video;
    }

    public void setVideo(List<VideoDetailVideoBean> video) {
        this.video = video;
    }
}