package com.jyh.kxt.score.json;

/**
 * 项目名:KxtProfessional
 * 类描述:积分明细 - 本月明细
 * 创建人:苟蒙蒙
 * 创建日期:2017/9/5.
 */

public class ScoreDetailDayJson {
    private String task_title;    //标题 签到...
    private String task_time;    //任务时间
    private String task_award;    //任务奖励积分

    public ScoreDetailDayJson() {
    }

    public ScoreDetailDayJson(String task_title, String task_time, String task_award) {

        this.task_title = task_title;
        this.task_time = task_time;
        this.task_award = task_award;
    }

    public String getTask_title() {

        return task_title;
    }

    public void setTask_title(String task_title) {
        this.task_title = task_title;
    }

    public String getTask_time() {
        return task_time;
    }

    public void setTask_time(String task_time) {
        this.task_time = task_time;
    }

    public String getTask_award() {
        return task_award;
    }

    public void setTask_award(String task_award) {
        this.task_award = task_award;
    }
}
