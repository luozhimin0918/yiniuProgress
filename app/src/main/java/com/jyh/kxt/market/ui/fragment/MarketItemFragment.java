package com.jyh.kxt.market.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.market.bean.MarketNavBean;
import com.jyh.kxt.market.presenter.MarketMainPresenter;
import com.jyh.kxt.market.presenter.MarketOtherPresenter;
import com.library.widget.handmark.PullToRefreshListView;

import butterknife.BindView;

/**
 * Created by Mr'Dai on 2017/4/25.
 * 行情 -行情 -item
 */
public class MarketItemFragment extends BaseFragment implements AbsListView.OnScrollListener {

    @BindView(R.id.ptrlv_content) public PullToRefreshListView ptrlvContent;
    @BindView(R.id.rl_list_nav) public RelativeLayout rlListNav;

    public ListView refreshableView;
    public MarketNavBean navBean;

    private boolean isZhuYePage = false;//是否是主页面
    private MarketVPFragment marketVPFragment;
    private MarketMainPresenter marketMainPresenter; //行情首页P
    private MarketOtherPresenter marketOtherPresenter;//其他行情P

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
        ptrlvContent.setOnScrollListener(this);
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
}
