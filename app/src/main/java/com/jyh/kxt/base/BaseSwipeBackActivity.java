package com.jyh.kxt.base;

import android.graphics.Color;
import android.os.Build;
import android.view.ViewGroup;

import com.jyh.kxt.base.widget.SlidingBackLayout;

/**
 * Created by Mr'Dai on 2017/3/3.
 */

public class BaseSwipeBackActivity extends BaseActivity {

    @Override
    protected void setContentView(int layoutResID, StatusBarColor statusBarColor) {
        super.setContentView(layoutResID, statusBarColor);
        extensionSwipeBackLayout(statusBarColor);
    }

    @Override
    protected void setBindingView(int layoutResID, StatusBarColor statusBarColor) {
        super.setBindingView(layoutResID, statusBarColor);
        extensionSwipeBackLayout(statusBarColor);
    }

    private void extensionSwipeBackLayout(StatusBarColor statusBarColor) {
        int matchParent = ViewGroup.LayoutParams.MATCH_PARENT;
        SlidingBackLayout slidingBackLayout = new SlidingBackLayout(getContext());
        slidingBackLayout.setLayoutParams(new ViewGroup.LayoutParams(matchParent, matchParent));

        if (statusBarColor.color != -1 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ViewGroup replaceLayout = (ViewGroup) findViewById(com.library.R.id.contents);

            ViewGroup parent = (ViewGroup) replaceLayout.getParent();
            parent.removeView(replaceLayout);
            slidingBackLayout.addView(replaceLayout);
            parent.addView(slidingBackLayout);
        } else {
            ViewGroup contentView = (ViewGroup) findViewById(android.R.id.content);
            ViewGroup parent = (ViewGroup) contentView.getParent();
            parent.removeView(contentView);
            slidingBackLayout.addView(contentView);
            parent.addView(slidingBackLayout);
        }
    }
}
