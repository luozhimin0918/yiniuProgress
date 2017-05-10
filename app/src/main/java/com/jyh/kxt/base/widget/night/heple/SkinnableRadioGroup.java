package com.jyh.kxt.base.widget.night.heple;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.RadioGroup;

import com.jyh.kxt.R;

/**
 * 项目名:FirstGold 2.1.3
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/3/10.
 */

public class SkinnableRadioGroup extends RadioGroup implements Skinnable {

    private AttrsHelper mAttrsHelper;

    public SkinnableRadioGroup(Context context) {
        this(context, null);
    }

    public SkinnableRadioGroup(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.radioButtonStyle);
    }

    public SkinnableRadioGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);

        mAttrsHelper = new AttrsHelper();

        TypedArray a;

        a = context.obtainStyledAttributes(attrs,
                R.styleable.SkinnableView,
                defStyleAttr, 0);
        mAttrsHelper.storeAttributeResource(a, R.styleable.SkinnableView);
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
    }

    @Override
    public boolean isSkinnable() {
        return true;
    }

    @Override
    public void setSkinnable(boolean skinnable) {

    }
}
