package com.jyh.kxt.main.presenter;

import android.os.Handler;
import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.base.constant.VarConstant;
import com.jyh.kxt.base.utils.SocketUtils;
import com.jyh.kxt.index.json.ConfigJson;
import com.jyh.kxt.index.json.SocketJson;
import com.jyh.kxt.main.adapter.FastInfoAdapter;
import com.jyh.kxt.main.json.flash.FlashJson;
import com.jyh.kxt.main.ui.fragment.FlashFragment;
import com.jyh.kxt.main.widget.FastInfoPinnedListView;
import com.library.util.SPUtils;
import com.library.widget.handmark.PullToRefreshBase;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import de.tavendo.autobahn.WebSocket;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketOptions;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/21.
 */

public class FlashPresenter extends BasePresenter implements FastInfoPinnedListView.FooterListener,PullToRefreshBase.OnRefreshListener2 {

    @BindObject FlashFragment flashFragment;

    private WebSocketConnection connection;
    private List<FlashJson> flashs = new ArrayList<>();
    private String kx_server;

    public FlashPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void init() {

        flashFragment.plRootView.loadWait();

        initSocket();
    }

    /**
     * 初始化Socket
     */
    private void initSocket() {
        connection = new WebSocketConnection();

        connect();
    }

    /**
     * 连接Socket
     */
    private void connect() {
        String configStr = SPUtils.getString(mContext, SpConstant.CONFIG);
        if (configStr != null) {
            ConfigJson config = JSONObject.parseObject(configStr, ConfigJson.class);
            if (config != null) {
                SocketJson kuaixun_ws = config.getKuaixun_ws();
                if (kuaixun_ws != null) {
                    kx_server = kuaixun_ws.getServer();
                    WebSocketOptions options = new WebSocketOptions();
                    options.setReceiveTextMessagesRaw(true);
                    options.setSocketConnectTimeout(30000);
                    options.setSocketReceiveTimeout(10000);
                    List<BasicNameValuePair> headers = new ArrayList<>();
                    headers.add(new BasicNameValuePair(IntentConstant.SOCKET_ORIGIN, VarConstant.SOCKET_DOMAIN));
                    try {
                        connection.connect(kx_server + "?token=" + SocketUtils.getToken(), null, connectionHandler, options, headers);
                    } catch (WebSocketException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private long nowdate;
    private String loginStr;
    private String historyStr;

    private WebSocket.ConnectionHandler connectionHandler = new WebSocket.ConnectionHandler() {
        @Override
        public void onOpen() {
            try {
                handler.removeCallbacksAndMessages(null);

                loginStr = "{\n" +
                        "\n" +
                        "    \"cmd\":\"login\",\n" +
                        "    \"number\":\"20\"\n" +
                        "\n" +
                        "}";
                byte[] bytes = loginStr.getBytes("UTF-8");
                connection.sendRawTextMessage(bytes);
                //发送心跳包
                handler.sendEmptyMessageDelayed(1, 10000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onClose(int i, String s) {

        }

        @Override
        public void onTextMessage(String s) {
            nowdate = System.currentTimeMillis();
            try {
                org.json.JSONObject object = new org.json.JSONObject(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onRawTextMessage(byte[] bytes) {
            try {
                org.json.JSONObject socket = new org.json.JSONObject(new String(bytes, "UTF-8"));
                String cmd = socket.getString(IntentConstant.SOCKET_CMD);
                String status = socket.getString(IntentConstant.SOCKET_STATUS);
                if ("1".equals(status))
                    switch (cmd) {
                        case VarConstant.SOCKET_CMD_LOGIN:
                            try {
                                List<String> initFlashStr = JSON.parseArray(socket.getJSONArray(IntentConstant.SOCKET_MSG).toString(),
                                        String.class);
                                List<FlashJson> initflashs = new ArrayList<>();
                                for (String initFlash : initFlashStr) {
                                    initflashs.add(JSON.parseObject(initFlash, FlashJson.class));
                                }
                                flashs = initflashs;
                                FastInfoAdapter adapter = new FastInfoAdapter(flashs, mContext);
                                flashFragment.lvContent.setAdapter(adapter);
                                flashFragment.plRootView.loadOver();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                flashFragment.plRootView.loadError();
                            }
                            break;
                        case VarConstant.SOCKET_CMD_HISTORY:
                            List<String> moreFlashStr = JSON.parseArray(socket.getJSONArray(IntentConstant.SOCKET_MSG).toString(),
                                    String.class);
                            List<FlashJson> moreFlashs = new ArrayList<>();
                            for (String s : moreFlashStr) {
                                moreFlashs.add(JSON.parseObject(s, FlashJson.class));
                            }
                            flashs.addAll(moreFlashs);
                            break;
                        case VarConstant.SOCKET_CMD_TIMELY:
                            FlashJson newFlash = JSON.parseObject(socket.getJSONArray(IntentConstant.SOCKET_MSG).toString(), FlashJson
                                    .class);
                            flashs.add(0, newFlash);
                            break;
                    }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onBinaryMessage(byte[] bytes) {

        }
    };

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            connection.sendTextMessage("");
            if ((System.currentTimeMillis() - nowdate) > 5000 && nowdate != 0) {
                if (connection != null) {
                    if (connection.isConnected())
                        connection.disconnect();
                    else
                        connect();
                } else {
                    initSocket();
                }
            }
            handler.sendEmptyMessageDelayed(1, 10000);
            return false;
        }
    });

    public void onDestroy() {
        if (connection != null && connection.isConnected()) {
            connection.disconnect();
        }
        connection = null;
    }

    @Override
    public void startLoadMore() {

    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {

    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {

    }
}
