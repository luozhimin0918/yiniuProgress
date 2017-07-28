package com.jyh.kxt.base.util;

import com.jyh.kxt.base.json.AdItemJson;
import com.jyh.kxt.base.json.AdTitleItemBean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/7/28.
 */

public class AdUtils {
    public static List<AdTitleItemBean> checkAdPosition(List<AdTitleItemBean> ads) {
        if (ads == null) return null;
        Iterator<AdTitleItemBean> iterator = ads.iterator();
        while (iterator.hasNext()) {
            AdTitleItemBean next = iterator.next();
            String position = next.getPosition();
            if (!"1".equals(position) && !"2".equals(position)) {
                iterator.remove();
            }
        }
        if (ads.size() == 0) return null;
        int size = ads.size();
        if (size == 1) {
            return ads;
        } else {
            List<AdTitleItemBean> adTitles = new ArrayList<>();
            AdTitleItemBean ad1 = ads.get(0);
            AdTitleItemBean ad2 = ads.get(1);
            String position1 = ad1.getPosition();
            String position2 = ad2.getPosition();
            if (position1 == null) {
                if (position2 == null) return ads;
                if (position2.equals("2")) return ads;
                if (position2.equals("1")) {
                    adTitles.add(ad2);
                    adTitles.add(ad1);
                    return adTitles;
                }
            } else if (position1.equals("1")) {
                return ads;
            } else if (position1.equals("2")) {
                adTitles.add(ad2);
                adTitles.add(ad1);
                return adTitles;
            }
        }
        return ads;
    }
}
