package com.jyh.kxt.main.ui.fragment;

import android.os.Bundle;

import com.jyh.kxt.R;
import com.jyh.kxt.av.presenter.VideoItemPresenter;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.main.presenter.NewsItemPresenter;
import com.library.widget.handmark.PullToRefreshListView;

import butterknife.BindView;

/**
 * 项目名:Kxt
 * 类描述:要闻item
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/12.
 */

public class NewsItemFragment extends BaseFragment {

    private NewsItemPresenter newsItemPresenter;

    @BindView(R.id.plv_content) public PullToRefreshListView plvContent;
    private String name;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_news_item);

        newsItemPresenter = new NewsItemPresenter(this);

        Bundle arguments = getArguments();
        name = arguments.getString(IntentConstant.NAME);

        newsItemPresenter.setAdapter();
    }
}
