package com.jyh.kxt.main.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.BaseFragmentAdapter;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.index.ui.ClassifyActivity;
import com.jyh.kxt.main.presenter.NewsPresenter;
import com.library.widget.tablayout.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 项目名:Kxt
 * 类描述: 要闻Fragment
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/12.
 */

public class NewsFragment extends BaseFragment {

    private NewsPresenter newsPresenter;

    @BindView(R.id.stl_navigation_bar) SlidingTabLayout stlNavigationBar;
    @BindView(R.id.vp_news_list) ViewPager vpNewsList;

    private List<Fragment> fragmentList;
    private String[] tabs;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_news);
        newsPresenter = new NewsPresenter(this);
        tabs = getResources().getStringArray(R.array.nav_video_test);

        generateListFragment();

        vpNewsList.setAdapter(new BaseFragmentAdapter(getChildFragmentManager(), fragmentList));
        stlNavigationBar.setViewPager(vpNewsList, tabs);

        newsPresenter.addOnPageChangeListener(vpNewsList);
    }

    private void generateListFragment() {
        fragmentList = new ArrayList<>();

        for (int i = 0; i < tabs.length; i++) {
            NewsItemFragment itemFragment = new NewsItemFragment();
            Bundle args = new Bundle();
            args.putString(IntentConstant.NAME, tabs[i]);
            itemFragment.setArguments(args);

            fragmentList.add(itemFragment);
        }
    }

    @OnClick(R.id.iv_more)
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_more:
                newsPresenter.more();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IntentConstant.REQUESTCODE1 && resultCode == Activity.RESULT_OK) {
            newsPresenter.index = data.getIntExtra(IntentConstant.INDEX, 0);
            stlNavigationBar.setCurrentTab(newsPresenter.index);
        }
    }
}
