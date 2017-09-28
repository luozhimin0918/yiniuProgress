package com.jyh.kxt.trading.ui.fragment;

import android.content.Intent;
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
import com.jyh.kxt.datum.bean.DataGroup;
import com.jyh.kxt.explore.json.NewsNavJson;
import com.jyh.kxt.trading.presenter.ArticleFragmentPresenter;
import com.library.util.RegexValidateUtil;
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

public class ArticleFragment extends BaseFragment implements PageLoadLayout.OnAfreshLoadListener, ViewPager
        .OnPageChangeListener,
        OnTabSelectListener {
    @BindView(R.id.stl_navigation_bar) public SlidingTabLayout stlNavigationBar;
    @BindView(R.id.iv_more) ImageView ivMore;
    @BindView(R.id.vp_news_list) ViewPager vpNewsList;
    @BindView(R.id.pl_rootView) public PageLoadLayout plRootView;
    private ArticleFragmentPresenter presenter;

    private List<Fragment> fragmentList = new ArrayList<>();
    private String[] tabs;
    private BaseFragmentAdapter adapter;
    private String selTab;
    private List<NewsNavJson> newsNavJson;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_news);
        presenter = new ArticleFragmentPresenter(this);

        plRootView.setOnAfreshLoadListener(this);
        stlNavigationBar.setOnTabSelectListener(this);
//        DisplayMetrics screenDisplay = SystemUtil.getScreenDisplay(getContext());
//        stlNavigationBar.setTabWidth(SystemUtil.px2dp(getContext(), screenDisplay.widthPixels / 4));

        Bundle arguments = getArguments();
        if (arguments != null) {
            selTab = arguments.getString(IntentConstant.TAB);
        }
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
                if (fragment instanceof BaseFragment) {
                    ((BaseFragment) fragment).onChangeTheme();
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (presenter != null) {
            getQueue().cancelAll(presenter.getClass().getName());
        }
    }

    public void init(List<NewsNavJson> newsNavs) {
        this.newsNavJson = newsNavs;
        int size = newsNavs.size();
        tabs = new String[size];

        int index = 0;
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
            if (i == 0)
                itemFragment.setMain(true);
            else
                itemFragment.setMain(false);
            fragmentList.add(itemFragment);

            if (!RegexValidateUtil.isEmpty(selTab)) {
                if (selTab.equals(newsNavJson.getId())) {
                    index = i;
                }
            }

        }

        FragmentManager childFragmentManager = getChildFragmentManager();
        adapter = new BaseFragmentAdapter(childFragmentManager, fragmentList);
        vpNewsList.setAdapter(adapter);
        stlNavigationBar.setViewPager(vpNewsList, tabs);
        presenter.addOnPageChangeListener(vpNewsList);
        plRootView.loadOver();

        vpNewsList.setCurrentItem(index);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (requestCode == 100) {
            stlNavigationBar.setCurrentTab(data.getIntExtra(IntentConstant.INDEX, presenter.index));
        } else {
            if (fragmentList != null) {
                for (Fragment fragment : fragmentList) {
                    fragment.onActivityResult(requestCode, resultCode, data);
                }
            }
        }
    }

    public String[] getTabs() {
        return tabs;
    }

    public void setSelTab(String selTab) {
        this.selTab = selTab;
        int size = fragmentList.size();
        if (tabs != null && tabs.length > 0 && size > 0) {
            for (int i = 0; i < size; i++) {
                NewsNavJson news = newsNavJson.get(i);
                if (!RegexValidateUtil.isEmpty(selTab)) {
                    if (selTab.equals(news.getId())) {
                        vpNewsList.setCurrentItem(i);
                    }
                }

            }
        }
    }

}
