package com.jyh.kxt.base.util.emoje;

import android.text.style.ForegroundColorSpan;

/**
 * Created by Mr'Dai on 2017/8/24.
 */

public class MyForegroundColorSpan extends ForegroundColorSpan {
    protected String mKeyWords;

    /**
     * 构造方法
     *
     * @param color    颜sè值
     * @param keyWords 显示的文字
     */
    public MyForegroundColorSpan(int color, String keyWords) {
        super(color);
        mKeyWords = keyWords;
    }

    /**
     * 原始文字
     *
     * @return
     */
    protected String keyWords() {
        return mKeyWords;
    }

    /**
     * 转化为代码
     *
     * @return
     */
    protected String toCode() {
        return mKeyWords;
    }
}