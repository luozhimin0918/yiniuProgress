package com.jyh.kxt.index.ui;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.index.presenter.WebPresenter;

import butterknife.BindView;

public class WebActivity extends BaseActivity {

    @BindView(R.id.activity_web) public LinearLayout llWebParent;

    private WebPresenter webPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        webPresenter = new WebPresenter(this);
        webPresenter.addWebView();
    }
}
