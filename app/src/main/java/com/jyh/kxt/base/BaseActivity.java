package com.jyh.kxt.base;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
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


/**
 * Created by Mr'Dai on 2017/2/22.
 */

public class BaseActivity extends LibActivity implements IBaseView {

    //跳转Activity 策略
    private int intentAnimation = 0;
    //动画退出
    private boolean isAnimationFinish;

    private PopupWindow waitPopup;
    private SkinnableViewInflater mSkinnableViewInflater;
    private SkinnableCallback mSkinnableCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtil.addActivityToThemeCache(this);

        Boolean isNight = SPUtils.getBoolean(this, SpConstant.SETTING_DAY_NIGHT);
        if (isNight)
            setDayNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else
            setDayNightMode(AppCompatDelegate.MODE_NIGHT_NO);
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

    public void setDayNightMode(@AppCompatDelegate.NightMode int nightMode) {
        final boolean isPost21 = Build.VERSION.SDK_INT >= 21;

        if (mSkinnableCallback != null) {
            mSkinnableCallback.beforeApplyDayNight();
        }
        getDelegate().setLocalNightMode(nightMode);

        if (isPost21) {
            applyDayNightForStatusBar();
        }

        View decorView = getWindow().getDecorView();
        applyDayNightForView(decorView);

        if (mSkinnableCallback != null) {
            mSkinnableCallback.onApplyDayNight();
        }
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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void applyDayNightForStatusBar() {
        TypedArray a = getTheme().obtainStyledAttributes(0, new int[]{
                android.R.attr.statusBarColor
        });
        int color = a.getColor(0, 0);
        getWindow().setStatusBarColor(color);
        a.recycle();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ThemeUtil.removeActivityFromThemeCache(this);
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

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        addOverridePendingTransition(0);
    }


    public void finish(boolean isAnimationFinish) {
        this.isAnimationFinish = isAnimationFinish;
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        if (isAnimationFinish) {
            intentAnimation = -1;
            addOverridePendingTransition(1);
        } else {
            addOverridePendingTransition(1);
        }
    }

    public void setIntentAnimation(int intentAnimation) {
        this.intentAnimation = intentAnimation;
    }

    /**
     * @param from 0 表示StartActivity动画,进入
     *             1 表示finish动画,退出
     */
    private void addOverridePendingTransition(int from) {
        switch (intentAnimation) {
            case -1://动画滑动退出的时候
                overridePendingTransition(0, from == 1 ? R.anim.activity_out1 : 0);
                break;
            case 0:

                break;
            case 1:
                overridePendingTransition(from == 0 ? R.anim.activity_in : 0, from == 1 ? R.anim.activity_out : 0);
                break;
            case 2:

                break;
        }
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

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public interface SkinnableCallback {
        void beforeApplyDayNight();

        void onApplyDayNight();
    }
}
