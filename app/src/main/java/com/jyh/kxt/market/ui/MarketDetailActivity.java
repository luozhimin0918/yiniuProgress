package com.jyh.kxt.market.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.json.ShareJson;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.base.utils.MarketUtil;
import com.jyh.kxt.base.utils.UmengShareTool;
import com.jyh.kxt.base.widget.LoadX5WebView;
import com.jyh.kxt.market.bean.MarketDetailBean;
import com.jyh.kxt.market.bean.MarketItemBean;
import com.jyh.kxt.user.json.UserJson;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;
import com.library.bean.EventBusClass;
import com.library.util.LogUtil;
import com.library.util.RegexValidateUtil;
import com.library.widget.PageLoadLayout;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.trycatch.mysnackbar.Prompt;
import com.trycatch.mysnackbar.TSnackbar;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MarketDetailActivity extends BaseActivity {

    @BindView(R.id.iv_bar_break) ImageView ivBarBreak;
    @BindView(R.id.tv_bar_title) TextView tvBarTitle;

    @BindView(R.id.ll_market_detail_optional) LinearLayout llMarketDetailOptional;
    @BindView(R.id.iv_market_detail_optional_img) ImageView ivOptionalImage;
    @BindView(R.id.tv_market_detail_optional) TextView tvOptional;
    @BindView(R.id.ll_market_detail_share) LinearLayout llMarketDetailShare;
    @BindView(R.id.activity_market_detail) RelativeLayout activityMarketDetail;
    @BindView(R.id.lwv_content) LoadX5WebView lwvContent;
    @BindView(R.id.pll_content) PageLoadLayout pageLoadLayout;

    private MarketItemBean marketItemBean;
    private List<MarketItemBean> marketItemList;

    private String quotesChartUrl;
    private boolean defaultUpdateAddStatus = false;
    /**
     * 是否允许添加, true 表示可以添加  图标为+  网络请求为删除  K线图修改的
     */
    private boolean updateAddStatus = true;
    private ShareJson shareJson;

    private MarketDetailBean mMarketDetailBean;
    private MarketDetailBean.ShareBean mDetailShare;

    @OnClick({R.id.ll_market_detail_optional, R.id.ll_market_detail_share, R.id.iv_bar_break})
    public void onOptionClick(View view) {
        switch (view.getId()) {
            case R.id.ll_market_detail_optional:
                if (!pageLoadLayout.isSuccessLoadOver()) {
                    return;
                }
                addOrDeleteMarket();
                break;
            case R.id.ll_market_detail_share:
                if (!pageLoadLayout.isSuccessLoadOver()) {
                    return;
                }
                shareJson = new ShareJson(mDetailShare.getTitle(),
                        mDetailShare.getUrl(),
                        mDetailShare.getDescript(),
                        null,
                        null,
                        UmengShareTool.TYPE_DEFAULT,
                        null,
                        null,
                        null,
                        false, false);
                shareJson.setShareFromSource(2);
                shareJson.setWeiBoDiscript(mMarketDetailBean.getShare_sina_title());
                UmengShareTool.initUmengLayout(MarketDetailActivity.this,
                        shareJson,
                        marketItemBean,
                        view, null);
                break;
            case R.id.iv_bar_break:
                onBackPressed();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_detail, StatusBarColor.THEME1);
        requestInitDetail();
    }

    private void requestInitDetail() {
        pageLoadLayout.loadWait();
        marketItemBean = getIntent().getParcelableExtra(IntentConstant.MARKET);
        VolleyRequest volleyRequest = new VolleyRequest(this, mQueue);
        JSONObject jsonParam = volleyRequest.getJsonParam();
        jsonParam.put("code", marketItemBean.getCode());
        volleyRequest.doGet(HttpConstant.MARKET_DETAIL, jsonParam, new HttpListener<MarketDetailBean>() {
            @Override
            protected void onResponse(MarketDetailBean mMarketDetailBean) {
                MarketDetailActivity.this.mMarketDetailBean = mMarketDetailBean;
                marketItemList = MarketUtil.getMarketEditOption(getContext());
                if (marketItemList == null) {
                    marketItemList = new ArrayList<>();
                }
                marketItemBean = null;
                marketItemBean = mMarketDetailBean.getData();

                quotesChartUrl = mMarketDetailBean.getQuotes_chart_url();
                mDetailShare = mMarketDetailBean.getShare();

                lwvContent.build();
                lwvContent.loadUrl(quotesChartUrl);
                lwvContent.setOverWriteWebChromeClient(new WebChromeClient() {
                    @Override
                    public void onReceivedTitle(WebView webView, String s) {
                        super.onReceivedTitle(webView, s);
                        if (!RegexValidateUtil.isEmpty(s)) {
                            tvBarTitle.setText(s);
                        }
                    }
                });
                verifyOptionAppend();
                pageLoadLayout.loadOver();
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                pageLoadLayout.loadError();
            }
        });
    }

    /*private void loadMarketUrl() {
        try {
            marketItemList = MarketUtil.getMarketEditOption(getContext());
            if (marketItemList == null) {
                marketItemList = new ArrayList<>();
            }
            String appConfig = SPUtils.getString(this, SpConstant.INIT_LOAD_APP_CONFIG);
            MainInitJson mainInitJson = JSONObject.parseObject(appConfig, MainInitJson.class);
            marketItemBean = getIntent().getParcelableExtra(IntentConstant.MARKET);

            quotesChartUrl = mainInitJson.getQuotes_chart_url();
            quotesShareUrl = mainInitJson.getUrl_quotes_share();
            quotesChartUrl = quotesChartUrl.replaceAll("\\{code\\}", marketItemBean.getCode());
            quotesShareUrl = quotesShareUrl.replaceAll("\\{code\\}", marketItemBean.getCode());
            quotesChartUrl = quotesChartUrl.replaceAll("\\{system\\}", VarConstant.HTTP_SYSTEM_VALUE);
            quotesChartUrl = quotesChartUrl.replaceAll("\\{version\\}", VarConstant.HTTP_VERSION_VALUE);

            lwvContent.build();
            lwvContent.loadUrl(quotesChartUrl);
            lwvContent.setOverWriteWebChromeClient(new WebChromeClient() {
                @Override
                public void onReceivedTitle(WebView webView, String s) {
                    super.onReceivedTitle(webView, s);
                    if (!RegexValidateUtil.isEmpty(s)) {
                        tvBarTitle.setText(s);
                    }
                }
            });
            verifyOptionAppend();
            pageLoadLayout.loadOver();
        } catch (Exception e) {
            e.printStackTrace();

            pageLoadLayout.loadWait();

            VolleyRequest volleyRequest = new VolleyRequest(this, mQueue);
            volleyRequest.doGet(HttpConstant.CONFIG, new HttpListener<String>() {
                @Override
                protected void onResponse(String patchInfo) {
                    SPUtils.save(MarketDetailActivity.this, SpConstant.INIT_LOAD_APP_CONFIG, patchInfo);
                    loadMarketUrl();
                }

                @Override
                protected void onErrorResponse(VolleyError error) {
                    super.onErrorResponse(error);
                    pageLoadLayout.loadEmptyData();
                }
            });

        }
    }*/

    private void addOrDeleteMarket() {
        if (TextUtils.isEmpty(marketItemBean.getName()) || TextUtils.isEmpty(marketItemBean.getCode())) {
            Toast.makeText(this, "数据不全操作失败", Toast.LENGTH_SHORT).show();
            return;
        }

        if (updateAddStatus) {
            if (marketItemList.size() > 30) {
                TSnackbar.make(ivBarBreak, "自选行情数量太多,超出30条了喔,添加失败", TSnackbar.LENGTH_LONG, TSnackbar
                        .APPEAR_FROM_TOP_TO_DOWN)
                        .setPromptThemBackground(Prompt.WARNING).show();
                return;
            }
            marketItemList.add(marketItemBean);
            ivOptionalImage.setSelected(true);
            TSnackbar.make(ivBarBreak, "添加成功", TSnackbar.LENGTH_LONG, TSnackbar.APPEAR_FROM_TOP_TO_DOWN)
                    .setPromptThemBackground(Prompt.WARNING).show();
        } else {
            marketItemList.remove(marketItemBean);
            ivOptionalImage.setSelected(false);
            TSnackbar.make(ivBarBreak, "已删除", TSnackbar.LENGTH_LONG, TSnackbar.APPEAR_FROM_TOP_TO_DOWN)
                    .setPromptThemBackground(Prompt.WARNING).show();
        }
        updateAddStatus = !updateAddStatus;

        MarketUtil.saveMarketEditOption(getContext(), marketItemList, 2);
        updateOptionIcon();
    }

    private void verifyOptionAppend() {
        int existPosition = -1;
        for (int i = 0; i < marketItemList.size(); i++) {
            if (marketItemList.get(i).getCode().equals(marketItemBean.getCode())) {
                updateAddStatus = false;
                existPosition = i;
            }
        }
        if (existPosition != -1) {
            marketItemList.remove(existPosition);
            marketItemList.add(existPosition, marketItemBean);
        }
        defaultUpdateAddStatus = updateAddStatus;
        updateOptionIcon();
    }

    private void updateOptionIcon() {
        if (updateAddStatus) {
            ivOptionalImage.setSelected(false);
            tvOptional.setText("添加自选");
        } else {
            ivOptionalImage.setSelected(true);
            tvOptional.setText("删除自选");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UmengShareTool.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        try {
            requestAddOrDeleteOptions();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onBackPressed();
    }

    private void requestAddOrDeleteOptions() {
        if (defaultUpdateAddStatus == updateAddStatus) {
            return;
        }

        MarketUtil.saveMarketEditOption(getContext(), marketItemList, 2);
        EventBusClass eventBusClass = new EventBusClass(
                EventBusClass.MARKET_OPTION_UPDATE,
                marketItemList);
        EventBus.getDefault().post(eventBusClass);

        UserJson userInfo = LoginUtils.getUserInfo(this);
        if (userInfo == null) {
            return;
        }


        String url;
        if (updateAddStatus) {
            url = HttpConstant.QUOTES_DELFAVOR;
        } else {
            url = HttpConstant.QUOTES_ADDFAVOR;
        }

        VolleyRequest volleyRequest = new VolleyRequest(getContext(), getQueue());

        volleyRequest.setTag("");
        JSONObject jsonParam = volleyRequest.getJsonParam();
        jsonParam.put("uid", userInfo.getUid());
        jsonParam.put("accessToken", userInfo.getToken());
        jsonParam.put("code", marketItemBean.getCode());

        volleyRequest.doPost(url, jsonParam, new HttpListener<String>() {
            @Override
            protected void onResponse(String s) {
                LogUtil.e(LogUtil.TAG, "onResponse() called with: s = [" + s + "]");
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (shareJson != null) {
                Bitmap shareBitmap = shareJson.getBitmap();
                if (shareBitmap != null && !shareBitmap.isRecycled()) {
                    shareBitmap.recycle();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
