package com.jyh.kxt.base;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.jyh.kxt.base.annotation.BindActivity;

import java.lang.reflect.Field;

/**
 * Created by Mr'Dai on 2017/2/23.
 */

public class BasePresenter {
    protected IBaseView iBaseView;
    protected RequestQueue mQueue;
    protected Context mContext;

    public BasePresenter(IBaseView iBaseView) {
        this.iBaseView = iBaseView;
        injectAnnotation();
        mQueue = iBaseView.getQueue();
        mContext = iBaseView.getContext();
    }

    public void showWaitDialog(String tips) {
        iBaseView.showWaitDialog(tips);
    }

    public void showWaitDialog() {
        showWaitDialog(null);
    }

    public void dismissWaitDialog() {
        iBaseView.dismissWaitDialog();
    }


    private void injectAnnotation() {
        try {
            Field[] fields = this.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                boolean fieldFlag = fields[i].isAnnotationPresent(BindActivity.class);
                if (fieldFlag) {
                    Field field = fields[i];
                    field.setAccessible(true);
                    field.set(this, iBaseView);
                    field.setAccessible(false);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
