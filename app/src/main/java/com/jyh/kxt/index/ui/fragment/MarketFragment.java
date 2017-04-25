package com.jyh.kxt.index.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.ImageView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.market.ui.fragment.MarketVPFragment;
import com.jyh.kxt.market.ui.fragment.OptionalFragment;
import com.library.base.LibActivity;
import com.library.widget.tablayout.SegmentTabLayout;
import com.library.widget.tablayout.listener.OnTabSelectListener;

import butterknife.BindView;

/**
 * 首页-行情
 */
public class MarketFragment extends BaseFragment implements OnTabSelectListener {

    @BindView(R.id.iv_left_icon) ImageView ivLeftIcon;
    @BindView(R.id.iv_right_icon2) ImageView ivRightIcon2;
    @BindView(R.id.iv_right_icon1) ImageView ivRightIcon1;

    @BindView(R.id.stl_navigation_bar) SegmentTabLayout stlNavigationBar;

    private BaseFragment marketVPFragment, optionalFragment;
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

    @Override
    public void onTabSelect(int position) {
        BaseFragment currentFragment;
        if (position == 0) {
            currentFragment = marketVPFragment = marketVPFragment == null ? new MarketVPFragment() : marketVPFragment;
        } else {
            currentFragment = optionalFragment = optionalFragment == null ? new OptionalFragment() : optionalFragment;
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
}
