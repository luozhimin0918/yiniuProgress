package com.jyh.kxt.base.widget.night.heple;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.jyh.kxt.R;
import com.library.widget.tablayout.SegmentTabLayout;


/**
 * Created by geminiwen on 16/6/15.
 */
public class SkinnableSegmentTabLayout extends SegmentTabLayout implements Skinnable {

    private AttrsHelper mAttrsHelper;

    public SkinnableSegmentTabLayout(Context context) {
        this(context, null);
    }

    public SkinnableSegmentTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SkinnableSegmentTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mAttrsHelper = new AttrsHelper();

        TypedArray a;
        a = context.obtainStyledAttributes(attrs,
                R.styleable.SkinnableView,
                defStyleAttr, 0);
        mAttrsHelper.storeAttributeResource(a, R.styleable.SkinnableView);
        a.recycle();

        a = context.obtainStyledAttributes(attrs,
                R.styleable.SegmentTabLayout,
                defStyleAttr, 0);
        mAttrsHelper.storeAttributeResource(a, R.styleable.SegmentTabLayout);
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

        key = R.styleable.SegmentTabLayout[R.styleable.SegmentTabLayout_tl_bar_color];
        Integer barColorResource = mAttrsHelper.getAttributeResource(key);
        if (barColorResource != null) {
            Integer barColor = ContextCompat.getColor(context, barColorResource);
            setDarColor(barColor);
        }

        key = R.styleable.SegmentTabLayout[R.styleable.SegmentTabLayout_tl_indicator_color];
        Integer indicatorColorResource = mAttrsHelper.getAttributeResource(key);
        if (indicatorColorResource != null) {
            Integer indicatorColor = ContextCompat.getColor(context, indicatorColorResource);
            setIndicatorColor(indicatorColor);
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
