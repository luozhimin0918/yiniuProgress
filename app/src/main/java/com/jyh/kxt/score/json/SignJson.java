package com.jyh.kxt.score.json;

/**
 * 项目名:KxtProfessional
 * 类描述:签到
 * 创建人:苟蒙蒙
 * 创建日期:2017/9/5.
 */

public class SignJson {
    private String award;//奖励
    private String code;//签到的code值( 1 对应 首签   2 .....)
    private String description;//"首签"

    public SignJson() {
    }

    public String getAward() {
        return award;
    }

    public void setAward(String award) {
        this.award = award;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SignJson(String award, String code, String description) {

        this.award = award;
        this.code = code;
        this.description = description;
    }
}
