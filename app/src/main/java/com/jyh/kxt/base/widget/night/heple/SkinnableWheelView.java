package com.jyh.kxt.base.widget.night.heple;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.jyh.kxt.R;
import com.jyh.kxt.base.widget.pickerview.lib.WheelView;

/**
 * Created by geminiwen on 16/6/15.
 */
public class SkinnableWheelView extends WheelView implements Skinnable {

    private AttrsHelper mAttrsHelper;

    public SkinnableWheelView(Context context) {
        this(context, null);
    }

    public SkinnableWheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mAttrsHelper = new AttrsHelper();

        TypedArray a;
        a = context.obtainStyledAttributes(attrs,
                R.styleable.pickerview,
                0, 0);
        mAttrsHelper.storeAttributeResource(a, R.styleable.pickerview);
        a.recycle();
        a = context.obtainStyledAttributes(attrs,
                R.styleable.SkinnableView,
                0, 0);
        mAttrsHelper.storeAttributeResource(a, R.styleable.SkinnableView);
        a.recycle();
    }

    @Override
    public void applyDayNight() {
        Context context = getContext();
        int key;

        key = R.styleable.pickerview[R.styleable.pickerview_pickerview_dividerColor];
        Integer dividerColorResource = mAttrsHelper.getAttributeResource(key);
        if (dividerColorResource != null) {
            setDividerColor(ContextCompat.getColor(context, dividerColorResource));
        }

        key = R.styleable.pickerview[R.styleable.pickerview_pickerview_textColorCenter];
        Integer textCenterColorResource = mAttrsHelper.getAttributeResource(key);
        if (textCenterColorResource != null) {
            setTextColorCenter(ContextCompat.getColor(context, textCenterColorResource));
        }

        key = R.styleable.pickerview[R.styleable.pickerview_pickerview_textColorOut];
        Integer textOutColorResource = mAttrsHelper.getAttributeResource(key);
        if (textOutColorResource != null) {
            setTextColorOut(ContextCompat.getColor(context, textOutColorResource));
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
