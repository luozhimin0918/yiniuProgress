package com.jyh.kxt.index.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.index.presenter.WebPresenter;
import com.library.base.http.VarConstant;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 公用WebActivity
 */
public class WebActivity extends BaseActivity {

    @BindView(R.id.activity_web) public LinearLayout llWebParent;
    @BindView(R.id.iv_bar_break) ImageView ivBarBreak;
    @BindView(R.id.tv_bar_title) TextView tvBarTitle;
    @BindView(R.id.iv_bar_function) TextView ivBarFunction;

    private WebPresenter webPresenter;
    private String title;
    private String url;
    private String source;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web, StatusBarColor.THEME1);

        webPresenter = new WebPresenter(this);

        getExtra(getIntent());

        if (title != null)
            tvBarTitle.setText(title);

        webPresenter.addWebView(url);

    }

    /**
     * 获取参数
     *
     * @param intent
     */
    private void getExtra(Intent intent) {
        title = intent.getStringExtra(IntentConstant.NAME);
//        url = intent.getStringExtra(IntentConstant.WEBURL);
        url = "http://test.kxtadi.kuaixun56.com/Webview/view/id/2966";
        source = intent.getStringExtra(IntentConstant.SOURCE);
    }

    @OnClick({R.id.iv_bar_break, R.id.iv_bar_function})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_bar_break:
                onBackPressed();
                break;
            case R.id.iv_bar_function:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (source != null && WelcomeActivity.class.getName().equals(source)) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
        super.onBackPressed();
    }
}
