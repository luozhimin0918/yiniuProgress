package com.jyh.kxt.main.presenter;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.constant.SpConstant;
import com.library.base.http.VarConstant;
import com.jyh.kxt.main.adapter.FastInfoAdapter;
import com.jyh.kxt.main.json.flash.FlashJson;
import com.jyh.kxt.main.ui.fragment.FlashFragment;
import com.jyh.kxt.main.widget.FastInfoPinnedListView;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;
import com.library.util.EncryptionUtils;
import com.library.util.SPUtils;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshBase;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import de.tavendo.autobahn.WebSocket;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketOptions;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/21.
 */

public class FlashPresenter extends BasePresenter implements FastInfoPinnedListView.FooterListener, PullToRefreshBase.OnRefreshListener2,
        PageLoadLayout.OnAfreshLoadListener {

    @BindObject FlashFragment flashFragment;

    private WebSocketConnection connection;
    public FastInfoAdapter adapter;
    private RequestQueue queue;
    private VolleyRequest request;
    private String lastId;

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
        if (connection == null)
            connection = new WebSocketConnection();
        if (connection.isConnected())
            connection.disconnect();
        connect();
    }

    /**
     * 连接Socket
     */
    private void connect() {
        String configStr = SPUtils.getString(mContext, SpConstant.CONFIG);
        if (!TextUtils.isEmpty(configStr)) {
            final WebSocketOptions options = new WebSocketOptions();
            options.setReceiveTextMessagesRaw(false);
            options.setSocketConnectTimeout(30000);
            options.setSocketReceiveTimeout(10000);
            final List<BasicNameValuePair> headers = new ArrayList<>();
            headers.add(new BasicNameValuePair(IntentConstant.SOCKET_ORIGIN, VarConstant.SOCKET_DOMAIN));
            if (queue == null)
                queue = flashFragment.getQueue();
            if (request == null) {
                request = new VolleyRequest(mContext, queue);
                request.setTag(getClass().getName());
            }
            JSONObject object = request.getJsonParam();
            try {
                object.put(IntentConstant.SOCKET_CLIENT, VarConstant.HTTP_CLIENT);

                request.doGet(HttpConstant.SOCKET_TOKEN_KX + EncryptionUtils.createJWT(com.library
                        .base.http
                        .VarConstant.KEY, object.toString()), new HttpListener<String>() {
                    @Override
                    protected void onResponse(String str) {
                        try {
                            org.json.JSONObject jsonObject = new org.json.JSONObject(str);
                            String server = jsonObject.getString("server");
                            String token = jsonObject.getString("token");
                            connection.connect(server + "?token=" + token, null, connectionHandler, options,
                                    headers);

                        } catch (Exception e) {
                            e.printStackTrace();
                            flashFragment.plRootView.loadError();
                        }
                    }

                    @Override
                    protected void onErrorResponse(VolleyError error) {
                        super.onErrorResponse(error);
                        flashFragment.plRootView.loadError();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private long nowdate;
    private String loginStr = "{\"cmd\":\"login\",\"number\":20}";
    private String historyStr = "{\"cmd\":\"history\",\"lastid\":\"%s\",\"number\":30}";

    private WebSocket.ConnectionHandler connectionHandler = new WebSocket.ConnectionHandler() {
        @Override
        public void onOpen() {
            try {
                connection.sendTextMessage(loginStr);
                //发送心跳包
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
            getNewMsg(s);
            try {
                flashFragment.lvContent.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            flashFragment.lvContent.onRefreshComplete();
                            flashFragment.lvContent.getRefreshableView().goneFoot();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onRawTextMessage(byte[] bytes) {
//            getNewMsg(bytes);
//            try {
//                flashFragment.lvContent.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        flashFragment.lvContent.onRefreshComplete();
//                    }
//                }, 500);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }

        @Override
        public void onBinaryMessage(byte[] bytes) {

        }
    };

    /**
     * 得到数据,处理数据
     *
     * @param str
     */
    private void getNewMsg(String str) {
        try {
            org.json.JSONObject socket = new org.json.JSONObject(str);
            String cmd = socket.getString(IntentConstant.SOCKET_CMD);
            String status = socket.getString(IntentConstant.SOCKET_STATUS);
            if ("1".equals(status))
                switch (cmd) {
                    case VarConstant.SOCKET_CMD_LOGIN:
                        List<String> initFlashStr = JSON.parseArray(socket.getJSONArray(IntentConstant.SOCKET_MSG).toString(),
                                String.class);
                        List<FlashJson> initflashs = new ArrayList<>();
                        for (String initFlash : initFlashStr) {
                            initflashs.add(JSON.parseObject(initFlash, FlashJson.class));
                        }
                        if (initflashs.size() > 0) {
                            lastId = initflashs.get(initflashs.size() - 1).getSocre();
                            if (adapter == null) {
                                adapter = new FastInfoAdapter(initflashs, mContext);
                                flashFragment.lvContent.setAdapter(adapter);
                            } else {
                                adapter.setData(initflashs);
                            }
                            flashFragment.plRootView.loadOver();
                        } else {
                            flashFragment.plRootView.loadEmptyData();
                        }
                        break;
                    case VarConstant.SOCKET_CMD_HISTORY:
                        List<String> moreFlashStr = JSON.parseArray(socket.getJSONArray(IntentConstant.SOCKET_MSG).toString(),
                                String.class);
                        List<FlashJson> moreFlashs = new ArrayList<>();
                        for (String s : moreFlashStr) {
                            moreFlashs.add(JSON.parseObject(s, FlashJson.class));
                        }
                        lastId = moreFlashs.get(moreFlashs.size() - 1).getSocre();
                        adapter.addData(moreFlashs);
                        break;
                    case VarConstant.SOCKET_CMD_TIMELY:
                        String doWhat = socket.optString(IntentConstant.SOCKET_DO);
                        if (doWhat == null) {
                            //添加新快讯
                            FlashJson newFlash = JSON.parseObject(socket.getString(IntentConstant.SOCKET_MSG).toString(), FlashJson
                                    .class);
                            adapter.addData(newFlash);
                        } else {

                            List<FlashJson> flashJsons = adapter.getData();
                            int size = flashJsons.size();
                            switch (doWhat) {
                                case VarConstant.SOCKET_DO_DELETE:
                                    //删除
                                    for (int i = 0; i < size; i++) {
                                        if (flashJsons.get(i) instanceof FlashJson) {
                                            FlashJson flashJson = flashJsons.get(i);
                                            String id = socket.getJSONObject(IntentConstant.SOCKET_MSG).getString(IntentConstant.SOCKET_ID);
                                            if (id.equals(flashJson.getSocre())) {
                                                flashJsons.remove(flashJson);
                                            }
                                        }
                                    }
                                    adapter.setData(flashJsons);
                                    break;
                                case VarConstant.SOCKET_DO_MODIFY:
                                    //修改
                                    for (int i = 0; i < size; i++) {
                                        if (flashJsons.get(i) instanceof FlashJson) {
                                            FlashJson flashJson = flashJsons.get(i);
                                            String localId = flashJson.getSocre();
                                            FlashJson remoteFlash = JSON.parseObject(socket.getJSONObject(IntentConstant.SOCKET_MSG)
                                                    .toString(), FlashJson.class);
                                            String remoteId = remoteFlash.getSocre();
                                            if (localId.equals(remoteId)) {
                                                flashJsons.set(i, remoteFlash);
                                            }
                                        }
                                    }
                                    adapter.setData(flashJsons);
                                    break;
                                default:
                                    //添加新快讯
                                    FlashJson newFlash = JSON.parseObject(socket.getString(IntentConstant.SOCKET_MSG).toString(), FlashJson
                                            .class);
                                    adapter.addData(newFlash);
                                    break;
                            }
                        }
                        break;
                }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onDestroy() {
        if (connection != null && connection.isConnected()) {
            connection.disconnect();
        }
        connection = null;
    }

    @Override
    public void startLoadMore() {
        Log.i("kuaixun", "startLoadMore");
        connection.sendTextMessage(String.format(historyStr, lastId));
    }

    @Override
    public void onPullDownToRefresh(final PullToRefreshBase refreshView) {
        Log.i("kuaixun", "onPullDownToRefresh");
        connection.sendTextMessage(loginStr);
    }

    @Override
    public void onPullUpToRefresh(final PullToRefreshBase refreshView) {
        Log.i("kuaixun", "onPullUpToRefresh");
        refreshView.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshView.onRefreshComplete();
            }
        }, 500);
    }

    @Override
    public void OnAfreshLoad() {
        init();
    }

    /**
     * 筛选
     */
    public void filtrate() {
        adapter.filtrate();
    }
}
