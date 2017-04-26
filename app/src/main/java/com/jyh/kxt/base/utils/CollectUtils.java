package com.jyh.kxt.base.utils;

import android.content.Context;

import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.constant.SpConstant;
import com.library.util.SPUtils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * 项目名:Kxt
 * 类描述:收藏工具类
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/25.
 */

public class CollectUtils {

    public static final String TYPE_FLASH = "flash";

    /**
     * 收藏
     *
     * @param context
     * @param type
     * @param id
     */
    public static void collect(Context context, String type, String id, ObserverData observable) {
        try {
            switch (type) {
                case TYPE_FLASH:
                    Set<String> flashSet = SPUtils.getStringSet(context, SpConstant.COLLECT_FLASH);
                    if (flashSet == null) flashSet = new HashSet<>();
                    flashSet.add(id);
                    observable.callback(null);
                    break;
            }
        } catch (Exception e) {
            observable.onError(e);
        }
    }

    /**
     * 取消收藏
     *
     * @param context
     * @param type
     * @param id
     */
    public static void unCollect(Context context, String type, String id, ObserverData observable) {
        try {
            switch (type) {
                case TYPE_FLASH:
                    Set<String> flashSet = SPUtils.getStringSet(context, SpConstant.COLLECT_FLASH);
                    if (flashSet == null) observable.onError(null);
                    flashSet.remove(id);
                    observable.callback(null);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isCollect(Context context, String type, String id) {
        switch (type) {
            case TYPE_FLASH:
                Set<String> flashSet = SPUtils.getStringSet(context, SpConstant.COLLECT_FLASH);
                if (flashSet == null) return false;
                Iterator<String> iterator = flashSet.iterator();
                while (iterator.hasNext()) {
                    if (id.equals(iterator.next())) {
                        return true;
                    }
                }
                break;
        }
        return false;
    }
}
