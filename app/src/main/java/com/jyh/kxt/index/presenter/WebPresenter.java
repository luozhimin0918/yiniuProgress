package com.jyh.kxt.index.presenter;

import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.widget.LoadX5WebView;
import com.jyh.kxt.index.impl.WebBuild;
import com.jyh.kxt.index.ui.WebActivity;

/**
 * Created by Mr'Dai on 2017/4/6.
 */

public class WebPresenter extends BasePresenter {
    @BindObject WebActivity webActivity;

    public WebPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void addWebView(String url) {
        WebBuild webBuild = new LoadX5WebView(mContext).build();
        webBuild.loadUrl(url);

        webActivity.llWebParent.addView(webBuild.getWebParentView());
    }
}
