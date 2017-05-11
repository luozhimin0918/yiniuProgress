package com.jyh.kxt.main.json.flash;

import com.library.util.DateUtils;

import java.util.Comparator;

/**
 * 项目名:Kxt
 * 类描述:快讯排序工具
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/10.
 */

public class FlashJsonComparator implements Comparator<FlashJson> {
    @Override
    public int compare(FlashJson o1, FlashJson o2) {
        try {
            long o1time = Long.parseLong(DateUtils.transfromTime(o1.getTime(), DateUtils.TYPE_YMDHMS));
            long o2time = Long.parseLong(DateUtils.transfromTime(o2.getTime(), DateUtils.TYPE_YMDHMS));
            return (int) (o2time - o1time);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
