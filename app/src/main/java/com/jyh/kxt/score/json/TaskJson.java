package com.jyh.kxt.score.json;

/**
 * 项目名:KxtProfessional
 * 类描述:任务
 * 创建人:苟蒙蒙
 * 创建日期:2017/9/5.
 */

public class TaskJson {

    private String description;//任务描述
    private String award;   //奖励
    private String num_finished;   //成功数量
    private String total_tasks;   //任务总数量
    private String o_class;//跳转到某个Intent
    private String o_id;
    private String o_action;
    private String is_finished;
    private String type;//0 每日任务 1 新手任务
    private String title;

    public TaskJson() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIs_finished() {
        return is_finished;
    }

    public void setIs_finished(String is_finished) {
        this.is_finished = is_finished;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAward() {
        return award;
    }

    public void setAward(String award) {
        this.award = award;
    }

    public String getNum_finished() {
        return num_finished;
    }

    public void setNum_finished(String num_finished) {
        this.num_finished = num_finished;
    }

    public String getTotal_tasks() {
        return total_tasks;
    }

    public void setTotal_tasks(String total_tasks) {
        this.total_tasks = total_tasks;
    }

    public String getO_class() {
        return o_class;
    }

    public void setO_class(String o_class) {
        this.o_class = o_class;
    }

    public String getO_id() {
        return o_id;
    }

    public void setO_id(String o_id) {
        this.o_id = o_id;
    }

    public String getO_action() {
        return o_action;
    }

    public void setO_action(String o_action) {
        this.o_action = o_action;
    }

    public TaskJson(String description, String award, String num_finished, String total_tasks, String o_class, String o_id, String
            o_action, String is_finished, String type, String title) {
        this.description = description;
        this.award = award;
        this.num_finished = num_finished;
        this.total_tasks = total_tasks;
        this.o_class = o_class;
        this.o_id = o_id;
        this.o_action = o_action;
        this.is_finished = is_finished;
        this.type = type;
        this.title = title;
    }
}
