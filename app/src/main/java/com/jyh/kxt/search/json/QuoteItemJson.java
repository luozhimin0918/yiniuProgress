package com.jyh.kxt.search.json;

/**
 * 项目名:KxtProfessional
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/8/8.
 */

public class QuoteItemJson {

    /**
     * id : 188
     * name : 美燃油11
     * code : HONX
     */

    private String id;
    private String name;
    private String code;
    private String letter;
    private String pinyin;

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
