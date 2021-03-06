package com.jyh.kxt.trading.ui.fragment;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
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

        try {
            EventBus.getDefault().register(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        } else if (eventBusClass.fromCode == EventBusClass.EVENT_VIEW_POINT_TOP) {
            viewpointPresenter.viewpointAdapter.setTop((String)eventBusClass.intentObj);
        } else if (eventBusClass.fromCode == EventBusClass.EVENT_VIEW_POINT_DEL) {
            viewpointPresenter.viewpointAdapter.delViewpoint((String) eventBusClass.intentObj);
        }
    }

    private void initView() {
        PinnedSectionListView mRefreshableView = mPullPinnedListView.getRefreshableView();
        mRefreshableView.setDivider(new ColorDrawable(ContextCompat.getColor(getContext(), R.color.line_background4)));
        mRefreshableView.setDividerHeight(0);
        mRefreshableView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        mRefreshableView.setFriction(ViewConfiguration.getScrollFriction() * 5);
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
        mPllContent.loadWait();
        viewpointPresenter.requestInitData(PullToRefreshBase.Mode.PULL_FROM_START);
    }

    @Override
    public void onChangeTheme() {
        super.onChangeTheme();

        mPullPinnedListView.getRefreshableView().setShadowVisible(true);
        viewpointPresenter.onChangeTheme();
//        rollViewPager.setBackground(null);
    }

    public void doubleClickFragment() {
        try {
            viewpointPresenter.viewpointAdapter.onTabSelect(0, 0);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPullPinnedListView.getRefreshableView().setSelection(0);
                }
            }, 200);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getQueue().cancelAll(ViewpointPresenter.class.getName());
        try {
            EventBus.getDefault().unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
