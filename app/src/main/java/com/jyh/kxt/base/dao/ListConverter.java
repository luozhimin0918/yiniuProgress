package com.jyh.kxt.base.dao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.List;

/**
 * Created by Mr'Dai on 2017/8/14.
 */

public class ListConverter implements PropertyConverter<List<String>, String> {

    @Override
    public List<String> convertToEntityProperty(String databaseValue) {
        if (databaseValue == null) {
            return null;
        } else {
            return JSONArray.parseArray(databaseValue, String.class);
        }
    }

    @Override
    public String convertToDatabaseValue(List<String> entityProperty) {
        if (entityProperty == null) {
            return null;
        } else {
            return JSON.toJSONString(entityProperty);
        }
    }
}