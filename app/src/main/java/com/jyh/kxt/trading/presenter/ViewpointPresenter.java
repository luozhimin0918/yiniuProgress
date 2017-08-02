package com.jyh.kxt.trading.presenter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.custom.RollDotViewPager;
import com.jyh.kxt.base.custom.RollViewPager;
import com.jyh.kxt.trading.adapter.HotHeadAdapter;
import com.jyh.kxt.trading.adapter.ViewpointAdapter;
import com.jyh.kxt.trading.json.ViewPointBean;
import com.jyh.kxt.trading.json.ViewPointTradeBean;
import com.jyh.kxt.trading.ui.fragment.ViewpointFragment;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/7/26.
 */

public class ViewpointPresenter extends BasePresenter {

    @BindObject ViewpointFragment mViewpointFragment;

    private LinearLayout headLinearLayout;
    private List<ViewPointTradeBean> tradeBeanList = new ArrayList<>();

    public ViewpointPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void requestInitData() {
        //写上网络请求

        VolleyRequest mVolleyRequest = new VolleyRequest(mContext, mQueue);
        mVolleyRequest.setTag(getClass().getName());

        JSONObject mainParam = mVolleyRequest.getJsonParam();
        mainParam.put("type", "chosen");
        mVolleyRequest.doGet(HttpConstant.TRADE_MAIN, mainParam, new HttpListener<String>() {
            @Override
            protected void onResponse(String manJson) {
                ViewPointBean viewPointBean = JSONObject.parseObject(manJson, ViewPointBean.class);

                /**
                 * 处理Adapter
                 */
                tradeBeanList.addAll(viewPointBean.getTrade());

                ViewPointTradeBean viewPointTradeBean = new ViewPointTradeBean();
                viewPointTradeBean.setItemViewType(0);
                tradeBeanList.add(0, viewPointTradeBean);

                ViewpointAdapter viewpointAdapter = new ViewpointAdapter(mContext, tradeBeanList);
                mViewpointFragment.mPullPinnedListView.setAdapter(viewpointAdapter);

                /**
                 * 初始化LinearLayout
                 */
                headLinearLayout = new LinearLayout(mContext);
                headLinearLayout.setLayoutParams(
                        new ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT));
                headLinearLayout.setOrientation(LinearLayout.VERTICAL);
                /**
                 * 加入头部
                 */
                View mGridView = LayoutInflater.from(mContext)
                        .inflate(R.layout.view_viewpoint_grid_hot,
                                mViewpointFragment.mPullPinnedListView,
                                false);

                RollDotViewPager rollDotViewPager = (RollDotViewPager) mGridView.findViewById(R.id.rdvp_content);
                rollDotViewPager.setViewPageToDotAbout();
                RollViewPager rollViewPager = rollDotViewPager.getRollViewPager();
                rollViewPager
                        .setGridMaxCount(6)
                        .setDataList(viewPointBean.getHot())
                        .setGridViewItemData(
                                new RollViewPager.GridViewItemData() {
                                    @Override
                                    public void itemData(List dataSubList, GridView gridView) {
                                        HotHeadAdapter headAdapter =
                                                new HotHeadAdapter(mContext, dataSubList);
                                        gridView.setAdapter(headAdapter);
                                    }
                                });
                rollDotViewPager.build();
                headLinearLayout.addView(mGridView);


                //添加头部
                mViewpointFragment.mPullPinnedListView.getRefreshableView().addHeaderView(headLinearLayout);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
            }
        });
    }
}
