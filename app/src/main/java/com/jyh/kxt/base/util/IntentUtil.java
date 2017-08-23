package com.jyh.kxt.base.util;

import java.util.HashMap;

/**
 * Created by Mr'Dai on 2017/8/8.
 */

public class IntentUtil {
    static HashMap<String, Object> intentMap = new HashMap<>();

    public static final String OBJECT = "object";

    public static void putObject(String tag, Object object) {
        intentMap.put(tag, object);
    }

    public static Object getObject(String tag) {
        Object object = intentMap.get(tag);

        intentMap.remove(tag);
        intentMap.clear();

        return object;
    }
}
