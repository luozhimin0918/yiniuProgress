package com.jyh.kxt.chat.json;

import java.util.List;

/**
 * 项目名:KxtProfessional
 * 类描述:私信列表
 * 创建人:苟蒙蒙
 * 创建日期:2017/8/31.
 */

public class LetterJson {

    private List<LetterListJson> list;//普通消息
    private String show_red_dot;//系统消息

    public LetterJson() {
    }

    public LetterJson(List<LetterListJson> list, String show_red_dot) {

        this.list = list;
        this.show_red_dot = show_red_dot;
    }

    public List<LetterListJson> getList() {

        return list;
    }

    public void setList(List<LetterListJson> list) {
        this.list = list;
    }

    public String getShow_red_dot() {
        return show_red_dot;
    }

    public void setShow_red_dot(String show_red_dot) {
        this.show_red_dot = show_red_dot;
    }
}
