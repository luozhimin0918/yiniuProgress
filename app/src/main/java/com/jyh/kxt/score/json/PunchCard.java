package com.jyh.kxt.score.json;

import java.util.List;

/**
 * 项目名:KxtProfessional
 * 类描述:签到
 * 创建人:苟蒙蒙
 * 创建日期:2017/9/5.
 */

public class PunchCard {
    private String punch_card_days; //连续打卡天数
    private List<SignJson> pubch_card_award; //打卡的奖励

    public PunchCard() {
    }

    public PunchCard(String punch_card_days, List<SignJson> pubch_card_award) {
        this.punch_card_days = punch_card_days;
        this.pubch_card_award = pubch_card_award;
    }

    public String getPunch_card_days() {
        return punch_card_days;
    }

    public void setPunch_card_days(String punch_card_days) {
        this.punch_card_days = punch_card_days;
    }

    public List<SignJson> getPubch_card_award() {
        return pubch_card_award;
    }

    public void setPubch_card_award(List<SignJson> pubch_card_award) {
        this.pubch_card_award = pubch_card_award;
    }
}
