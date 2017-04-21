package com.jyh.kxt.main.ui.fragment;

import android.os.Bundle;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.main.presenter.FlashPresenter;
import com.jyh.kxt.main.widget.FastInfoPinnedListView;
import com.jyh.kxt.main.widget.FastInfoPullPinnedListView;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshBase;

import butterknife.BindView;

/**
 * 项目名:Kxt
 * 类描述: 快讯Fragment
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/12.
 */

public class FlashFragment extends BaseFragment implements FastInfoPinnedListView.FooterListener {

    @BindView(R.id.lv_content) public FastInfoPullPinnedListView lvContent;
    @BindView(R.id.pl_rootView) public PageLoadLayout plRootView;
    private FlashPresenter flashPresenter;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_flash);

        flashPresenter = new FlashPresenter(this);
        FastInfoPinnedListView refreshableView = lvContent.getRefreshableView();
        refreshableView.addFooterListener(flashPresenter);
        lvContent.setOnRefreshListener(flashPresenter);
        flashPresenter.init();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        flashPresenter.onDestroy();
    }

    @Override
    public void startLoadMore() {

    }
}
