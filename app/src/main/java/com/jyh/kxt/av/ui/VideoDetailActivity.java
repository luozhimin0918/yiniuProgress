package com.jyh.kxt.av.ui;

import android.app.Service;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.av.presenter.ReplyMessagePresenter;
import com.jyh.kxt.av.presenter.VideoDetailPresenter;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.util.PopupUtil;
import com.jyh.kxt.base.util.SoftKeyBoardListener;
import com.jyh.kxt.base.util.emoje.EmoticonsUtils;
import com.jyh.kxt.base.util.emoje.utils.Utils;
import com.superplayer.library.SuperPlayer;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 视听-视屏详细页
 */
public class VideoDetailActivity extends BaseActivity implements SoftKeyBoardListener.OnSoftKeyBoardChangeListener {

    @BindView(R.id.view_super_player) public SuperPlayer spVideo;
    @BindView(R.id.tv_reply_message) TextView tvReplyMessage;
    @BindView(R.id.activity_video_detail) LinearLayout llDetailContent;

    private VideoDetailPresenter videoDetailPresenter;

    //评论相关
    private PopupUtil replyMessagePopup;
    private ReplyMessagePresenter replyMessagePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);

        EmoticonsUtils.initEmoticonsDB(this);

        setContentView(R.layout.activity_video_detail, StatusBarColor.NO_COLOR);

        videoDetailPresenter = new VideoDetailPresenter(this);
        videoDetailPresenter.initVideo();
    }


    @OnClick({R.id.tv_reply_message})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.tv_reply_message:
                showReplyMessageView();
                break;
        }
    }

    /**
     * 弹出回复的PopWindow
     */
    private void showReplyMessageView() {

        if (replyMessagePopup == null) {
            replyMessagePresenter = new ReplyMessagePresenter(this);

            replyMessagePopup = new PopupUtil(this);
            View replyMessageView = replyMessagePopup.createPopupView(R.layout.view_reply_message);

            replyMessagePresenter.initView(replyMessageView, replyMessagePopup);

            PopupUtil.Config config = new PopupUtil.Config();

            config.outsideTouchable = true;
            config.alpha = 0.5f;
            config.bgColor = 0X00000000;

            config.animationStyle = R.style.PopupWindow_Style2;
            config.width = WindowManager.LayoutParams.MATCH_PARENT;
            config.height = WindowManager.LayoutParams.WRAP_CONTENT;

            replyMessagePopup.setConfig(config);

            SoftKeyBoardListener.setListener(VideoDetailActivity.this, this);
        }
        replyMessagePresenter.isShowEmoJiView = false;

        replyMessagePopup.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
//        replyMessagePopup.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

        replyMessagePopup.showAtLocation(tvReplyMessage, Gravity.BOTTOM, 0, 0);

        replyMessagePopup.setOnDismissListener(new PopupUtil.OnDismissListener() {
            @Override
            public void onDismiss() {
                replyMessagePresenter.goneEmoJeView();
            }
        });
    }

    @Override
    public void keyBoardShow(int height) {

        Utils.setDefKeyboardHeight(this, height);

        if (replyMessagePresenter != null) {
            replyMessagePresenter.adjustEmoJeView(height);
        }
    }

    @Override
    public void keyBoardHide(int height) {
        if (replyMessagePopup != null &&
                replyMessagePresenter != null &&
                !replyMessagePresenter.isShowEmoJiView) {
            replyMessagePopup.dismiss();
        }
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
        if (replyMessagePopup != null) {
            replyMessagePopup.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (spVideo != null) {
            spVideo.onDestroy();
        }
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
