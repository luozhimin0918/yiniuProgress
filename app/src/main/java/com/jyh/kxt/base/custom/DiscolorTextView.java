package com.jyh.kxt.base.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.graphics.ColorUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

import com.jyh.kxt.R;

/**
 * Created by Mr'Dai on 2017/3/21.
 * <p>
 * 减少Drawable文件夹下面的XML,Butto 点击之后变色 松开之后恢复颜色进行封装
 */

public class DiscolorTextView extends TextView {

    private int clickFontColor, defaultFontColor;

    public DiscolorTextView(Context context) {
        this(context, null);
    }

    public DiscolorTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DiscolorTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.DiscolorTextView, defStyle, 0);

        defaultFontColor = array.getColor(R.styleable.DiscolorTextView_defaultFontColor, 0);

        int alpha = ColorUtils.setAlphaComponent(defaultFontColor, 100);
        clickFontColor = array.getColor(R.styleable.DiscolorTextView_clickFontColor, alpha);

        array.recycle();

        transformColorHelp(defaultFontColor);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isEnabled()) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    transformColorHelp(clickFontColor);
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    transformColorHelp(defaultFontColor);
                    break;
            }
        }
        super.onTouchEvent(event);
        return true;
    }

    private void transformColorHelp(int fontColor) {
        setTextColor(fontColor);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
    }
}
