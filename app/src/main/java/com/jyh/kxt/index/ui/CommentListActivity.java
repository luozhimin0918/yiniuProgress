package com.jyh.kxt.index.ui;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.index.presenter.CommentListPresenter;

import butterknife.BindView;
import butterknife.OnClick;

import static com.jyh.kxt.R.id.comm_list_rb1;
import static com.jyh.kxt.R.id.comm_list_rb2;
import static com.jyh.kxt.R.id.comm_list_rb3;

/**
 * 评论列表
 */
public class CommentListActivity extends BaseActivity {

    @BindView(R.id.iv_bar_break) ImageView ivBarBreak;

    @BindView(R.id.comment_list_my_comment) TextView commentListMyComment;
    @BindView(R.id.comm_list_my_line) View commListMyLine;

    @BindView(R.id.comment_list_reply_me) TextView commentListReplyMe;
    @BindView(R.id.comm_list_reply_line) View commListReplyLine;

    @BindView(R.id.fl_content) public FrameLayout flContent;

    private CommentListPresenter commentListPresenter;

    private int navPosition = 0;
    public int listType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_list, StatusBarColor.THEME1);

        commentListPresenter = new CommentListPresenter(this);
        commentListPresenter.requestList(navPosition, listType);

    }

    @OnClick({R.id.iv_bar_break, R.id.comment_list_my_comment, R.id.comment_list_reply_me,
                     comm_list_rb1, comm_list_rb2, comm_list_rb3})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.iv_bar_break:
                onBackPressed();
                return;
            case R.id.comment_list_my_comment:
                navPosition = 0;
                navItemClick(navPosition);
                break;
            case R.id.comment_list_reply_me:
                navPosition = 1;
                navItemClick(navPosition);
                break;
            case comm_list_rb1:
                listType = 0;
                break;
            case comm_list_rb2:
                listType = 1;
                break;
            case comm_list_rb3:
                listType = 2;
                break;
        }
        commentListPresenter.requestList(navPosition, listType);
    }

    private void navItemClick(int navPosition) {
        commentListMyComment.setTextColor(ContextCompat.getColor(this, navPosition == 0 ? R.color.blue1 : R.color.font_color64));
        commListMyLine.setVisibility(navPosition == 0 ? View.VISIBLE : View.GONE);

        commentListReplyMe.setTextColor(ContextCompat.getColor(this, navPosition == 1 ? R.color.blue1 : R.color.font_color64));
        commListReplyLine.setVisibility(navPosition == 1 ? View.VISIBLE : View.GONE);
    }
}
