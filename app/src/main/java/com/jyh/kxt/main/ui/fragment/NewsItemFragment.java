package com.jyh.kxt.main.ui.fragment;

import android.os.Bundle;
import android.widget.ListView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.main.presenter.NewsItemPresenter;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;

import butterknife.BindView;

/**
 * 项目名:Kxt
 * 类描述:要闻item
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/12.
 */

public class NewsItemFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener2, PageLoadLayout.OnAfreshLoadListener {

    private NewsItemPresenter newsItemPresenter;

    @BindView(R.id.plv_content) public PullToRefreshListView plvContent;
    @BindView(R.id.pl_rootView) public PageLoadLayout plRootView;
    private String name;
    private boolean isMain;//是否为首页

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_news_item);

        newsItemPresenter = new NewsItemPresenter(this);

        plvContent.setMode(PullToRefreshBase.Mode.BOTH);

        plvContent.setOnRefreshListener(this);
        plRootView.setOnAfreshLoadListener(this);

        Bundle arguments = getArguments();
        name = arguments.getString(IntentConstant.NAME);
        int index = arguments.getInt(IntentConstant.INDEX);
        isMain = index == 0 ? true : false;

        newsItemPresenter.setMain(isMain);

        newsItemPresenter.init(arguments);

    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        newsItemPresenter.onPullDownToRefresh(refreshView);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        newsItemPresenter.onPullUpToRefresh(refreshView);
    }

    @Override
    public void OnAfreshLoad() {

    }
}
