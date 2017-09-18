package com.jyh.kxt.index.presenter;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.base.widget.LoadX5WebView;
import com.jyh.kxt.index.impl.WebBuild;
import com.jyh.kxt.index.json.WebShareJson;
import com.jyh.kxt.index.ui.WebActivity;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;
import com.library.util.SPUtils;

/**
 * Created by Mr'Dai on 2017/4/6.
 */

public class WebPresenter extends BasePresenter {
    @BindObject WebActivity webActivity;
    public LoadX5WebView loadX5WebView;
    private VolleyRequest request;


    public WebPresenter(IBaseView iBaseView) {
        super(iBaseView);
        request = new VolleyRequest(mContext, mQueue);
        request.setTag(getClass().getName());
    }

    public void addWebView(String title, String url) {
        loadX5WebView = new LoadX5WebView(mContext);
        loadX5WebView.setDefaultJavaScriptEnabled(webActivity.javaScriptEnabled);//必须在构建之前
        WebBuild webBuild = loadX5WebView.build();

        if ("联系我们".equals(title) || "意见反馈".equals(title)) {
            Boolean isNight = SPUtils.getBoolean(mContext, SpConstant.SETTING_DAY_NIGHT);
            String yejian = "?night=";
            if (url.contains("?")) {
                yejian = "&night=";
            }

            if (isNight) {
                url += yejian + "1";
            } else {
                url += yejian + "0";
            }
        }

        webBuild.loadUrl(url);
        JSONObject jsonParam = request.getJsonParam();
        jsonParam.put("url", url);
        request.doGet(HttpConstant.SHARE_WEB, jsonParam, new HttpListener<WebShareJson>() {
            @Override
            protected void onResponse(WebShareJson webShare) {
                webActivity.setWebShare(webShare);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                webActivity.setWebShare(null);
            }
        });

        webActivity.llWebParent.addView(webBuild.getWebParentView());
    }

    public void setOnJsListener(ObserverData<Boolean> observerData) {
        if (loadX5WebView != null)
            loadX5WebView.setOnJsListener(observerData);
    }
}
