package com.jyh.kxt.market.presenter;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
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
import com.jyh.kxt.base.utils.MarketConnectUtil;
import com.jyh.kxt.base.utils.MarketUtil;
import com.jyh.kxt.base.widget.night.heple.SkinnableTextView;
import com.jyh.kxt.databinding.ItemMarketRecommend2Binding;
import com.jyh.kxt.databinding.ItemMarketRecommendBinding;
import com.jyh.kxt.index.json.TypeDataJson;
import com.jyh.kxt.index.ui.WebActivity;
import com.jyh.kxt.main.json.AdJson;
import com.jyh.kxt.market.adapter.MarketGridAdapter;
import com.jyh.kxt.market.adapter.MarketMainItemAdapter;
import com.jyh.kxt.market.bean.MarketItemBean;
import com.jyh.kxt.market.bean.MarketMainBean;
import com.jyh.kxt.market.ui.MarketDetailActivity;
import com.jyh.kxt.market.ui.fragment.MarketItemFragment;
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
    private MarketMainBean marketBean;
    private ArrayList<SkinnableTextView> mAdTextViewList;

    public MarketMainPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void eventBusUpdate(List<MarketItemBean> marketList) {

        LinearLayout hqLayoutView = (LinearLayout) mainHeaderView.findViewWithTag("hqLayoutView");
        if (hqLayoutView != null) {
            hqLayoutView.removeAllViews();
        }

        MarketMainBean marketMainBean = new MarketMainBean();
        marketMainBean.setData(marketList);

        createFavorView(marketMainBean, hqLayoutView);
    }

    /**
     * 生成首页的View
     */

    public void generateMainHeaderView() {

        mainHeaderView = new LinearLayout(mContext);
        mainHeaderView.setOrientation(LinearLayout.VERTICAL);
        AbsListView.LayoutParams mainHeaderParams = new AbsListView.LayoutParams(
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
                            try {
                                createFavorView(JSON.parseObject(JSON.toJSONString(marketBean), MarketMainBean.class));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case "hot":
                            try {
                                createHotView(JSON.parseObject(JSON.toJSONString(marketBean), MarketMainBean.class));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case "ad":
                            try {
                                AdJson ads = JSON.parseObject(JSON.toJSONString(marketBean.getData()), AdJson.class);
                                if (ads != null && (ads.getPic_ad() != null || (ads.getText_ad() != null && ads.getText_ad().size() > 0)))
                                    createAdView(ads);
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
            final AdJson.AdItemJson mPicAd = ads.getPic_ad();

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
                    intent.putExtra(IntentConstant.NAME, "广告");
                    intent.putExtra(IntentConstant.WEBURL, mPicAd.getHref());
                    mContext.startActivity(intent);
                }
            });
            try {
                mAdTextViewList = new ArrayList<>();
                List<AdJson.AdItemJson> mTextAd = ads.getText_ad();
                LayoutInflater mInflater = LayoutInflater.from(mContext);

                for (final AdJson.AdItemJson adItemJson : mTextAd) {
                    View adLayoutView = mInflater.inflate(R.layout.item_news_ad, mainHeaderView, false);

                    SkinnableTextView mAdTextView = (SkinnableTextView) adLayoutView.findViewById(R.id.tv_news_ad_title);
                    mAdTextView.setText(" • " + adItemJson.getTitle());
                    SkinnableTextView mAdTraitView = (SkinnableTextView) adLayoutView.findViewById(R.id.tv_news_ad_trait);

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
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void createFavorView(MarketMainBean marketBean) {

        Boolean isInit = SPUtils.getBoolean(mContext, SpConstant.INIT_MARKET_MY_OPTION);
        if (isInit) {
            List<MarketItemBean> data = marketBean.getData();
            data.clear();
            data.addAll(MarketUtil.getMarketEditOption(mContext));
        }

        createFavorView(marketBean, null);
    }

    private void createFavorView(MarketMainBean marketBean, LinearLayout hqLayoutView) {
        if (hqLayoutView == null) {
            hqLayoutView = new LinearLayout(mContext);
            hqLayoutView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
                    .LayoutParams.WRAP_CONTENT));
            hqLayoutView.setTag("hqLayoutView");
            hqLayoutView.setOrientation(LinearLayout.VERTICAL);

            mainHeaderView.addView(hqLayoutView);
        }

        this.marketBean = marketBean;
        if (marketBean.getData() == null || marketBean.getData().size() == 0) {
            return;
        }


        View titleBlue = LayoutInflater.from(mContext).inflate(R.layout.view_title_blue, null);
        TextView tvTitle = (TextView) titleBlue.findViewById(R.id.tv_title);
        tvTitle.setText("我的自选");
        hqLayoutView.addView(titleBlue);

        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(mContext);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        horizontalScrollView.setHorizontalScrollBarEnabled(false);

        hqLayoutView.addView(horizontalScrollView, lp);

        LinearLayout hqLayout = new LinearLayout(mContext);
        ViewGroup.LayoutParams hqParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        hqLayout.setLayoutParams(hqParams);

        LayoutInflater mInflate = LayoutInflater.from(mContext);

        for (final MarketItemBean marketItemBean : marketBean.getData()) {
            marketCodeList.add(marketItemBean.getCode());
            marketItemFragment.marketMap1.put(marketItemBean.getCode(), marketItemBean);

            marketItemBean.setChange(marketItemFragment.replacePositive(marketItemBean.getChange()));
            marketItemBean.setRange(marketItemFragment.replacePositive(marketItemBean.getRange()));

            ItemMarketRecommend2Binding dataBinding = DataBindingUtil.inflate(mInflate,
                    R.layout.item_market_recommend2,
                    hqLayout,
                    false);

            int nameFontColor = ContextCompat.getColor(mContext, R.color.font_color5);
            dataBinding.setNameFontColor(nameFontColor);

            dataBinding.setBean(marketItemBean);
            View v = dataBinding.getRoot();
            hqLayout.addView(v);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, MarketDetailActivity.class);
                    intent.putExtra(IntentConstant.MARKET, marketItemBean);
                    mContext.startActivity(intent);
                }
            });
        }
        MarketUtil.saveMarketEditOption(mContext, marketBean.getData(), 0);
        horizontalScrollView.addView(hqLayout);
    }

    private void createHotView(MarketMainBean marketBean) {
        createPaddingView(6);

        View titleBlue = LayoutInflater.from(mContext).inflate(R.layout.view_title_blue, null);
        TextView tvTitle = (TextView) titleBlue.findViewById(R.id.tv_title);
        tvTitle.setText("热门行情");
        mainHeaderView.addView(titleBlue);

        View navigationView = LayoutInflater.from(mContext).inflate(R.layout.view_market_navigation, null);
        navigationView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.font_color61));
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

        for (MarketItemBean marketItemBean : marketBean.getData()) {
            marketCodeList.add(marketItemBean.getCode());
            marketItemFragment.marketMap2.put(marketItemBean.getCode(), marketItemBean);

            marketItemBean.setChange(marketItemFragment.replacePositive(marketItemBean.getChange()));
            marketItemBean.setRange(marketItemFragment.replacePositive(marketItemBean.getRange()));

            marketItemBean.setSwitchTarget(marketItemBean.getRange());
        }

        marketMainItemAdapter = new MarketMainItemAdapter(mContext, marketBean.getData());
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
        marketItemFragment.tvTargetNav.setText(marketItemFragment.switchItemType == 0 ? "涨跌幅" : "涨跌额");
        if (tvTargetNav != null) {
            tvTargetNav.setText(marketItemFragment.switchItemType == 0 ? "涨跌幅" : "涨跌额");
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
                        childAt.setBackgroundColor(ContextCompat.getColor(mContext, R.color.font_color61));
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
