package com.jyh.kxt.index.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.market.ui.MarketEditActivity;
import com.jyh.kxt.market.ui.fragment.MarketVPFragment;
import com.jyh.kxt.market.ui.fragment.OptionalFragment;
import com.library.base.LibActivity;
import com.library.widget.tablayout.SegmentTabLayout;
import com.library.widget.tablayout.listener.OnTabSelectListener;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 首页-行情
 */
public class MarketFragment extends BaseFragment implements OnTabSelectListener {

    @BindView(R.id.iv_left_icon) ImageView ivLeftIcon;
    @BindView(R.id.tv_right_icon1) TextView tvRightIcon1;

    @BindView(R.id.stl_navigation_bar) SegmentTabLayout stlNavigationBar;

    public BaseFragment marketVPFragment, optionalFragment;
    private BaseFragment lastFragment;

    public static MarketFragment newInstance() {
        MarketFragment fragment = new MarketFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_market, LibActivity.StatusBarColor.THEME1);

        String[] mTitles = getResources().getStringArray(R.array.nav_market);
        stlNavigationBar.setTabData(mTitles);
        stlNavigationBar.setOnTabSelectListener(this);

        onTabSelect(0);
    }

    private int position = 0;

    @Override
    public void onTabSelect(int position) {
        BaseFragment currentFragment;
        if (position == 0) {
            tvRightIcon1.setText("");
            tvRightIcon1.setBackgroundResource(R.mipmap.icon_search);
            currentFragment = marketVPFragment = marketVPFragment == null ? new MarketVPFragment() : marketVPFragment;
        } else {
            tvRightIcon1.setText("编辑");
            tvRightIcon1.setBackground(null);
            currentFragment = optionalFragment = optionalFragment == null ? new OptionalFragment() : optionalFragment;
        }
        this.position = position;
        replaceFragment(currentFragment);
        lastFragment = currentFragment;
    }

    public int getTabSelectPosition() {
        return position;
    }

    @Override
    public void onTabReselect(int position) {

    }

    @OnClick(R.id.tv_right_icon1)
    public void onClickRightTop(View view) {
        if (position == 1) {//如果是行情 - 编辑
            Intent marketEditIntent = new Intent(MarketFragment.this.getContext(), MarketEditActivity.class);
            startActivity(marketEditIntent);
        }
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




}
