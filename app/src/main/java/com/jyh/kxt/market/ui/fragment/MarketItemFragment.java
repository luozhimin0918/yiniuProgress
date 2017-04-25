package com.jyh.kxt.market.ui.fragment;

import android.os.Bundle;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.market.bean.MarketNavBean;
import com.jyh.kxt.market.presenter.MarketMainPresenter;
import com.jyh.kxt.market.presenter.MarketOtherPresenter;
import com.library.widget.handmark.PullToRefreshListView;

import butterknife.BindView;

/**
 * Created by Mr'Dai on 2017/4/25.
 * 行情 -行情 -item
 */
public class MarketItemFragment extends BaseFragment {

    @BindView(R.id.ptrlv_content) PullToRefreshListView ptrlvContent;

    private MarketVPFragment marketVPFragment;
    private MarketNavBean navBean;

    private BasePresenter marketMainPresenter, marketOtherPresenter;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_market_item);
    }

    @Override
    public void userVisibleHint() {
        marketVPFragment = (MarketVPFragment) getParentFragment();
        navBean = marketVPFragment.getNavBean(this);

        if ("zhuYe".equals(navBean.getCode())) {
            marketMainPresenter = new MarketMainPresenter(this);
        } else {
            marketOtherPresenter = new MarketOtherPresenter(this);
        }
    }
}
