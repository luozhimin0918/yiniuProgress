package com.jyh.kxt.explore.presenter;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.explore.ui.MoreActivity;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/8.
 */

public class MorePresenter extends BasePresenter {

    @BindObject MoreActivity moreActivity;

    private String type;
    private VolleyRequest request;

    public MorePresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void init(String type) {
        this.type = type;
        if (request == null)
            request = new VolleyRequest(mContext, mQueue);
        request.doGet(getUrl(request), new HttpListener<Object>() {
            @Override
            protected void onResponse(Object o) {

            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
            }
        });
    }

    private String getUrl(VolleyRequest request) {
        String url = "";
        switch (type) {
            case VarConstant.EXPLORE_ACTIVITY:
                break;
            case VarConstant.EXPLORE_BLOG_WRITER:
                break;
            case VarConstant.EXPLORE_TOPIC:
                break;
        }
        JSONObject jsonParam = request.getJsonParam();
        return url;
    }
}
