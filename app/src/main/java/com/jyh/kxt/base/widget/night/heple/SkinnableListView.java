package com.jyh.kxt.base.widget.night.heple;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

import com.jyh.kxt.R;


/**
 * Created by geminiwen on 16/6/15.
 */
public class SkinnableListView extends ListView implements Skinnable {

    private AttrsHelper mAttrsHelper;

    public SkinnableListView(Context context) {
        this(context, null);
    }

    public SkinnableListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SkinnableListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mAttrsHelper = new AttrsHelper();

        TypedArray a;
        a = context.obtainStyledAttributes(attrs,
                R.styleable.SkinnableView,
                defStyleAttr, 0);
        mAttrsHelper.storeAttributeResource(a, R.styleable.SkinnableView);
        a.recycle();
        a = context.obtainStyledAttributes(attrs,
                R.styleable.SkinnableListView,
                defStyleAttr, 0);
        mAttrsHelper.storeAttributeResource(a, R.styleable.SkinnableListView);
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

        key = R.styleable.SkinnableListView[R.styleable.SkinnableListView_android_divider];
        Integer dividerResource = mAttrsHelper.getAttributeResource(key);
        if (dividerResource != null) {
            Drawable dividerDrawable = ContextCompat.getDrawable(context, dividerResource);
            setDivider(dividerDrawable);
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
