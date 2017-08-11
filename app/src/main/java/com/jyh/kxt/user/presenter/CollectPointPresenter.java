package com.jyh.kxt.user.presenter;

import android.os.Handler;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.annotation.ObserverData;
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
        mTradeHandlerUtil = TradeHandlerUtil.getInstance();
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
            //将本地数据提交到网络，再请求网络数据
            mTradeHandlerUtil.updata(mContext, new ObserverData<Boolean>() {
                @Override
                public void callback(Boolean aBoolean) {
                    requestGetData();
                }

                @Override
                public void onError(Exception e) {
                    requestGetData();
                }
            }, localityCount);
        }
    }

    private void requestGetData() {
        VolleyRequest mVolleyRequest = new VolleyRequest(mContext, mQueue);
        mVolleyRequest.setTag(getClass().getName());
        final JSONObject mainParam = mVolleyRequest.getJsonParam();

        mainParam.put(VarConstant.HTTP_UID, LoginUtils.getUserInfo(mContext).getUid());

        mVolleyRequest.doGet(HttpConstant.COLLECT_POINT, mainParam, new HttpListener<List<ViewPointTradeBean>>() {
            @Override
            protected void onResponse(List<ViewPointTradeBean> manJson) {
                collectPointFragment.mPullPinnedListView.onRefreshComplete();
                if (manJson == null || manJson.size() == 0) {
                    collectPointFragment.plRootView.setNullImgId(R.mipmap.icon_collect_null);
                    collectPointFragment.plRootView.setNullText(mContext.getString(R.string.error_collect_null));
                    collectPointFragment.plRootView.loadEmptyData();
                } else {
                    if (pullFromStart == PullToRefreshBase.Mode.PULL_FROM_START) {
                        notifyAdapter(manJson);
                        collectPointFragment.plRootView.loadOver();
                    } else {
                        notifyAdapter(manJson);
                    }
                }
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                collectPointFragment.plRootView.loadError();
            }
        });
    }

    private void getLocalityData() {
        if (pullFromStart == PullToRefreshBase.Mode.PULL_FROM_START) {
            localityCount = 0;
        }
        List<ViewPointTradeBean> localityCollectList = mTradeHandlerUtil.getLocalityCollectList(mContext, localityCount, VarConstant
                .LIST_MAX_SIZE);
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
