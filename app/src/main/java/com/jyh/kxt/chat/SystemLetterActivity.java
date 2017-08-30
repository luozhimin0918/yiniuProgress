package com.jyh.kxt.chat;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 项目名:KxtProfessional
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/8/28.
 */

public class SystemLetterActivity extends BaseActivity implements PageLoadLayout.OnAfreshLoadListener,PullToRefreshBase.OnRefreshListener2 {

    @BindView(R.id.iv_bar_break) ImageView ivBarBreak;
    @BindView(R.id.tv_bar_title) TextView tvBarTitle;
    @BindView(R.id.iv_bar_function) ImageView ivBarFunction;
    @BindView(R.id.pl_content) PullToRefreshListView plContent;
    @BindView(R.id.pl_rootView) PageLoadLayout plRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_letter);

        initView();

    }

    private void initView() {
        ivBarFunction.setVisibility(View.INVISIBLE);
        tvBarTitle.setText("消息");

        plRootView.setOnAfreshLoadListener(this);

        plContent.setDividerNull();
        plContent.setMode(PullToRefreshBase.Mode.BOTH);
        plContent.setOnRefreshListener(this);
    }

    @OnClick(R.id.iv_bar_break)
    public void onClick() {
    }

    @Override
    public void OnAfreshLoad() {

    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {

    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {

    }
}
