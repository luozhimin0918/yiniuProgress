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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        //fixkxt 2017/9/15 9:13 describe:  需要检查接口返回
        Context mContext = iBaseView.getContext();
        RequestQueue mQueue = iBaseView.getQueue();

        UserJson userInfo = LoginUtils.getUserInfo(mContext);
        if (userInfo == null) {
            return;
        }

        if (signInfoJson != null) {

            long currentYMDLong = 0L;
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                String formatYMD = sdf.format(new Date());
                Date parseYMD = sdf.parse(formatYMD);
                currentYMDLong = parseYMD.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (signInfoJson.getAcquireTime() == currentYMDLong && signInfoJson.getUid().equals(userInfo.getUid())) {
                if (deliveryListener != null) {
                    deliveryListener.onResponse(signInfoJson);
                }
                return;
            }
        }


        VolleyRequest request = new VolleyRequest(mContext, mQueue);
        request.setTag("signInfo");

        JSONObject jsonParam = request.getJsonParam();
        jsonParam.put("uid", userInfo.getUid());

        request.doPost(HttpConstant.COINS_SIGN, jsonParam, new HttpListener<SignInfoJson>() {
            @Override
            protected void onResponse(SignInfoJson signState) {
                signInfoJson = signState;
                signInfoJson.setAcquireTime();

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
