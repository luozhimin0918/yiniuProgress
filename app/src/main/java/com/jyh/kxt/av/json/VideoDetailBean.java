package com.jyh.kxt.av.json;

import com.jyh.kxt.main.json.SlideJson;

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
    private List<CommentBean> comment;
    private String create_time;
    private String id;
    private String num_comment;
    private String num_good;
    private String num_play;
    private String picture;
    private String title;
    private String url;
    private String url_share;
    private String share_image;
    private String introduce;
    private List<VideoDetailVideoBean> video;
    private String share_sina_title;

    private List<SlideJson> ads;

    public String getShare_image() {
        return share_image;
    }

    public void setShare_image(String share_image) {
        this.share_image = share_image;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public List<CommentBean> getComment() {
        return comment;
    }

    public void setComment(List<CommentBean> comment) {
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

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getUrl_share() {
        return url_share;
    }

    public void setUrl_share(String url_share) {
        this.url_share = url_share;
    }

    public String getShare_sina_title() {
        return share_sina_title;
    }

    public void setShare_sina_title(String share_sina_title) {
        this.share_sina_title = share_sina_title;
    }

    public List<SlideJson> getAds() {
        return ads;
    }

    public void setAds(List<SlideJson> ads) {
        this.ads = ads;
    }
}
