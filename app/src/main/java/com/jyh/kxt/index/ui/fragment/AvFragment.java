package com.jyh.kxt.index.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.jyh.kxt.R;
import com.jyh.kxt.av.ui.fragment.RankFragment;
import com.jyh.kxt.av.ui.fragment.VideoFragment;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.BaseFragmentAdapter;
import com.library.base.LibActivity;
import com.library.widget.tablayout.SegmentTabLayout;
import com.library.widget.tablayout.listener.OnTabSelectListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 首页-视听
 */
public class AvFragment extends BaseFragment implements OnTabSelectListener, ViewPager.OnPageChangeListener {

    public static AvFragment newInstance() {
        AvFragment fragment = new AvFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @BindView(R.id.stl_navigation_bar) SegmentTabLayout stlNavigationBar;
    @BindView(R.id.vp_audio_visual)public ViewPager vpAudioVisual;
    @BindView(R.id.iv_left_icon) ImageView ivLeftIcon;
    @BindView(R.id.iv_right_icon2) ImageView ivRightIcon2;
    @BindView(R.id.iv_right_icon1) ImageView ivRightIcon1;

    private List<Fragment> fragmentList = new ArrayList<>();

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_av, LibActivity.StatusBarColor.THEME1);

        String[] mTitles = getResources().getStringArray(R.array.nav_audio_visual);
        stlNavigationBar.setTabData(mTitles);
        stlNavigationBar.setOnTabSelectListener(this);

        fragmentList.add(new VideoFragment());
        fragmentList.add(new RankFragment());

        FragmentManager fm = getChildFragmentManager();
        vpAudioVisual.setAdapter(new BaseFragmentAdapter(fm, fragmentList));

        vpAudioVisual.addOnPageChangeListener(this);
    }

    @Override
    public void onTabSelect(int position) {
        vpAudioVisual.setCurrentItem(position);
    }

    @Override
    public void onTabReselect(int position) {

    }

    @OnClick({R.id.iv_left_icon, R.id.iv_right_icon2, R.id.iv_right_icon1})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left_icon:

                break;
            case R.id.iv_right_icon2:

                break;
            case R.id.iv_right_icon1:

                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        stlNavigationBar.setCurrentTab(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : fragmentList) {
            if (fragment != null) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
}
