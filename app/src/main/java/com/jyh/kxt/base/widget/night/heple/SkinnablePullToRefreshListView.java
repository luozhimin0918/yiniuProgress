package com.jyh.kxt.base.widget.night.heple;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.jyh.kxt.R;
import com.library.widget.handmark.PullToRefreshListView;


/**
 * Created by geminiwen on 16/6/15.
 */
public class SkinnablePullToRefreshListView extends PullToRefreshListView implements Skinnable {

    private AttrsHelper mAttrsHelper;

    public SkinnablePullToRefreshListView(Context context) {
        this(context, null);
    }

    public SkinnablePullToRefreshListView(Context context, AttributeSet attrs) {
        this(context, attrs, Mode.BOTH,0);
    }

    public SkinnablePullToRefreshListView(Context context, AttributeSet attrs, Mode mode, int defStyleAttr) {
        super(context, attrs, mode, defStyleAttr);
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

        mRefreshableView.setDivider(new ColorDrawable(ContextCompat.getColor(getContext(), R.color.line_color)));
        mRefreshableView.setDividerHeight(1);
    }

    @Override
    public boolean isSkinnable() {
        return true;
    }

    @Override
    public void setSkinnable(boolean skinnable) {
    }
}
