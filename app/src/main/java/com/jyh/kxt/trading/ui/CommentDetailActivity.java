package com.jyh.kxt.trading.ui;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.av.json.CommentBean;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.presenter.CommentPresenter;
import com.jyh.kxt.trading.presenter.CommentDetailPresenter;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;

import butterknife.BindView;
import butterknife.OnClick;

public class CommentDetailActivity extends BaseActivity implements CommentPresenter.OnCommentPublishListener {

    @BindView(R.id.tv_bar_title) TextView tvBarTitle;
    @BindView(R.id.ptrlv_content) public PullToRefreshListView pullToRefreshListView;

    public String commentId;
    public String viewPointId;
    public CommentPresenter mCommentPresenter;
    private CommentDetailPresenter commentDetailPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_detail, StatusBarColor.THEME1);
        tvBarTitle.setText("回复");

        commentId = getIntent().getStringExtra("commentId");
        viewPointId = getIntent().getStringExtra("viewPointId");

        initView();

        mCommentPresenter = new CommentPresenter(this);
        commentDetailPresenter = new CommentDetailPresenter(this);
        commentDetailPresenter.requestListData();

        mCommentPresenter.setOnCommentPublishListener(this);
    }

    private void initView() {
        ListView mRefreshableView = pullToRefreshListView.getRefreshableView();
        mRefreshableView.setDivider(new ColorDrawable(ContextCompat.getColor(getContext(), R.color.line_background4)));
        mRefreshableView.setDividerHeight(0);
        mRefreshableView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.DISABLED);
    }

    @OnClick({R.id.iv_bar_break, R.id.comment_publish})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.iv_bar_break:
                onBackPressed();
                break;
            case R.id.comment_publish:
                mCommentPresenter.showReplyMessageView(view);
                break;
        }
    }

    /**
     * @param popupWindow
     * @param etContent
     * @param commentBean
     * @param parentId    0 表示回复的新评论  1 回复别人的评论  2 回复别人的回复评论
     */
    @Override
    public void onPublish(PopupWindow popupWindow, EditText etContent, CommentBean commentBean, int parentId) {
        commentDetailPresenter.requestIssueComment(popupWindow, etContent, commentBean, parentId);
    }
}
