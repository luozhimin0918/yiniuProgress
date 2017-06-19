package com.jyh.kxt.market.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.BaseFragmentAdapter;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.index.ui.ClassifyActivity;
import com.jyh.kxt.market.bean.MarketNavBean;
import com.jyh.kxt.market.presenter.MarketVPPresenter;
import com.library.util.RegexValidateUtil;
import com.library.widget.PageLoadLayout;
import com.library.widget.tablayout.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

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
    public List<MarketNavBean> marketNavList;
    private String[] titles;
    private String selTab = "";

    @OnClick(R.id.iv_more)
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.iv_more:

                Intent intent = new Intent(getContext(), ClassifyActivity.class);
                intent.putExtra(IntentConstant.INDEX, stlNavigationBar.getCurrentTab());
                intent.putExtra(IntentConstant.ACTIONNAV, titles);
                ((Activity) getContext()).startActivityForResult(intent, IntentConstant.REQUESTCODE1);
                break;
        }
    }

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_market_vp);

        marketVPPresenter = new MarketVPPresenter(this);
        marketVPPresenter.requestMarketNavData();

        pllContent.setOnAfreshLoadListener(this);
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

        titles = new String[fragmentSize];

        int selIndex = 0;

        for (int i = 0; i < fragmentSize; i++) {
            MarketNavBean marketNavBean = marketNavList.get(i);

            MarketItemFragment marketItemFragment = new MarketItemFragment();
            marketItemList.add(marketItemFragment);
            titles[i] = marketNavBean.getName();
            if (!RegexValidateUtil.isEmpty(selTab) && selTab.equals(titles[i])) {
                selIndex = i;
            }
        }


        FragmentManager fm = getChildFragmentManager();
        BaseFragmentAdapter pageAdapter = new BaseFragmentAdapter(fm, marketItemList) {
            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                if (!"zhuYe".equals(MarketVPFragment.this.marketNavList.get(position).getCode())) {
                    super.destroyItem(container, position, object);
                }
            }
        };

        vpContent.setAdapter(pageAdapter);
        stlNavigationBar.setViewPager(vpContent, titles);
        vpContent.addOnPageChangeListener(this);

        stlNavigationBar.setCurrentTab(selIndex);
    }


    public MarketNavBean getNavBean(Fragment fragment) {
        int indexOf = marketItemList.indexOf(fragment);
        return marketNavList.get(indexOf);
    }

    public void sendSocketParams() {
        if (vpContent != null) {
            int currentItem = vpContent.getCurrentItem();
            MarketItemFragment marketItemFragment = (MarketItemFragment) marketItemList.get(currentItem);
            marketItemFragment.onPageSelected();
        }
    }

    @Override
    public void OnAfreshLoad() {
        marketVPPresenter.requestMarketNavData();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        try {
            MarketItemFragment marketItemFragment = (MarketItemFragment) marketItemList.get(position);
            marketItemFragment.onPageSelected();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void onItemDestroyView(MarketItemFragment marketItemFragment) {
        try {
            int indexOf = marketItemList.indexOf(marketItemFragment);
            MarketNavBean marketNavBean = marketNavList.get(indexOf);
            getQueue().cancelAll(marketNavBean.getCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onChangeTheme() {
        super.onChangeTheme();
        if (marketItemList != null) {
            for (Fragment fragment : marketItemList) {
                if (fragment instanceof BaseFragment) {
                    ((BaseFragment) fragment).onChangeTheme();
                }
            }
        }
        stlNavigationBar.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IntentConstant.REQUESTCODE1 && resultCode == Activity.RESULT_OK) {
            int index = data.getIntExtra(IntentConstant.INDEX, 0);
            stlNavigationBar.setCurrentTab(index);
        }
    }

    public String[] getTabs() {
        return titles;
    }

    public void setSelTab(String selTab) {
        this.selTab = selTab;
    }
}
