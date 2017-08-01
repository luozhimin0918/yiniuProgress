package com.jyh.kxt.trading.presenter;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.trading.ui.ViewpointFragment;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/7/26.
 */

public class ViewpointPresenter extends BasePresenter {

    @BindObject ViewpointFragment mViewpointFragment;

    public ViewpointPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void requestInitData() {
        //写上网络请求

        VolleyRequest mVolleyRequest = new VolleyRequest(mContext, mQueue);
        mVolleyRequest.setTag(getClass().getName());

        JSONObject mainParam = mVolleyRequest.getJsonParam();
        mainParam.put("type","chosen");
        mVolleyRequest.doGet(HttpConstant.TRADE_MAIN, mainParam, new HttpListener<String>() {
            @Override
            protected void onResponse(String mainJson) {


                System.out.println(mainJson);
//                ViewpointAdapter viewpointAdapter = new ViewpointAdapter(mContext);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
            }
        });


//        View mGridView = LayoutInflater.from(mContext)
//                .inflate(R.layout.view_viewpoint_grid_hot,
//                        mViewpointFragment.mPullPinnedListView,
//                        false);
//
//
//        //添加头部
//        mViewpointFragment.mPullPinnedListView.getRefreshableView().addHeaderView(mGridView);

    }
}
