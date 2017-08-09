package com.jyh.kxt.trading.presenter;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
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
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.index.ui.AttentionActivity;
import com.jyh.kxt.trading.adapter.HotHeadAdapter;
import com.jyh.kxt.trading.adapter.ViewpointAdapter;
import com.jyh.kxt.trading.json.ViewPointBean;
import com.jyh.kxt.trading.json.ViewPointTradeBean;
import com.jyh.kxt.trading.ui.AuthorListActivity;
import com.jyh.kxt.trading.ui.fragment.ViewpointFragment;
import com.jyh.kxt.user.ui.LoginOrRegisterActivity;
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

    private AlertDialog loginPop;
    private LinearLayout headLinearLayout;
    public ViewpointAdapter viewpointAdapter;

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
                mViewpointFragment.mPllContent.loadOver();
                ViewPointBean viewPointBean = JSONObject.parseObject(manJson, ViewPointBean.class);

                /**
                 * 处理Adapter
                 */
                tradeBeanList.addAll(viewPointBean.getTrade());

                viewpointAdapter = new ViewpointAdapter(mContext, tradeBeanList, "chosen");
                viewpointAdapter.bindListView(mViewpointFragment.mPullPinnedListView);

                /**
                 * 初始化LinearLayout
                 */
                headLinearLayout = new LinearLayout(mContext);
                headLinearLayout.setLayoutParams(
                        new AbsListView.LayoutParams(
                                AbsListView.LayoutParams.MATCH_PARENT,
                                AbsListView.LayoutParams.WRAP_CONTENT));
                headLinearLayout.setOrientation(LinearLayout.VERTICAL);
                /**
                 * 加入头部
                 */
                View mGridView = LayoutInflater.from(mContext)
                        .inflate(R.layout.view_viewpoint_grid_hot,
                                mViewpointFragment.mPullPinnedListView,
                                false);
                //查看更多
                mGridView.findViewById(R.id.tv_all).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mContext.startActivity(new Intent(mContext, AuthorListActivity.class));
                    }
                });
                //我的关注
                mGridView.findViewById(R.id.tv_myattention).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (LoginUtils.isLogined(mContext)) {
                            mContext.startActivity(new Intent(mContext, AttentionActivity.class));
                        } else {
                            showLoginDialog();
                        }
                    }
                });

                RollDotViewPager rollDotViewPager = (RollDotViewPager) mGridView.findViewById(R.id.rdvp_content);
                rollDotViewPager.setViewPageToDotAbout();
                RollViewPager rollViewPager = rollDotViewPager.getRollViewPager();
                rollViewPager
                        .setNumColumns(4)
                        .setGridMaxCount(8)
                        .setDataList(viewPointBean.getHot())
                        .setShowPaddingLine(false)
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
                mViewpointFragment.mPllContent.loadError();
            }
        });
    }


    /**
     * 显示登录dialog
     */
    public void showLoginDialog() {
        if (loginPop == null) {
            loginPop = new AlertDialog.Builder(mContext)
                    .setTitle("提醒")
                    .setMessage("请先登录")
                    .setNegativeButton("登录", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mContext.startActivity(new Intent(mContext, LoginOrRegisterActivity.class));
                        }
                    }).setPositiveButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create();
        }
        if (loginPop.isShowing()) {
            loginPop.dismiss();
        }
        loginPop.show();
    }
}
