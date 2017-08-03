package com.jyh.kxt.base.custom;

import android.widget.GridView;

/**
 * Created by Mr'Dai on 2017/8/3.
 */

public class MeasureGridView extends GridView {
    public MeasureGridView(android.content.Context context,
                           android.util.AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 设置不滚动
     */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);

    }
}
