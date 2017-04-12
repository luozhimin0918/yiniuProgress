package com.jyh.kxt.index.ui.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.library.base.LibActivity;
import com.library.widget.tablayout.SegmentTabLayout;
import com.library.widget.tablayout.listener.OnTabSelectListener;

import butterknife.BindView;

/**
 * 首页-要闻
 */
public class HomeFragment extends BaseFragment implements OnTabSelectListener {

    @BindView(R.id.stl_navigation_bar) SegmentTabLayout stlNavigationBar;
    @BindView(R.id.vp_content) ViewPager vpContent;

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

    }

    @Override
    public void onTabSelect(int position) {

    }

    @Override
    public void onTabReselect(int position) {

    }
}
