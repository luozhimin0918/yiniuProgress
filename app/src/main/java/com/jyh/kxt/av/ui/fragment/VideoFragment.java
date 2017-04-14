package com.jyh.kxt.av.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.jyh.kxt.R;
import com.jyh.kxt.av.presenter.VideoPresenter;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.BaseFragmentAdapter;
import com.jyh.kxt.base.constant.IntentConstant;
import com.library.base.LibActivity;
import com.library.widget.tablayout.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Mr'Dai on 2017/4/10.
 * 视听-视听
 */

public class VideoFragment extends BaseFragment {

    @BindView(R.id.stl_navigation_bar) SlidingTabLayout stlNavigationBar;
    @BindView(R.id.vp_video_list) ViewPager vpVideoList;

    private VideoPresenter videoPresenter;

    private String[] mTitles;
    private List<Fragment> fragmentList;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_video, LibActivity.StatusBarColor.THEME1);

        videoPresenter = new VideoPresenter(this);

        mTitles = getResources().getStringArray(R.array.nav_video_test);

        generateListFragment();

        FragmentManager fm = getChildFragmentManager();
        vpVideoList.setAdapter(new BaseFragmentAdapter(fm, fragmentList));
        stlNavigationBar.setViewPager(vpVideoList, mTitles);

        videoPresenter.addOnPageChangeListener(vpVideoList);
    }

    private void generateListFragment() {
        fragmentList = new ArrayList<>();

        for (int i = 0; i < mTitles.length; i++) {
            VideoItemFragment itemFragment = new VideoItemFragment();
            Bundle args = new Bundle();
            args.putString(IntentConstant.NAME, mTitles[i]);
            itemFragment.setArguments(args);

            fragmentList.add(itemFragment);
        }
    }

    @OnClick(R.id.iv_more)
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_more:
                videoPresenter.more();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IntentConstant.REQUESTCODE1 && resultCode == Activity.RESULT_OK) {
            videoPresenter.index = data.getIntExtra(IntentConstant.INDEX, 0);
            stlNavigationBar.setCurrentTab(videoPresenter.index);
        }
    }
}
