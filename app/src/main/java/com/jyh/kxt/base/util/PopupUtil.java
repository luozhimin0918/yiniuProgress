package com.jyh.kxt.base.util;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 * Created by Mr'Dai on 2017/4/5.
 */

public class PopupUtil extends PopupWindow {

    private Activity mActivity;
    private Config config;
    private OnDismissListener onDismissListener;

    public interface OnDismissListener {
        void onDismiss();
    }

    public PopupUtil(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public PopupUtil(int width, int height, Activity mActivity) {
        super(width, height);
        this.mActivity = mActivity;
    }

    public PopupUtil(View contentView, int width, int height, Activity mActivity) {
        super(contentView, width, height);
        this.mActivity = mActivity;
    }

    public PopupUtil(View contentView, int width, int height, boolean focusable, Activity mActivity) {
        super(contentView, width, height, focusable);
        this.mActivity = mActivity;
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    public View createPopupView(int resLayout) {
        LayoutInflater mInflater = LayoutInflater.from(mActivity);
        View contentView = mInflater.inflate(resLayout, null);
        setContentView(contentView);
        return contentView;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    private void applyConfig() {
        if (config != null) {

            setWidth(config.width);
            setHeight(config.height);

            setFocusable(config.outsideTouchable);
            setOutsideTouchable(config.outsideTouchable);

            ColorDrawable dw = new ColorDrawable(config.bgColor);
            setBackgroundDrawable(dw);

            setAnimationStyle(config.animationStyle);
            darkenBackground(config.alpha);

            if (config.softInputMode != 0) {
                setSoftInputMode(config.softInputMode);
            }
            if (config.softInputMode2 != 0) {
                setSoftInputMode(config.softInputMode2);
            }


            setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    darkenBackground(1.0f);
                    if (onDismissListener != null) {
                        onDismissListener.onDismiss();
                    }
                }
            });
        }
    }

    private void darkenBackground(float alpha) {
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = alpha;
        mActivity.getWindow().setAttributes(lp);
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        applyConfig();
        super.showAtLocation(parent, gravity, x, y);
    }

    public static class Config {
        public boolean outsideTouchable;
        public int animationStyle;

        public int bgColor;
        public float alpha;

        public int width;
        public int height;
        public int softInputMode;
        public int softInputMode2;
    }
}
