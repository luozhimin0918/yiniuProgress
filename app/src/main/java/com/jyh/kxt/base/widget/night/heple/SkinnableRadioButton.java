package com.jyh.kxt.base.widget.night.heple;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.RadioButton;

import com.jyh.kxt.R;

/**
 * 项目名:FirstGold 2.1.3
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/3/10.
 */

public class SkinnableRadioButton extends RadioButton implements Skinnable {

    private AttrsHelper mAttrsHelper;

    public SkinnableRadioButton(Context context) {
        this(context, null);
    }

    public SkinnableRadioButton(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.radioButtonStyle);
    }

    public SkinnableRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mAttrsHelper = new AttrsHelper();

        TypedArray a;

        a = context.obtainStyledAttributes(attrs,
                R.styleable.SkinnableView,
                defStyleAttr, 0);
        mAttrsHelper.storeAttributeResource(a, R.styleable.SkinnableView);
        a.recycle();

        a = context.obtainStyledAttributes(attrs,
                R.styleable.SkinnableRadioButton,
                defStyleAttr, 0);
        mAttrsHelper.storeAttributeResource(a, R.styleable.SkinnableRadioButton);
        a.recycle();
    }

    @Override
    public void applyDayNight() {
        Context context = getContext();
        int key;

        key = R.styleable.SkinnableView[R.styleable.SkinnableView_android_background];
        Integer backgroundResource = mAttrsHelper.getAttributeResource(key);
        if (backgroundResource != null) {
            Drawable background = ContextCompat.getDrawable(context, backgroundResource);
            setBackgroundDrawable(background);
        }

        key = R.styleable.SkinnableRadioButton[R.styleable.SkinnableRadioButton_android_drawableTop];
        Integer backgroundResource2 = mAttrsHelper.getAttributeResource(key);
        if (backgroundResource2 != null) {
            Drawable background = ContextCompat.getDrawable(context, backgroundResource2);
            background.setBounds(0, 0, background.getMinimumWidth(), background.getMinimumHeight());
            setCompoundDrawables(null, background, null, null);
    }}

    @Override
    public boolean isSkinnable() {
        return true;
    }

    @Override
    public void setSkinnable(boolean skinnable) {

    }
}
