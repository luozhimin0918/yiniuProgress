package com.jyh.kxt.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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

    private boolean isUserVisible = false;
    private boolean isCreateView = false;
    private boolean isViewPagerToCurrentLocate = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Activity mParentActivity = getActivity();
        if (mParentActivity instanceof IBaseView) {
            iBaseView = (IBaseView) mParentActivity;
        }
        View view = super.onCreateView(inflater, container, savedInstanceState);
        isCreateView = true;
        if (isViewPagerToCurrentLocate && !isUserVisible) {
            userVisibleHint();
            isUserVisible = true;
        }
        return view;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isViewPagerToCurrentLocate = isVisibleToUser;
        if (isViewPagerToCurrentLocate) {
            onResume();

            if (isCreateView && !isUserVisible) {
                userVisibleHint();
                isUserVisible = true;
            }
        }
    }

    public void userVisibleHint() {

    }

    public void updateStatusBarColor() {
        if (replaceLayout != null) {
            View fragmentStatusBar = replaceLayout.findViewWithTag("fragment_statusBar");
            if (fragmentStatusBar != null) {
                int bgColor = ContextCompat.getColor(getContext(), statusBarColor.color);
                fragmentStatusBar.setBackgroundColor(bgColor);
            }
        }
    }

    public void onChangeTheme() {

    }

    @Override
    public void onResume() {
        super.onResume();
        onChangeTheme();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isUserVisible = false;
        isCreateView = false;
        isViewPagerToCurrentLocate = false;
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

    /**
     * 网络监听
     *
     * @param netMobile
     */
    public void onNetChange(int netMobile) {

    }
}
