package com.jyh.kxt.index.json;

/**
 * 项目名:Kxt
 * 类描述:首页
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/17.
 */

public class TypeDataJson {
    private String type;
    private Object data;
    private String id;
    private String title;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
