package com.jyh.kxt.av.ui;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
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
public class VideoDetailActivity extends BaseActivity implements CommentPresenter.OnCommentPublishListener {

    @BindView(R.id.view_super_player) public SuperPlayer spVideo;

    @BindView(R.id.activity_video_detail) LinearLayout llDetailContent;
    @BindView(R.id.tv_title) public TextView tvTitle;
    @BindView(R.id.tv_playCount) public TextView tvPlayCount;
    @BindView(R.id.rv_message) public PullToRefreshListView rvMessage;
    @BindView(R.id.iv_break) ImageView ivBreak;
    @BindView(R.id.iv_comment) ImageView ivComment;
    @BindView(R.id.iv_collect) ImageView ivCollect;
    @BindView(R.id.iv_like) ImageView ivLike;
    @BindView(R.id.iv_share) ImageView ivShare;
    @BindView(R.id.pll_content) public PageLoadLayout pllContent;
    @BindView(R.id.tv_commentCount) public TextView tvCommentCount;

    private VideoDetailPresenter videoDetailPresenter;
    public CommentPresenter commentPresenter;

    //视频ID
    public String videoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //保证只有一个Video界面
        Activity singleActivity = ActivityManager
                .getInstance()
                .getSingleActivity(VideoDetailActivity.class);
        if(singleActivity!=null){
            singleActivity.finish();
        }

        super.onCreate(savedInstanceState);

        videoId = getIntent().getStringExtra(IntentConstant.O_ID);

        setContentView(R.layout.activity_video_detail, StatusBarColor.NO_COLOR);

        videoDetailPresenter = new VideoDetailPresenter(this);
        commentPresenter = new CommentPresenter(this);

        commentPresenter.setOnCommentPublishListener(this);
        pllContent.loadWait(PageLoadLayout.BgColor.TRANSPARENT8, "正在进入..");

        videoDetailPresenter.requestInitVideo(PullToRefreshBase.Mode.PULL_FROM_START);

        rvMessage.setMode(PullToRefreshBase.Mode.DISABLED);
        rvMessage.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                videoDetailPresenter.requestInitVideo(PullToRefreshBase.Mode.PULL_FROM_END);
            }
        });
    }


    @OnClick({R.id.iv_break, R.id.iv_comment, R.id.iv_collect, R.id.iv_like, R.id.iv_share})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.iv_break:
                onBackPressed();
                break;
            case R.id.iv_comment:
                //回复
                commentPresenter.showReplyMessageView(tvTitle);
                break;
            case R.id.iv_collect:
                //收藏
                break;
            case R.id.iv_like:
                //点赞
                break;
            case R.id.iv_share:
                //分享
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

}
