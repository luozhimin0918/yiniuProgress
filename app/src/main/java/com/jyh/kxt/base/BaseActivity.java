package com.jyh.kxt.base;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.VectorEnabledTintResources;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.bumptech.glide.Glide;
import com.jyh.kxt.R;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.base.widget.night.ThemeUtil;
import com.jyh.kxt.base.widget.night.heple.Skinnable;
import com.jyh.kxt.base.widget.night.skinnable.SkinnableViewInflater;
import com.library.base.LibActivity;
import com.library.util.SPUtils;
import com.library.util.SystemUtil;

import java.util.List;


/**
 * Created by Mr'Dai on 2017/2/22.
 */

public class BaseActivity extends LibActivity implements IBaseView {

    private PopupWindow waitPopup;

    private SkinnableViewInflater mSkinnableViewInflater;
    private SkinnableCallback mSkinnableCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ThemeUtil.addActivityToThemeCache(this);
        Boolean isNight = SPUtils.getBoolean(this, SpConstant.SETTING_DAY_NIGHT);
        if (isNight) {
            setDayNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            setDayNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }


    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        if (mSkinnableViewInflater == null) {
            mSkinnableViewInflater = new SkinnableViewInflater();
        }
        final boolean isPre21 = Build.VERSION.SDK_INT < 21;
        final boolean inheritContext = isPre21 && shouldInheritContext((ViewParent) parent);
        return mSkinnableViewInflater.createView(parent,
                name,
                context,
                attrs,
                inheritContext,
                isPre21, /* Only read android:theme pre-L (L+ handles this anyway) */
                true, /* Read read app:theme as a fallback at all times for legacy reasons */
                VectorEnabledTintResources.shouldBeUsed() /* Only tint wrap the context if enabled */
        );
    }

    @Override
    protected void onResume() {
        super.onResume();

        boolean isNightTheme = SPUtils.getBoolean(this, SpConstant.SETTING_DAY_NIGHT);
        boolean changeCurrentActionTheme = ThemeUtil.isChangeCurrentActionTheme(this);
        if (changeCurrentActionTheme) {
            if (isNightTheme) {
                setDayNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                setDayNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            ThemeUtil.changeCacheActionTheme(this);
        }
    }

    public void setDayNightMode(@AppCompatDelegate.NightMode int nightMode) {
        final boolean isPost21 = Build.VERSION.SDK_INT >= 21;

        if (mSkinnableCallback != null) {
            mSkinnableCallback.beforeApplyDayNight();
        }
        getDelegate().setLocalNightMode(nightMode);

        if (isPost21) {
        }

        View decorView = getWindow().getDecorView();
        applyDayNightForView(decorView);

        if (mSkinnableCallback != null) {
            mSkinnableCallback.onApplyDayNight();
        }

        View statusBar = decorView.findViewWithTag("statusBar");
        if (statusBar != null) {
            int bgColor = ContextCompat.getColor(this, statusBarColor.color);
            statusBar.setBackgroundColor(bgColor);
        }

        try {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            if (fragments != null) {
                for (Fragment fragment : fragments) {
                    if (fragment instanceof BaseFragment) {
                        BaseFragment baseFragment = (BaseFragment) fragment;
                        baseFragment.updateStatusBarColor();
                        baseFragment.onChangeTheme();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        onChangeTheme();
    }

    private void applyDayNightForView(View view) {
        if (view instanceof Skinnable && !"filter".equals(view.getTag())) {
            Skinnable skinnable = (Skinnable) view;
            if (skinnable.isSkinnable()) {
                skinnable.applyDayNight();
            }
        }
        if (view instanceof ViewGroup && !"filter".equals(view.getTag())) {
            ViewGroup parent = (ViewGroup) view;
            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                applyDayNightForView(parent.getChildAt(i));
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ThemeUtil.removeActivityFromThemeCache(this);
        Glide.get(this).clearMemory();
    }

    @Override
    public RequestQueue getQueue() {
        return mQueue;
    }

    @Override
    public void showWaitDialog(final String tipInfo) {
        View waitView = LayoutInflater.from(this).inflate(R.layout.dialog_wait, null);
        ImageView ivProgress = (ImageView) waitView.findViewById(R.id.iv_progress);
        TextView tvDesc = (TextView) waitView.findViewById(R.id.tv_desc);

        if (!TextUtils.isEmpty(tipInfo)) {
            tvDesc.setText(tipInfo);
        } else {
            tvDesc.setVisibility(View.GONE);
        }

        Glide.with(this).load(R.mipmap.loading).asGif().into(ivProgress);

        waitPopup = new PopupWindow(waitView);

        waitPopup.setWidth(SystemUtil.dp2px(this, 80));
        waitPopup.setHeight(SystemUtil.dp2px(this, 80));

        waitPopup.setFocusable(true);
        waitPopup.setOutsideTouchable(true);

        ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
        waitPopup.setBackgroundDrawable(dw);

        waitPopup.setAnimationStyle(R.style.PopupWindow_Style1);

        waitPopup.showAtLocation(waitView, Gravity.CENTER, 0, 0);
    }

    @Override
    public void dismissWaitDialog() {
        if (waitPopup != null && waitPopup.isShowing() && !isFinishing()) {
            waitPopup.dismiss();
        }
    }

    protected void onChangeTheme() {

    }

    @Override
    public Context getContext() {
        return this;
    }


    private boolean shouldInheritContext(ViewParent parent) {
        if (parent == null) {
            // The initial parent is null so just return false
            return false;
        }
        final View windowDecor = getWindow().getDecorView();
        while (true) {
            if (parent == null) {
                return true;
            } else if (parent == windowDecor ||
                    !(parent instanceof View) ||
                    ViewCompat.isAttachedToWindow((View) parent)) {
                return false;
            }
            parent = parent.getParent();
        }
    }

    public interface SkinnableCallback {
        void beforeApplyDayNight();

        void onApplyDayNight();
    }
}
