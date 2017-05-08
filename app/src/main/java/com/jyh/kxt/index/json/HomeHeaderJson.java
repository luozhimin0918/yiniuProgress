package com.jyh.kxt.index.json;

/**
 * 项目名:Kxt
 * 类描述:首页
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/17.
 */

public class HomeHeaderJson {
    private String type;
    private Object data;

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

    @Override
    public String toString() {
        return "HomeHeaderJson{" +
                "type='" + type + '\'' +
                ", data=" + data +
                '}';
    }
}
