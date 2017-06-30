package com.library.widget.viewpager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 项目名:Kxt
 * 类描述:可控的Viewpager
 * 创建人:苟蒙蒙
 * 创建日期:2017/6/30.
 */

public class ControllableViewPager extends ViewPager {

    private boolean isScrollable = true;// 是否可滑动

    public ControllableViewPager(Context context) {
        super(context);
    }

    public ControllableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        if (isScrollable)
            return super.onTouchEvent(arg0);
        else
            return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        if (isScrollable)
            return super.onInterceptTouchEvent(arg0);
        else
            return false;
    }

    public boolean isScrollable() {
        return isScrollable;
    }

    public void setScrollable(boolean scrollable) {
        isScrollable = scrollable;
    }
}
