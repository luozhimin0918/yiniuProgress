package com.library.util;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Mr'Dai on 2017/9/13.
 */

public class ViewCompatUtil {

    /**
     * 帮忙点击
     *
     * @param rootView
     * @param clickX
     * @param clickY
     */
    public static void performClickView(View rootView, float clickX, float clickY) {
        parseViewGroup(rootView, clickX, clickY);
    }

    private static void parseViewGroup(View rootView, float clickX, float clickY) {
        if (rootView instanceof ViewGroup) {
            ViewGroup parseGroup = (ViewGroup) rootView;

            boolean pinnedViewTouched = isPinnedViewTouched(rootView, clickX, clickY);
            if(pinnedViewTouched && parseGroup.isClickable()){
                parseGroup.callOnClick();
            }

            for (int i = 0; i < parseGroup.getChildCount(); i++) {
                View childAt = parseGroup.getChildAt(i);
                parseViewGroup(childAt, clickX, clickY);
            }
        } else {
            boolean pinnedViewTouched = isPinnedViewTouched(rootView, clickX, clickY);
            if (pinnedViewTouched && rootView.isClickable()) {
                rootView.callOnClick();
            }
        }
    }

    private static boolean isPinnedViewTouched(View rootView, float x, float y) {
        Rect rect = new Rect();
        rootView.getGlobalVisibleRect(rect);
        if(x > Math.abs(rect.left) && x < Math.abs(rect.right) &&
            y >Math.abs(rect.top) && y <Math.abs(rect.bottom) ){
            return true;
        }
        return false;
    }

}
