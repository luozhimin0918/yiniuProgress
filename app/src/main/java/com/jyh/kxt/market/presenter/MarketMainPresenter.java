package com.jyh.kxt.market.presenter;

import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.utils.MarketConnectUtil;
import com.jyh.kxt.databinding.ItemMarketRecommendBinding;
import com.jyh.kxt.market.adapter.MarketMainItemAdapter;
import com.jyh.kxt.market.adapter.MarketRecommendAdapter;
import com.jyh.kxt.market.bean.MarketItemBean;
import com.jyh.kxt.market.bean.MarketMainBean;
import com.jyh.kxt.market.ui.fragment.MarketItemFragment;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;
import com.library.util.SystemUtil;
import com.library.widget.recycler.DividerGridItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr'Dai on 2017/4/25.
 * 行情 - 行情 - 首页
 */

public class MarketMainPresenter extends BasePresenter {

    @BindObject MarketItemFragment marketItemFragment;

    private LinearLayout mainHeaderView;

    public MarketMainPresenter(IBaseView iBaseView) {
        super(iBaseView);
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
        volleyRequest.doGet(HttpConstant.MARKET_INDEX, json, new HttpListener<List<MarketMainBean>>() {
            @Override
            protected void onResponse(List<MarketMainBean> marketMainList) {
                List<MarketItemBean> marketItemList = new ArrayList<>();

                for (MarketMainBean marketBean : marketMainList) {
                    marketItemList.addAll(marketBean.getData());

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

                MarketConnectUtil.getInstance().sendSocketParams(iBaseView, marketItemList);

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
        RecyclerView recommendView = new RecyclerView(mContext);
        GridLayoutManager layout = new GridLayoutManager(mContext, 3) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }

            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recommendView.setLayoutManager(layout);
        recommendView.addItemDecoration(new DividerGridItemDecoration(mContext));

        MarketRecommendAdapter adapter = new MarketRecommendAdapter(mContext, marketBean.getData());
        recommendView.setAdapter(adapter);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        mainHeaderView.addView(recommendView, lp);
    }

    private void createFavorView(MarketMainBean marketBean) {
        if (marketBean.getData() == null || marketBean.getData().size() == 0) {
            return;
        }
        createPaddingView(6);

        View titleBlue = LayoutInflater.from(mContext).inflate(R.layout.view_title_blue, mainHeaderView);
        TextView tvTitle = (TextView) titleBlue.findViewById(R.id.tv_title);
        tvTitle.setText("我的自选");

        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(mContext);
        int horizontalHeight = SystemUtil.dp2px(mContext, 80);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                horizontalHeight);

        mainHeaderView.addView(horizontalScrollView, lp);

        LinearLayout hqLayout = new LinearLayout(mContext);
        ViewGroup.LayoutParams hqParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                horizontalHeight);
        hqLayout.setLayoutParams(hqParams);

        LayoutInflater mInflate = LayoutInflater.from(mContext);

        for (MarketItemBean marketItemBean : marketBean.getData()) {

            ItemMarketRecommendBinding dataBinding = DataBindingUtil.inflate(mInflate,
                    R.layout.item_market_recommend,
                    hqLayout,
                    false);
            dataBinding.setBean(marketItemBean);

        }

        horizontalScrollView.addView(hqLayout);
    }

    private void createHotView(MarketMainBean marketBean) {
        createPaddingView(6);

        View titleBlue = LayoutInflater.from(mContext).inflate(R.layout.view_title_blue, mainHeaderView);
        TextView tvTitle = (TextView) titleBlue.findViewById(R.id.tv_title);
        tvTitle.setText("热门行情");

        View navigationView = LayoutInflater.from(mContext).inflate(R.layout.view_market_navigation, null);
        navigationView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.font_color61));
        navigationView.findViewById(R.id.view_line).setVisibility(View.GONE);

        mainHeaderView.addView(navigationView);

        MarketMainItemAdapter marketMainItemAdapter = new MarketMainItemAdapter(mContext, marketBean.getData());
        marketItemFragment.refreshableView.setAdapter(marketMainItemAdapter);
    }

    private void createPaddingView(int heightPx) {
        View view = new View(mContext);
        int height = SystemUtil.dp2px(mContext, heightPx);

        int paddingColor = ContextCompat.getColor(mContext, R.color.bg_color2);

        view.setBackgroundColor(paddingColor);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
        mainHeaderView.addView(view, lp);
    }
}
