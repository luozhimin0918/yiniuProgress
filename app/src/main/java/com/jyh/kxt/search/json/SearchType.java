package com.jyh.kxt.search.json;

/**
 * 项目名:KxtProfessional
 * 类描述:搜索类型
 * 创建人:苟蒙蒙
 * 创建日期:2017/8/7.
 */

public class SearchType {
    private String name;
    private String code;

    public SearchType() {
    }

    public SearchType(String name, String code) {

        this.name = name;
        this.code = code;
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
