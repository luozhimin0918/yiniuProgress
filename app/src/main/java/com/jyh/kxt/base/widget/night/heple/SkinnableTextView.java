package com.jyh.kxt.base.widget.night.heple;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.jyh.kxt.R;


/**
 * Created by geminiwen on 16/6/15.
 */
public class SkinnableTextView extends AppCompatTextView implements Skinnable {

    private AttrsHelper mAttrsHelper;

    public SkinnableTextView(Context context) {
        this(context, null);
    }

    public SkinnableTextView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public SkinnableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mAttrsHelper = new AttrsHelper();

        TypedArray a;

        a = context.obtainStyledAttributes(attrs,
                R.styleable.SkinnableView,
                defStyleAttr, 0);
        mAttrsHelper.storeAttributeResource(a, R.styleable.SkinnableView);
        a.recycle();

        a = context.obtainStyledAttributes(attrs,
                R.styleable.SkinnableTextView,
                defStyleAttr, 0);
        mAttrsHelper.storeAttributeIndex(a, R.styleable.SkinnableTextView);
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

        key = R.styleable.SkinnableTextView[R.styleable.SkinnableTextView_android_textColor];
        Integer textColorResource = mAttrsHelper.getAttributeResource(key);
        if (textColorResource != null) {
            ColorStateList color = ContextCompat.getColorStateList(context, textColorResource);
            setTextColor(color);
        }

        key = R.styleable.SkinnableTextView[R.styleable.SkinnableTextView_android_drawableLeft];
        Integer drawableLeftResource = mAttrsHelper.getAttributeResource(key);
        if (drawableLeftResource != null) {
            Drawable drawableLeft = ContextCompat.getDrawable(context, drawableLeftResource);
            setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null);
        }

        key = R.styleable.SkinnableTextView[R.styleable.SkinnableTextView_android_drawableTop];
        Integer drawableTopResource = mAttrsHelper.getAttributeResource(key);
        if (drawableTopResource != null) {
            Drawable drawableTop = ContextCompat.getDrawable(context, drawableTopResource);
            setCompoundDrawablesWithIntrinsicBounds(null, drawableTop, null, null);
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
