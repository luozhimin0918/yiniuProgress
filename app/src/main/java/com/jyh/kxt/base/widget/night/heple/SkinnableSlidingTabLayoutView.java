package com.jyh.kxt.base.widget.night.heple;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.jyh.kxt.R;
import com.library.widget.tablayout.SlidingTabLayout;


/**
 * Created by geminiwen on 16/6/15.
 */
public class SkinnableSlidingTabLayoutView extends SlidingTabLayout implements Skinnable {

    private AttrsHelper mAttrsHelper;

    public SkinnableSlidingTabLayoutView(Context context) {
        this(context, null);
    }

    public SkinnableSlidingTabLayoutView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SkinnableSlidingTabLayoutView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mAttrsHelper = new AttrsHelper();

        TypedArray a;
        a = context.obtainStyledAttributes(attrs,
                R.styleable.SkinnableView,
                defStyleAttr, 0);
        mAttrsHelper.storeAttributeResource(a, R.styleable.SkinnableView);
        a.recycle();
        a = context.obtainStyledAttributes(attrs,
                R.styleable.SkinnableSlidingTabLayoutView,
                defStyleAttr, 0);
        mAttrsHelper.storeAttributeResource(a, R.styleable.SkinnableSlidingTabLayoutView);
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

        key = R.styleable.SkinnableSlidingTabLayoutView[R.styleable.SkinnableSlidingTabLayoutView_tl_divider_color];
        Integer dividerColorResource = mAttrsHelper.getAttributeResource(key);
        if (dividerColorResource != null) {
            Integer dividerColor = ContextCompat.getColor(context, dividerColorResource);
            setDividerColor(dividerColor);
        }

        key = R.styleable.SkinnableSlidingTabLayoutView[R.styleable.SkinnableSlidingTabLayoutView_tl_indicator_color];
        Integer indicatorColorResource = mAttrsHelper.getAttributeResource(key);
        if (indicatorColorResource != null) {
            Integer indicatorColor = ContextCompat.getColor(context, indicatorColorResource);
            setIndicatorColor(indicatorColor);
        }

        key = R.styleable.SkinnableSlidingTabLayoutView[R.styleable.SkinnableSlidingTabLayoutView_tl_textSelectColor];
        Integer textSelectColorResource = mAttrsHelper.getAttributeResource(key);
        if (textSelectColorResource != null) {
            Integer textSelectColor = ContextCompat.getColor(context, textSelectColorResource);
            setTextSelectColor(textSelectColor);
        }

        key = R.styleable.SkinnableSlidingTabLayoutView[R.styleable.SkinnableSlidingTabLayoutView_tl_textUnselectColor];
        Integer textUnselectColorResource = mAttrsHelper.getAttributeResource(key);
        if (textUnselectColorResource != null) {
            Integer textUnselectColor = ContextCompat.getColor(context, textUnselectColorResource);
            setTextUnselectColor(textUnselectColor);
        }

        key = R.styleable.SkinnableSlidingTabLayoutView[R.styleable.SkinnableSlidingTabLayoutView_tl_underline_color];
        Integer underlineColorResource = mAttrsHelper.getAttributeResource(key);
        if (underlineColorResource != null) {
            Integer underlineColor = ContextCompat.getColor(context, underlineColorResource);
            setUnderlineColor(underlineColor);
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
