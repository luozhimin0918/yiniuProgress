package com.jyh.kxt.market.presenter;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.base.custom.RollDotViewPager;
import com.jyh.kxt.base.custom.RollViewPager;
import com.jyh.kxt.base.impl.OnSocketTextMessage;
import com.jyh.kxt.base.json.AdItemJson;
import com.jyh.kxt.base.json.AdTitleIconBean;
import com.jyh.kxt.base.json.AdTitleItemBean;
import com.jyh.kxt.base.utils.MarketConnectUtil;
import com.jyh.kxt.base.utils.MarketUtil;
import com.jyh.kxt.base.widget.AdvertLayout;
import com.jyh.kxt.base.widget.night.heple.SkinnableTextView;
import com.jyh.kxt.databinding.ItemMarketRecommend2Binding;
import com.jyh.kxt.index.json.TypeDataJson;
import com.jyh.kxt.index.ui.WebActivity;
import com.jyh.kxt.index.ui.fragment.MarketFragment;
import com.jyh.kxt.main.json.AdJson;
import com.jyh.kxt.market.adapter.MarketGridAdapter;
import com.jyh.kxt.market.adapter.MarketMainItemAdapter;
import com.jyh.kxt.market.bean.MarketHotBean;
import com.jyh.kxt.market.bean.MarketItemBean;
import com.jyh.kxt.market.bean.MarketMainBean;
import com.jyh.kxt.market.ui.MarketDetailActivity;
import com.jyh.kxt.market.ui.fragment.MarketItemFragment;
import com.jyh.kxt.market.ui.fragment.MarketVPFragment;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;
import com.library.util.RegexValidateUtil;
import com.library.util.SPUtils;
import com.library.util.SystemUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr'Dai on 2017/4/25.
 * 行情 - 行情 - 首页
 */

public class MarketMainPresenter extends BasePresenter implements OnSocketTextMessage {

    @BindObject MarketItemFragment marketItemFragment;

    private LinearLayout mainHeaderView;
    public JSONArray marketCodeList = new JSONArray();
    private MarketMainItemAdapter marketMainItemAdapter;

    /**
     * 首页的 角标
     */
    private TextView tvTargetNav;

    private ArrayList<MarketGridAdapter> marketGridAdapters = new ArrayList<>();
    private RollDotViewPager recommendView;
    //    private MarketMainBean marketBean;
    private ArrayList<SkinnableTextView> mAdTextViewList;

    private AdvertLayout advertLayout;

    public MarketMainPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }


    /**
     * 生成首页的View
     */

    public void generateMainHeaderView() {

        mainHeaderView = new LinearLayout(mContext);
        mainHeaderView.setOrientation(LinearLayout.VERTICAL);
        final AbsListView.LayoutParams mainHeaderParams = new AbsListView.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT,
                AbsListView.LayoutParams.WRAP_CONTENT);
        mainHeaderView.setLayoutParams(mainHeaderParams);

        //去获得HeadView 视图
        VolleyRequest volleyRequest = new VolleyRequest(mContext, mQueue);
        JSONObject json = volleyRequest.getJsonParam();
        volleyRequest.setTag(marketItemFragment.navBean.getCode());
        volleyRequest.doGet(HttpConstant.MARKET_INDEX, json, new HttpListener<List<TypeDataJson>>() {
            @Override
            protected void onResponse(List<TypeDataJson> marketMainList) {
                marketItemFragment.pageLoadLayout.loadOver();
                for (TypeDataJson marketBean : marketMainList) {

                    String name = marketBean.getType();
                    switch (name) {
                        case "main":
                            try {
                                createMainView(JSON.parseObject(JSON.toJSONString(marketBean), MarketMainBean.class));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case "favor":
                            break;
                        case "hot":
                            try {
                                createHotView(JSON.parseObject(JSON.toJSONString(marketBean), MarketHotBean.class));
                            } catch (Exception e) {
                                e.printStackTrace();
                                MarketHotBean marketHotBean = new MarketHotBean();
                                marketHotBean.setType("hot");
                                MarketHotBean.DataBean dataBean = new MarketHotBean.DataBean();
                                dataBean.setData(JSON.parseArray(JSONObject.toJSONString(marketBean.getData()), MarketItemBean.class));
                                marketHotBean.setData(dataBean);
                                createHotView(marketHotBean);
                            }
                            break;
                        case "ad":
                            try {
                                AdJson ads = JSON.parseObject(JSON.toJSONString(marketBean.getData()), AdJson.class);
                                if (ads != null && (ads.getPic_ad() != null || (ads.getText_ad() != null && ads
                                        .getText_ad().size() > 0))) {
                                    createAdView(ads);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                    }
                }

                MarketConnectUtil.getInstance().sendSocketParams(
                        iBaseView,
                        marketCodeList,
                        MarketMainPresenter.this);

                marketItemFragment.refreshableView.addHeaderView(mainHeaderView);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                marketItemFragment.pageLoadLayout.loadError();
                super.onErrorResponse(error);
            }
        });
    }

    private void createAdView(AdJson ads) {
        createPaddingView(1);
        LinearLayout adView = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.news_header_ad, null);
        ImageView iv_ad = (ImageView) adView.findViewById(R.id.iv_ad);

        try {
            final AdItemJson mPicAd = ads.getPic_ad();
            if (mPicAd != null) {
                iv_ad.getLayoutParams().height = SystemUtil.dp2px(mContext, ads.getPic_ad().getImageHeight());

                String picture = mPicAd.getPicture();
                if (RegexValidateUtil.isEmpty(picture)) {
                    iv_ad.setVisibility(View.GONE);
                } else {
                    iv_ad.setVisibility(View.VISIBLE);
                }
                Glide.with(mContext).load(picture).error(R.mipmap.icon_def_news)
                        .placeholder(R.mipmap.icon_def_news).into(iv_ad);

                mainHeaderView.addView(adView);

                adView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, WebActivity.class);
                        intent.putExtra(IntentConstant.NAME, mPicAd.getTitle());
                        intent.putExtra(IntentConstant.WEBURL, mPicAd.getHref());
                        intent.putExtra(IntentConstant.AUTOOBTAINTITLE, true);
                        mContext.startActivity(intent);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mAdTextViewList = new ArrayList<>();
            List<AdItemJson> mTextAd = ads.getText_ad();
            if (mTextAd == null || mTextAd.size() == 0) {
                return;
            }
            LayoutInflater mInflater = LayoutInflater.from(mContext);

            for (final AdItemJson adItemJson : mTextAd) {
                View adLayoutView = mInflater.inflate(R.layout.item_news_ad, mainHeaderView, false);

                SkinnableTextView mAdTextView = (SkinnableTextView) adLayoutView.findViewById(R.id
                        .tv_news_ad_title);
                mAdTextView.setText(" • " + adItemJson.getTitle());
                SkinnableTextView mAdTraitView = (SkinnableTextView) adLayoutView.findViewById(R.id
                        .tv_news_ad_trait);

                mainHeaderView.addView(adLayoutView);

                mAdTextViewList.add(mAdTextView);
                mAdTextViewList.add(mAdTraitView);

                adLayoutView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, WebActivity.class);
                        intent.putExtra(IntentConstant.NAME, adItemJson.getTitle());
                        intent.putExtra(IntentConstant.WEBURL, adItemJson.getHref());
                        mContext.startActivity(intent);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        createPaddingView(1);

    }

    /*
     * 创建MainHeaderView
     */
    private void createMainView(MarketMainBean marketBean) {
        createPaddingView(1);

        for (MarketItemBean marketItemBean : marketBean.getData()) {
            marketCodeList.add(marketItemBean.getCode());
            marketItemFragment.marketMap.put(marketItemBean.getCode(), marketItemBean);

            marketItemBean.setChange(marketItemFragment.replacePositive(marketItemBean.getChange()));
            marketItemBean.setRange(marketItemFragment.replacePositive(marketItemBean.getRange()));
        }

        List<MarketItemBean> data = marketBean.getData();

        recommendView = new RollDotViewPager(mContext);
        RollViewPager rollViewPager = recommendView.getRollViewPager();
        rollViewPager.setGridMaxCount(6).setDataList(data).setGridViewItemData(new RollViewPager.GridViewItemData() {
            @Override
            public void itemData(List dataSubList, GridView gridView) {
                MarketGridAdapter adapter = new MarketGridAdapter(mContext, dataSubList);
                gridView.setAdapter(adapter);
                marketGridAdapters.add(adapter);
            }
        });
        recommendView.build();

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                SystemUtil.dp2px(mContext, 170));
        recommendView.setLayoutParams(lp);

        mainHeaderView.addView(recommendView);

        createPaddingView(6);
    }


    private void createHotView(MarketHotBean marketBean) {
        if (marketBean == null) {
            marketItemFragment.pageLoadLayout.loadError();
            return;
        }
        createPaddingView(6);

        MarketHotBean.DataBean data = marketBean.getData();
        advertLayout = new AdvertLayout(mContext);
        advertLayout.setAdvertData("热门行情", data.getAd(), data.getIcon());

        mainHeaderView.addView(advertLayout);

        View navigationView = LayoutInflater.from(mContext).inflate(R.layout.view_market_navigation, null);
        navigationView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.line_background2));
        navigationView.findViewById(R.id.view_line).setVisibility(View.GONE);
        tvTargetNav = (TextView) navigationView.findViewById(R.id.tv_target_nav);
        tvTargetNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                marketItemFragment.navClick(v);
            }
        });
        navigationView.setTag("navigationView");
        mainHeaderView.addView(navigationView);

        List<MarketItemBean> marketItemBeens = data.getData();
        for (MarketItemBean marketItemBean : marketItemBeens) {
            marketCodeList.add(marketItemBean.getCode());
            marketItemFragment.marketMap2.put(marketItemBean.getCode(), marketItemBean);

            marketItemBean.setChange(marketItemFragment.replacePositive(marketItemBean.getChange()));
            marketItemBean.setRange(marketItemFragment.replacePositive(marketItemBean.getRange()));

            marketItemBean.setSwitchTarget(marketItemBean.getRange());
        }

        marketMainItemAdapter = new MarketMainItemAdapter(mContext, marketItemBeens);
        marketItemFragment.refreshableView.setAdapter(marketMainItemAdapter);
    }

    private void createPaddingView(int heightPx) {
        createPaddingView(heightPx, mainHeaderView);
    }

    private void createPaddingView(int heightPx, LinearLayout parentLayout) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_line, null);
        int height = SystemUtil.dp2px(mContext, heightPx);
        int paddingColor = ContextCompat.getColor(mContext, R.color.bg_color2);
        view.setBackgroundColor(paddingColor);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
        parentLayout.addView(view, lp);
        view.setTag("lineTag");
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
        marketItemFragment.tvTargetNav.setText(marketItemFragment.switchItemType == 0 ? "涨跌幅" : "涨跌");
        if (tvTargetNav != null) {
            tvTargetNav.setText(marketItemFragment.switchItemType == 0 ? "涨跌幅" : "涨跌");
        }

        for (MarketItemBean marketItemBean : marketMainItemAdapter.dataList) {
            marketItemBean.setSwitchTarget(
                    marketItemFragment.switchItemType == 0 ? marketItemBean.getRange() : marketItemBean.getChange());
        }
    }

    /**
     * 改变主题
     */
    public void onChangeTheme() {

        boolean isNight = SPUtils.getBoolean(mContext, SpConstant.SETTING_DAY_NIGHT);
        try {
            if (mAdTextViewList != null) {
                for (SkinnableTextView skinnableTextView : mAdTextViewList) {
                    skinnableTextView.setTextColor(ContextCompat.getColor(mContext, R.color.font_color5));
                }
            }
            //重新设置热门行情主题色
            if (marketMainItemAdapter != null) {
                marketMainItemAdapter.notifyDataSetChanged();
            }
            if (marketGridAdapters != null) {
                for (MarketGridAdapter marketGridAdapter : marketGridAdapters) {
                    marketGridAdapter.notifyDataSetChanged();
                }
            }

            //重新设置头部行情主题
            if (recommendView != null) {
                recommendView.onChangeTheme();
            }


            if (mainHeaderView != null) {
                for (int i = 0; i < mainHeaderView.getChildCount(); i++) {
                    View childAt = mainHeaderView.getChildAt(i);
                    if ("lineTag".equals(childAt.getTag())) {
                        int paddingColor = ContextCompat.getColor(mContext, R.color.bg_color2);
                        childAt.setBackgroundColor(paddingColor);
                    } else if ("navigationView".equals(childAt.getTag())) {
                        childAt.setBackgroundColor(ContextCompat.getColor(mContext, R.color.line_background2));
                    }
                }
            }

            if (advertLayout != null) {
                advertLayout.onChangerTheme();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
