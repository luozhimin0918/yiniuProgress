package com.jyh.kxt.market.presenter;

import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.market.adapter.MarketRecommendAdapter;
import com.jyh.kxt.market.bean.MarketMainBean;
import com.jyh.kxt.market.ui.fragment.MarketItemFragment;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;

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
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
            }
        });

        mainHeaderView.setBackgroundColor(Color.RED);

        ListView refreshableView = marketItemFragment.ptrlvContent.getRefreshableView();
        refreshableView.addHeaderView(mainHeaderView);
    }

    /*
     * 创建MainHeaderView
     */
    private void createMainView(MarketMainBean marketBean) {
        RecyclerView recommendView = new RecyclerView(mContext);
        recommendView.setLayoutManager(new GridLayoutManager(mContext, 3));

        MarketRecommendAdapter adapter = new MarketRecommendAdapter(mContext, marketBean.getData());
        recommendView.setAdapter(adapter);

        mainHeaderView.addView(recommendView);
    }

    private void createFavorView(MarketMainBean marketBean) {
        View titleBlue = LayoutInflater.from(mContext).inflate(R.layout.view_title_blue, mainHeaderView);
        TextView tvTitle = (TextView) titleBlue.findViewById(R.id.tv_title);
        tvTitle.setText("我的自选");

    }

    private void createHotView(MarketMainBean marketBean) {
        View titleBlue = LayoutInflater.from(mContext).inflate(R.layout.view_title_blue, mainHeaderView);
        TextView tvTitle = (TextView) titleBlue.findViewById(R.id.tv_title);
        tvTitle.setText("热门行情");

    }
}
