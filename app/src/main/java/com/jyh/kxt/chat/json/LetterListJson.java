package com.jyh.kxt.chat.json;

/**
 * 项目名:KxtProfessional
 * 类描述:私信列表bean
 * 创建人:苟蒙蒙
 * 创建日期:2017/8/28.
 */

public class LetterListJson {
    private String name;
    private boolean isShowDelBtn;

    public LetterListJson() {
    }

    public LetterListJson(String name, boolean isShowDelBtn) {

        this.name = name;
        this.isShowDelBtn = isShowDelBtn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isShowDelBtn() {
        return isShowDelBtn;
    }

    public void setShowDelBtn(boolean showDelBtn) {
        isShowDelBtn = showDelBtn;
    }
}
