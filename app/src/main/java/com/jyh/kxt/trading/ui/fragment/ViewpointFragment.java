package com.jyh.kxt.trading.ui.fragment;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewConfiguration;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.trading.presenter.ViewpointPresenter;
import com.jyh.kxt.trading.util.TradeHandlerUtil;
import com.library.bean.EventBusClass;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.listview.PinnedSectionListView;
import com.library.widget.listview.PullPinnedListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/7/26.
 */

public class ViewpointFragment extends BaseFragment implements PageLoadLayout.OnAfreshLoadListener {

    @BindView(R.id.pll_content) public PageLoadLayout mPllContent;
    @BindView(R.id.pplv_content) public PullPinnedListView mPullPinnedListView;

    private ViewpointPresenter viewpointPresenter;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_trading_viewpoint);

        initView();

        mPllContent.loadWait();
        mPllContent.setOnAfreshLoadListener(this);

        viewpointPresenter = new ViewpointPresenter(this);
        viewpointPresenter.requestInitData(PullToRefreshBase.Mode.PULL_FROM_START);

        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void onViewPointEventBus(EventBusClass eventBusClass) {
        if (eventBusClass.fromCode == EventBusClass.EVENT_VIEW_POINT_HANDLER) {
            TradeHandlerUtil.EventHandlerBean intentObj = (TradeHandlerUtil.EventHandlerBean) eventBusClass.intentObj;
            viewpointPresenter.viewpointAdapter.handlerEventBus(intentObj);

        } else if (eventBusClass.fromCode == EventBusClass.EVENT_LOGIN) {//登录之后刷新关注
            viewpointPresenter.viewpointAdapter.loginAccount();
        } else if (eventBusClass.fromCode == EventBusClass.EVENT_LOGOUT) {//退出登录之后关注数据清空
            viewpointPresenter.viewpointAdapter.exitAccount();
        }
    }

    private void initView() {
        PinnedSectionListView mRefreshableView = mPullPinnedListView.getRefreshableView();
        mRefreshableView.setDivider(new ColorDrawable(ContextCompat.getColor(getContext(), R.color.line_background4)));
        mRefreshableView.setDividerHeight(0);
        mRefreshableView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        mRefreshableView.setFriction(ViewConfiguration.getScrollFriction() * 10);
        mPullPinnedListView.setMode(PullToRefreshBase.Mode.BOTH);
        mPullPinnedListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<PinnedSectionListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<PinnedSectionListView> refreshView) {
                viewpointPresenter.requestInitData(PullToRefreshBase.Mode.PULL_FROM_START);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<PinnedSectionListView> refreshView) {
                viewpointPresenter.requestInitData(PullToRefreshBase.Mode.PULL_FROM_END);
            }
        });
    }

    @Override
    public void OnAfreshLoad() {
        viewpointPresenter.requestInitData(PullToRefreshBase.Mode.PULL_FROM_START);
    }

    @Override
    public void onChangeTheme() {
        super.onChangeTheme();

        mPullPinnedListView.getRefreshableView().setShadowVisible(true);
        viewpointPresenter.viewpointAdapter.notifyDataSetChanged();
        viewpointPresenter.rollDotViewPager.onChangeTheme();
        viewpointPresenter.vLine.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.line_color2));

        View viewPointHotTitle = viewpointPresenter.mGridHotViewLayout.findViewById(R.id.viewpoint_hot_title);
        viewPointHotTitle.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.slidingTabLayout_bgColor));
//        rollViewPager.setBackground(null);
    }
}
