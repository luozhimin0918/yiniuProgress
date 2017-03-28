package com.jyh.kxt.base.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

import com.jyh.kxt.R;

/**
 * Created by Mr'Dai on 2017/3/21.
 * <p>
 * 减少Drawable文件夹下面的XML,Button 点击之后变色 松开之后恢复颜色进行封装
 */

public class DiscolorButton extends Button {

    private int clickBackground, defaultBackground, clickFontColor, defaultFontColor;
    private int cornerRadius;

    public DiscolorButton(Context context) {
        this(context, null);
    }

    public DiscolorButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DiscolorButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.DiscolorButton, defStyle, 0);

        defaultBackground = array.getColor(R.styleable.DiscolorButton_defaultBackground, 0);

        int alpha = ColorUtils.setAlphaComponent(defaultBackground, 200);
        clickBackground = array.getColor(R.styleable.DiscolorButton_clickBackground, alpha);


        clickFontColor = array.getColor(R.styleable.DiscolorButton_clickFontColor, 0);
        defaultFontColor = array.getColor(R.styleable.DiscolorButton_defaultFontColor, 0);

        cornerRadius = array.getDimensionPixelSize(R.styleable.DiscolorButton_cornerRadius, 0);

        array.recycle();

        transformColorHelp(defaultBackground, defaultFontColor);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                transformColorHelp(clickBackground, clickFontColor);
                break;
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                transformColorHelp(defaultBackground, defaultFontColor);
                break;
        }
        super.onTouchEvent(event);
        return true;
    }

    private void transformColorHelp(int bgColor, int fontColor) {
        GradientDrawable mGradientDrawable = new GradientDrawable();
        mGradientDrawable.setCornerRadius(cornerRadius);
        mGradientDrawable.setColor(bgColor);
        ViewCompat.setBackground(this, mGradientDrawable);
        setTextColor(fontColor);
    }
}
