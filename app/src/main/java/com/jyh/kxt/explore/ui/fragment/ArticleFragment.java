package com.jyh.kxt.explore.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.BaseFragmentAdapter;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.explore.json.NewsNavJson;
import com.jyh.kxt.explore.presenter.ArticleFragmentPresenter;
import com.jyh.kxt.main.ui.fragment.NewsItemFragment;
import com.library.widget.PageLoadLayout;
import com.library.widget.tablayout.SlidingTabLayout;
import com.library.widget.tablayout.listener.OnTabSelectListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 项目名:Kxt
 * 类描述:名家专栏-文章
 * 创建人:苟蒙蒙
 * 创建日期:2017/6/9.
 */

public class ArticleFragment extends BaseFragment implements PageLoadLayout.OnAfreshLoadListener, ViewPager.OnPageChangeListener,
        OnTabSelectListener {
    @BindView(R.id.stl_navigation_bar) SlidingTabLayout stlNavigationBar;
    @BindView(R.id.iv_more) ImageView ivMore;
    @BindView(R.id.vp_news_list) ViewPager vpNewsList;
    @BindView(R.id.pl_rootView) public PageLoadLayout plRootView;
    private ArticleFragmentPresenter presenter;

    private List<Fragment> fragmentList = new ArrayList<>();
    private String[] tabs;
    private BaseFragmentAdapter adapter;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_news);
        presenter = new ArticleFragmentPresenter(this);

        plRootView.setOnAfreshLoadListener(this);
        stlNavigationBar.setOnTabSelectListener(this);

        presenter.init();
    }

    @OnClick(R.id.iv_more)
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_more:
                presenter.more(tabs);
                break;
        }
    }

    @Override
    public void OnAfreshLoad() {
        if (fragmentList != null && adapter != null) {
            fragmentList.clear();
            adapter.notifyDataSetChanged();
        }
        plRootView.loadWait();
        presenter.init();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onTabSelect(int position) {

    }

    @Override
    public void onTabReselect(int position) {

    }

    @Override
    public void onChangeTheme() {
        super.onChangeTheme();
        if (fragmentList != null) {
            for (Fragment fragment : fragmentList) {
                if (fragment instanceof BaseFragment)
                    ((BaseFragment) fragment).onChangeTheme();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (presenter != null)
            getQueue().cancelAll(presenter.getClass().getName());
    }

    public void init(List<NewsNavJson> newsNavs) {
        int size = newsNavs.size();
        tabs = new String[size];

        for (int i = 0; i < size; i++) {
            NewsNavJson newsNavJson = newsNavs.get(i);
            tabs[i] = newsNavJson.getName();
            ArticleItemFragment itemFragment = new ArticleItemFragment();
            Bundle args = new Bundle();
            args.putString(IntentConstant.NAME, tabs[i]);
            args.putString(IntentConstant.CODE, newsNavJson.getId());
            args.putString(IntentConstant.TYPE, newsNavJson.getType());
            args.putInt(IntentConstant.INDEX, i);
            itemFragment.setArguments(args);
            fragmentList.add(itemFragment);
        }

        FragmentManager childFragmentManager = getChildFragmentManager();
        adapter = new BaseFragmentAdapter(childFragmentManager, fragmentList);
        vpNewsList.setAdapter(adapter);
        stlNavigationBar.setViewPager(vpNewsList, tabs);
        presenter.addOnPageChangeListener(vpNewsList);
        plRootView.loadOver();

    }
}
