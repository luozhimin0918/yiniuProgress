package com.jyh.kxt.base.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jyh.kxt.R;
import com.jyh.kxt.index.impl.WebBuild;
import com.library.base.http.VarConstant;
import com.library.util.SystemUtil;
import com.tencent.smtt.export.external.extension.interfaces.IX5WebViewExtension;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

/**
 * Created by Mr'Dai on 2016/11/22.
 */
public class LoadX5WebView extends FrameLayout implements WebBuild {

    private View failureView;
    private WebView wvContent;
    private ProgressBar pbLoading;

    private WebSettings mSettings;

    private String webUrl;

    public LoadX5WebView(Context context) {
        super(context);
    }

    public LoadX5WebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadX5WebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public WebBuild build() {
        pbLoading = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleHorizontal);
        pbLoading.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, SystemUtil.dp2px(getContext(), 2), 0));
        pbLoading.setProgressDrawable(ContextCompat.getDrawable(getContext(), R.drawable.shape_progress_bar_bg));

        wvContent = new WebView(getContext());
        wvContent.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        mSettings = wvContent.getSettings();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mSettings.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        mSettings.setLoadWithOverviewMode(true);
        mSettings.setBlockNetworkImage(false);
        mSettings.setUseWideViewPort(true);
        mSettings.setSupportZoom(false);

//        mSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mSettings.setJavaScriptEnabled(true);
        mSettings.setAppCacheEnabled(true);
        mSettings.setUserAgent("kxtapp" + "_" + VarConstant.HTTP_SYSTEM_VALUE + "_" + VarConstant.HTTP_VERSION_VALUE);

        wvContent.setWebViewClient(new MyWebViewClient());
        wvContent.setWebChromeClient(new MyWebChromeClient());

        int color = ContextCompat.getColor(getContext(), R.color.white);
        wvContent.setBackgroundColor(color);
        wvContent.getBackground().setAlpha(255);

        ColorDrawable colorDrawable = new ColorDrawable(color);
        wvContent.setForeground(colorDrawable);

        wvContent.addJavascriptInterface(new getShareInfoInterface(), "shareInfoInterface");

        IX5WebViewExtension ix5 = wvContent.getX5WebViewExtension();
        if (null != ix5) {
            ix5.setScrollBarFadingEnabled(false);
        }

        this.addView(wvContent);
        this.addView(pbLoading);

        return this;
    }

    @Override
    public WebView getWebView() {
        return wvContent;
    }

    @Override
    public ViewGroup getWebParentView() {
        setLayoutParams(new ViewGroup.LayoutParams
                (
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                ));
        return this;
    }

    @Override
    public void loadUrl(String url) {
        String connector = "?";
        if (url.contains("?")) {
            connector = "&";
        }
        webUrl = url + connector + VarConstant.HTTP_VERSION + "=" + VarConstant.HTTP_VERSION_VALUE
                + "&" + VarConstant.HTTP_SYSTEM + "=" + VarConstant.HTTP_SYSTEM_VALUE;
        wvContent.loadUrl(url);
    }

    @Override
    public void loadData(String data) {
        wvContent.loadDataWithBaseURL(null, data, "text/html", "utf-8", null);
    }

    public class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url == null) {
                return true;
            }
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            view.loadUrl("javascript:function getShareInfo(){" +
                    "var shareVal=document.getElementById(\"webView_share\").innerText;" +
                    "window.shareInfoInterface.getShareInfo(shareVal);" +
                    "}");
            view.loadUrl("javascript:getShareInfo()");
            wvContent.setForeground(null);
        }

        @Override
        public void onReceivedError(WebView webView, int errorCode, String description, String failingUrl) {
            super.onReceivedError(webView, errorCode, description, failingUrl);
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView webView, String url) {
            return super.shouldInterceptRequest(webView, url);
        }
    }

    /**
     * 使用   google chrome
     */
    public class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {

            pbLoading.setProgress(newProgress);

            if (newProgress == 100) {
                pbLoading.setVisibility(View.GONE);
            } else {
                if (pbLoading.getVisibility() == View.GONE) {
                    pbLoading.setVisibility(View.VISIBLE);
                }
            }

            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);

            CharSequence notFound = "404";
            CharSequence notFound2 = "找不到网页";
            CharSequence notFound3 = "网页无法打开";

            if (title.contains(notFound) ||
                    title.contains(notFound2) ||
                    title.contains(notFound3)) {
                wvContent.loadUrl("about:blank");
                showFailureView();
            }
        }
    }

    private void showFailureView() {
        pbLoading.setVisibility(GONE);

        failureView = LayoutInflater.from(getContext()).inflate(R.layout.web_load_error, null);
        failureView.findViewById(R.id.error_reload).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    pbLoading.setVisibility(View.VISIBLE);
                    removeView(failureView);
                    wvContent.loadUrl(webUrl);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        this.addView(failureView);
        failureView.bringToFront();
    }

    public class getShareInfoInterface {
        @JavascriptInterface
        public void getShareInfo(String shareInfo) {
            Toast.makeText(getContext(), shareInfo, Toast.LENGTH_SHORT).show();
        }
    }
}
