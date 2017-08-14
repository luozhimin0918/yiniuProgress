package com.jyh.kxt.market.presenter;

import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.market.ui.MarketDetailActivity;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;

/**
 * Created by Mr'Dai on 2017/7/18.
 */

public class MarketDetailPresenter extends BasePresenter {

    @BindObject MarketDetailActivity chartActivity;

    public MarketDetailPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    //    type  areas candlestick
    //    row 数据条数
    //    code 行情代码 大写
    //    interval 时间参数
    //    d代表天  m代表月
    public void requestChartData(String code, int fromSource, HttpListener<String> httpListener) {
        VolleyRequest volleyRequest = new VolleyRequest(mContext, mQueue);


        String url = "http://120.26.97.99:15772/chartbyrows?interval=%s&type=%s&code=%s";
        String marketDataUrl;

        if (fromSource == 0) {
            marketDataUrl = String.format(url, "1", "areas", code);
        } else {
            String time = "1";
            switch (fromSource) {
                case 1:
                    time = "5";
                    break;
                case 2:
                    time = "15";
                    break;
                case 3:
                    time = "30";
                    break;
                case 4:
                    time = "1h";
                    break;
                case 5:
                    time = "1d";
                    break;
                case 6:
                    time = "1w";
                    break;
                case 7:
                    time = "1m";
                    break;
            }

            marketDataUrl = String.format(url, time, "candlestick", code);
        }

        volleyRequest.setDefaultDecode(false);
        volleyRequest.setTag(code);
        volleyRequest.doGet(marketDataUrl, httpListener);
    }

    public void onConfigurationChanged(Configuration newConfig) {

        chartActivity.portrait = newConfig.orientation == Configuration.ORIENTATION_PORTRAIT;
        if (chartActivity.portrait) {
            int mShowFlags =
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.INVISIBLE;
            chartActivity.tvBarTitle.setSystemUiVisibility(mShowFlags);
            chartActivity.marketFunctionNav.setVisibility(View.VISIBLE);

        } else {
            int mHideFlags =
                    View.SYSTEM_UI_FLAG_LOW_PROFILE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            chartActivity.tvBarTitle.setSystemUiVisibility(mHideFlags);
            chartActivity.marketFunctionNav.setVisibility(View.GONE);

        }

        /**
         * 重置HeadLayout 布局信息
         */
        chartActivity.marketHeadParentLayout.removeAllViews();
        View headLayoutView = LayoutInflater.from(mContext).inflate(
                chartActivity.portrait ? R.layout.view_market_head_portrait : R.layout.view_market_head_landscape,
                chartActivity.marketHeadParentLayout,
                false);
        chartActivity.marketHeadParentLayout.addView(headLayoutView);
        chartActivity.marketHeadParentLayout.requestLayout();

        resetViewPointer(chartActivity.portrait, headLayoutView);
    }

    /**
     * 重置所有Head 的控件指向
     *
     * @param portrait
     * @param headLayoutView
     */
    private void resetViewPointer(boolean portrait, View headLayoutView) {

        chartActivity.marketHeadLayout = (ViewGroup) headLayoutView.findViewById(R.id.market_head_layout);

        chartActivity.marketChartLow = (TextView) headLayoutView.findViewById(R.id.market_chart_low);
        chartActivity.marketChartZde = (TextView) headLayoutView.findViewById(R.id.market_chart_zde);
        chartActivity.marketChartZdf = (TextView) headLayoutView.findViewById(R.id.market_chart_zdf);
        chartActivity.marketChartZuoshou = (TextView) headLayoutView.findViewById(R.id.market_chart_zuoshou);
        chartActivity.marketChartJinkai = (TextView) headLayoutView.findViewById(R.id.market_chart_jinkai);
        chartActivity.marketChartZuigao = (TextView) headLayoutView.findViewById(R.id.market_chart_zuigao);
        chartActivity.marketChartZuidi = (TextView) headLayoutView.findViewById(R.id.market_chart_zuidi);

        chartActivity.ivBarBreak = (ImageView) headLayoutView.findViewById(R.id.iv_bar_break);
        chartActivity.ivUpdateView = (ImageView) headLayoutView.findViewById(R.id.iv_bar_function);

        chartActivity.tvBarCode = (TextView) headLayoutView.findViewById(R.id.tv_bar_code);
        chartActivity.tvBarTitle = (TextView) headLayoutView.findViewById(R.id.tv_bar_title);

        //必须要的
        if (portrait) {
            chartActivity.marketChartLastTime = (TextView) headLayoutView.findViewById(R.id.market_chart_update_time);
            chartActivity.marketChartLastTime.setText(chartActivity.marketSocketBean.lastTime);
        }

        chartActivity.onTextMessage(chartActivity.marketSocketBean.getJsonData());

        chartActivity.tvBarTitle.setText(chartActivity.mMarketDetailBean.getData().getName());
        chartActivity.tvBarCode.setText(chartActivity.mMarketDetailBean.getData().getCode());

        resetInitHeadViewClick();
    }

    private void resetInitHeadViewClick() {
        chartActivity.ivBarBreak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chartActivity.onBackPressed();
            }
        });
        chartActivity.ivUpdateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chartActivity.updateChartDate();
            }
        });
    }
}
