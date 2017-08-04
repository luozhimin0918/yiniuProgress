package com.jyh.kxt.base.widget;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.jyh.kxt.R;

/**
 * Created by Mr'Dai on 2017/8/3.
 */

public class SimplePopupWindow extends PopupWindow {

    private Activity mActivity;

    public SimplePopupWindow(Activity mActivity) {
        this.mActivity = mActivity;
        initPopupWindow();
    }

    public interface SimplePopupListener {
        void onCreateView(View popupView);

        void onDismiss();
    }


    private SimplePopupListener simplePopupListener;

    public void setSimplePopupListener(SimplePopupListener simplePopupListener) {
        this.simplePopupListener = simplePopupListener;
    }

    public void initPopupWindow() {
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        setFocusable(true);
        setOutsideTouchable(true);

        ColorDrawable dw = new ColorDrawable(0X00000000);
        setBackgroundDrawable(dw);

        setAnimationStyle(R.style.PopupWindow_Style2);
        darkenBackground(mActivity, 0.5f);

        setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                darkenBackground(mActivity, 1.0f);

                if (simplePopupListener != null) {
                    simplePopupListener.onDismiss();
                }
            }
        });

    }


    public void show(int layoutRes) {
        View popupView = LayoutInflater.from(mActivity).inflate(layoutRes, null);
        if (simplePopupListener != null) {
            simplePopupListener.onCreateView(popupView);
        }
        setContentView(popupView);
        showAtLocation(popupView, Gravity.BOTTOM, 0, 0);
    }

    private void darkenBackground(Activity mActivity, float alpha) {
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = alpha;
        mActivity.getWindow().setAttributes(lp);
    }
}
