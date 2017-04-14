package com.jyh.kxt.base.widget;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Mr'Dai on 2017/4/14.
 */

public class TrendChartTextView extends TextView {

    private Rect frameRect;

    public TrendChartTextView(Context context) {
        this(context, null);
    }

    public TrendChartTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TrendChartTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {

        setVisibility(View.GONE);
    }


    public void setPoint(Point point) {

        if (getVisibility() == GONE) {
            setVisibility(View.VISIBLE);
        }

        int width = getWidth();
        int height = getHeight();

        if (point.x + width > frameRect.width()) {
            setTranslationX(point.x - width);
        } else {
            setTranslationX(point.x);
        }

        if (point.y + height > frameRect.height()) {
            setTranslationY(point.y - height);
        } else {
            setTranslationY(point.y);
        }
    }

    public void setFrameRect(Rect frameRect) {
        this.frameRect = frameRect;
    }
}
