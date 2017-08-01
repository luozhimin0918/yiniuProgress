package com.jyh.kxt.trading.presenter;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.trading.json.ColumnistNavJson;
import com.jyh.kxt.trading.ui.AuthorListActivity;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;

import java.util.List;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/7/31.
 */

public class AuthorListPresenter extends BasePresenter {

    private final VolleyRequest request;
    @BindObject AuthorListActivity activity;

    public AuthorListPresenter(IBaseView iBaseView) {
        super(iBaseView);
        request = new VolleyRequest(mContext, mQueue);
        request.setTag(getClass().getName());
    }

    public void initTabInfo() {
        request.doGet(HttpConstant.TRADING_COLUMNIST_NAV, request.getJsonParam(), new HttpListener<List<ColumnistNavJson>>() {
            @Override
            protected void onResponse(List<ColumnistNavJson> navs) {
                activity.initTabInfo(navs);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                activity.plRootView.loadError();
            }
        });
    }
}
