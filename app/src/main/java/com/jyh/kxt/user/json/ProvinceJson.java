package com.jyh.kxt.user.json;


import com.jyh.kxt.base.widget.pickerview.model.IPickerViewData;

import java.io.Serializable;
import java.util.List;

/**
 * <json数据源>
 *
 * @author: 小嵩
 * @date: 2017/3/16 15:36
 */

public class ProvinceJson implements IPickerViewData, Serializable {


    /**
     * name : 省份
     * city : [{"name":"北京市","area":["东城区","西城区","崇文区","宣武区","朝阳区"]}]
     */

    private String name;
    private List<CityBean> city;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CityBean> getCityList() {
        return city;
    }

    public void setCityList(List<CityBean> city) {
        this.city = city;
    }

    // 实现 IPickerViewData 接口，
    // 这个用来显示在PickerView上面的字符串，
    // PickerView会通过IPickerViewData获取getPickerViewText方法显示出来。
    @Override
    public String getPickerViewText() {
        return this.name;
    }

    public ProvinceJson(List<CityBean> city, String name) {
        this.city = city;
        this.name = name;
    }

    public ProvinceJson() {
    }
}
