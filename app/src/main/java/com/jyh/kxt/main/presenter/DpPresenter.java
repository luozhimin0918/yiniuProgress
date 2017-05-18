package com.jyh.kxt.main.presenter;

import com.android.volley.VolleyError;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.main.json.NewsNavJson;
import com.jyh.kxt.main.ui.activity.DpActivity;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;

import java.util.List;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/16.
 */

public class DpPresenter extends BasePresenter {

    @BindObject DpActivity dpActivity;
    private VolleyRequest request;

    public DpPresenter(IBaseView iBaseView) {
        super(iBaseView);
        if (request == null) {
            request = new VolleyRequest(mContext, mQueue);
            request.setTag(getClass().getName());
        }
    }

    public void initActionBar() {
        request.doGet(HttpConstant.DP_CODE, new HttpListener<List<NewsNavJson>>() {
            @Override
            protected void onResponse(List<NewsNavJson> navs) {
                dpActivity.initActionBar(navs);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                dpActivity.plRootView.loadError();
            }
        });
    }
}
