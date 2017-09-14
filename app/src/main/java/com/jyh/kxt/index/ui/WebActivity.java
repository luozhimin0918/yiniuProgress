package com.jyh.kxt.index.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.json.ShareItemJson;
import com.jyh.kxt.base.json.UmengShareBean;
import com.jyh.kxt.base.util.PopupUtil;
import com.jyh.kxt.base.utils.OnPopupFunListener;
import com.jyh.kxt.base.utils.UmengShareUI;
import com.jyh.kxt.base.utils.UmengShareUtil;
import com.jyh.kxt.base.widget.LoadX5WebView;
import com.jyh.kxt.index.presenter.WebPresenter;
import com.library.util.RegexValidateUtil;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;

import java.util.ArrayList;
import java.util.List;

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
    private String title = "";
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

        ivBarFunction.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.icon_video_more));
        ivBarFunction.setVisibility(View.VISIBLE);
        webPresenter.addWebView(title, url);

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

                    UmengShareBean umengShareBean = new UmengShareBean();
                    umengShareBean.setTitle(x5WebView.shareTitle == null ? title : x5WebView.shareTitle);
                    umengShareBean.setDetail("");

                    if (x5WebView.sharePic != null) {
                        umengShareBean.setImageUrl(x5WebView.sharePic);
                    }

                    //创建下面的功能Adapter
                    List<ShareItemJson> functionList = new ArrayList<>();
                    functionList.add(new ShareItemJson(R.mipmap.icon_share_link_open, "浏览器"));
                    functionList.add(new ShareItemJson(R.mipmap.icon_share_link_refresh, "刷新"));
                    functionList.add(new ShareItemJson(UmengShareUtil.FUN_COPY_URL, R.mipmap.icon_share_link, "复制链接"));
                    functionList.add(new ShareItemJson(UmengShareUtil.FUN_CLOSE_POP, R.mipmap.icon_share_close, "取消"));

                    umengShareBean.setWebUrl(x5WebView.shareUrl == null ? url : x5WebView.shareUrl);
                    umengShareBean.setFromSource(UmengShareUtil.SHARE_ADVERT);
                    final UmengShareUI umengShareUI = new UmengShareUI(this);
                    final PopupUtil popupUtil = umengShareUI.showSharePopup(umengShareBean, functionList);
                    umengShareUI.setOnPopupFunListener(new OnPopupFunListener() {
                        @Override
                        public void onClickItem(View itemView, ShareItemJson mShareItemJson, RecyclerView.Adapter recyclerAdapter) {
                            int icon = mShareItemJson.icon;
                            switch (icon) {
                                case R.mipmap.icon_share_link_open:
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse(url));
                                    startActivity(intent);
                                    break;
                                case R.mipmap.icon_share_link_refresh:
                                    webPresenter.loadX5WebView.getWebView().reload();
                                    break;
                            }
                            popupUtil.dismiss();
                        }
                    });
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        try {
            webPresenter.loadX5WebView.getWebView().loadUrl("about:blank");
            super.onBackPressed();
        } catch (Exception e) {
            e.printStackTrace();
            super.onBackPressed();
        }
    }
}
