package com.jyh.kxt.index.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.jyh.kxt.R;
import com.jyh.kxt.av.ui.fragment.RankFragment;
import com.jyh.kxt.av.ui.fragment.VideoFragment;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.BaseFragmentAdapter;
import com.library.base.LibActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 首页-视听
 */
public class AvFragment extends BaseFragment implements OnTabSelectListener {

    public static AvFragment newInstance() {
        AvFragment fragment = new AvFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @BindView(R.id.stl_navigation_bar) SegmentTabLayout stlNavigationBar;
    @BindView(R.id.vp_audio_visual) ViewPager vpAudioVisual;

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
    }

    @Override
    public void onTabSelect(int position) {
        vpAudioVisual.setCurrentItem(position);
    }

    @Override
    public void onTabReselect(int position) {

    }
}
