package com.jyh.kxt.explore.ui;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.explore.presenter.MorePresenter;
import com.library.base.http.VarConstant;
import com.library.widget.PageLoadLayout;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 项目名:Kxt
 * 类描述:更多
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/8.
 */

public class MoreActivity extends BaseActivity implements PageLoadLayout.OnAfreshLoadListener {

    @BindView(R.id.tv_bar_title) TextView tvBarTitle;
    @BindView(R.id.rv_content) RecyclerView rvContent;
    @BindView(R.id.pl_rootView) PageLoadLayout plRootView;

    private MorePresenter morePresenter;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_more, StatusBarColor.THEME1);

        morePresenter = new MorePresenter(this);

        plRootView.setOnAfreshLoadListener(this);
        type = getIntent().getStringExtra(IntentConstant.TYPE);

        String name = "";
        switch (type) {
            case VarConstant.EXPLORE_ACTIVITY:
                name = "活动";
                break;
            case VarConstant.EXPLORE_BLOG_WRITER:
                name = "名家";
                break;
            case VarConstant.EXPLORE_TOPIC:
                name = "专题";
                break;
        }
        tvBarTitle.setText(name);

        plRootView.loadWait();
        morePresenter.init(type);
    }

    @OnClick(R.id.iv_bar_break)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_bar_break:
                onBackPressed();
                break;
        }
    }

    @Override
    public void OnAfreshLoad() {
        //重新加载
        if (type != null) {
            plRootView.loadWait();
            morePresenter.init(type);
        } else {
            plRootView.loadError();
        }
    }
}
