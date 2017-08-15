package com.jyh.kxt.base.dao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jyh.kxt.trading.json.ShareDictBean;

import org.greenrobot.greendao.converter.PropertyConverter;

/**
 * Created by Mr'Dai on 2017/8/14.
 */

public class ShareDictConverter implements PropertyConverter<ShareDictBean, String> {

    @Override
    public ShareDictBean convertToEntityProperty(String databaseValue) {
        if (databaseValue == null) {
            return null;
        } else {
            return JSONObject.parseObject(databaseValue, ShareDictBean.class);
        }
    }

    @Override
    public String convertToDatabaseValue(ShareDictBean entityProperty) {
        if (entityProperty == null) {
            return null;
        } else {
            return JSON.toJSONString(entityProperty);
        }
    }
}