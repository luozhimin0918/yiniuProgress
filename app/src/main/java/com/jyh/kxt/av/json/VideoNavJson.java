package com.jyh.kxt.av.json;

/**
 * 项目名:Kxt
 * 类描述:视听导航
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/17.
 */

public class VideoNavJson {
    private String id;
    private String name;

    @Override
    public String toString() {
        return "VideoNavJson{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
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
}
