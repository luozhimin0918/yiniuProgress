package com.jyh.kxt.base.widget.night.heple;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

import com.jyh.kxt.R;


/**
 * Created by geminiwen on 16/6/15.
 */
public class SkinnableButton extends AppCompatButton implements Skinnable {

    private AttrsHelper mAttrsHelper;

    public SkinnableButton(Context context) {
        this(context, null);
    }

    public SkinnableButton(Context context, AttributeSet attrs) {
        this(context, attrs, android.support.v7.appcompat.R.attr.buttonStyle);
    }

    public SkinnableButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mAttrsHelper = new AttrsHelper();

        TypedArray a;

        a = context.obtainStyledAttributes(attrs,
                R.styleable.SkinnableView,
                defStyleAttr, 0);
        mAttrsHelper.storeAttributeResource(a, R.styleable.SkinnableView);
        a.recycle();

        a = context.obtainStyledAttributes(attrs,
                R.styleable.SkinnableButton,
                defStyleAttr, 0);
        mAttrsHelper.storeAttributeResource(a, R.styleable.SkinnableButton);
        a.recycle();
    }

    @Override
    public void applyDayNight() {
        Context context = getContext();
        int key;

        key = R.styleable.SkinnableView[R.styleable.SkinnableView_android_background];
        Integer backgroundResource = mAttrsHelper.getAttributeResource(key);
        if (backgroundResource != null) {
            Drawable drawable = ContextCompat.getDrawable(context, backgroundResource);
            setBackgroundDrawable(drawable);
        }

        key = R.styleable.SkinnableView[R.styleable.SkinnableView_android_backgroundTint];
        Integer backgroundTintResource = mAttrsHelper.getAttributeResource(key);
        if (backgroundTintResource != null) {
            ColorStateList backgroundTint = ContextCompat.getColorStateList(context, backgroundTintResource);
            setSupportBackgroundTintList(backgroundTint);
        }

        key = R.styleable.SkinnableButton[R.styleable.SkinnableButton_android_textColor];
        Integer textColorResource = mAttrsHelper.getAttributeResource(key);
        if (textColorResource != null) {
            ColorStateList color = ContextCompat.getColorStateList(context, textColorResource);
            setTextColor(color);
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
