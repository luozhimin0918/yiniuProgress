package com.jyh.kxt.main.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.BaseFragmentAdapter;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.index.json.MainInitJson;
import com.jyh.kxt.index.ui.MainActivity;
import com.jyh.kxt.index.ui.WebActivity;
import com.jyh.kxt.main.json.AdJson;
import com.jyh.kxt.main.json.MainNewsContentJson;
import com.jyh.kxt.main.json.NewsNavJson;
import com.jyh.kxt.main.json.SlideJson;
import com.jyh.kxt.main.presenter.NewsPresenter;
import com.jyh.kxt.market.bean.MarketItemBean;
import com.library.util.SPUtils;
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

    @BindView(R.id.stl_navigation_bar) public SlidingTabLayout stlNavigationBar;
    @BindView(R.id.pl_rootView) public PageLoadLayout plRootView;
    @BindView(R.id.vp_news_list) ViewPager vpNewsList;

    private List<Fragment> fragmentList;
    private String[] tabs;
    private BaseFragmentAdapter adapter;
    private FragmentManager childFragmentManager;
    private String selTab;


    private View funView;

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
    public void initView(List<NewsNavJson> newsNavs, List<SlideJson> slides, List<SlideJson> shortcuts,
                         List<MarketItemBean> quotes,
                         AdJson ad, MainNewsContentJson news, ArrayList<String> list) {
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
                args.putParcelable(IntentConstant.NEWS_NEWS, news);
                args.putStringArrayList(IntentConstant.NEWS_LIST, list);
            }
            itemFragment.setArguments(args);
            fragmentList.add(itemFragment);
        }

        childFragmentManager = NewsFragment.this.getChildFragmentManager();
        adapter = new BaseFragmentAdapter(childFragmentManager, fragmentList);
        vpNewsList.setAdapter(adapter);
        stlNavigationBar.setViewPager(vpNewsList, tabs);
        newsPresenter.addOnPageChangeListener(vpNewsList);
        plRootView.loadOver();

    }

    /**
     * 切换的时候使用
     *
     * @param flActionBarFun
     */
    private Context mFragmentContext;
    private MainInitJson mainInitJson;

    public void onTabSelect(FrameLayout flActionBarFun) {
        try {
            flActionBarFun.removeAllViews();
            mFragmentContext = flActionBarFun.getContext();

            if (funView == null) {
                String loadInit = SPUtils.getString(mFragmentContext, SpConstant.INIT_LOAD_APP_CONFIG);
                mainInitJson = JSON.parseObject(loadInit, MainInitJson.class);
                String advertUrl = mainInitJson.getIndex_ad().getIcon();

                funView = LayoutInflater.from(mFragmentContext).inflate(R.layout.action_bar_news, flActionBarFun, false);

                //图标广告
                ImageView imgAdvert = (ImageView) funView.findViewById(R.id.iv_right_icon);
                imgAdvert.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            String appConfig = SPUtils.getString(mFragmentContext, SpConstant.INIT_LOAD_APP_CONFIG);

                            MainActivity mainActivity = (MainActivity) getActivity();

                            MainInitJson mainInitJson = JSONObject.parseObject(appConfig, MainInitJson.class);
                            MainInitJson.IndexAdBean indexAd = mainInitJson.getIndex_ad();
                            mainActivity.mainPresenter.showPopAdvertisement(indexAd);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                imgAdvert.setVisibility(View.GONE);

                //文字广告
                RelativeLayout rvTxtAdvertLayout = (RelativeLayout) funView.findViewById(R.id.rl_txt_advert);
                ImageView ivTxtAdvertJB = (ImageView) funView.findViewById(R.id.iv_txt_advert_jb);
                TextView tvTxtAdvertName = (TextView) funView.findViewById(R.id.tv_right_txt);

                tvTxtAdvertName.setTextColor(ContextCompat.getColor(mFragmentContext, R.color.font_color2));
                tvTxtAdvertName.setText("盈利翻倍");
                Glide.with(mFragmentContext).load(advertUrl).into(new GlideDrawableImageViewTarget(imgAdvert));

                tvTxtAdvertName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent webIntent = new Intent(mFragmentContext, WebActivity.class);
                        webIntent.putExtra(IntentConstant.WEBURL, "https://www.baidu.com/");
                        startActivity(webIntent);
                    }
                });
            }
            flActionBarFun.addView(funView);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        if (fragmentList != null) {
            for (Fragment fragment : fragmentList) {
                if (fragment instanceof BaseFragment) {
                    ((BaseFragment) fragment).onChangeTheme();
                }
            }
        }
        if (stlNavigationBar != null) stlNavigationBar.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            List<Fragment> fragments = childFragmentManager.getFragments();
            for (int i = 0; i < fragments.size(); i++) {
                Fragment fragment = fragments.get(i);
                if (fragment != null) {
                    NewsItemFragment mNewsItemFragment = (NewsItemFragment) fragment;
                    mNewsItemFragment.onResume();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendSocketParams() {
        NewsItemFragment fragment = (NewsItemFragment) fragmentList.get(0);
        fragment.newsItemPresenter.sendSocketParams();
    }

    public String[] getTabs() {
        return tabs;
    }

    public void setSelTab(String selTab) {
        this.selTab = selTab;
    }

}
