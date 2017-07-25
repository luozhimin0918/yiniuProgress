package com.jyh.kxt.base.utils;


import android.graphics.Color;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/7/25.
 */

public class ColorFormatUtils {

    public static int formatColor(String color) {
        color = color.trim();
        String colorCopy = "#FF" + color.substring(color.indexOf("#") + 1);
        return Color.parseColor(colorCopy);
    }

}
