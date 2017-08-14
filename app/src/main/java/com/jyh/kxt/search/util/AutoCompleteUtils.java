package com.jyh.kxt.search.util;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.search.json.QuoteItemJson;
import com.library.util.SPUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 项目名:KxtProfessional
 * 类描述:联想查询工具
 * 创建人:苟蒙蒙
 * 创建日期:2017/8/14.
 */

public class AutoCompleteUtils {

    /**
     * 保存联想信息
     *
     * @param mContext
     * @param data
     */
    public static void saveData(Context mContext, List<QuoteItemJson> data) {
        if (data == null || data.size() == 0)
            return;
        HashSet<String> set = new HashSet<>(SPUtils.getStringSet(mContext, SpConstant.SEARCH_QUOTE));
        for (QuoteItemJson quoteItemJson : data) {
            String quoteString = JSON.toJSONString(quoteItemJson);
            set.add(quoteString);
        }
        SPUtils.save(mContext, SpConstant.SEARCH_QUOTE, set);
    }

    /**
     * 保存联想信息
     *
     * @param mContext
     * @param data
     */
    public static void saveData(Context mContext, QuoteItemJson data) {
        if (data == null)
            return;
        HashSet<String> set = new HashSet<>(SPUtils.getStringSet(mContext, SpConstant.SEARCH_QUOTE));
        String quoteString = JSON.toJSONString(data);
        set.add(quoteString);
        SPUtils.save(mContext, SpConstant.SEARCH_QUOTE, set);
    }

    /**
     * 获取联想信息
     *
     * @param mContext
     * @return
     */
    public static List<QuoteItemJson> getData(Context mContext) {
        Set<String> set = SPUtils.getStringSet(mContext, SpConstant.SEARCH_QUOTE);
        if (set == null || set.size() == 0)
            return null;
        else {
            List<QuoteItemJson> quotes = new ArrayList<>();
            for (String quoteString : set) {
                quotes.add(JSON.parseObject(quoteString, QuoteItemJson.class));
            }
            return quotes;
        }
    }

}
