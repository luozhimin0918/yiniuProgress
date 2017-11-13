package com.jyh.kxt.index.ui;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.http.GlobalHttpRequest;
import com.jyh.kxt.base.util.emoje.EmoticonsUtils;
import com.jyh.kxt.base.utils.JumpUtils;
import com.jyh.kxt.index.presenter.WelcomePresenter;
import com.jyh.kxt.push.PushJsonHandle;

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

//        startActivity(new Intent(this,GuideActivity.class));
//        this.finish();
//        return;

        //初始化操作
        EmoticonsUtils.loadEmoticonToDB(this);


        //针对华为推送

        Uri mPushUrl = getIntent().getData();
        if (mPushUrl != null) {
            if ("kxtapp".equals(mPushUrl.getScheme()) && "com.kxt.push".equals(mPushUrl.getAuthority())) {
                String message = mPushUrl.getQueryParameter("message");
                PushJsonHandle.getInstance().notificationHuaWeiDisplay(WelcomeActivity.this, message);
                this.finish();
                return;
            }
        }

        //华为推送 - 通知栏打开的时候
        setContentView(R.layout.activity_welcome, StatusBarColor.NO_COLOR);

        welcomePresenter = new WelcomePresenter(this);
        welcomePresenter.checkIsShowAdvert();
        welcomePresenter.requestMemberInfo();
        welcomePresenter.requestPreNews();
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
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ivWelcome != null) {
            ivWelcome.setImageBitmap(null);
        }
    }
}
