package com.jyh.kxt.av.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.av.json.CommentBean;
import com.jyh.kxt.av.presenter.VideoDetailPresenter;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.presenter.CommentPresenter;
import com.jyh.kxt.base.utils.UmengShareTool;
import com.library.manager.ActivityManager;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;
import com.superplayer.library.SuperPlayer;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 视听-视屏详细页
 */
public class VideoDetailActivity extends BaseActivity implements CommentPresenter.OnCommentPublishListener, PageLoadLayout.OnAfreshLoadListener {

    @BindView(R.id.view_super_player) public SuperPlayer spVideo;

    @BindView(R.id.activity_video_detail) LinearLayout llDetailContent;
    @BindView(R.id.rv_message) public PullToRefreshListView rvMessage;
    @BindView(R.id.iv_break) ImageView ivBreak;
    @BindView(R.id.iv_comment) ImageView ivComment;
    @BindView(R.id.iv_collect) public ImageView ivCollect;
    @BindView(R.id.iv_like) public ImageView ivLike;
    @BindView(R.id.iv_share) ImageView ivShare;
    @BindView(R.id.pll_content) public PageLoadLayout pllContent;
    @BindView(R.id.tv_commentCount) public TextView tvCommentCount;
    @BindView(R.id.ll_nav) LinearLayout llNav;

    private VideoDetailPresenter videoDetailPresenter;
    public CommentPresenter commentPresenter;

    //视频ID
    public String videoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail, StatusBarColor.NO_COLOR);

       int mShowFlags =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.INVISIBLE;
        spVideo.setSystemUiVisibility(mShowFlags);

        videoId = getIntent().getStringExtra(IntentConstant.O_ID);
        videoDetailPresenter = new VideoDetailPresenter(this);
        commentPresenter = new CommentPresenter(this);

        commentPresenter.setOnCommentPublishListener(this);
        pllContent.loadWait(PageLoadLayout.BgColor.WHITE, "正在进入..");

        videoDetailPresenter.requestInitVideo(PullToRefreshBase.Mode.PULL_FROM_START);

        rvMessage.setMode(PullToRefreshBase.Mode.DISABLED);
        rvMessage.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                videoDetailPresenter.requestInitVideo(PullToRefreshBase.Mode.PULL_FROM_END);
            }
        });

        pllContent.setOnAfreshLoadListener(this);

        ActivityManager
                .getInstance()
                .finishNoCurrentActivity(VideoDetailActivity.class, VideoDetailActivity.this);  //保证只有一个Video界面

    }


    @OnClick({R.id.iv_break, R.id.iv_comment, R.id.iv_collect, R.id.iv_like, R.id.iv_share})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.iv_break:
                onBackPressed();
                break;
            case R.id.iv_comment:
                //回复
                commentPresenter.showReplyMessageView(view);
                break;
            case R.id.iv_collect:
                //收藏
                videoDetailPresenter.collect();
                break;
            case R.id.iv_like:
                //点赞
                videoDetailPresenter.attention();
                break;
            case R.id.iv_share:
                //分享
                videoDetailPresenter.share();
                break;
        }
    }

    @Override
    public void onPublish(PopupWindow popupWindow, EditText etContent, CommentBean commentBean, int parentId) {
        videoDetailPresenter.requestIssueComment(popupWindow, etContent, commentBean, parentId);
    }

    /**
     * 下面的这几个Activity的生命状态很重要
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (spVideo != null) {
            spVideo.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        commentPresenter.onResume();
        if (spVideo != null) {
            spVideo.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (spVideo != null) {
            spVideo.onDestroy();
        }

        System.gc();
        Runtime.getRuntime().gc();
        System.runFinalization();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (spVideo != null) {
            boolean portrait = newConfig.orientation == Configuration.ORIENTATION_PORTRAIT;
            llNav.setVisibility(portrait ? View.VISIBLE : View.GONE);
            spVideo.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onBackPressed() {
        if (spVideo != null && spVideo.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UmengShareTool.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    protected void onChangeTheme() {
        super.onChangeTheme();
        if (commentPresenter != null) {
            commentPresenter.onChangeTheme();
        }
    }

    @Override
    public void OnAfreshLoad() {
        pllContent.loadOver();
        videoDetailPresenter.requestInitVideo(PullToRefreshBase.Mode.PULL_FROM_START);
    }
}
