package com.jyh.kxt.trading.ui;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ListView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.presenter.CommentPresenter;
import com.jyh.kxt.trading.presenter.ViewPointDetailPresenter;
import com.library.widget.handmark.PullToRefreshListView;

import butterknife.BindView;

public class ViewPointDetailActivity extends BaseActivity {

    @BindView(R.id.pplv_content) public PullToRefreshListView mPullPinnedListView;

    public CommentPresenter commentPresenter;
    private ViewPointDetailPresenter viewPointDetailPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_point_detail, StatusBarColor.THEME1);

        commentPresenter = new CommentPresenter(this);//初始化评论相关

        initView();

        viewPointDetailPresenter = new ViewPointDetailPresenter(this);
        viewPointDetailPresenter.requestInitData();
    }

    private void initView() {
        ListView mRefreshableView = mPullPinnedListView.getRefreshableView();
        mRefreshableView.setDivider(new ColorDrawable(ContextCompat.getColor(getContext(), R.color.line_background4)));
        mRefreshableView.setDividerHeight(0);
        mRefreshableView.setOverScrollMode(View.OVER_SCROLL_NEVER);
    }
}
