package com.jyh.kxt.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.library.base.LibFragment;

/**
 * Created by Mr'Dai on 2017/3/28.
 */

public abstract class BaseFragment extends LibFragment implements IBaseView {

    private IBaseView iBaseView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Activity mParentActivity = getActivity();
        if (mParentActivity instanceof IBaseView) {
            iBaseView = (IBaseView) mParentActivity;
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }


    /**
     * 显示屏幕Dialog
     *
     * @param tipInfo
     */
    @Override
    public void showWaitDialog(String tipInfo) {
        iBaseView.showWaitDialog(tipInfo);
    }

    /**
     * 取消等待的Dialog
     */
    @Override
    public void dismissWaitDialog() {
        iBaseView.dismissWaitDialog();
    }

    /**
     * 得到Activity 对应的Queue
     *
     * @return
     */
    @Override
    public RequestQueue getQueue() {
        return iBaseView.getQueue();
    }
}
