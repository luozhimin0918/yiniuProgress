package com.jyh.kxt.user.json;

import java.io.Serializable;
import java.util.List;

public class CityBean implements Serializable {
    /**
     * name : 城市
     * area : ["东城区","西城区","崇文区","昌平区"]
     */
    private String name;
    private List<String> area;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getArea() {
        return area;
    }

    public void setArea(List<String> area) {
        this.area = area;
    }

    public CityBean(String name, List<String> area) {
        this.name = name;
        this.area = area;
    }

    public CityBean() {
    }
}