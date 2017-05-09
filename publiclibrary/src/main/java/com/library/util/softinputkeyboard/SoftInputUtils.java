package com.library.util.softinputkeyboard;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

/**
 * 项目名:V9LIVE
 * 类描述: 软键盘工具类(解决软键盘遮盖按钮问题)
 * 创建人:苟蒙蒙
 * 创建日期:2017/3/1.
 */

public class SoftInputUtils {
    /**
     * 1、获取main在窗体的可视区域
     * 2、获取main在窗体的不可视区域高度
     * 3、判断不可视区域高度
     * 1、大于150：键盘显示  获取Scroll的窗体坐标
     * 算出main需要滚动的高度，使scroll显示。
     * 2、小于150：键盘隐藏
     *
     * @param main   根布局
     * @param scroll 需要显示的最下方View
     */
    public static void addLayoutListener(final View main, final View scroll) {
        main.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                main.getWindowVisibleDisplayFrame(rect);
                int mainInvisibleHeight = main.getRootView().getHeight() - rect.bottom;
                if (mainInvisibleHeight > 150) {
                    //软键盘弹出
                    int[] location = new int[2];
                    scroll.getLocationInWindow(location);
//                    int srollHeight = (location[1] + scroll.getHeight()) - rect.bottom;
                    int srollHeight = (location[1] + scroll.getHeight() + ((LinearLayout.LayoutParams) scroll.getLayoutParams())
                            .bottomMargin) - rect.bottom;
                    main.scrollTo(0, srollHeight);
                } else {
                    //软键盘隐藏
                    main.scrollTo(0, 0);
                }
            }
        });
    }
}
