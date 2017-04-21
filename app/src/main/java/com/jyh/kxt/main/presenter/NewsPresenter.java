package com.jyh.kxt.main.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.base.constant.VarConstant;
import com.jyh.kxt.base.utils.SocketUtils;
import com.jyh.kxt.index.json.ConfigJson;
import com.jyh.kxt.index.json.SocketJson;
import com.jyh.kxt.index.ui.ClassifyActivity;
import com.jyh.kxt.main.json.AdJson;
import com.jyh.kxt.main.json.NewsHomeHeaderJson;
import com.jyh.kxt.main.json.NewsJson;
import com.jyh.kxt.main.json.NewsNavJson;
import com.jyh.kxt.main.json.QuotesJson;
import com.jyh.kxt.main.json.SlideJson;
import com.jyh.kxt.main.ui.fragment.NewsFragment;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;
import com.library.util.SPUtils;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.tavendo.autobahn.WebSocket;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketOptions;

/**
 * 项目名:Kxt
 * 类描述:要闻
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/12.
 */

public class NewsPresenter extends BasePresenter {

    @BindObject()
    NewsFragment newsFragment;

    public int index = 0;
    private RequestQueue queue;
    private VolleyRequest request;
    private WebSocketConnection connection;
    private String kx_server;

    public NewsPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void addOnPageChangeListener(ViewPager vpNewsList) {
        vpNewsList.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                index = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void more(String[] tabs) {
        Intent intent = new Intent(mContext, ClassifyActivity.class);
        intent.putExtra(IntentConstant.INDEX, index);
        intent.putExtra(IntentConstant.ACTIONNAV, tabs);
        ((Activity) mContext).startActivityForResult(intent, IntentConstant.REQUESTCODE1);
    }

    public void init() {

        newsFragment.plRootView.loadWait();

        queue = newsFragment.getQueue();
        if (queue == null)
            queue = Volley.newRequestQueue(mContext);

        request = new VolleyRequest(mContext, queue);

        request.doGet(HttpConstant.INDEX_MAIN, new HttpListener<List<NewsHomeHeaderJson>>() {

            @Override
            protected void onResponse(List<NewsHomeHeaderJson> newsHomeHeaderJsons) {

                List<NewsNavJson> newsNavs = null;
                List<SlideJson> slide = null;
                List<SlideJson> shortcut = null;
                List<QuotesJson> quotes = null;
                List<NewsJson> news = null;
                AdJson ad = null;

                ArrayList<String> list = new ArrayList<>();

                for (NewsHomeHeaderJson headerJson : newsHomeHeaderJsons) {
                    switch (headerJson.getType()) {
                        case VarConstant.NEWS_NAV:
                            JSONArray newsNavArray = (JSONArray) headerJson.getData();
                            if (newsNavArray == null)
                                break;
                            newsNavs = JSON.parseArray(newsNavArray.toString(), NewsNavJson.class);
                            break;
                        case VarConstant.NEWS_SLIDE:
                            JSONArray slideArray = (JSONArray) headerJson.getData();
                            if (slideArray == null) break;
                            slide = JSON.parseArray(slideArray.toString(), SlideJson.class);
                            if (slide.size() > 0)
                                list.add(VarConstant.NEWS_SLIDE);
                            break;
                        case VarConstant.NEWS_SHORTCUT:
                            JSONArray shortcutArray = (JSONArray) headerJson.getData();
                            if (shortcutArray == null) break;
                            shortcut = JSON.parseArray(shortcutArray.toString(), SlideJson.class);
                            if (shortcut.size() > 0)
                                list.add(VarConstant.NEWS_SHORTCUT);
                            break;
                        case VarConstant.NEWS_LIST:
                            JSONArray newsArray = (JSONArray) headerJson.getData();
                            if (newsArray == null) break;
                            news = JSON.parseArray(newsArray.toString(), NewsJson.class);
                            break;
                        case VarConstant.NEWS_QUOTES:
                            JSONArray quotesArray = (JSONArray) headerJson.getData();
                            if (quotesArray == null) break;
                            quotes = JSON.parseArray(quotesArray.toString(), QuotesJson.class);
                            if (quotes.size() > 0)
                                list.add(VarConstant.NEWS_QUOTES);
                            break;
                        case VarConstant.NEWS_AD:
                            JSONObject adObj = (JSONObject) headerJson.getData();
                            if (adObj == null) break;

                            SlideJson ad_img = adObj.getObject("pic_ad", SlideJson.class);

                            List<SlideJson> ad_text_list = JSON.parseArray(adObj.getJSONArray("text_ad").toString(), SlideJson
                                    .class);
                            SlideJson[] ad_text = ad_text_list.toArray(new SlideJson[ad_text_list.size()]);

                            ad = new AdJson(ad_img, ad_text);
                            list.add(VarConstant.NEWS_AD);
                            break;
                    }
                }
                newsFragment.initView(newsNavs, slide, shortcut, quotes, ad, news, list);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                try {
                    newsFragment.plRootView.loadError();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

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
                        connection.connect(kx_server + SocketUtils.getToken(), null, connectionHandler, options, headers);
                    } catch (WebSocketException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private long nowdate;
    private WebSocket.ConnectionHandler connectionHandler = new WebSocket.ConnectionHandler() {
        @Override
        public void onOpen() {
            handler.removeCallbacksAndMessages(null);
            //发送心跳包
            handler.sendEmptyMessageDelayed(1, 10000);
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
                org.json.JSONObject object = new org.json.JSONObject(new String(bytes, "UTF-8"));
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
        if (connection != null && connection.isConnected())
            connection.disconnect();
        connection = null;
    }

}
