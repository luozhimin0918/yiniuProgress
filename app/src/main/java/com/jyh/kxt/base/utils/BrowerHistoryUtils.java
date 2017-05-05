package com.jyh.kxt.base.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;

import com.alibaba.fastjson.JSON;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.main.json.NewsJson;
import com.library.util.RegexValidateUtil;
import com.library.util.SPUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名:Kxt
 * 类描述:浏览记录工具类
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/5.
 */

public class BrowerHistoryUtils {

    /**
     * 保存浏览记录
     *
     * @param context
     * @param news
     */
    public static void save(Context context, NewsJson news) {
        String history = "";
        try {
            history = JSON.toJSONString(news);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!RegexValidateUtil.isEmpty(history)) {
            String historyStr = SPUtils.getString(context, SpConstant.BROWER_HISTORY);
            if (RegexValidateUtil.isEmpty(historyStr)) {
                historyStr = history;
            } else {
                String[] splitHistory = historyStr.split(",,..,,..");
                for (String s : splitHistory) {
                    if (s.equals(history)) {
                        return;
                    }
                }
                historyStr += ",,..,,.." + history;
            }
            SPUtils.save(context, SpConstant.BROWER_HISTORY, historyStr);
        }

    }

    /**
     * 清空浏览历史
     *
     * @param context
     */
    public static void clear(Context context) {
        SPUtils.save(context, SpConstant.BROWER_HISTORY, "");
    }

    /**
     * 获取浏览历史
     *
     * @param context
     * @return
     */
    public static List<NewsJson> getHistory(Context context) {
        String historyStr = SPUtils.getString(context, SpConstant.BROWER_HISTORY);
        if (RegexValidateUtil.isEmpty(historyStr)) {
            return null;
        } else {
            String[] splitHistory = historyStr.split(",,..,,..");
            if (splitHistory == null || splitHistory.length == 0) {
                return null;
            }

            List<NewsJson> newsJsons = new ArrayList<>();
            for (String s : splitHistory) {
                try {
                    NewsJson newsJson = JSON.parseObject(s, NewsJson.class);
                    if (newsJson != null) {
                        newsJsons.add(newsJson);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return newsJsons;
        }
    }

    /**
     * 获取浏览状态
     *
     * @param context
     * @param newsJson
     * @return
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean isBrowered(Context context, NewsJson newsJson) {
        List<NewsJson> history = getHistory(context);
        if (history == null) return false;
        for (NewsJson news : history) {
            if (news.equals(newsJson)) {
                return true;
            }
        }
        return false;
    }

}
