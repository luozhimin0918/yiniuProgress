package com.jyh.kxt.base.widget.night.heple;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.jyh.kxt.R;


/**
 * Created by geminiwen on 16/6/15.
 */
public class SkinnableRecyclerView extends RecyclerView implements Skinnable {

    private AttrsHelper mAttrsHelper;

    public SkinnableRecyclerView(Context context) {
        this(context, null);
    }

    public SkinnableRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SkinnableRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mAttrsHelper = new AttrsHelper();

        TypedArray a;
        a = context.obtainStyledAttributes(attrs,
                R.styleable.SkinnableView,
                defStyleAttr, 0);
        mAttrsHelper.storeAttributeResource(a, R.styleable.SkinnableView);
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

    }

    @Override
    public boolean isSkinnable() {
        return true;
    }

    @Override
    public void setSkinnable(boolean skinnable) {
    }
}
