package com.jyh.kxt.chat.json;

/**
 * 项目名:KxtProfessional
 * 类描述:系统消息
 * 创建人:苟蒙蒙
 * 创建日期:2017/8/29.
 */

public class LetterSysJson {

    private String content;
    private String datetime;

    public LetterSysJson() {
    }

    public LetterSysJson(String content, String datetime) {

        this.content = content;
        this.datetime = datetime;
    }

    public String getContent() {

        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}
