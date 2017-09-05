package com.jyh.kxt.score.json;

/**
 * 项目名:KxtProfessional
 * 类描述:积分明细 - 月度汇总
 * 创建人:苟蒙蒙
 * 创建日期:2017/9/5.
 */

public class ScoreDetailMonthJson {
   private String task_title;    //月份title
   private String task_award_in;    //积分收入
   private String task_award_out;    //积分支出

    public String getTask_title() {
        return task_title;
    }

    public void setTask_title(String task_title) {
        this.task_title = task_title;
    }

    public String getTask_award_in() {
        return task_award_in;
    }

    public void setTask_award_in(String task_award_in) {
        this.task_award_in = task_award_in;
    }

    public String getTask_award_out() {
        return task_award_out;
    }

    public void setTask_award_out(String task_award_out) {
        this.task_award_out = task_award_out;
    }

    public ScoreDetailMonthJson() {

    }

    public ScoreDetailMonthJson(String task_title, String task_award_in, String task_award_out) {

        this.task_title = task_title;
        this.task_award_in = task_award_in;
        this.task_award_out = task_award_out;
    }
}
