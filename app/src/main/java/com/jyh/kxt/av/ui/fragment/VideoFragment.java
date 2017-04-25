package com.jyh.kxt.av.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jyh.kxt.R;
import com.jyh.kxt.av.json.VideoNavJson;
import com.jyh.kxt.av.presenter.VideoPresenter;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.BaseFragmentAdapter;
import com.jyh.kxt.base.constant.IntentConstant;
import com.library.base.LibActivity;
import com.library.bean.EventBusClass;
import com.library.widget.PageLoadLayout;
import com.library.widget.tablayout.SlidingTabLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Mr'Dai on 2017/4/10.
 * 视听-视听
 */

public class VideoFragment extends BaseFragment implements PageLoadLayout.OnAfreshLoadListener {

    @BindView(R.id.stl_navigation_bar) SlidingTabLayout stlNavigationBar;
    @BindView(R.id.vp_video_list) ViewPager vpVideoList;
    @BindView(R.id.pl_rootView) public PageLoadLayout plRootView;

    private VideoPresenter videoPresenter;

    private List<Fragment> fragmentList;
    private String[] tabs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            EventBus.getDefault().register(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_video);

        videoPresenter = new VideoPresenter(this);

        plRootView.setOnAfreshLoadListener(this);

        videoPresenter.init();

        videoPresenter.addOnPageChangeListener(vpVideoList);
    }

    @OnClick(R.id.iv_more)
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_more:
                videoPresenter.more(tabs);
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

    public void ininTab(List<VideoNavJson> videoNavJsons) {
        int size = videoNavJsons.size();
        tabs = new String[size];
        generateListFragment(videoNavJsons);

        stlNavigationBar.setViewPager(vpVideoList, tabs);

        plRootView.loadOver();
    }

    private void generateListFragment(List<VideoNavJson> videoNavJsons) {
        fragmentList = new ArrayList<>();

        for (int i = 0; i < tabs.length; i++) {

            VideoNavJson videoNav = videoNavJsons.get(i);
            tabs[i] = videoNav.getName();
            VideoItemFragment itemFragment = new VideoItemFragment();
            Bundle args = new Bundle();
            args.putString(IntentConstant.NAME, tabs[i]);
            args.putString(IntentConstant.CODE, videoNav.getId());
            itemFragment.setArguments(args);

            fragmentList.add(itemFragment);
        }

        FragmentManager fm = getChildFragmentManager();
        vpVideoList.setAdapter(new BaseFragmentAdapter(fm, fragmentList));
    }

    @Override
    public void OnAfreshLoad() {
        videoPresenter.init();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            EventBus.getDefault().unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
