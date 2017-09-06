package com.jyh.kxt.score.json;

import java.util.List;

/**
 * 项目名:KxtProfessional
 * 类描述:签到
 * 创建人:苟蒙蒙
 * 创建日期:2017/9/5.
 */

public class PunchCardJson {
    private int days; //连续打卡天数
    private List<SignJson> rules; //打卡的奖励

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public List<SignJson> getRules() {
        return rules;
    }

    public void setRules(List<SignJson> rules) {
        this.rules = rules;
    }

    public PunchCardJson() {

    }

    public PunchCardJson(int days, List<SignJson> rules) {

        this.days = days;
        this.rules = rules;
    }
}
