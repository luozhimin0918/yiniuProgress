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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
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
import com.jyh.kxt.databinding.ItemMarketRecommendBinding;
import com.jyh.kxt.market.adapter.MarketGridAdapter;
import com.jyh.kxt.market.adapter.MarketMainItemAdapter;
import com.jyh.kxt.market.bean.MarketItemBean;
import com.jyh.kxt.market.bean.MarketMainBean;
import com.jyh.kxt.market.ui.MarketDetailActivity;
import com.jyh.kxt.market.ui.fragment.MarketItemFragment;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;
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
        volleyRequest.doGet(HttpConstant.MARKET_INDEX, json, new HttpListener<List<MarketMainBean>>() {
            @Override
            protected void onResponse(List<MarketMainBean> marketMainList) {

                for (MarketMainBean marketBean : marketMainList) {

                    String name = marketBean.getType();
                    switch (name) {
                        case "main":
                            createMainView(marketBean);
                            break;
                        case "favor":
                            createFavorView(marketBean);
                            break;
                        case "hot":
                            createHotView(marketBean);
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
                super.onErrorResponse(error);
            }
        });
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
                SystemUtil.dp2px(mContext, 230));
        recommendView.setLayoutParams(lp);

        mainHeaderView.addView(recommendView);
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

        this.marketBean = marketBean;
        if (marketBean.getData() == null || marketBean.getData().size() == 0) {
            return;
        }
        if (hqLayoutView == null) {
            hqLayoutView = new LinearLayout(mContext);
            hqLayoutView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
                    .LayoutParams.WRAP_CONTENT));
            hqLayoutView.setTag("hqLayoutView");
            hqLayoutView.setOrientation(LinearLayout.VERTICAL);

            mainHeaderView.addView(hqLayoutView);
        }
        createPaddingView(6, hqLayoutView);

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
            marketItemFragment.marketMap.put(marketItemBean.getCode(), marketItemBean);

            marketItemBean.setChange(marketItemFragment.replacePositive(marketItemBean.getChange()));
            marketItemBean.setRange(marketItemFragment.replacePositive(marketItemBean.getRange()));

            ItemMarketRecommendBinding dataBinding = DataBindingUtil.inflate(mInflate,
                    R.layout.item_market_recommend,
                    null,
                    false);

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

        mainHeaderView.addView(navigationView);

        for (MarketItemBean marketItemBean : marketBean.getData()) {
            marketCodeList.add(marketItemBean.getCode());
            marketItemFragment.marketMap.put(marketItemBean.getCode(), marketItemBean);

            marketItemBean.setChange(marketItemFragment.replacePositive(marketItemBean.getChange()));
            marketItemBean.setRange(marketItemFragment.replacePositive(marketItemBean.getRange()));

            marketItemBean.setSwitchTarget(marketItemBean.getRange());
        }

        marketMainItemAdapter = new MarketMainItemAdapter(mContext, marketBean.getData());
        marketItemFragment.refreshableView.setAdapter(marketMainItemAdapter);
    }

    private void createPaddingView(int heightPx) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_line, null);
        int height = SystemUtil.dp2px(mContext, heightPx);
        int paddingColor = ContextCompat.getColor(mContext, R.color.bg_color2);
        view.setBackgroundColor(paddingColor);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
        mainHeaderView.addView(view, lp);
    }

    private void createPaddingView(int heightPx, LinearLayout parentLayout) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_line, null);
        int height = SystemUtil.dp2px(mContext, heightPx);
        int paddingColor = ContextCompat.getColor(mContext, R.color.bg_color2);
        view.setBackgroundColor(paddingColor);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
        parentLayout.addView(view, lp);
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

            //重新设置我的自选主题色
            if (marketBean.getData() == null || marketBean.getData().size() == 0) {
                return;
            }
            LayoutInflater mInflate = LayoutInflater.from(mContext);
            for (MarketItemBean marketItemBean : marketBean.getData()) {
                marketCodeList.add(marketItemBean.getCode());
                marketItemFragment.marketMap.put(marketItemBean.getCode(), marketItemBean);

                marketItemBean.setChange(marketItemFragment.replacePositive(marketItemBean.getChange()));
                marketItemBean.setRange(marketItemFragment.replacePositive(marketItemBean.getRange()));

                ItemMarketRecommendBinding dataBinding = DataBindingUtil.inflate(mInflate,
                        R.layout.item_market_recommend,
                        null,
                        false);

                dataBinding.setBean(marketItemBean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
