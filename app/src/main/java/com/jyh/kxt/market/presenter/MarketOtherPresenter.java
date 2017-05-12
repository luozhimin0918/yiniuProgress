package com.jyh.kxt.market.presenter;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.market.adapter.MarketMainItemAdapter;
import com.jyh.kxt.market.bean.MarketItemBean;
import com.jyh.kxt.market.ui.fragment.MarketItemFragment;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;

import java.util.List;

/**
 * Created by Mr'Dai on 2017/4/25.
 */

public class MarketOtherPresenter extends BasePresenter {

    @BindObject MarketItemFragment marketItemFragment;

    private MarketMainItemAdapter marketMainItemAdapter;

    public MarketOtherPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void generateAdapter() {
        VolleyRequest volleyRequest = new VolleyRequest(mContext, mQueue);
        JSONObject json = volleyRequest.getJsonParam();

        json.put("code", marketItemFragment.navBean.getCode());

        volleyRequest.doGet(HttpConstant.MARKET_LIST, json, new HttpListener<List<MarketItemBean>>() {
            @Override
            protected void onResponse(List<MarketItemBean> marketMainList) {
                marketMainItemAdapter = new MarketMainItemAdapter(mContext, marketMainList);
                marketItemFragment.ptrlvContent.setAdapter(marketMainItemAdapter);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
            }
        });
    }
}
