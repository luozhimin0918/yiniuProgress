package com.jyh.kxt.user.presenter;

import android.os.Handler;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.trading.json.ViewPointTradeBean;
import com.jyh.kxt.trading.util.TradeHandlerUtil;
import com.jyh.kxt.user.adapter.CollectPointAdapter;
import com.jyh.kxt.user.json.UserJson;
import com.jyh.kxt.user.ui.fragment.CollectPointFragment;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.window.ToastView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr'Dai on 2017/8/10.
 */

public class CollectPointPresenter extends BasePresenter {
    @BindObject CollectPointFragment collectPointFragment;

    public CollectPointPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    private PullToRefreshBase.Mode pullFromStart;
    private CollectPointAdapter collectPointAdapter;
    private List<ViewPointTradeBean> pointList = new ArrayList<>();

    private int localityCount = 0;
    private TradeHandlerUtil mTradeHandlerUtil;

    public void setMode(PullToRefreshBase.Mode pullFromStart) {
        this.pullFromStart = pullFromStart;
    }

    /**
     * 初始化收藏
     */
    public void initData() {
        UserJson userInfo = LoginUtils.getUserInfo(mContext);
        if (userInfo == null) {
            getLocalityData();
        } else {
            requestGetData();
        }
    }

    private void requestGetData() {
        VolleyRequest mVolleyRequest = new VolleyRequest(mContext, mQueue);
        mVolleyRequest.setTag(getClass().getName());
        JSONObject mainParam = mVolleyRequest.getJsonParam();

        mVolleyRequest.doGet(HttpConstant.TRADE_MAIN, mainParam, new HttpListener<String>() {
            @Override
            protected void onResponse(String manJson) {
                collectPointFragment.mPullPinnedListView.onRefreshComplete();

                if (pullFromStart == PullToRefreshBase.Mode.PULL_FROM_START) {

                } else {

                }
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
            }
        });
    }

    private void getLocalityData() {
        if (pullFromStart == PullToRefreshBase.Mode.PULL_FROM_START) {
            localityCount = 0;
        }

        mTradeHandlerUtil = TradeHandlerUtil.getInstance();
        List<ViewPointTradeBean> localityCollectList = mTradeHandlerUtil.getLocalityCollectList(mContext, localityCount, VarConstant.LIST_MAX_SIZE);
        if (localityCollectList.size() != 0) {
            localityCount += localityCollectList.size();
        }
        notifyAdapter(localityCollectList);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                collectPointFragment.mPullPinnedListView.onRefreshComplete();
            }
        }, 500);
    }

    private void notifyAdapter(List<ViewPointTradeBean> dataList) {
        if (pullFromStart == PullToRefreshBase.Mode.PULL_FROM_START) {
            if (dataList.size() == 0) {
                collectPointFragment.plRootView.setNullImgId(R.mipmap.icon_collect_null);
                collectPointFragment.plRootView.loadEmptyData();
            } else {
                collectPointFragment.plRootView.loadOver();

                pointList.clear();
                pointList.addAll(dataList);

                if (collectPointAdapter == null) {
                    collectPointAdapter = new CollectPointAdapter(mContext, pointList, collectPointFragment.articleContentPresenter);
                    collectPointFragment.mPullPinnedListView.setAdapter(collectPointAdapter);
                    collectPointFragment.setCollectPointAdapter(collectPointAdapter);
                } else {
                    collectPointAdapter.notifyDataSetChanged();
                }
            }
        } else {
            if (dataList.size() == 0) {
                ToastView.makeText3(mContext, "暂无更多数据");
//                collectPointFragment.mPullPinnedListView.noMoreData();
            } else {
                pointList.addAll(dataList);
                collectPointAdapter.notifyDataSetChanged();
            }
        }
    }
}
