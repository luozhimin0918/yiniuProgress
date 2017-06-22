package com.jyh.kxt.base.widget.night.heple;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.jyh.kxt.R;
import com.jyh.kxt.base.widget.SearchEditText;


/**
 * Created by geminiwen on 16/6/15.
 */
public class SkinnableSearchEditText extends SearchEditText implements Skinnable {

    private AttrsHelper mAttrsHelper;

    public SkinnableSearchEditText(Context context) {
        this(context, null);
    }

    public SkinnableSearchEditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public SkinnableSearchEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mAttrsHelper = new AttrsHelper();

        TypedArray a;

        a = context.obtainStyledAttributes(attrs,
                R.styleable.SkinnableView,
                defStyleAttr, 0);
        mAttrsHelper.storeAttributeResource(a, R.styleable.SkinnableView);
        a.recycle();

        a = context.obtainStyledAttributes(attrs,
                R.styleable.SkinnableSearchEditText,
                defStyleAttr, 0);
        mAttrsHelper.storeAttributeIndex(a, R.styleable.SkinnableSearchEditText);
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

        key = R.styleable.SkinnableSearchEditText[R.styleable.SkinnableSearchEditText_searchBackground];
        Integer bgResource = mAttrsHelper.getAttributeResource(key);
        if (bgResource != null) {
            Drawable bgDrawable = ContextCompat.getDrawable(context, bgResource);
            setBackground(bgDrawable);
        }

        key = R.styleable.SkinnableSearchEditText[R.styleable.SkinnableSearchEditText_searchTextColor];
        Integer textColorResource = mAttrsHelper.getAttributeResource(key);
        if (textColorResource != null) {
            int textColor = ContextCompat.getColor(context, textColorResource);
            setTextColor(textColor);
        }

        key = R.styleable.SkinnableSearchEditText[R.styleable.SkinnableSearchEditText_searchHintColor];
        Integer hintColorRes = mAttrsHelper.getAttributeResource(key);
        if (hintColorRes != null) {
            int hintColor = ContextCompat.getColor(context, hintColorRes);
            setHintColor(hintColor);
        }

        changeImg();
    }

    @Override
    public boolean isSkinnable() {
        return true;
    }

    @Override
    public void setSkinnable(boolean skinnable) {
    }
}
