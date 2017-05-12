package com.jyh.kxt.market.presenter;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.market.adapter.MarketMainItemAdapter;
import com.jyh.kxt.market.bean.MarketItemBean;
import com.jyh.kxt.market.ui.fragment.OptionalFragment;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;

import java.util.List;

/**
 * Created by Mr'Dai on 2017/4/25.
 */

public class OptionalPresenter extends BasePresenter {
    @BindObject OptionalFragment optionalFragment;

    public OptionalPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void generateAdapter() {
        VolleyRequest volleyRequest = new VolleyRequest(mContext, mQueue);
        JSONObject json = volleyRequest.getJsonParam();

        json.put("code", "WH");

        volleyRequest.doGet(HttpConstant.MARKET_LIST, json, new HttpListener<List<MarketItemBean>>() {
            @Override
            protected void onResponse(List<MarketItemBean> marketMainList) {
                MarketMainItemAdapter  marketMainItemAdapter = new MarketMainItemAdapter(mContext, marketMainList);
                optionalFragment.ptrContent.setAdapter(marketMainItemAdapter);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
            }
        });
    }
}
