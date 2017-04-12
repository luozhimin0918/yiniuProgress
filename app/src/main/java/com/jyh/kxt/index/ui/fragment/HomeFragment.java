package com.jyh.kxt.index.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.main.ui.fragment.FlashFragment;
import com.jyh.kxt.main.ui.fragment.NewsFragment;
import com.library.base.LibActivity;
import com.library.widget.tablayout.SegmentTabLayout;
import com.library.widget.tablayout.listener.OnTabSelectListener;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 首页-要闻
 */
public class HomeFragment extends BaseFragment implements OnTabSelectListener {

    @BindView(R.id.stl_navigation_bar) SegmentTabLayout stlNavigationBar;
    @BindView(R.id.fl_content) FrameLayout flContent;
    @BindView(R.id.iv_left_icon) ImageView ivLeftIcon;
    @BindView(R.id.iv_right_icon2) ImageView ivRightIcon2;
    @BindView(R.id.iv_right_icon1) ImageView ivRightIcon1;

    private BaseFragment lastFragment;
    private BaseFragment newsFragment;
    private BaseFragment flashFrament;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_home, LibActivity.StatusBarColor.THEME1);

        String[] mTitles = getResources().getStringArray(R.array.nav_index);
        stlNavigationBar.setTabData(mTitles);
        stlNavigationBar.setOnTabSelectListener(this);

        ivRightIcon1.setImageResource(R.mipmap.icon_search);

        onTabSelect(0);
    }

    @Override
    public void onTabSelect(int position) {
        BaseFragment currentFragment;
        if (position == 0) {
            currentFragment = newsFragment = newsFragment == null ? new NewsFragment() : newsFragment;
        } else {
            currentFragment = flashFrament = flashFrament == null ? new FlashFragment() : flashFrament;
        }
        replaceFragment(currentFragment);
        lastFragment = currentFragment;
    }

    @Override
    public void onTabReselect(int position) {

    }

    private void replaceFragment(BaseFragment toFragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

        if (lastFragment != null) {
            transaction.hide(lastFragment);
        }

        if (toFragment.isAdded()) {
            transaction.show(toFragment);
        } else {
            transaction.add(R.id.fl_content, toFragment);
        }

        transaction.commitAllowingStateLoss();
    }

    @OnClick({R.id.iv_left_icon, R.id.iv_right_icon2, R.id.iv_right_icon1})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left_icon:
                //个人中心
                break;
            case R.id.iv_right_icon2:
                break;
            case R.id.iv_right_icon1:
                //搜索
                break;
        }
    }
}
