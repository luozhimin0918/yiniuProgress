package com.jyh.kxt.trading.ui;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.trading.presenter.ViewpointPresenter;
import com.library.widget.PageLoadLayout;
import com.library.widget.listview.PinnedSectionListView;
import com.library.widget.listview.PullPinnedListView;

import butterknife.BindView;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/7/26.
 */

public class ViewpointFragment extends BaseFragment {

    @BindView(R.id.pll_content) PageLoadLayout mPllContent;
    @BindView(R.id.pplv_content) public PullPinnedListView mPullPinnedListView;

    private ViewpointPresenter viewpointPresenter;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_trading_viewpoint);

        initView();

        viewpointPresenter = new ViewpointPresenter(this);
        viewpointPresenter.requestInitData();
    }

    private void initView() {
        PinnedSectionListView mRefreshableView = mPullPinnedListView.getRefreshableView();
        mRefreshableView.setDivider(new ColorDrawable(ContextCompat.getColor(getContext(),
                com.library.R.color.line_background4)));
        mRefreshableView.setDividerHeight(1);
        mRefreshableView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        mPullPinnedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long viewType) {
            }
        });

    }
}
