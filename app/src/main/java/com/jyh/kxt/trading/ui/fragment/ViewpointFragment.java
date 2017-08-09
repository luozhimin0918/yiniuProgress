package com.jyh.kxt.trading.ui.fragment;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.trading.presenter.ViewpointPresenter;
import com.jyh.kxt.trading.util.TradeHandlerUtil;
import com.library.bean.EventBusClass;
import com.library.widget.PageLoadLayout;
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
        viewpointPresenter.requestInitData();

        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void onViewPointEventBus(EventBusClass eventBusClass) {
        if (eventBusClass.fromCode == EventBusClass.EVENT_VIEW_POINT_HANDLER) {
            TradeHandlerUtil.EventHandlerBean intentObj = (TradeHandlerUtil.EventHandlerBean) eventBusClass.intentObj;
            viewpointPresenter.viewpointAdapter.handlerEventBus(intentObj);
        }
    }

    private void initView() {
        PinnedSectionListView mRefreshableView = mPullPinnedListView.getRefreshableView();
        mRefreshableView.setDivider(new ColorDrawable(ContextCompat.getColor(getContext(), R.color.line_background4)));
        mRefreshableView.setDividerHeight(0);
        mRefreshableView.setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    @Override
    public void OnAfreshLoad() {
        viewpointPresenter.requestInitData();
    }
}
