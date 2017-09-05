package com.jyh.kxt.score.json;

import java.util.List;

/**
 * 项目名:KxtProfessional
 * 类描述:任务
 * 创建人:苟蒙蒙
 * 创建日期:2017/9/5.
 */

public class TaskAllJson {
    private String type;
    private String title;
    private List<TaskJson> data;

    public TaskAllJson() {
    }

    public TaskAllJson(String type, List<TaskJson> data) {

        this.type = type;
        this.data = data;
    }

    public TaskAllJson(String type, String title, List<TaskJson> data) {
        this.type = type;
        this.title = title;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<TaskJson> getData() {
        return data;
    }

    public void setData(List<TaskJson> data) {
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
