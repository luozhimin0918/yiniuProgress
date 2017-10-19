package com.jyh.kxt.base.widget;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by Mr'Dai on 2017/10/18.
 */

public class FixedImageSizeLayout extends FrameLayout {
    private int fixedImageHeight = 0;

    public FixedImageSizeLayout(@NonNull Context context) {
        super(context);
    }

    public FixedImageSizeLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FixedImageSizeLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (fixedImageHeight == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            super.onMeasure(widthMeasureSpec, fixedImageHeight);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (bottom - top > 0 && fixedImageHeight == 0) {
            fixedImageHeight = bottom - top;
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            layoutParams.height = fixedImageHeight;
            setLayoutParams(layoutParams);
        }
    }
}
