package com.jyh.kxt.market.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.utils.MarketConnectUtil;
import com.jyh.kxt.base.utils.MarketUtil;
import com.jyh.kxt.market.bean.MarketItemBean;
import com.jyh.kxt.market.bean.MarketNavBean;
import com.jyh.kxt.market.presenter.MarketMainPresenter;
import com.jyh.kxt.market.presenter.MarketOtherPresenter;
import com.library.bean.EventBusClass;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static org.greenrobot.eventbus.ThreadMode.MAIN;

/**
 * Created by Mr'Dai on 2017/4/25.
 * 行情 -行情 -item
 */
public class MarketItemFragment extends BaseFragment implements AbsListView.OnScrollListener {

    @BindView(R.id.pll_content) public PageLoadLayout pageLoadLayout;
    @BindView(R.id.ptrlv_content) public PullToRefreshListView ptrlvContent;
    @BindView(R.id.rl_list_nav) public RelativeLayout rlListNav;
    @BindView(R.id.tv_target_nav) public TextView tvTargetNav;

    public ListView refreshableView;
    public MarketNavBean navBean;

    private boolean isZhuYePage = false;//是否是主页面
    private MarketVPFragment marketVPFragment;
    private MarketMainPresenter marketMainPresenter; //行情首页P
    private MarketOtherPresenter marketOtherPresenter;//其他行情P

    /**
     * 切换Item 类型  0 涨跌幅   1 涨跌额
     */
    public int switchItemType = 0;
    public HashMap<String, MarketItemBean> marketMap = new HashMap<>();
    public HashMap<String, MarketItemBean> marketMap1 = new HashMap<>();
    public HashMap<String, MarketItemBean> marketMap2 = new HashMap<>();


    @Override
    protected void onInitialize(Bundle savedInstanceState) {

        setContentView(R.layout.fragment_market_item);
    }

    @Override
    public void userVisibleHint() {
        marketVPFragment = (MarketVPFragment) getParentFragment();
        navBean = marketVPFragment.getNavBean(this);

        refreshableView = ptrlvContent.getRefreshableView();

        if ("zhuYe".equals(navBean.getCode())) { //主页的P
            isZhuYePage = true;

            marketMainPresenter = new MarketMainPresenter(this);
            marketMainPresenter.generateMainHeaderView();

            rlListNav.setVisibility(View.GONE);
        } else {
            isZhuYePage = false;

            RelativeLayout.LayoutParams contentParams = (RelativeLayout.LayoutParams) ptrlvContent.getLayoutParams();
            contentParams.addRule(RelativeLayout.BELOW, R.id.rl_list_nav);
            ptrlvContent.setLayoutParams(contentParams);

            marketOtherPresenter = new MarketOtherPresenter(this);
            marketOtherPresenter.generateAdapter();

        }
        ptrlvContent.setMode(PullToRefreshBase.Mode.DISABLED);
        ptrlvContent.setOnScrollListener(this);
        if (isZhuYePage) {
            EventBus.getDefault().register(this);
        }
    }

    @Subscribe(threadMode = MAIN)
    public void onMarketEvent(EventBusClass eventBus) {
        switch (eventBus.fromCode) {
            case EventBusClass.MARKET_OPTION_UPDATE:
                if (isZhuYePage) {
                    List<MarketItemBean> marketList = (List<MarketItemBean>) eventBus.intentObj;
                    marketMainPresenter.eventBusUpdate(marketList);
                }
                break;
        }
    }

    @OnClick(R.id.rl_target_nav)
    public void navClick(View view) {
        if (isZhuYePage) {
            marketMainPresenter.switchItemContent();
        } else {
            marketOtherPresenter.switchItemContent();
        }
    }

    //重新发送Socket参数
    public void onPageSelected() {
        if (isZhuYePage) {
            if (marketMainPresenter == null) {
                return;
            }
            MarketConnectUtil.getInstance().sendSocketParams(
                    this,
                    marketMainPresenter.marketCodeList,
                    marketMainPresenter);
        } else {
            if (marketOtherPresenter == null) {
                return;
            }
            MarketConnectUtil.getInstance().sendSocketParams(
                    this,
                    marketOtherPresenter.marketCodeList,
                    marketOtherPresenter);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }


    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (isZhuYePage) {
            if (firstVisibleItem > 1 && !rlListNav.isShown()) { //显示在顶部  名字  最新价   涨跌幅
                rlListNav.setVisibility(View.VISIBLE);

                TranslateAnimation translateAnimation = new TranslateAnimation(
                        Animation.RELATIVE_TO_SELF, 0.0f,
                        Animation.RELATIVE_TO_SELF, 0.0f,
                        Animation.RELATIVE_TO_SELF, -1.0f,
                        Animation.RELATIVE_TO_SELF, 0.0f);

                translateAnimation.setDuration(300);

                rlListNav.startAnimation(translateAnimation);


            }
            if (firstVisibleItem <= 1 && rlListNav.isShown()) {
                //取消掉顶部的导航
                rlListNav.setVisibility(View.GONE);
            }
        }
    }

    public void mapToMarketBean(String text) {
        MarketUtil.mapToMarketBean(ptrlvContent, switchItemType, marketMap, text);
        MarketUtil.mapToMarketBean(ptrlvContent, switchItemType, marketMap1, text);
        MarketUtil.mapToMarketBean(ptrlvContent, switchItemType, marketMap2, text);
    }

    public String replacePositive(String defStr) {
        return MarketUtil.replacePositive(defStr);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (isZhuYePage) {
            EventBus.getDefault().unregister(this);
        }
        try {
            ((MarketVPFragment) getParentFragment()).onItemDestroyView(MarketItemFragment.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onChangeTheme() {
        super.onChangeTheme();
        if (marketMainPresenter != null) {
            marketMainPresenter.onChangeTheme();
        }
        if (ptrlvContent != null) {
            ptrlvContent.onChangeTheme();
        }
    }
}
