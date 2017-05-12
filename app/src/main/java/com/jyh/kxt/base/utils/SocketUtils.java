package com.jyh.kxt.base.utils;

import com.jyh.kxt.base.constant.IntentConstant;
import com.library.base.http.VarConstant;
import com.library.util.SystemUtil;

import org.json.JSONObject;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketConnectionHandler;
import de.tavendo.autobahn.WebSocketException;

/**
 * 项目名:Kxt
 * 类描述:Socket 工具类
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/20.
 */

public class SocketUtils {

    private static SocketUtils mSocketUtils;

    public static SocketUtils getInstance() {
        if (mSocketUtils == null) {
            mSocketUtils = new SocketUtils();
        }
        return mSocketUtils;
    }

    /**
     * 获取token
     *
     * @return
     */
    public String getToken() {
        String token = "";
        try {
            JSONObject object = new JSONObject();
            object.put(IntentConstant.SOCKET_TIME, "" + System.currentTimeMillis() / 1000);
            object.put(IntentConstant.SOCKET_DOMAIN, VarConstant.SOCKET_DOMAIN);
            object.put(IntentConstant.SOCKET_REMOTE_ADDR, SystemUtil.getHostIP());

            token = Encrypt.encrypt(object.toString(), VarConstant.SOCKET_KEY, 6000);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return token;
    }


    interface OnMarketListener {
        void onCall(String result);
    }

    private OnMarketListener onMarketListener;
    private WebSocketConnection mConnection;


    private void connectMarketSocket(OnMarketListener onMarketListener) {
        this.onMarketListener = onMarketListener;



        try {
            String url = "";
            mConnection = new WebSocketConnection();
            mConnection.connect(url, new WebSocketConnectionHandler() {
                @Override
                public void onOpen() {

                }

                @Override
                public void onTextMessage(String payload) {
                    if (SocketUtils.this.onMarketListener != null) {
                        SocketUtils.this.onMarketListener.onCall(payload);
                    }
                }

                @Override
                public void onClose(int code, String reason) {

                }
            });
        } catch (WebSocketException e) {
            e.printStackTrace();
        }
    }
}
