package com.jyh.kxt.trading.json;

import java.util.List;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/7/26.
 */

public class ViewpointJson {

    private String author;
    private String author_id;
    private String author_picture;
    private String time;
    private String content;
    private List<String> imgs;
    private String num_commend;
    private String num_like;

    public ViewpointJson() {
    }

    public ViewpointJson(String author, String author_id, String author_picture, String time, String content, List<String> imgs, String
            num_commend, String num_like) {

        this.author = author;
        this.author_id = author_id;
        this.author_picture = author_picture;
        this.time = time;
        this.content = content;
        this.imgs = imgs;
        this.num_commend = num_commend;
        this.num_like = num_like;
    }

    public String getAuthor() {

        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(String author_id) {
        this.author_id = author_id;
    }

    public String getAuthor_picture() {
        return author_picture;
    }

    public void setAuthor_picture(String author_picture) {
        this.author_picture = author_picture;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getImgs() {
        return imgs;
    }

    public void setImgs(List<String> imgs) {
        this.imgs = imgs;
    }

    public String getNum_commend() {
        return num_commend;
    }

    public void setNum_commend(String num_commend) {
        this.num_commend = num_commend;
    }

    public String getNum_like() {
        return num_like;
    }

    public void setNum_like(String num_like) {
        this.num_like = num_like;
    }
}
