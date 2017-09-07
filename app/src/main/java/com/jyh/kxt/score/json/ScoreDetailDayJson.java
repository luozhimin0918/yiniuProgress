package com.jyh.kxt.score.json;

/**
 * 项目名:KxtProfessional
 * 类描述:积分明细 - 本月明细
 * 创建人:苟蒙蒙
 * 创建日期:2017/9/5.
 */

public class ScoreDetailDayJson {
    private String title;    //标题 签到...
    private String time;    //任务时间
    private String award;    //任务奖励积分

    public ScoreDetailDayJson() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAward() {
        return award;
    }

    public void setAward(String award) {
        this.award = award;
    }

    public ScoreDetailDayJson(String title, String time, String award) {

        this.title = title;
        this.time = time;
        this.award = award;
    }
}
