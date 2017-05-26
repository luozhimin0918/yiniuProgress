package com.jyh.kxt.market.ui;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.widget.LoadX5WebView;
import com.jyh.kxt.market.bean.MarketItemBean;

import butterknife.BindView;

public class MarketDetailActivity extends BaseActivity {

    @BindView(R.id.iv_bar_break) ImageView ivBarBreak;
    @BindView(R.id.tv_bar_title) TextView tvBarTitle;

    @BindView(R.id.ll_market_detail_optional) LinearLayout llMarketDetailOptional;
    @BindView(R.id.ll_market_detail_share) LinearLayout llMarketDetailShare;
    @BindView(R.id.activity_market_detail) RelativeLayout activityMarketDetail;
    @BindView(R.id.lwv_content) LoadX5WebView lwvContent;

    private MarketItemBean marketItemBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_detail, StatusBarColor.THEME1);

        marketItemBean = getIntent().getParcelableExtra("market");
    }
}
