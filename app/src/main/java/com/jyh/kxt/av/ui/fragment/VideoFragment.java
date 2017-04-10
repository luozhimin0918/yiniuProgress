package com.jyh.kxt.av.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import com.flyco.tablayout.SlidingTabLayout;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.BaseFragmentAdapter;
import com.jyh.kxt.base.constant.IntentConstant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Mr'Dai on 2017/4/10.
 * 视听-视听
 */

public class VideoFragment extends BaseFragment {

    @BindView(R.id.stl_navigation_bar) SlidingTabLayout stlNavigationBar;
    @BindView(R.id.vp_video_list) ViewPager vpVideoList;

    private String[] mTitles;
    private List<Fragment> fragmentList;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_video);

        mTitles = getResources().getStringArray(R.array.nav_video_test);

        generateListFragment();

        FragmentManager fm = getChildFragmentManager();
        vpVideoList.setAdapter(new BaseFragmentAdapter(fm, fragmentList));
        stlNavigationBar.setViewPager(vpVideoList, mTitles);
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
}
