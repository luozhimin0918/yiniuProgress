package com.jyh.kxt.index.presenter;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.index.ui.fragment.ExploreFragment;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/26.
 */

public class ExplorePresenter extends BasePresenter {

    @BindObject ExploreFragment exploreFragment;
    private RequestQueue queue;
    private VolleyRequest request;

    public ExplorePresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void init() {
        if (queue == null)
            queue = exploreFragment.getQueue();
        if (request == null)
            request = new VolleyRequest(mContext, queue);
        request.doGet(HttpConstant.EXPLORE, new HttpListener<Object>() {
            @Override
            protected void onResponse(Object o) {

            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
            }
        });
    }
}
