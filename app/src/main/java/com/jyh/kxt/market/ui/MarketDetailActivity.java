package com.jyh.kxt.market.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.base.widget.LoadX5WebView;
import com.jyh.kxt.index.json.MainInitJson;
import com.jyh.kxt.market.bean.MarketItemBean;
import com.library.base.http.VarConstant;
import com.library.util.SPUtils;
import com.trycatch.mysnackbar.Prompt;
import com.trycatch.mysnackbar.TSnackbar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MarketDetailActivity extends BaseActivity {

    @BindView(R.id.iv_bar_break) ImageView ivBarBreak;
    @BindView(R.id.tv_bar_title) TextView tvBarTitle;

    @BindView(R.id.ll_market_detail_optional) LinearLayout llMarketDetailOptional;
    @BindView(R.id.iv_market_detail_optional_img) ImageView ivOptionalImage;
    @BindView(R.id.ll_market_detail_share) LinearLayout llMarketDetailShare;
    @BindView(R.id.activity_market_detail) RelativeLayout activityMarketDetail;
    @BindView(R.id.lwv_content) LoadX5WebView lwvContent;

    private MarketItemBean marketItemBean;
    private List<MarketItemBean> marketItemList;

    /**
     * 是否允许添加, true 表示可以添加  为+
     */
    private boolean isAllowAddOptional = true;

    @OnClick({R.id.ll_market_detail_optional, R.id.ll_market_detail_share})
    public void onOptionClick(View view) {
        switch (view.getId()) {
            case R.id.ll_market_detail_optional:
                addOrDeleteMarket();
                break;
            case R.id.ll_market_detail_share:

                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_detail, StatusBarColor.THEME1);

        try {
            String marketOption = SPUtils.getString(this, SpConstant.MARKET_MY_OPTION);
            marketItemList = JSONArray.parseArray(marketOption, MarketItemBean.class);
            if (marketItemList == null) {
                marketItemList = new ArrayList<>();
            }
            String appConfig = SPUtils.getString(this, SpConstant.INIT_LOAD_APP_CONFIG);
            MainInitJson mainInitJson = JSONObject.parseObject(appConfig, MainInitJson.class);
            marketItemBean = getIntent().getParcelableExtra("market");

            String quotesChartUrl = mainInitJson.getQuotes_chart_url();
            quotesChartUrl = quotesChartUrl.replaceAll("\\{code\\}", marketItemBean.getCode());
            quotesChartUrl = quotesChartUrl.replaceAll("\\{system\\}", VarConstant.HTTP_SYSTEM_VALUE);
            quotesChartUrl = quotesChartUrl.replaceAll("\\{version\\}", VarConstant.HTTP_VERSION_VALUE);

            lwvContent.build();
            lwvContent.loadUrl(quotesChartUrl);
            verifyOptionAppend();

            tvBarTitle.setText(marketItemBean.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addOrDeleteMarket() {
        if (isAllowAddOptional) {
            marketItemList.add(marketItemBean);
            TSnackbar.make(ivBarBreak, "添加成功", TSnackbar.LENGTH_LONG, TSnackbar.APPEAR_FROM_TOP_TO_DOWN)
                    .setPromptThemBackground(Prompt.WARNING).show();
        } else {
            marketItemList.remove(marketItemBean);
            TSnackbar.make(ivBarBreak, "已删除", TSnackbar.LENGTH_LONG, TSnackbar.APPEAR_FROM_TOP_TO_DOWN)
                    .setPromptThemBackground(Prompt.WARNING).show();
        }
        isAllowAddOptional = !isAllowAddOptional;

        String listStr = JSON.toJSONString(marketItemList);
        SPUtils.save(this, SpConstant.MARKET_MY_OPTION, listStr);
        updateOptionIcon();
    }

    private void verifyOptionAppend() {
        int existPosition = -1;
        for (int i = 0; i < marketItemList.size(); i++) {
            if (marketItemList.get(i).getCode().equals(marketItemBean.getCode())) {
                isAllowAddOptional = false;
                existPosition = i;
            }
        }
        if (existPosition != -1) {
            marketItemList.remove(existPosition);
            marketItemList.add(existPosition, marketItemBean);
        }
        updateOptionIcon();
    }

    private void updateOptionIcon() {
        if (isAllowAddOptional) {
            ivOptionalImage.setImageResource(R.mipmap.icon_more);
        } else {
            ivOptionalImage.setImageResource(R.mipmap.icon_option_delete);
        }
    }
}
