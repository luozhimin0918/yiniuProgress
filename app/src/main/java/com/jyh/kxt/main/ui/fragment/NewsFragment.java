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
import com.jyh.kxt.main.json.NewsJson;
import com.jyh.kxt.main.json.NewsNavJson;
import com.jyh.kxt.main.json.QuotesJson;
import com.jyh.kxt.main.json.SlideJson;
import com.jyh.kxt.main.presenter.NewsPresenter;
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

        newsPresenter.init();
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

    /**
     * 初始化首页内容
     *
     * @param newsNavs
     * @param slides
     * @param shortcuts
     * @param quotes
     * @param ads
     * @param news
     */
    public void initView(List<NewsNavJson> newsNavs, List<SlideJson> slides, List<SlideJson> shortcuts, List<QuotesJson> quotes,
                         List<SlideJson> ads, List<NewsJson> news) {
        if (newsNavs == null) return;
        int size = newsNavs.size();
        tabs = new String[size];
        fragmentList = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            tabs[i] = newsNavs.get(i).getName();
            NewsItemFragment itemFragment = new NewsItemFragment();
            Bundle args = new Bundle();
            args.putString(IntentConstant.NAME, tabs[i]);
            args.putInt(IntentConstant.INDEX, i);
            if (i == 0) {
                //首页
                args.putParcelableArrayList(IntentConstant.NEWS_SLIDE, (ArrayList<? extends Parcelable>) slides);
                args.putParcelableArrayList(IntentConstant.NEWS_SHORTCUTS, (ArrayList<? extends Parcelable>) shortcuts);
                args.putParcelableArrayList(IntentConstant.NEWS_QUOTES, (ArrayList<? extends Parcelable>) quotes);
                args.putParcelableArrayList(IntentConstant.NEWS_ADS, (ArrayList<? extends Parcelable>) ads);
                args.putParcelableArrayList(IntentConstant.NEWS_NEWS, (ArrayList<? extends Parcelable>) news);
            }
            itemFragment.setArguments(args);
            fragmentList.add(itemFragment);
        }

        vpNewsList.setAdapter(new BaseFragmentAdapter(getChildFragmentManager(), fragmentList));
        stlNavigationBar.setViewPager(vpNewsList, tabs);
        newsPresenter.addOnPageChangeListener(vpNewsList);
    }
}
