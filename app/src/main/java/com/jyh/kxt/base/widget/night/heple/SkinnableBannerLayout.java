package com.jyh.kxt.base.widget.night.heple;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.jyh.kxt.R;
import com.library.widget.viewpager.BannerLayout;


/**
 * Created by geminiwen on 16/6/15.
 */
public class SkinnableBannerLayout extends BannerLayout implements Skinnable {

    private AttrsHelper mAttrsHelper;

    public SkinnableBannerLayout(Context context) {
        this(context, null);
    }

    public SkinnableBannerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SkinnableBannerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mAttrsHelper = new AttrsHelper();

        TypedArray a;
        a = context.obtainStyledAttributes(attrs,
                R.styleable.SkinnableView,
                defStyleAttr, 0);
        mAttrsHelper.storeAttributeResource(a, R.styleable.SkinnableView);
        a.recycle();

        a = context.obtainStyledAttributes(attrs,
                R.styleable.SkinnableGeneralBannerLayout,
                defStyleAttr, 0);
        mAttrsHelper.storeAttributeResource(a, R.styleable.SkinnableGeneralBannerLayout);
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

        key = R.styleable.SkinnableBannerLayout[R.styleable.SkinnableBannerLayout_selectedIndicatorColor];
        Integer selectedIndicatorColorResource = mAttrsHelper.getAttributeResource(key);
        key = R.styleable.SkinnableBannerLayout[R.styleable.SkinnableBannerLayout_unSelectedIndicatorColor];
        Integer unselectedIndicatorColorResource = mAttrsHelper.getAttributeResource(key);
        if (selectedIndicatorColorResource != null&&unselectedIndicatorColorResource != null) {
            int selectedIndicatorColor = ContextCompat.getColor(context, selectedIndicatorColorResource);
            int unselectedIndicatorColor = ContextCompat.getColor(context, unselectedIndicatorColorResource);
            setColors(selectedIndicatorColor,unselectedIndicatorColor);
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
