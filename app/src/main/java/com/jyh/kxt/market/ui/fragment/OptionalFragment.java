package com.jyh.kxt.market.ui.fragment;

import android.os.Bundle;
import android.widget.RelativeLayout;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.market.presenter.OptionalPresenter;
import com.library.widget.handmark.PullToRefreshListView;

import butterknife.BindView;

/**
 * Created by Mr'Dai on 2017/4/25.
 * 行情 - 自选
 */

public class OptionalFragment extends BaseFragment {

    @BindView(R.id.ptrlv_content) public PullToRefreshListView ptrContent;

    private OptionalPresenter optionalPresenter;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_market_item);

        //初始化
        RelativeLayout.LayoutParams contentParams = (RelativeLayout.LayoutParams) ptrContent.getLayoutParams();
        contentParams.addRule(RelativeLayout.BELOW, R.id.rl_list_nav);
        ptrContent.setLayoutParams(contentParams);

        optionalPresenter = new OptionalPresenter(this);
        optionalPresenter.generateAdapter();
    }
}
