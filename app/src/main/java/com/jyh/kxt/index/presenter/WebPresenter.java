package com.jyh.kxt.index.presenter;

import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindActivity;
import com.jyh.kxt.base.widget.LoadX5WebView;
import com.jyh.kxt.index.impl.WebBuild;
import com.jyh.kxt.index.ui.WebActivity;

/**
 * Created by Mr'Dai on 2017/4/6.
 */

public class WebPresenter extends BasePresenter {
    @BindActivity WebActivity webActivity;

    public WebPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void addWebView() {
        WebBuild webBuild = new LoadX5WebView(mContext).build();
        webBuild.loadUrl("http://www.17sucai.com/pins/demoshow/23876");

        webActivity.llWebParent.addView(webBuild.getWebParentView());
    }
}
