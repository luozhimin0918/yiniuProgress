package com.jyh.kxt.market.ui;

import android.os.Bundle;

import com.alibaba.fastjson.JSONArray;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.market.bean.MarketItemBean;
import com.jyh.kxt.market.kline.bean.MarketTrendBean;
import com.jyh.kxt.market.kline.mychart.MyLineChart;
import com.jyh.kxt.market.presenter.MarketDetailChartPresenter;
import com.jyh.kxt.market.presenter.MinutePresenter;
import com.library.base.http.HttpListener;

import java.util.List;

import butterknife.BindView;

public class MarketDetailChartActivity extends BaseActivity implements ViewPortHandler.OnLongPressIndicatorHandler {

    @BindView(R.id.line_chart) public MyLineChart minuteChartView;

    private MinutePresenter minutePresenter;
    private MarketDetailChartPresenter marketDetailChartPresenter;

    private MarketItemBean marketItemBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_detail_chart, StatusBarColor.NO_COLOR);

        marketItemBean = getIntent().getParcelableExtra(IntentConstant.MARKET);

        minutePresenter = new MinutePresenter(this);
        minutePresenter.initChart(this);

        marketDetailChartPresenter = new MarketDetailChartPresenter(this);
        requestChartData(1);
    }

    private void requestChartData(final int fromSource) {
        marketDetailChartPresenter.requestChartData(marketItemBean.getCode(),
                new HttpListener<String>() {
                    @Override
                    protected void onResponse(String list) {
                        List<MarketTrendBean> marketTrendList = JSONArray.parseArray(list, MarketTrendBean.class);
                        if (fromSource == 1) {
                            minutePresenter.setMinuteData(marketTrendList);
                        }
                    }
                });
    }


    @Override
    public void longPressIndicator(int xIndex, LineData lineData) {

    }
}
