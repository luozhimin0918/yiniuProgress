package com.jyh.kxt.main.json;

/**
 * 项目名:Kxt
 * 类描述:要闻导航
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/17.
 */

public class NewsNavJson {
    private String name;  //名称
    private String code;  //代号  gushi|guijinshu|waihui|yuanyou|shendu

    @Override
    public String toString() {
        return "NewsNavJson{" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                '}';
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
