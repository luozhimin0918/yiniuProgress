package com.jyh.kxt.base;

import android.view.ViewGroup;

import com.jyh.kxt.base.widget.SlidingBackLayout;

/**
 * Created by Mr'Dai on 2017/3/3.
 */

public class BaseSwipeBackActivity extends BaseActivity {

    @Override
    protected void setContentView(int layoutResID, StatusBarColor statusBarColor) {
        super.setContentView(layoutResID, statusBarColor);
        extensionSwipeBackLayout();
    }

    @Override
    protected void setBindingView(int layoutResID, StatusBarColor statusBarColor) {
        super.setBindingView(layoutResID, statusBarColor);
        extensionSwipeBackLayout();
    }

    private void extensionSwipeBackLayout() {
        int matchParent = ViewGroup.LayoutParams.MATCH_PARENT;
        SlidingBackLayout slidingBackLayout = new SlidingBackLayout(getContext());
        slidingBackLayout.setLayoutParams(new ViewGroup.LayoutParams(matchParent, matchParent));

        ViewGroup replaceLayout = (ViewGroup) findViewById(android.R.id.content);
        ViewGroup parent = (ViewGroup) replaceLayout.getParent();
        parent.removeView(replaceLayout);
        slidingBackLayout.addView(replaceLayout);
        parent.addView(slidingBackLayout);
    }
}