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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Mr'Dai on 2017/4/25.
 */

public class MarketOtherPresenter extends BasePresenter implements OnSocketTextMessage {

    @BindObject MarketItemFragment marketItemFragment;

    private JSONArray marketCodeList = new JSONArray();
    private MarketMainItemAdapter marketMainItemAdapter;
    private HashMap<String, MarketItemBean> marketMap = new HashMap<>();

    public MarketOtherPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void generateAdapter() {
        VolleyRequest volleyRequest = new VolleyRequest(mContext, mQueue);
        JSONObject json = volleyRequest.getJsonParam();

        json.put("code", marketItemFragment.navBean.getCode());

        volleyRequest.doGet(HttpConstant.MARKET_LIST, json, new HttpListener<List<MarketItemBean>>() {
            @Override
            protected void onResponse(List<MarketItemBean> marketList) {
                marketMainItemAdapter = new MarketMainItemAdapter(mContext, marketList);
                marketItemFragment.ptrlvContent.setAdapter(marketMainItemAdapter);

                for (MarketItemBean marketItemBean : marketList) {

                    marketCodeList.add(marketItemBean.getCode());
                    marketMap.put(marketItemBean.getCode(), marketItemBean);
                }

                MarketConnectUtil.getInstance().sendSocketParams(
                        iBaseView,
                        marketCodeList,
                        MarketOtherPresenter.this);

            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
            }
        });
    }

    @Override
    public void onTextMessage(String text) {
        try {
            mapToMarketBean(text);
        } catch (Exception e) {
            try {
                List<String> jsonList = JSONArray.parseArray(text, String.class);
                for (String itemJson : jsonList) {
                    mapToMarketBean(itemJson);
                }
            } catch (Exception e1) {
            }
        }
    }

    private void mapToMarketBean(String text) {
        JSONObject jsonObject = JSONObject.parseObject(text);
        String code = jsonObject.getString("c");
        String price = jsonObject.getString("p");   //最新价  3.5951
        String change = jsonObject.getString("d");  //涨跌额  0.0238
        String range = jsonObject.getString("df");  //涨跌幅 "0.6664239912636867%",

        MarketItemBean marketItemBean = marketMap.get(code);
        if (marketItemBean != null) {
            marketItemBean.setPrice(price);
            marketItemBean.setChange(reserveDecimals(change, 4));

            //设置涨跌幅
            String rangeReserve = reserveDecimals(range, 2);
            marketItemBean.setRange(rangeReserve);


            if (rangeReserve.contains("-")) {
                marketItemBean.setBgGlint(2);
            } else {
                marketItemBean.setBgGlint(1);
            }
        }
    }

    private String reserveDecimals(String defStr, int places) {
        //先替换负数和百分比
        boolean isIncludePositiveNumber = defStr.contains("-");
        boolean isIncludePercentage = defStr.contains("%");

        String clearSpecialChars = defStr.replace("-", "").replace("%", "");

        BigDecimal bigDecimal = new BigDecimal(clearSpecialChars);
        String after = bigDecimal.setScale(places, BigDecimal.ROUND_HALF_UP).toString();

        if (isIncludePositiveNumber) {
            after = "-" + after;
        }
        if (isIncludePercentage) {
            after = after + "%";
        }
        return after;
    }
}
