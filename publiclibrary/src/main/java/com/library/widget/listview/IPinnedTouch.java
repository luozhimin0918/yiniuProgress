package com.library.widget.listview;

import android.view.View;

/**
 * Created by Mr'Dai on 2017/9/13.
 */

public interface IPinnedTouch {
    /**
     * 得到点击的TouchTarget
     *
     * @return
     */
    void dispatchTouchEvent(View view);
}
