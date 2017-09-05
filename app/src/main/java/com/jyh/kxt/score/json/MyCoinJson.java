package com.jyh.kxt.score.json;

import java.util.List;

/**
 * 项目名:KxtProfessional
 * 类描述:我的金币
 * 创建人:苟蒙蒙
 * 创建日期:2017/9/5.
 */

public class MyCoinJson {

    private PunchCard punch_card;//签到
    private String my_award_num;//我的金币数
    private List<TaskAllJson> data;//任务列表

    public MyCoinJson() {
    }

    public MyCoinJson(PunchCard punch_card, String my_award_num, List<TaskAllJson> data) {
        this.punch_card = punch_card;
        this.my_award_num = my_award_num;
        this.data = data;
    }

    public PunchCard getPunch_card() {
        return punch_card;
    }

    public void setPunch_card(PunchCard punch_card) {
        this.punch_card = punch_card;
    }

    public String getMy_award_num() {
        return my_award_num;
    }

    public void setMy_award_num(String my_award_num) {
        this.my_award_num = my_award_num;
    }

    public List<TaskAllJson> getData() {
        return data;
    }

    public void setData(List<TaskAllJson> data) {
        this.data = data;
    }

}
