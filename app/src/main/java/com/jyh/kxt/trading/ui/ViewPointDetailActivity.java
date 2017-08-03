package com.jyh.kxt.trading.ui;

import android.os.Bundle;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.presenter.CommentPresenter;
import com.jyh.kxt.trading.presenter.ViewPointDetailPresenter;
import com.library.widget.handmark.PullToRefreshListView;

import butterknife.BindView;

public class ViewPointDetailActivity extends BaseActivity {

    @BindView(R.id.pplv_content) public PullToRefreshListView pplvContent;

    public CommentPresenter commentPresenter;
    private ViewPointDetailPresenter viewPointDetailPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_point_detail, StatusBarColor.THEME1);

        commentPresenter = new CommentPresenter(this);//初始化评论相关

        viewPointDetailPresenter = new ViewPointDetailPresenter(this);
        viewPointDetailPresenter.requestInitData();
    }
}
