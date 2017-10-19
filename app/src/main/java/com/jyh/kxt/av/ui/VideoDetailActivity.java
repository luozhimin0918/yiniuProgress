package com.jyh.kxt.av.ui;

import android.content.Intent;
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
import com.jyh.kxt.base.utils.UmengShareUI;
import com.jyh.kxt.base.widget.ThumbView3;
import com.jyh.kxt.base.widget.night.ThemeUtil;
import com.jyh.kxt.push.PushUtil;
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
public class VideoDetailActivity extends BaseActivity implements CommentPresenter.OnCommentPublishListener,
        PageLoadLayout.OnAfreshLoadListener,SuperPlayer.OnBackPressListener {

    @BindView(R.id.view_super_player) public SuperPlayer spVideo;

    @BindView(R.id.activity_video_detail) LinearLayout llDetailContent;
    @BindView(R.id.rv_message) public PullToRefreshListView rvMessage;
    @BindView(R.id.iv_comment) ImageView ivComment;
    @BindView(R.id.iv_collect) public ImageView ivCollect;
    @BindView(R.id.iv_like) public ImageView ivLike;
    @BindView(R.id.iv_share) ImageView ivShare;
    @BindView(R.id.pll_content) public PageLoadLayout pllContent;
    @BindView(R.id.tv_commentCount) public TextView tvCommentCount;
    @BindView(R.id.tv_zanCount) public TextView tvZanCount;
    @BindView(R.id.ll_nav) public LinearLayout llNav;
    @BindView(R.id.thumb_view_zan) public ThumbView3 mThumbView;

    private VideoDetailPresenter videoDetailPresenter;
    public CommentPresenter commentPresenter;

    //视频ID
    public String videoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail, StatusBarColor.NO_COLOR);


        ActivityManager
                .getInstance()
                .finishNoCurrentActivity(VideoDetailActivity.class, VideoDetailActivity.this);  //保证只有一个Video界面

        videoId = getIntent().getStringExtra(IntentConstant.O_ID);
        videoDetailPresenter = new VideoDetailPresenter(this);
        commentPresenter = new CommentPresenter(this);
        spVideo.setBackPressList(this);
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
        brightnessSlide();

//        int mShowFlags =
//                View.SYSTEM_UI_FLAG_FULLSCREEN
//                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.INVISIBLE;
//        rvMessage.setSystemUiVisibility(mShowFlags);
    }


    @OnClick({R.id.tv_comment, R.id.iv_comment, R.id.iv_collect, R.id.iv_like, R.id.iv_share})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.tv_comment:
                //回复
                if (videoDetailPresenter.videoDetailBean == null) {
                    return;
                }
                commentPresenter.showReplyMessageView(view);
                break;
            case R.id.iv_comment:
                //回复
                if (videoDetailPresenter.videoDetailBean == null) {
                    return;
                }
                commentPresenter.showReplyMessageView(view);
                break;
            case R.id.iv_collect:
                //收藏
                if (videoDetailPresenter.videoDetailBean == null) {
                    return;
                }
                videoDetailPresenter.collect();
                break;
            case R.id.iv_like:
                //点赞
                if (videoDetailPresenter.videoDetailBean == null) {
                    return;
                }
                videoDetailPresenter.attention();
                break;
            case R.id.iv_share:
                //分享
                if (videoDetailPresenter.videoDetailBean == null) {
                    return;
                }
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
        PushUtil.pushToMainActivity(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UmengShareUI.onActivityResult(this, requestCode, resultCode, data);
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

    @Override
    protected void updateActivityMask(int nightMode) {

    }

    private void brightnessSlide() {
        int theme = ThemeUtil.getAlertTheme(this);
        switch (theme) {
            case android.support.v7.appcompat.R.style.Theme_AppCompat_DayNight_Dialog_Alert:
                float brightness = 0.1f;

                WindowManager.LayoutParams lpa = this.getWindow().getAttributes();
                lpa.screenBrightness = brightness;
                if (lpa.screenBrightness > 1.0f) {
                    lpa.screenBrightness = 1.0f;
                } else if (lpa.screenBrightness < 0.01f) {
                    lpa.screenBrightness = 0.01f;
                }
                this.getWindow().setAttributes(lpa);
                break;
            case android.support.v7.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert:
                break;
        }


    }

    @Override
    public void onSuperBackPress() {
        PushUtil.pushToMainActivity(this);
    }
}
