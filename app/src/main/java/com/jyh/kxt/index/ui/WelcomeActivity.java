package com.jyh.kxt.index.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.http.GlobalHttpRequest;
import com.jyh.kxt.base.utils.JumpUtils;
import com.jyh.kxt.index.presenter.WelcomePresenter;

import butterknife.BindView;
import butterknife.OnClick;
import cn.magicwindow.MLink;

/**
 * 启动-欢迎界面
 */
public class WelcomeActivity extends BaseActivity {

    @BindView(R.id.iv_welcome) public ImageView ivWelcome;
    @BindView(R.id.tv_advert_time) public TextView tvAdvertTime;

    private WelcomePresenter welcomePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        new WebView(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome, StatusBarColor.NO_COLOR);

        welcomePresenter = new WelcomePresenter(this);
        welcomePresenter.checkIsShowAdvert();
        welcomePresenter.requestMainData();
        welcomePresenter.requestMemberInfo();
        GlobalHttpRequest.getInstance().getSignInfo(this, null);

        welcomePresenter.initSharedPreferences();

        MLink.getInstance(this).registerWithAnnotation(this);

        Log.i("welcome", getIntent().toString());
        JumpUtils.MwJump(getIntent(), this);
    }

    @OnClick({R.id.tv_advert_time})
    public void onTimeClick(View view) {
        switch (view.getId()) {
            case R.id.tv_advert_time:
                welcomePresenter.isClickToWebAd = true;
                welcomePresenter.startToActivity(MainActivity.class);
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
