package com.jyh.kxt.main.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.BaseFragmentAdapter;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.main.json.AdJson;
import com.jyh.kxt.main.json.NewsJson;
import com.jyh.kxt.main.json.NewsNavJson;
import com.jyh.kxt.main.json.SlideJson;
import com.jyh.kxt.main.presenter.NewsPresenter;
import com.jyh.kxt.market.bean.MarketItemBean;
import com.library.widget.PageLoadLayout;
import com.library.widget.tablayout.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 项目名:Kxt
 * 类描述: 要闻Fragment
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/12.
 */

public class NewsFragment extends BaseFragment implements PageLoadLayout.OnAfreshLoadListener {

    private NewsPresenter newsPresenter;

    @BindView(R.id.stl_navigation_bar) SlidingTabLayout stlNavigationBar;
    @BindView(R.id.pl_rootView) public PageLoadLayout plRootView;
    @BindView(R.id.vp_news_list) ViewPager vpNewsList;

    private List<Fragment> fragmentList;
    private String[] tabs;
    private BaseFragmentAdapter adapter;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_news);
        newsPresenter = new NewsPresenter(this);

        newsPresenter.init();

        plRootView.setOnAfreshLoadListener(this);
    }

    @OnClick(R.id.iv_more)
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_more:
                newsPresenter.more(tabs);
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

    /**
     * 初始化首页内容
     *
     * @param newsNavs
     * @param slides
     * @param shortcuts
     * @param quotes
     * @param ad
     * @param news
     * @param list
     */
    public void initView(List<NewsNavJson> newsNavs, List<SlideJson> slides, List<SlideJson> shortcuts, List<MarketItemBean> quotes,
                         AdJson ad, List<NewsJson> news, ArrayList<String> list) {
        if (newsNavs == null) return;
        int size = newsNavs.size();
        tabs = new String[size];
        fragmentList = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            tabs[i] = newsNavs.get(i).getName();
            NewsItemFragment itemFragment = new NewsItemFragment();
            Bundle args = new Bundle();
            args.putString(IntentConstant.NAME, tabs[i]);
            args.putString(IntentConstant.CODE, newsNavs.get(i).getCode());
            args.putInt(IntentConstant.INDEX, i);
            if (i == 0) {
                //首页
                args.putParcelableArrayList(IntentConstant.NEWS_SLIDE, (ArrayList<? extends Parcelable>) slides);
                args.putParcelableArrayList(IntentConstant.NEWS_SHORTCUTS, (ArrayList<? extends Parcelable>) shortcuts);
                args.putParcelableArrayList(IntentConstant.NEWS_QUOTES, (ArrayList<? extends Parcelable>) quotes);
                args.putParcelable(IntentConstant.NEWS_ADS, ad);
                args.putParcelableArrayList(IntentConstant.NEWS_NEWS, (ArrayList<? extends Parcelable>) news);
                args.putStringArrayList(IntentConstant.NEWS_LIST, list);
            }
            itemFragment.setArguments(args);
            fragmentList.add(itemFragment);
        }

        adapter = new BaseFragmentAdapter(getChildFragmentManager(), fragmentList);
        vpNewsList.setAdapter(adapter);
        stlNavigationBar.setViewPager(vpNewsList, tabs);
        newsPresenter.addOnPageChangeListener(vpNewsList);
        plRootView.loadOver();
    }

    @Override
    public void OnAfreshLoad() {
        if (fragmentList != null && adapter != null) {
            fragmentList.clear();
            adapter.notifyDataSetChanged();
        }
        plRootView.loadWait();
        newsPresenter.reLoad();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getQueue().cancelAll(newsPresenter.getClass().getName());
    }

    @Override
    public void onChangeTheme() {
        super.onChangeTheme();
        if(fragmentList !=null)
            for (Fragment fragment : fragmentList) {
                if(fragment instanceof BaseFragment)
                    ((BaseFragment) fragment).onChangeTheme();
            }
        if (stlNavigationBar != null) stlNavigationBar.notifyDataSetChanged();
    }
}
