package com.jyh.kxt.index.presenter;

import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.base.widget.LoadX5WebView;
import com.jyh.kxt.index.impl.WebBuild;
import com.jyh.kxt.index.ui.WebActivity;
import com.library.util.SPUtils;

/**
 * Created by Mr'Dai on 2017/4/6.
 */

public class WebPresenter extends BasePresenter {
    @BindObject WebActivity webActivity;
    public LoadX5WebView loadX5WebView;

    public WebPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void addWebView(String url) {
        loadX5WebView = new LoadX5WebView(mContext);
        WebBuild webBuild = loadX5WebView.build();


        Boolean isNight = SPUtils.getBoolean(mContext, SpConstant.SETTING_DAY_NIGHT);

        String yejian = "?yejian=";
        if (url.contains("?")) {
            yejian = "&yejian=";
        }

        if (isNight) {
            url += yejian + "1";
        } else {
            url += yejian + "0";
        }

        webBuild.loadUrl(url);

        webActivity.llWebParent.addView(webBuild.getWebParentView());
    }

    public void setOnJsListener(ObserverData<Boolean> observerData) {
        if (loadX5WebView != null)
            loadX5WebView.setOnJsListener(observerData);
    }
}
