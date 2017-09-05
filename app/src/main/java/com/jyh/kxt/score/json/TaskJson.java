package com.jyh.kxt.score.json;

/**
 * 项目名:KxtProfessional
 * 类描述:任务
 * 创建人:苟蒙蒙
 * 创建日期:2017/9/5.
 */

public class TaskJson {
    private String task_describe;//任务描述
    private String task_award;   //奖励
    private String task_succ_num;   //成功数量
    private String task_sum_num;   //任务总数量
    private String o_class;//跳转到某个Intent
    private String o_id;
    private String o_action;

    public TaskJson() {
    }

    public TaskJson(String task_describe, String task_award, String task_succ_num, String task_sum_num, String o_class, String o_id,
                    String o_action) {

        this.task_describe = task_describe;
        this.task_award = task_award;
        this.task_succ_num = task_succ_num;
        this.task_sum_num = task_sum_num;
        this.o_class = o_class;
        this.o_id = o_id;
        this.o_action = o_action;
    }

    public String getTask_describe() {
        return task_describe;
    }

    public void setTask_describe(String task_describe) {
        this.task_describe = task_describe;
    }

    public String getTask_award() {
        return task_award;
    }

    public void setTask_award(String task_award) {
        this.task_award = task_award;
    }

    public String getTask_succ_num() {
        return task_succ_num;
    }

    public void setTask_succ_num(String task_succ_num) {
        this.task_succ_num = task_succ_num;
    }

    public String getTask_sum_num() {
        return task_sum_num;
    }

    public void setTask_sum_num(String task_sum_num) {
        this.task_sum_num = task_sum_num;
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
}
