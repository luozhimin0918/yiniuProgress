package com.jyh.kxt.explore.json;

/**
 * 项目名:Kxt
 * 类描述:活动
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/5.
 */

public class ActivityJson {
//    "remark":"美女空手套白狼，原来是参加了炒金大赛，速看她怎么赚钱>>",
//            "start_time":1490152200,
//            "status":"1",
//            "title":"同样年轻人，你用手机聊天，他却用手机炒金赚钱！",
//            "url":"http://img.kuaixun360.com/Uploads/Picture/2017-03-30/58dcbfa3512a1.jpg"

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String remark;
    private String start_time;
    private String status;
    private String title;
    private String url;


    public ActivityJson() {
    }

    public ActivityJson(String id, String remark, String start_time, String status, String title, String url) {
        this.id = id;
        this.remark = remark;
        this.start_time = start_time;
        this.status = status;
        this.title = title;
        this.url = url;
    }

    public String getRemark() {

        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
}
