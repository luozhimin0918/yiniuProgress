package com.jyh.kxt.market.presenter;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.impl.OnSocketTextMessage;
import com.jyh.kxt.base.utils.MarketConnectUtil;
import com.jyh.kxt.market.adapter.MarketMainItemAdapter;
import com.jyh.kxt.market.bean.MarketItemBean;
import com.jyh.kxt.market.ui.fragment.MarketItemFragment;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr'Dai on 2017/4/25.
 */

public class MarketOtherPresenter extends BasePresenter implements OnSocketTextMessage {

    @BindObject MarketItemFragment marketItemFragment;

    public JSONArray marketCodeList = new JSONArray();
    private List<MarketItemBean> marketDataList = new ArrayList<>();
    private MarketMainItemAdapter marketMainItemAdapter;

    public MarketOtherPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

   /* public void generateWebSocketAdapter() {
        marketItemFragment.pageLoadLayout.loadOver();

        MarketVPFragment parentFragment = (MarketVPFragment) marketItemFragment.getParentFragment();
        parentFragment.getNavBean(marketItemFragment);

        marketDataList.clear();
        marketCodeList.clear();
        marketItemFragment.marketMap.clear();

        List<MarketItemBean> marketList = new ArrayList<>();
        if (marketMainItemAdapter == null) {
            marketDataList.addAll(marketList);
            marketMainItemAdapter = new MarketMainItemAdapter(mContext, marketDataList);
            marketItemFragment.ptrlvContent.setAdapter(marketMainItemAdapter);
        } else {
            marketDataList.addAll(marketList);
            marketMainItemAdapter.notifyDataSetChanged();
        }

        for (MarketItemBean marketItemBean : marketDataList) {
            marketCodeList.add(marketItemBean.getCode());
            marketItemFragment.marketMap.put(marketItemBean.getCode(), marketItemBean);
            //赋值默认的初始值
            marketItemBean.setSwitchTarget(marketItemBean.getRange());
        }

        MarketConnectUtil.getInstance().sendSocketParams(
                iBaseView,
                marketCodeList,
                MarketOtherPresenter.this);
    }*/

    public void generateNetWorkAdapter() {
        VolleyRequest volleyRequest = new VolleyRequest(mContext, mQueue);
        volleyRequest.setTag(marketItemFragment.navBean.getCode());

        JSONObject json = volleyRequest.getJsonParam();
        json.put("code", marketItemFragment.navBean.getCode());
        volleyRequest.doGet(HttpConstant.MARKET_LIST, json, new HttpListener<List<MarketItemBean>>() {
            @Override
            protected void onResponse(final List<MarketItemBean> marketList) {
                marketItemFragment.pageLoadLayout.loadOver();
                marketDataList.clear();
                marketCodeList.clear();
                marketItemFragment.marketMap.clear();

                if (marketMainItemAdapter == null) {
                    marketDataList.addAll(marketList);
                    marketMainItemAdapter = new MarketMainItemAdapter(mContext, marketDataList);
                    marketItemFragment.ptrlvContent.setAdapter(marketMainItemAdapter);
                } else {
                    marketDataList.addAll(marketList);
                    marketMainItemAdapter.notifyDataSetChanged();
                }

                for (MarketItemBean marketItemBean : marketDataList) {
                    marketCodeList.add(marketItemBean.getCode());
                    marketItemFragment.marketMap.put(marketItemBean.getCode(), marketItemBean);
                    //赋值默认的初始值
                    marketItemBean.setSwitchTarget(marketItemBean.getRange());
                }

                MarketConnectUtil.getInstance().sendSocketParams(
                        iBaseView,
                        marketCodeList,
                        MarketOtherPresenter.this);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                marketItemFragment.pageLoadLayout.loadError();
                super.onErrorResponse(error);
            }
        });
    }

    @Override
    public void onTextMessage(String text) {
        try {
            marketItemFragment.mapToMarketBean(text);
        } catch (Exception e) {
            try {
                List<String> jsonList = JSONArray.parseArray(text, String.class);
                for (String itemJson : jsonList) {
                    marketItemFragment.mapToMarketBean(itemJson);
                }
            } catch (Exception e1) {
            }
        }
    }


    public void switchItemContent() {
        marketItemFragment.switchItemType = marketItemFragment.switchItemType == 0 ? 1 : 0;
        marketItemFragment.tvTargetNav.setText(marketItemFragment.switchItemType == 0 ? "涨跌幅" : "涨跌额");

        if (marketMainItemAdapter != null && marketMainItemAdapter.dataList != null) {
            for (MarketItemBean marketItemBean : marketMainItemAdapter.dataList) {
                marketItemBean.setSwitchTarget(
                        marketItemFragment.switchItemType == 0 ? marketItemBean.getRange() : marketItemBean.getChange());
            }
        }
    }

    public void onChangeTheme() {
        try {
            //重新设置热门行情主题色
            if (marketMainItemAdapter != null) {
                marketMainItemAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
