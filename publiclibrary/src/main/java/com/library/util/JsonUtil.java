package com.library.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * Created by Mr'Dai on 2017/6/21.
 */

public class JsonUtil {

    public static final <T> List<T> parseArray(String text, Class<T> clazz) {
        if (text == null) {
            return null;
        } else {
            List<Object> objectList = JSONArray.parseArray(text, Object.class);
            for (Object itemObj : objectList) {
                try {
                    JSONObject.parseObject(itemObj.toString(),clazz);
                } catch (Exception e) {
                    objectList.remove(itemObj);
                    e.printStackTrace();
                }
            }
            return JSONArray.parseArray(objectList.toString(), clazz);
        }
    }
}
