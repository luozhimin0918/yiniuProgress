package com.jyh.kxt.base.widget.night.heple;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.jyh.kxt.R;


/**
 * Created by geminiwen on 16/6/15.
 */
public class SkinnableTabLayout extends TabLayout implements Skinnable {

    private AttrsHelper mAttrsHelper;

    public SkinnableTabLayout(Context context) {
        this(context, null);
    }

    public SkinnableTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SkinnableTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mAttrsHelper = new AttrsHelper();

        TypedArray a;
        a = context.obtainStyledAttributes(attrs,
                R.styleable.SkinnableTabLayout,
                defStyleAttr, 0);
        mAttrsHelper.storeAttributeResource(a, R.styleable.SkinnableTabLayout);
        a.recycle();
    }

    @Override
    public void applyDayNight() {
        Context context = getContext();
        int key;

        key = R.styleable.SkinnableTabLayout[R.styleable.SkinnableTabLayout_android_background];
        Integer backgroundResource = mAttrsHelper.getAttributeResource(key);
        if (backgroundResource != null) {
            Drawable background = ContextCompat.getDrawable(context, backgroundResource);
            setBackground(background);
        }

        key = R.styleable.SkinnableTabLayout[R.styleable.SkinnableTabLayout_tabIndicatorColor];
        Integer indicatorColorResource = mAttrsHelper.getAttributeResource(key);
        if (indicatorColorResource != null) {
            int indicatorColor = ContextCompat.getColor(context, indicatorColorResource);
            setSelectedTabIndicatorColor(indicatorColor);
        }

        key = R.styleable.SkinnableTabLayout[R.styleable.SkinnableTabLayout_tabSelectedTextColor];
        Integer selectedTextColorResource = mAttrsHelper.getAttributeResource(key);

        key = R.styleable.SkinnableTabLayout[R.styleable.SkinnableTabLayout_tabTextColor];
        Integer textColorResource = mAttrsHelper.getAttributeResource(key);

        if(selectedTextColorResource!=null&&textColorResource!=null){
            int selectedTextColor= ContextCompat.getColor(context,selectedTextColorResource);
            int textColor= ContextCompat.getColor(context,textColorResource);
            setTabTextColors(textColor,selectedTextColor);
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
