package com.jyh.kxt.market.presenter;

import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.market.ui.MarketDetailActivity;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;

/**
 * Created by Mr'Dai on 2017/7/18.
 */

public class MarketDetailChartPresenter extends BasePresenter {

    @BindObject MarketDetailActivity chartActivity;

    public MarketDetailChartPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    //    type  areas candlestick
    //    row 数据条数
    //    code 行情代码 大写
    //    interval 时间参数
    //    d代表天  m代表月
    public void requestChartData(String code, int fromSource, HttpListener<String> httpListener) {
        VolleyRequest volleyRequest = new VolleyRequest(mContext, mQueue);


        String url = "http://120.26.97.99:15772/chartbyrows?interval=%s&type=%s&rows=%s&code=%s";
        String marketDataUrl;

        if (fromSource == 0) {
            marketDataUrl = String.format(url, "1", "areas", "800", code);
        } else {
            String time = "1";
            switch (fromSource) {
                case 1:
                    time = "5";
                    break;
                case 2:
                    time = "30";
                    break;
                case 3:
                    time = "1d";
                    break;
                case 4:
                    time = "7d";
                    break;
            }

            marketDataUrl = String.format(url, time  , "candlestick", "800", code);
        }

        volleyRequest.setDefaultDecode(false);
        volleyRequest.doGet(marketDataUrl, httpListener);
    }
}
