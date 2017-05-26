package com.jyh.kxt.index.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.index.presenter.WelcomePresenter;
import com.library.util.SPUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 启动-欢迎界面
 */
public class WelcomeActivity extends BaseActivity {

    @BindView(R.id.iv_welcome) public ImageView ivWelcome;
    @BindView(R.id.tv_advert_time) public TextView tvAdvertTime;

    private WelcomePresenter welcomePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome, StatusBarColor.NO_COLOR);

        welcomePresenter = new WelcomePresenter(this);
        welcomePresenter.checkIsShowAdvert();

        welcomePresenter.initSharedPreferences();

    }

    @OnClick({R.id.tv_advert_time })
    public void onTimeClick(View view){
        switch (view.getId()){
            case R.id.tv_advert_time:
                welcomePresenter.startToActivity(MainActivity.class);
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
