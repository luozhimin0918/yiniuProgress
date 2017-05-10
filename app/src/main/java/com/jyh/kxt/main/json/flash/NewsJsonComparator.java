package com.jyh.kxt.main.json.flash;

import com.jyh.kxt.main.json.NewsJson;

import java.util.Comparator;

/**
 * 项目名:Kxt
 * 类描述:要闻排序
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/9.
 */

public class NewsJsonComparator implements Comparator<NewsJson> {
    @Override
    public int compare(NewsJson o1, NewsJson o2) {

        try {
            long o1time = Long.parseLong(o1.getDatetime());
            long o2time = Long.parseLong(o2.getDatetime());
            return (int) (o2time - o1time);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }

    }
}
