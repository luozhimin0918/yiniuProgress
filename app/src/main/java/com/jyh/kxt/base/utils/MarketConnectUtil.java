package com.jyh.kxt.base.utils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.impl.OnSocketTextMessage;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.util.NetUtils;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketConnectionHandler;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketOptions;

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

    private OnSocketTextMessage onSocketTextMessage;
    private WebSocketConnection mConnection = new WebSocketConnection();

    private IBaseView iBaseView;
    private JSONArray jsonArray;

    public void sendSocketParams(IBaseView iBaseView, JSONArray jsonArray, OnSocketTextMessage onSocketTextMessage) {
        try {
            if (jsonArray == null || jsonArray.size() == 0) {
                return;
            }
            if (iBaseView == null) {//这里传入的有Fragment 可能被销毁
                return;
            }

            this.iBaseView = iBaseView;
            this.jsonArray = jsonArray;
            this.onSocketTextMessage = onSocketTextMessage;

            if (!mConnection.isConnected()) {
                requestConnectToken(iBaseView, jsonArray);
            } else {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("cmd", "login");
                jsonObject.put("codes", jsonArray);
                mConnection.sendTextMessage(jsonObject.toJSONString());

                Log.e("Socket 已连接发送参数", "sendSocketParams: " + jsonObject.toJSONString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestConnectToken(IBaseView iBaseView, final JSONArray jsonParams) {

        VolleyRequest volleyRequest = new VolleyRequest(iBaseView.getContext(), iBaseView.getQueue());
        JSONObject jsonParam = volleyRequest.getJsonParam();
        jsonParam.put(IntentConstant.SOCKET_CLIENT, VarConstant.HTTP_CLIENT);
        volleyRequest.doPost(HttpConstant.SOCKET_TOKEN_HQ, jsonParam, new HttpListener<String>() {
            @Override
            protected void onResponse(String s) {
                JSONObject jsonObject = JSONObject.parseObject(s);
                String server = jsonObject.getString("server");
                String token = jsonObject.getString("token");

                try {
                    String url = server + "?token=" + token;

                    WebSocketOptions options = new WebSocketOptions();
                    options.setReceiveTextMessagesRaw(false);
                    options.setSocketConnectTimeout(30000);
                    options.setSocketReceiveTimeout(10000);

                    List<BasicNameValuePair> headers = new ArrayList<>();
                    headers.add(new BasicNameValuePair(IntentConstant.SOCKET_ORIGIN, VarConstant.SOCKET_DOMAIN));
                    mConnection.connect(url, null, new WebSocketConnectionHandler() {
                        @Override
                        public void onOpen() {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("cmd", "login");
                            jsonObject.put("codes", jsonParams);
                            mConnection.sendTextMessage(jsonObject.toString());

                            handler.sendEmptyMessage(1);
                        }

                        @Override
                        public void onTextMessage(String payload) {
                            try {
                                if (onSocketTextMessage != null && payload != null && !"".equals(payload)) {
                                    onSocketTextMessage.onTextMessage(payload);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onClose(int code, String reason) {

                        }
                    }, options, headers);

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

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (mConnection == null) {
                mConnection = new WebSocketConnection();
            }

            if (!mConnection.isConnected() && NetUtils.isNetworkAvailable(iBaseView.getContext())) {
                sendSocketParams(iBaseView, jsonArray, onSocketTextMessage);
            }

            handler.sendEmptyMessageDelayed(1, 3 * 60 * 1000);
            return false;
        }
    });
}
