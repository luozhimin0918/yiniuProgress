package com.jyh.kxt.index.impl;

import android.view.ViewGroup;

import com.tencent.smtt.sdk.WebView;

/**
 * Created by Mr'Dai on 2017/4/7.
 */

public interface WebBuild {
    /**
     * 开始构建
     */
    WebBuild build();

    void loadUrl(String url);

    void loadData(String data);

    WebView getWebView();

    ViewGroup getWebParentView();
}
