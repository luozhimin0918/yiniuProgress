package com.jyh.kxt.datum.presenter;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.datum.ui.DatumHistoryActivity;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;
import com.library.util.LogUtil;

/**
 * Created by Mr'Dai on 2017/5/26.
 */

public class DatumHistoryPresenter extends BasePresenter {
    @BindObject DatumHistoryActivity datumHistoryActivity;

    public DatumHistoryPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }


    public void requestInitInfo() {
        VolleyRequest volleyRequest = new VolleyRequest(mContext, mQueue);
        JSONObject jsonParam = volleyRequest.getJsonParam();
        jsonParam.put("code", datumHistoryActivity.datumCode);

        volleyRequest.doPost(HttpConstant.DATA_FINANCE, jsonParam, new HttpListener<String>() {
            @Override
            protected void onResponse(String str) {
                LogUtil.e(LogUtil.TAG, "onResponse() called with: str = [" + str + "]");
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
            }
        });
    }
}
