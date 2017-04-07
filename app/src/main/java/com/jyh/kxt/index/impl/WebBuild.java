package com.jyh.kxt.index.impl;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Mr'Dai on 2017/4/7.
 */

public interface WebBuild {
    /**
     * 开始构建
     */
    WebBuild build();

    void loadUrl(String url);

    View getWebView();

    ViewGroup getWebParentView();
}
