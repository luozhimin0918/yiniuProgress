package com.jyh.kxt.trading.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.BaseFragmentAdapter;
import com.jyh.kxt.base.custom.RollDotViewPager;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.index.ui.AttentionActivity;
import com.jyh.kxt.trading.presenter.ViewpointPresenter;
import com.library.util.SystemUtil;
import com.library.widget.tablayout.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/7/26.
 */

public class ViewpointFragment extends BaseFragment implements OnScrollListener, ViewPager.OnPageChangeListener {

    @BindView(R.id.rdvp_content) RollDotViewPager rdvpContent;
    @BindView(R.id.stl_navigation_bar) SlidingTabLayout stlNavigationBar;
    @BindView(R.id.vp_content) ViewPager vpContent;
    @BindView(R.id.ll_headView) LinearLayout llHeadView;
    private ViewpointPresenter presenter;
    public boolean isListScrollAble;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_trading_viewpoint);
        presenter = new ViewpointPresenter(this);

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new ViewpointItemFragment());
        fragments.add(new ViewpointItemFragment());
        fragments.add(new ViewpointItemFragment());

        vpContent.setAdapter(new BaseFragmentAdapter(getChildFragmentManager(), fragments));
        vpContent.addOnPageChangeListener(this);

        stlNavigationBar.setViewPager(vpContent, new String[]{"精选", "全部", "关注"});
        stlNavigationBar.setCurrentTab(0);
        DisplayMetrics screenDisplay = SystemUtil.getScreenDisplay(getContext());
        stlNavigationBar.setTabWidth(SystemUtil.px2dp(getContext(), screenDisplay.widthPixels / 3));
    }

    @OnClick({R.id.tv_myattention, R.id.tv_all})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_myattention:
                //我的关注
                if (LoginUtils.isLogined(getContext())) {
                    startActivity(new Intent(getContext(), AttentionActivity.class));
                } else {
                    presenter.showLoginDialog();
                }
                break;
            case R.id.tv_all:
                // TODO: 2017/7/26 查看全部
                startActivity(new Intent(getContext(), AuthorListActivity.class));
                break;
        }
    }

    @Override
    public void onScroll(float x, float y) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) llHeadView
                .getLayoutParams();
        int boundHeight = -(llHeadView.getHeight() - stlNavigationBar.getHeight());
        if (y > 0) {
            layoutParams.topMargin = 0;
        } else {
            layoutParams.topMargin = boundHeight;
        }
        llHeadView.setLayoutParams(layoutParams);
    }

    public void resetHeadView() {
        isListScrollAble = false;
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) llHeadView
                .getLayoutParams();
        layoutParams.topMargin = 0;
        llHeadView.setLayoutParams(layoutParams);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        resetHeadView();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}

interface OnScrollListener {
    void onScroll(float x, float y);
}
