package com.jyh.kxt.main.presenter;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.main.adapter.FastInfoAdapter;
import com.jyh.kxt.main.json.flash.FlashJson;
import com.jyh.kxt.main.json.flash.Flash_KX;
import com.jyh.kxt.main.json.flash.Flash_NEWS;
import com.jyh.kxt.main.json.flash.Flash_RL;
import com.jyh.kxt.main.ui.fragment.FlashFragment;
import com.jyh.kxt.main.widget.FastInfoPinnedListView;
import com.jyh.kxt.push.json.CjInfo;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.util.EncryptionUtils;
import com.library.util.NetUtils;
import com.library.util.SPUtils;
import com.library.util.SystemUtil;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshBase;

import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

public class FlashPresenter extends BasePresenter implements FastInfoPinnedListView.FooterListener, PullToRefreshBase.OnRefreshListener2,
        PageLoadLayout.OnAfreshLoadListener {

    @BindObject FlashFragment flashFragment;

    private WebSocketConnection connection;
    public FastInfoAdapter adapter;
    private RequestQueue queue;
    private VolleyRequest request;
    private String lastId;
    private SoundPool mMediaPlayer;
    private String token;
    private String server;
    private WebSocketOptions options;
    private ArrayList<BasicNameValuePair> headers;

    private boolean isInit = true;

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
     *
     */
    private void connect() {
        options = new WebSocketOptions();
        options.setReceiveTextMessagesRaw(false);
        options.setSocketConnectTimeout(30000);
        options.setSocketReceiveTimeout(10000);
        headers = new ArrayList<>();
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
                        server = jsonObject.getString("server");
                        token = jsonObject.getString("token");
                        connection.connect(server + "?token=" + token, null, connectionHandler, options,
                                headers);

                    } catch (Exception e) {
                        e.printStackTrace();
                        if (isInit){
                            flashFragment.plRootView.loadError();
                            isInit=false;
                        }
                    }
                }

                @Override
                protected void onErrorResponse(VolleyError error) {
                    super.onErrorResponse(error);
                    if (isInit){
                        flashFragment.plRootView.loadError();
                        isInit=false;
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String loginStr = "{\"cmd\":\"login\",\"number\":20}";
    private String historyStr = "{\"cmd\":\"history\",\"lastid\":\"%s\",\"number\":30}";

    private WebSocket.ConnectionHandler connectionHandler = new WebSocket.ConnectionHandler() {
        @Override
        public void onOpen() {
            try {
                connection.sendTextMessage(loginStr);
                //发送心跳包
                handler.removeMessages(1);
                handler.sendEmptyMessage(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onClose(int i, String s) {

        }

        @Override
        public void onTextMessage(String s) {
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
                        Boolean isSound = SPUtils.getBooleanTrue(mContext, SpConstant.FLASH_FILTRATE_SOUND);
                        Boolean isTop = SPUtils.getBooleanTrue(mContext, SpConstant.FLASH_FILTRATE_TOP);
                        if (doWhat == null) {
                            //添加新快讯
                            FlashJson newFlash = JSON.parseObject(socket.getString(IntentConstant.SOCKET_MSG).toString(), FlashJson
                                    .class);
                            if (isTop) {
                                topNotice(newFlash);
                            }
                            if (isSound) {
                                soundNotice();
                            }
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
                                    if (isTop) {
                                        topNotice(newFlash);
                                    }
                                    if (isSound) {
                                        soundNotice();
                                    }
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

    /**
     * 新消息声音
     *
     * @throws IOException
     */
    private void soundNotice() throws IOException {
        // 使用来电铃声的铃声路径
        Uri uri = Uri.parse("android.resource://"
                + mContext.getPackageName() + "/" + R.raw.kxt_notify);
        // 如果为空，才构造，不为空，说明之前有构造过
        if (mMediaPlayer == null)
            mMediaPlayer = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
        mMediaPlayer.load(mContext, R.raw.kxt_notify, 1);
        mMediaPlayer.play(1, 1, 1, 0, 0, 1);
    }

    /**
     * 顶部通知
     *
     * @param newFlash
     */
    private void topNotice(FlashJson newFlash) {
        if (SystemUtil.isRunningForeground(mContext)) {
            // 1 得到通知管理器
            final NotificationManager nm = (NotificationManager) mContext
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(
                    mContext);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setDefaults(Notification.DEFAULT_VIBRATE);
            builder.setWhen(System.currentTimeMillis());
            builder.setAutoCancel(true);// 点击通知之后自动消失

            String content = newFlash.getContent();
            switch (newFlash.getCode()) {
                case VarConstant.SOCKET_FLASH_CJRL:
                    Flash_RL flash_rl = JSON.parseObject(content, Flash_RL.class);
                    RemoteViews views = new RemoteViews(mContext.getPackageName(),
                            R.layout.text_notification);

                    views.setTextViewText(R.id.calendar_item_listview_version2_nfi,
                            flash_rl.getTitle());

                    String[] splitTime = flash_rl.getTime().split(" ");
                    String[] splitTime2 = splitTime[1].split(":");

                    views.setTextViewText(R.id.calendar_listview_item_tv_title_time,
                            splitTime2[0] + ":" + splitTime2[1]);
                    views.setTextViewText(R.id.calendar_item_listview_version2_before, "前值:"
                            + flash_rl.getBefore());
                    views.setTextViewText(R.id.calendar_item_listview_version2_forecast,
                            "预测:" + flash_rl.getForecast());
                    views.setTextViewText(R.id.calendar_item_listview_version2_gongbu, ""
                            + flash_rl.getReality());

                    String yingString = "";

                    CjInfo cjInfo = new CjInfo();
                    String title = flash_rl.getEffect();
                    String[] titles = title.split("\\|");
                    if (titles.length > 0 && titles.length == 1) {
                        cjInfo.setEffectGood(titles[0]);// 利空
                    } else if (titles.length > 0
                            && titles.length == 2) {
                        cjInfo.setEffectBad(titles[1]);// 利多
                        cjInfo.setEffectGood(titles[0]);// 利空
                    } else {
                        cjInfo.setEffectMid("影响较小");
                    }

                    if (!TextUtils.isEmpty(cjInfo.getEffectMid())) {
                        yingString = cjInfo.getEffectMid();
                    } else {
                        if (!TextUtils.isEmpty(cjInfo.getEffectGood())) {
                            yingString += "利多 " + cjInfo.getEffectGood();
                        }
                        if (!TextUtils.isEmpty(cjInfo.getEffectBad())) {

                            if (!TextUtils.isEmpty(cjInfo.getEffectGood())) {
                                yingString += ",";
                            }
                            yingString += "利空 " + cjInfo.getEffectBad();
                        }
                    }


                    views.setTextViewText(R.id.calendar_item_listview_version2_4main_effect, "影响：" + yingString);
                    if (flash_rl.getImportance().equals("高")) {
                        views.setImageViewResource(R.id.calendar_item_nature,
                                R.mipmap.nature_high_bt);
                    } else if (flash_rl.getImportance().equals("低")) {
                        views.setImageViewResource(R.id.calendar_item_nature,
                                R.mipmap.nature_low_bt);
                    } else if (flash_rl.getImportance().equals("中")) {
                        views.setImageViewResource(R.id.calendar_item_nature,
                                R.mipmap.nature_mid_bt);
                    } else {
                        views.setImageViewResource(R.id.calendar_item_nature,
                                R.mipmap.nature_high_bt);
                    }
                    builder.setContent(views);
                    break;
                case VarConstant.SOCKET_FLASH_KUAIXUN:
                    Flash_KX flash_kx = JSON.parseObject(content, Flash_KX.class);
                    builder.setContentTitle(flash_kx.getTitle());
                    builder.setContentText(flash_kx.getTitle());
                    break;
                case VarConstant.SOCKET_FLASH_KXTNEWS:
                    Flash_NEWS flash_news = JSON.parseObject(content, Flash_NEWS.class);
                    builder.setContentTitle(flash_news.getTitle());
                    builder.setContentText(flash_news.getDescription());
                    break;
            }
            Notification build = builder.build();
            build.flags |= Notification.FLAG_AUTO_CANCEL;
            // 4发送通知
            final int id = new Random().nextInt(1000);
            nm.notify(id, build);
            flashFragment.lvContent.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        nm.cancel(id);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 5000);
        }
    }

    public void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        if (connection != null && connection.isConnected()) {
            connection.disconnect();
        }
        connection = null;
    }

    @Override
    public void startLoadMore() {
        Log.i("kuaixun", "startLoadMore");
        if (connection.isConnected()) {
            connection.sendTextMessage(String.format(historyStr, lastId));
        } else {
            try {
                connection.connect(server + "?token=" + token, null, connectionHandler, options,
                        headers);
            } catch (WebSocketException e) {
                e.printStackTrace();
            }
            flashFragment.lvContent.postDelayed(new Runnable() {
                @Override
                public void run() {
                    flashFragment.lvContent.onRefreshComplete();
                    flashFragment.lvContent.getRefreshableView().goneFoot();
                }
            }, 200);
        }
    }

    @Override
    public void onPullDownToRefresh(final PullToRefreshBase refreshView) {
        Log.i("kuaixun", "onPullDownToRefresh");
        if (connection.isConnected()) {
            connection.sendTextMessage(loginStr);
        } else {
            try {
                connection.connect(server + "?token=" + token, null, connectionHandler, options,
                        headers);
            } catch (WebSocketException e) {
                e.printStackTrace();
            }
            flashFragment.lvContent.postDelayed(new Runnable() {
                @Override
                public void run() {
                    flashFragment.lvContent.onRefreshComplete();
                }
            }, 200);
        }
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

    /**
     * 网络断开重连
     */
    public void reConnection() {
        if (connection == null)
            connection = new WebSocketConnection();
        if (connection.isConnected()) {
            connection.disconnect();
        }
        connect();
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (connection == null)
                connection = new WebSocketConnection();
            Log.i("flashSocket", "" + connection.isConnected());
            if (!connection.isConnected() && NetUtils.isNetworkAvailable(mContext))
                connect();
            handler.sendEmptyMessageDelayed(1, 300000);
            return false;
        }
    });
}
