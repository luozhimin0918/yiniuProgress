package com.jyh.kxt.trading.ui;

import android.os.Bundle;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.trading.presenter.CommentDetailPresenter;
import com.library.widget.handmark.PullToRefreshListView;

import butterknife.BindView;

public class CommentDetailActivity extends BaseActivity {

    @BindView(R.id.ptrlv_content) public PullToRefreshListView pullToRefreshListView;

    private CommentDetailPresenter commentDetailPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_detail, StatusBarColor.THEME1);

        commentDetailPresenter = new CommentDetailPresenter(this);
        commentDetailPresenter.requestInitData();
    }
}
