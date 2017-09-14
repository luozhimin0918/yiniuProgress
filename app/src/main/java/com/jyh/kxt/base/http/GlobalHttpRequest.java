package com.jyh.kxt.base.http;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.bean.SignInfoJson;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.user.json.UserJson;
import com.library.base.http.HttpDeliveryListener;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;

/**
 * Created by Mr'Dai on 2017/9/14.
 */

public class GlobalHttpRequest {

    private static GlobalHttpRequest globalHttpRequest;

    public static GlobalHttpRequest getInstance() {
        if (globalHttpRequest == null) {
            globalHttpRequest = new GlobalHttpRequest();
        }
        return globalHttpRequest;
    }

    /**
     * 签到状态，任务状态,存放临时内存
     */
    private static SignInfoJson signInfoJson;

    /**
     * 获取签到任务
     *
     * @param iBaseView
     */
    public void getSignInfo(IBaseView iBaseView, final HttpDeliveryListener<SignInfoJson> deliveryListener) {
        //todo  获取签到之后要比对
        Context mContext = iBaseView.getContext();
        RequestQueue mQueue = iBaseView.getQueue();

        if (signInfoJson != null) {
            if (deliveryListener != null) {
                deliveryListener.onResponse(signInfoJson);
            }
            return;
        }


        UserJson userInfo = LoginUtils.getUserInfo(mContext);
        if (userInfo == null) {
            return;
        }
        VolleyRequest request = new VolleyRequest(mContext, mQueue);
        request.setTag("signInfo");

        JSONObject jsonParam = request.getJsonParam();
        jsonParam.put("uid", userInfo.getUid());
        request.doPost(HttpConstant.COINS_SIGN, jsonParam, new HttpListener<String>() {
            @Override
            protected void onResponse(String signState) {
                signInfoJson = JSONObject.parseObject(signState, SignInfoJson.class);

                if (deliveryListener != null) {
                    deliveryListener.onResponse(signInfoJson);
                }
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                if (deliveryListener != null) {
                    deliveryListener.onErrorResponse();
                }
            }
        });
    }
}
