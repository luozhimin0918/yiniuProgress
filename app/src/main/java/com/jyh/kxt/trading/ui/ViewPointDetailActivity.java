package com.jyh.kxt.trading.ui;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.jyh.kxt.R;
import com.jyh.kxt.av.json.CommentBean;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.presenter.CommentPresenter;
import com.jyh.kxt.trading.presenter.ViewPointDetailPresenter;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;

import butterknife.BindView;

public class ViewPointDetailActivity extends BaseActivity implements CommentPresenter.OnCommentPublishListener {

    @BindView(R.id.pplv_content) public PullToRefreshListView mPullPinnedListView;

    public CommentPresenter commentPresenter;
    private ViewPointDetailPresenter viewPointDetailPresenter;

    public String detailId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_point_detail, StatusBarColor.THEME1);

        commentPresenter = new CommentPresenter(this);//初始化评论相关
        commentPresenter.setOnCommentPublishListener(this);

        detailId = getIntent().getStringExtra(IntentConstant.O_ID);

        initView();

        viewPointDetailPresenter = new ViewPointDetailPresenter(this);
        viewPointDetailPresenter.requestInitData();
    }

    private void initView() {
        ListView mRefreshableView = mPullPinnedListView.getRefreshableView();
        mRefreshableView.setDivider(new ColorDrawable(ContextCompat.getColor(getContext(), R.color.line_background4)));
        mRefreshableView.setDividerHeight(0);
        mRefreshableView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        mPullPinnedListView.setMode(PullToRefreshBase.Mode.DISABLED);
    }

    /**
     * @param popupWindow
     * @param etContent
     * @param commentBean
     * @param parentId    0 表示回复的新评论  1 回复别人的评论  2 回复别人的回复评论
     */
    @Override
    public void onPublish(PopupWindow popupWindow, EditText etContent, CommentBean commentBean, int parentId) {

    }
}
