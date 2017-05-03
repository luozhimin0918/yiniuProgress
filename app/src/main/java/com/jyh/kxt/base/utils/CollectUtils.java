package com.jyh.kxt.base.utils;

import android.content.Context;

import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.constant.SpConstant;
import com.library.util.SPUtils;
import com.library.widget.window.ToastView;

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

    /**
     * 快讯 收藏
     *
     * @param context
     * @param str
     * @param observable
     */
    public static void flashCollect(Context context, String str, ObserverData observable) {
        try {
            Set<String> set = SPUtils.getStringSet(context, SpConstant.COLLECT_FLASH);
            if (set == null)
                set = new HashSet<>();
            set.add(str);
            SPUtils.save(context, SpConstant.COLLECT_FLASH, set);
            observable.callback(null);
        } catch (Exception e) {
            e.printStackTrace();
            observable.onError(null);
        }
    }

    /**
     * 快讯 取消收藏
     *
     * @param context
     * @param str
     * @param observable
     */
    public static void flashUnCollect(Context context, String str, ObserverData observable) {
        try {
            Set<String> set = SPUtils.getStringSet(context, SpConstant.COLLECT_FLASH);
            if (set == null) {
                ToastView.makeText3(context, "未收藏");
                return;
            }
            set.remove(str);
            SPUtils.save(context, SpConstant.COLLECT_FLASH, set);
            observable.callback(null);
        } catch (Exception e) {
            e.printStackTrace();
            observable.onError(null);
        }
    }

    /**
     * 快讯 是否收藏
     *
     * @param context
     * @param str
     * @return
     */
    public static boolean flashIsCollect(Context context, String str) {
        Set<String> set = SPUtils.getStringSet(context, SpConstant.COLLECT_FLASH);
        if (set == null)
            return false;
        Iterator<String> iterator = set.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().equals(str)) {
                return true;
            }
        }
        return false;
    }
}
