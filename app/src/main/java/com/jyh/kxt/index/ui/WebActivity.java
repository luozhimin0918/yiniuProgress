package com.jyh.kxt.index.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.json.ShareJson;
import com.jyh.kxt.base.utils.UmengShareTool;
import com.jyh.kxt.base.widget.LoadX5WebView;
import com.jyh.kxt.index.presenter.WebPresenter;
import com.library.util.RegexValidateUtil;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;

import butterknife.BindView;
import butterknife.OnClick;

import static com.jyh.kxt.base.constant.IntentConstant.AUTOOBTAINTITLE;

/**
 * 公用WebActivity
 */
public class WebActivity extends BaseActivity {

    @BindView(R.id.activity_web) public LinearLayout llWebParent;
    @BindView(R.id.iv_bar_break) ImageView ivBarBreak;
    @BindView(R.id.tv_bar_title) TextView tvBarTitle;
    @BindView(R.id.iv_bar_function) ImageView ivBarFunction;

    private WebPresenter webPresenter;
    private String title;
    private String url;
    public boolean javaScriptEnabled = true;
    private boolean autoObtainTitle = false;
    private boolean initialLoadTitle = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web, StatusBarColor.THEME1);

        Intent intent = getIntent();

        title = intent.getStringExtra(IntentConstant.NAME);
        url = intent.getStringExtra(IntentConstant.WEBURL);
        javaScriptEnabled = intent.getBooleanExtra(IntentConstant.JAVASCRIPTENABLED, true);
        autoObtainTitle = intent.getBooleanExtra(AUTOOBTAINTITLE, false);

        webPresenter = new WebPresenter(this);

        ivBarFunction.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.icon_nav_share));
        ivBarFunction.setVisibility(View.INVISIBLE);

        webPresenter.addWebView(title, url);
        webPresenter.setOnJsListener(new ObserverData<Boolean>() {
            @Override
            public void callback(final Boolean showBtn) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (showBtn) {
                            ivBarFunction.setVisibility(View.VISIBLE);
                        } else {
                            ivBarFunction.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ivBarFunction.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });
        if (!TextUtils.isEmpty(title)) {
            tvBarTitle.setText(title);
        } else {
            autoObtainTitle = true;//没有传入Title 则自动匹配
        }

        if (autoObtainTitle) {
            webPresenter.loadX5WebView.setOverWriteWebChromeClient(new WebChromeClient() {
                @Override
                public void onReceivedTitle(WebView webView, String pageTitle) {
                    super.onReceivedTitle(webView, pageTitle);
                    if (!RegexValidateUtil.isEmpty(pageTitle)) {
                        tvBarTitle.setText(pageTitle);
                        if (initialLoadTitle) {
                            initialLoadTitle = false;
                            title = pageTitle;
                        }
                    }
                }
            });
        }
    }

    @OnClick({R.id.iv_bar_break, R.id.iv_bar_function})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_bar_break:
                onBackPressed();
                break;
            case R.id.iv_bar_function:
                if (webPresenter != null && webPresenter.loadX5WebView != null) {
                    LoadX5WebView x5WebView = webPresenter.loadX5WebView;
                    if (!RegexValidateUtil.isEmpty(x5WebView.shareUrl)) {
                        UmengShareTool.initUmengLayout(this, new ShareJson(x5WebView.shareTitle, x5WebView.shareUrl,
                                "", x5WebView
                                .sharePic, null,
                                UmengShareTool.TYPE_DEFAULT, null, null, null, false, false), null, ivBarBreak, null);
                    }
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (tvBarTitle.getText().toString().equals(title)) {
                super.onBackPressed();
            } else {
                WebView webView = webPresenter.loadX5WebView.getWebView();
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    super.onBackPressed();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            super.onBackPressed();
        }
    }
}
