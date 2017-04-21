package com.jyh.kxt.main.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.main.presenter.NewsContentPresenter;
import com.library.widget.PageLoadLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 项目名:Kxt
 * 类描述:要闻详情
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/18.
 */

public class NewsContentActivity extends BaseActivity {

    @BindView(R.id.pl_rootView)public PageLoadLayout plRootView;

    private NewsContentPresenter newsContentPresenter;

    public String id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_news_content, StatusBarColor.THEME1);

        newsContentPresenter = new NewsContentPresenter(this);

        Intent intent = getIntent();
        id = intent.getStringExtra(IntentConstant.O_ID);

        newsContentPresenter.init();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        id = intent.getStringExtra(IntentConstant.O_ID);
        newsContentPresenter.init();
    }
}
