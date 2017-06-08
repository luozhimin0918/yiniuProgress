package com.jyh.kxt.base.widget.night.heple;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.jyh.kxt.R;


/**
 * 项目名:skin-sprite-master
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/3/10.
 */

public class SkinnableImageView extends AppCompatImageView implements Skinnable {
    private AttrsHelper mAttrsHelper;

    public SkinnableImageView(Context context) {
        this(context, null);
    }

    public SkinnableImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public SkinnableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mAttrsHelper = new AttrsHelper();

        TypedArray a;

        a = context.obtainStyledAttributes(attrs, R.styleable.SkinnableView, defStyleAttr, 0);
        mAttrsHelper.storeAttributeResource(a, R.styleable.SkinnableView);
        a.recycle();

        a = context.obtainStyledAttributes(attrs, R.styleable.SkinnableImageView, defStyleAttr, 0);
        mAttrsHelper.storeAttributeResource(a, R.styleable.SkinnableImageView);
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

        key = R.styleable.SkinnableView[R.styleable.SkinnableView_android_backgroundTint];
        Integer backgroundTintResource = mAttrsHelper.getAttributeResource(key);
        if (backgroundTintResource != null) {
            ColorStateList backgroundTint = ContextCompat.getColorStateList(context, backgroundTintResource);
            setSupportBackgroundTintList(backgroundTint);
        }

        key = R.styleable.SkinnableImageView[R.styleable.SkinnableImageView_android_src];
        Integer srcResource = mAttrsHelper.getAttributeResource(key);
        if (srcResource != null) {
            setImageResource(srcResource);
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
