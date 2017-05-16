package com.jyh.kxt.base.utils;

import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.market.bean.MarketItemBean;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;

import java.util.List;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketConnectionHandler;
import de.tavendo.autobahn.WebSocketException;

/**
 * Created by Mr'Dai on 2017/5/12.
 */

public class MarketConnectUtil {
    private static MarketConnectUtil marketConnectUtil;

    public static MarketConnectUtil getInstance() {
        if (marketConnectUtil == null) {
            marketConnectUtil = new MarketConnectUtil();
        }
        return marketConnectUtil;
    }

    private WebSocketConnection mConnection = new WebSocketConnection();

    public void sendSocketParams(IBaseView iBaseView, List<MarketItemBean> list) {
//        JSONArray jsonArray = new JSONArray();
//
//        for (MarketItemBean marketItemBean : list) {
//            jsonArray.add(marketItemBean.getCode());
//        }
//
//        sendSocketParams(iBaseView, jsonArray);
    }

    public void sendSocketParams(IBaseView iBaseView, JSONArray jsonArray) {
        if (!mConnection.isConnected()) {
            requestConnectToken(iBaseView, jsonArray);
        } else {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cmd", "login");
            jsonObject.put("codes", jsonArray.toString());
            mConnection.sendTextMessage(jsonObject.toJSONString());
        }
    }

    private void requestConnectToken(IBaseView iBaseView, final JSONArray jsonParams) {
        VolleyRequest volleyRequest = new VolleyRequest(iBaseView.getContext(), iBaseView.getQueue());
        JSONObject jsonParam = volleyRequest.getJsonParam();

        volleyRequest.doPost(HttpConstant.SOCKET_TOKEN_HQ, jsonParam, new HttpListener<String>() {

            @Override
            protected void onResponse(String s) {
                JSONObject jsonObject = JSONObject.parseObject(s);
                String server = jsonObject.getString("server");
                String token = jsonObject.getString("token");

                try {
                    String url = server + "?" + token;
                    mConnection.connect(url, new WebSocketConnectionHandler() {
                        @Override
                        public void onOpen() {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("cmd", "login");
                            jsonObject.put("codes", jsonParams.toString());
                            mConnection.sendTextMessage(jsonObject.toJSONString());
                        }

                        @Override
                        public void onTextMessage(String payload) {
                            Log.e("onTextMessage", "onTextMessage: " + payload);
                        }

                        @Override
                        public void onClose(int code, String reason) {

                        }
                    });
                } catch (WebSocketException e) {
                    e.printStackTrace();
                }

            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
            }
        });
    }
}
