package com.jyh.kxt.market.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.BaseFragmentAdapter;
import com.jyh.kxt.market.bean.MarketNavBean;
import com.jyh.kxt.market.presenter.MarketVPPresenter;
import com.library.widget.PageLoadLayout;
import com.library.widget.tablayout.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Mr'Dai on 2017/4/11.
 * 行情 - 行情
 */
public class MarketVPFragment extends BaseFragment implements ViewPager.OnPageChangeListener, PageLoadLayout
        .OnAfreshLoadListener {

    @BindView(R.id.stl_navigation_bar) public SlidingTabLayout stlNavigationBar;
    @BindView(R.id.vp_content) public ViewPager vpContent;
    @BindView(R.id.pll_content) public PageLoadLayout pllContent;

    private MarketVPPresenter marketVPPresenter;

    private List<Fragment> marketItemList;
    private List<MarketNavBean> marketNavList;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_market_vp);

        marketVPPresenter = new MarketVPPresenter(this);
        marketVPPresenter.requestMarketNavData();

        pllContent.setOnAfreshLoadListener(this);
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

    /**
     * 导航栏请求初始化完成
     */
    public void responseMarketNavData(List<MarketNavBean> marketNavList) {
        this.marketNavList = marketNavList;
        this.marketItemList = new ArrayList<>();

        //这里Fragment 数量加1 增加主页
        int fragmentSize = marketNavList.size() + 1;

        MarketNavBean mMarketNavBean = new MarketNavBean();
        mMarketNavBean.setName("主页");
        mMarketNavBean.setCode("zhuYe");
        marketNavList.add(0, mMarketNavBean);

        String[] titles = new String[fragmentSize];

        for (int i = 0; i < fragmentSize; i++) {
            MarketNavBean marketNavBean = marketNavList.get(i);

            MarketItemFragment marketItemFragment = new MarketItemFragment();
            marketItemList.add(marketItemFragment);

            titles[i] = marketNavBean.getName();
        }


        FragmentManager fm = getChildFragmentManager();
        BaseFragmentAdapter pageAdapter = new BaseFragmentAdapter(fm, marketItemList);

        vpContent.setAdapter(pageAdapter);
        stlNavigationBar.setViewPager(vpContent, titles);
    }


    public MarketNavBean getNavBean(Fragment fragment) {
        int indexOf = marketItemList.indexOf(fragment);
        return marketNavList.get(indexOf);
    }

    @Override
    public void OnAfreshLoad() {
        marketVPPresenter.requestMarketNavData();
    }
}
