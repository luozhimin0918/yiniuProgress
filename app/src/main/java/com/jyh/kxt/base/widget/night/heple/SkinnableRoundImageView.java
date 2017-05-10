package com.jyh.kxt.base.widget.night.heple;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.jyh.kxt.R;
import com.jyh.kxt.base.custom.RoundImageView;

/**
 * 项目名:FirstGold 2.1.3
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/3/14.
 */

public class SkinnableRoundImageView extends RoundImageView implements Skinnable {

    private final AttrsHelper mAttrsHelper;

    public SkinnableRoundImageView(Context context) {
        this(context, null);
    }

    public SkinnableRoundImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SkinnableRoundImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mAttrsHelper = new AttrsHelper();

        TypedArray a;
        a = context.obtainStyledAttributes(attrs,
                R.styleable.SkinnableView,
                defStyle, 0);
        mAttrsHelper.storeAttributeResource(a, R.styleable.SkinnableView);
        a.recycle();

        a = context.obtainStyledAttributes(attrs,
                R.styleable.SkinnableRoundImageView,
                defStyle, 0);
        mAttrsHelper.storeAttributeResource(a, R.styleable.SkinnableRoundImageView);
        a.recycle();
    }

    @Override
    public void applyDayNight() {

        Object tag = getTag();
        if (tag instanceof String) {
            String signTag = (String) tag;
            if ("mask".equals(signTag)) {// 蒙板 ->>> 针对不需要更改图片但是需要更改透明度的而言
                return;
            }
        }

        Context context = getContext();
        int key;

        key = R.styleable.SkinnableView[R.styleable.SkinnableView_android_background];
        Integer backgroundResource = mAttrsHelper.getAttributeResource(key);
        if (backgroundResource != null) {
            Drawable background = ContextCompat.getDrawable(context, backgroundResource);
            setBackgroundDrawable(background);
        }

        key = R.styleable.SkinnableRoundImageView[R.styleable.SkinnableRoundImageView_android_src];
        Integer backgroundResource2 = mAttrsHelper.getAttributeResource(key);
        if (backgroundResource2 != null) {
            Drawable background = ContextCompat.getDrawable(context, backgroundResource2);
            setImageDrawable(background);
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
