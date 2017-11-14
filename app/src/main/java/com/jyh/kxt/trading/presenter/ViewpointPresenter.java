package com.jyh.kxt.trading.presenter;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.jyh.kxt.base.json.AdItemJson;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.base.widget.night.heple.SkinnableTextView;
import com.jyh.kxt.datum.bean.AdJson;
import com.jyh.kxt.index.ui.AttentionActivity;
import com.jyh.kxt.index.ui.WebActivity;
import com.jyh.kxt.trading.adapter.HotHeadAdapter;
import com.jyh.kxt.trading.adapter.ViewpointAdapter;
import com.jyh.kxt.trading.json.ViewPointBean;
import com.jyh.kxt.trading.json.ViewPointTradeBean;
import com.jyh.kxt.trading.ui.AuthorListActivity;
import com.jyh.kxt.trading.ui.fragment.ViewpointFragment;
import com.jyh.kxt.user.json.UserJson;
import com.jyh.kxt.user.ui.LoginActivity;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;
import com.library.util.RegexValidateUtil;
import com.library.util.SPUtils;
import com.library.util.SystemUtil;
import com.library.widget.handmark.PullToRefreshBase;

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

    public View mGridHotViewLayout;
    public RollDotViewPager rollDotViewPager;
    public ViewpointAdapter viewpointAdapter;
    public View vLine,vLine2;
    private ImageView ivAd;
    private LinearLayout llAd;
    private AdJson ads;

    private List<SkinnableTextView> adTvs=new ArrayList<>();
    private List<SkinnableTextView> adTvs2=new ArrayList<>();


    public ViewpointPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void requestInitData(final PullToRefreshBase.Mode pullFromStart) {
        //写上网络请求
        VolleyRequest mVolleyRequest = new VolleyRequest(mContext, mQueue);
        mVolleyRequest.setTag(getClass().getName());
        JSONObject mainParam = mVolleyRequest.getJsonParam();


        String requestNavigationType = "";
        switch (viewpointAdapter == null ? 0 : viewpointAdapter.navigationTabClickPosition) {
            case 0:
                requestNavigationType = "chosen";
                break;
            case 1:
                requestNavigationType = "all";
                break;
            case 2:
                requestNavigationType = "follow";
                break;
        }
        mainParam.put("type", requestNavigationType);

        UserJson userInfo = LoginUtils.getUserInfo(mContext);
        if (userInfo == null && "follow".equals(requestNavigationType)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mViewpointFragment.mPllContent.loadOver();
                    mViewpointFragment.mPullPinnedListView.onRefreshComplete();
                    footViewLoadIng(false);
                }
            }, 500);
            return;
        }
        if (userInfo != null) {
            mainParam.put("uid", userInfo.getUid());
        }

        if (pullFromStart == PullToRefreshBase.Mode.PULL_FROM_END) {
            footViewLoadIng(true);

            List<ViewPointTradeBean> tradeBeanList = viewpointAdapter.pointListMap.get(requestNavigationType);
            String lastId = tradeBeanList.get(tradeBeanList.size() - 1).o_id;
            mainParam.put("last_id", lastId);
        }

        mVolleyRequest.doGet(HttpConstant.TRADE_MAIN, mainParam, new HttpListener<String>() {
            @Override
            protected void onResponse(String manJson) {

                boolean isNight= SPUtils.getBoolean(mContext, SpConstant.SETTING_DAY_NIGHT);
                if (pullFromStart == PullToRefreshBase.Mode.PULL_FROM_START) {

                    if (headLinearLayout != null) {
                        mViewpointFragment.mPullPinnedListView.getRefreshableView().removeHeaderView(headLinearLayout);
                    }

                    mViewpointFragment.mPllContent.loadOver();
                    ViewPointBean viewPointBean = JSONObject.parseObject(manJson, ViewPointBean.class);

                    /**
                     * 处理Adapter
                     */
                    if (viewpointAdapter == null) {
                        viewpointAdapter = new ViewpointAdapter(mContext, viewPointBean.getTrade());
                        viewpointAdapter.bindListView(mViewpointFragment.mPullPinnedListView);
                    } else {
                        List<ViewPointTradeBean> newTradeBeanList = viewPointBean.getTrade();
                        viewpointAdapter.refreshAdapterData(newTradeBeanList, pullFromStart);
                    }
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
                    mGridHotViewLayout = LayoutInflater.from(mContext)
                            .inflate(R.layout.view_viewpoint_grid_hot,
                                    mViewpointFragment.mPullPinnedListView,
                                    false);
                    //查看更多
                    mGridHotViewLayout.findViewById(R.id.tv_all).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mContext.startActivity(new Intent(mContext, AuthorListActivity.class));
                        }
                    });
                    //我的关注
                    mGridHotViewLayout.findViewById(R.id.tv_myattention).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (LoginUtils.isLogined(mContext)) {
                                mContext.startActivity(new Intent(mContext, AttentionActivity.class));
                            } else {
                                showLoginDialog();
                            }
                        }
                    });

                    vLine = mGridHotViewLayout.findViewById(R.id.v_line);
                    //广告
                    vLine2=mGridHotViewLayout.findViewById(R.id.v_line2);
                    ViewGroup adRoot= (ViewGroup) mGridHotViewLayout.findViewById(R.id.ll_ad_root);
                    ivAd= (ImageView) adRoot.findViewById(R.id.iv_ad);
                    llAd= (LinearLayout) adRoot.findViewById(R.id.ll_ad);
                    if(viewPointBean.getAds()!=null){
                        ads=viewPointBean.getAds();
                        try {
                            final AdItemJson mPicAd = ads.getPic_ad();
                            if (mPicAd != null) {
                                ivAd.getLayoutParams().height = SystemUtil.dp2px(mContext, ads.getPic_ad().getImageHeight());

                                String picture = mPicAd.getPicture();
                                if (RegexValidateUtil.isEmpty(picture)) {
                                    ivAd.setVisibility(View.GONE);
                                } else {
                                    ivAd.setVisibility(View.VISIBLE);
                                }
                                Glide.with(mContext).load(picture).error(R.mipmap.icon_def_news)
                                        .placeholder(R.mipmap.icon_def_news).into(ivAd);

                                adRoot.setOnClickListener(new View.OnClickListener() {
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
                            List<AdItemJson> mTextAd = ads.getText_ad();
                            if (mTextAd != null && mTextAd.size() != 0) {
                                LayoutInflater mInflater = LayoutInflater.from(mContext);
                                adTvs.clear();
                                adTvs2.clear();
                                for (final AdItemJson adItemJson : mTextAd) {
                                    View adLayoutView = mInflater.inflate(R.layout.item_news_ad, adRoot, false);

                                    SkinnableTextView mAdTextView = (SkinnableTextView) adLayoutView.findViewById(R.id
                                            .tv_news_ad_title);
                                    mAdTextView.setText(" • " + adItemJson.getTitle());

                                    mAdTextView.setTextColor(Color.parseColor(isNight?adItemJson.getNight_color():adItemJson.getDay_color()));

                                    SkinnableTextView mAdTraitView = (SkinnableTextView) adLayoutView.findViewById(R.id
                                            .tv_news_ad_trait);

                                    adLayoutView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(mContext, WebActivity.class);
                                            intent.putExtra(IntentConstant.NAME, adItemJson.getTitle());
                                            intent.putExtra(IntentConstant.WEBURL, adItemJson.getHref());
                                            mContext.startActivity(intent);
                                        }
                                    });
                                    adTvs.add(mAdTextView);
                                    adTvs2.add(mAdTraitView);
                                    llAd.addView(adLayoutView);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }else{
                        adRoot.setVisibility(View.GONE);
                        vLine2.setVisibility(View.GONE);
                    }

                    rollDotViewPager = (RollDotViewPager) mGridHotViewLayout.findViewById(R.id.rdvp_content);
                    rollDotViewPager.setViewPageToDotAbout();
                    RollViewPager rollViewPager = rollDotViewPager.getRollViewPager();
                    rollViewPager.setNumColumns(4).setGridMaxCount(8).setDataList(viewPointBean.getHot())
                            .setShowPaddingLine(false)
                            .setGridViewItemData(
                                    new RollViewPager.GridViewItemData() {
                                        @Override
                                        public void itemData(List dataSubList, GridView gridView) {
                                            HotHeadAdapter headAdapter = new HotHeadAdapter(mContext, dataSubList);
                                            gridView.setAdapter(headAdapter);
                                        }
                                    });
                    rollViewPager.setEnableBackgroundColor(false);
                    rollDotViewPager.build();
                    headLinearLayout.addView(mGridHotViewLayout);

                    //添加头部
                    mViewpointFragment.mPullPinnedListView.getRefreshableView().addHeaderView(headLinearLayout);
                } else {
                    ViewPointBean viewPointBean = JSONObject.parseObject(manJson, ViewPointBean.class);
                    List<ViewPointTradeBean> newTradeBeanList = viewPointBean.getTrade();

                    if (newTradeBeanList.size() == 0) {
                        mViewpointFragment.mPullPinnedListView.addFootNoMoreData();
                    } else {
                        viewpointAdapter.refreshAdapterData(newTradeBeanList, pullFromStart);
                    }
                    footViewLoadIng(false);
                }
                mViewpointFragment.mPullPinnedListView.onRefreshComplete();
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                mViewpointFragment.mPllContent.loadError();
                mViewpointFragment.mPullPinnedListView.onRefreshComplete();
                footViewLoadIng(false);
            }
        });
    }

    private TextView tvFootView;

    private void footViewLoadIng(boolean isShow) {
        if (isShow) {
            tvFootView = new TextView(mContext);
            tvFootView.setText("加载中...");
            tvFootView.setGravity(Gravity.CENTER);
            AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams
                    (
                            AbsListView.LayoutParams.MATCH_PARENT,
                            SystemUtil.dp2px(mContext, 35)
                    );

            tvFootView.setLayoutParams(layoutParams);
            mViewpointFragment.mPullPinnedListView.getRefreshableView().addFooterView(tvFootView);
        } else {
            if (tvFootView != null) {
                mViewpointFragment.mPullPinnedListView.getRefreshableView().removeFooterView(tvFootView);
                tvFootView = null;
            }
        }
    }

    public void onChangeTheme(){

        boolean isNight=SPUtils.getBoolean(mContext,SpConstant.SETTING_DAY_NIGHT);
        viewpointAdapter.notifyDataSetChanged();
        rollDotViewPager.onChangeTheme();
        vLine.setBackgroundColor(ContextCompat.getColor(mContext, R.color.line_color2));
        vLine2.setBackgroundColor(ContextCompat.getColor(mContext, R.color.line_color2));

        View viewPointHotTitle = mGridHotViewLayout.findViewById(R.id.viewpoint_hot_title);
        viewPointHotTitle.setBackgroundColor(ContextCompat.getColor(mContext, R.color.slidingTabLayout_bgColor));

        if(ads!=null&&ads.getText_ad()!=null){

            List<AdItemJson> text_ad = ads.getText_ad();
            int size = text_ad.size();
            for (int i = 0; i < size; i++) {
                adTvs.get(i).setTextColor(Color.parseColor(isNight?text_ad.get(i).getNight_color():text_ad.get(i).getDay_color()));
                adTvs2.get(i).setTextColor(ContextCompat.getColor(mContext,R.color.font_color6));
            }

        }

    }

    /**
     * 显示登录dialog
     */
    public void showLoginDialog() {
        if (loginPop == null) {
            loginPop = new AlertDialog.Builder(mContext)
                    .setTitle("温馨提示")
                    .setMessage("请先登录")
                    .setNegativeButton("登录", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mContext.startActivity(new Intent(mContext, LoginActivity.class));
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
