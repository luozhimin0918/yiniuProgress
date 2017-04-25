package com.jyh.kxt.market.presenter;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.market.bean.MarketNavBean;
import com.jyh.kxt.market.ui.fragment.MarketVPFragment;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;

import java.util.List;

/**
 * Created by Mr'Dai on 2017/4/25.
 */

public class MarketVPPresenter extends BasePresenter {

    @BindObject MarketVPFragment marketVPFragment;

    public MarketVPPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    /**
     * 获得导航栏数据
     */
    public void requestMarketNavData() {
        marketVPFragment.pllContent.loadWait();

        VolleyRequest volleyRequest = new VolleyRequest(mContext, mQueue);

        JSONObject jsonParam = volleyRequest.getJsonParam();
        volleyRequest.doGet(HttpConstant.MARKET_NAV, jsonParam, new HttpListener<List<MarketNavBean>>() {
            @Override
            protected void onResponse(List<MarketNavBean> marketNavList) {
                marketVPFragment.pllContent.loadOver();
                marketVPFragment.responseMarketNavData(marketNavList);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                marketVPFragment.pllContent.loadError();
            }
        });
    }
}
