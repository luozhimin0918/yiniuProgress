package com.jyh.kxt.explore.presenter;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.explore.ui.MoreActivity;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.util.EncryptionUtils;

import java.util.List;

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
        request.doGet(getUrl(request), new HttpListener<List>() {
            @Override
            protected void onResponse(List o) {
                moreActivity.init(o);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                moreActivity.loadError();
            }
        });
    }

    public void refresh() {
        request.doGet(getUrl(request), new HttpListener<List>() {
            @Override
            protected void onResponse(List o) {
                moreActivity.refresh(o);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
            }
        });
    }

    public void loadMore() {

    }

    private String getUrl(VolleyRequest request) {
        String url = "";
        JSONObject jsonParam = request.getJsonParam();
        String param = "";
        try {
            param = VarConstant.HTTP_CONTENT + EncryptionUtils.createJWT(VarConstant.KEY, jsonParam
                    .toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (type) {
            case VarConstant.EXPLORE_ACTIVITY:
                url = HttpConstant.EXPLORE_ACTIVITY + param;
                break;
            case VarConstant.EXPLORE_TOPIC:
                url = HttpConstant.EXPLORE_TOPIC + param;
                break;
        }
        return url;
    }
}
