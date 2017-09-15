package com.jyh.kxt.score.json;

import java.util.List;

/**
 * 项目名:KxtProfessional
 * 类描述:我的金币
 * 创建人:苟蒙蒙
 * 创建日期:2017/9/5.
 */

public class MyCoinJson {

    private int sign_state;//签到状态
    private int task_state;//任务完成状态
    private PunchCardJson punch_card;//签到
    private String num_coins;//我的金币数
    private List<TaskAllJson> task;//任务列表

    public MyCoinJson() {
    }

    public MyCoinJson(PunchCardJson punch_card, String num_coins, List<TaskAllJson> task) {

        this.punch_card = punch_card;
        this.num_coins = num_coins;
        this.task = task;
    }

    public PunchCardJson getPunch_card() {
        return punch_card;
    }

    public int getSign_state() {
        return sign_state;
    }

    public void setSign_state(int sign_state) {
        this.sign_state = sign_state;
    }

    public int getTask_state() {
        return task_state;
    }

    public void setTask_state(int task_state) {
        this.task_state = task_state;
    }

    public void setPunch_card(PunchCardJson punch_card) {
        this.punch_card = punch_card;
    }

    public String getNum_coins() {
        return num_coins;
    }

    public void setNum_coins(String num_coins) {
        this.num_coins = num_coins;
    }

    public List<TaskAllJson> getTask() {
        return task;
    }

    public void setTask(List<TaskAllJson> task) {
        this.task = task;
    }
}
