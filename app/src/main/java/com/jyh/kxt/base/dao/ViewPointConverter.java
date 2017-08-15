package com.jyh.kxt.base.dao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jyh.kxt.trading.json.ViewPointTradeBean;

import org.greenrobot.greendao.converter.PropertyConverter;

/**
 * Created by Mr'Dai on 2017/8/14.
 */

public class ViewPointConverter  implements PropertyConverter<ViewPointTradeBean, String> {

    @Override
    public ViewPointTradeBean convertToEntityProperty(String databaseValue) {
        if (databaseValue == null) {
            return null;
        } else {
            return JSONObject.parseObject(databaseValue, ViewPointTradeBean.class);
        }
    }

    @Override
    public String convertToDatabaseValue(ViewPointTradeBean entityProperty) {
        if (entityProperty == null) {
            return null;
        } else {
            return JSON.toJSONString(entityProperty);
        }
    }
}