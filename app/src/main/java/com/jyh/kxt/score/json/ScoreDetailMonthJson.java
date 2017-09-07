package com.jyh.kxt.score.json;

/**
 * 项目名:KxtProfessional
 * 类描述:积分明细 - 月度汇总
 * 创建人:苟蒙蒙
 * 创建日期:2017/9/5.
 */

public class ScoreDetailMonthJson {
    private String month;    //月份title
    private String income;    //积分收入
    private String expend;    //积分支出

    public ScoreDetailMonthJson() {

    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getIncome() {
        return income;
    }

    public void setIncome(String income) {
        this.income = income;
    }

    public String getExpend() {
        return expend;
    }

    public void setExpend(String expend) {
        this.expend = expend;
    }

    public ScoreDetailMonthJson(String month, String income, String expend) {
        this.month = month;
        this.income = income;
        this.expend = expend;
    }
}
