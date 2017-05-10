package com.jyh.kxt.base.json;

/**
 * 项目名:Kxt
 * 类描述:跳转相关
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/26.
 */

public class JumpJson {
    public String o_action;
    public String o_class;
    public String o_id;

    public JumpJson() {
    }

    public JumpJson(String o_action, String o_class, String o_id) {

        this.o_action = o_action;
        this.o_class = o_class;
        this.o_id = o_id;
    }

    public String getO_action() {
        return o_action;
    }

    public void setO_action(String o_action) {
        this.o_action = o_action;
    }

    public String getO_class() {
        return o_class;
    }

    public void setO_class(String o_class) {
        this.o_class = o_class;
    }

    public String getO_id() {
        return o_id;
    }

    public void setO_id(String o_id) {
        this.o_id = o_id;
    }
}
