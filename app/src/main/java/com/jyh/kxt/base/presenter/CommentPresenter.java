package com.jyh.kxt.base.presenter;

import android.app.Activity;
import android.app.Service;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jyh.kxt.R;
import com.jyh.kxt.av.json.VideoDetailVideoBean;
import com.jyh.kxt.av.presenter.ReplyMessagePresenter;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.util.PopupUtil;
import com.jyh.kxt.base.util.SoftKeyBoardListener;
import com.jyh.kxt.main.json.NewsJson;
import com.library.util.SystemUtil;
import com.library.widget.handmark.PullToRefreshListView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mr'Dai on 2017/5/4.
 */

public class CommentPresenter extends BasePresenter implements SoftKeyBoardListener.OnSoftKeyBoardChangeListener {
    public enum ClickName {
        MORE_VIEW_ITEM,//更多视图
        NONE_COMMENT//暂无评论
    }

    public interface OnCommentClickListener {
        void onClickView(ClickName clickName);
    }


    private OnCommentClickListener onCommentClickListener;

    public void setOnCommentClickListener(OnCommentClickListener onCommentClickListener) {
        this.onCommentClickListener = onCommentClickListener;
    }


    /**
     * 评论的
     */
    private OnCommentPublishListener onCommentPublishListener;

    public interface OnCommentPublishListener {
        void onPublish(PopupWindow popupWindow, EditText etContent);
    }

    public void setOnCommentPublishListener(OnCommentPublishListener onCommentPublishListener) {
        this.onCommentPublishListener = onCommentPublishListener;
    }

    /**
     * 这个地方直接传递Activity 方便调用生命周期
     */
    private BaseActivity mBaseActivity;

    public CommentPresenter(BaseActivity mBaseActivity) {
        super(mBaseActivity);
        this.mBaseActivity = mBaseActivity;
    }

    @BindView(R.id.tv_reply_message) TextView tvReplyMessage;
    @BindView(R.id.ll_more_video) LinearLayout llMoreVideo;

    private LinearLayout headView;
    private PopupUtil replyMessagePopup;
    private ReplyMessagePresenter replyMessagePresenter;

    public void bindListView(PullToRefreshListView listView) {
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        headView = (LinearLayout) mInflater.inflate(R.layout.view_comment_list, null);

        ButterKnife.bind(this, headView);

        listView.getRefreshableView().addHeaderView(headView);
        tvReplyMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReplyMessageView(tvReplyMessage);
            }
        });
    }

    public LinearLayout getHeadView() {
        return headView;
    }

    /**
     * 创建  填充更多的视图
     */
    public void createMoreVideoView(List<VideoDetailVideoBean> videoList) {
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        for (VideoDetailVideoBean videoDetailVideoBean : videoList) {

            String imageUrl = HttpConstant.IMG_URL + videoDetailVideoBean.getPicture();

            View moreVideoView = mInflater.inflate(R.layout.item_more_video, llMoreVideo, false);
            ImageView ivVideoCover = (ImageView) moreVideoView.findViewById(R.id.iv_video_cover);
            TextView ivVideoName = (TextView) moreVideoView.findViewById(R.id.iv_video_name);

            Glide.with(mContext).load(imageUrl).override(100, 100).into(ivVideoCover);
            ivVideoName.setText(videoDetailVideoBean.getTitle());

            llMoreVideo.addView(moreVideoView);
        }
    }

    /**
     * 创建  填充更多的视图
     */
    public void createMoreView(List<NewsJson> articleList) {
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        for (NewsJson mArticleJson : articleList) {

            String imageUrl = HttpConstant.IMG_URL + mArticleJson.getPicture();

            View moreVideoView = mInflater.inflate(R.layout.item_more_video, llMoreVideo, false);
            ImageView ivVideoCover = (ImageView) moreVideoView.findViewById(R.id.iv_video_cover);
            TextView ivVideoName = (TextView) moreVideoView.findViewById(R.id.iv_video_name);

            Glide.with(mContext).load(imageUrl).override(100, 100).into(ivVideoCover);
            ivVideoName.setText(mArticleJson.getTitle());

            llMoreVideo.addView(moreVideoView);
        }
    }

    /**
     * 暂无评论
     */
    public void createNoneComment() {
        int noneCommentHeight = SystemUtil.dp2px(mContext, 100);
        LinearLayout.LayoutParams commentParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, noneCommentHeight);

        commentParams.gravity = Gravity.CENTER;

        TextView tvNoneComment = new TextView(mContext);
        tvNoneComment.setLayoutParams(commentParams);
        tvNoneComment.setText("暂无评论,点击抢沙发");

        tvNoneComment.setGravity(Gravity.CENTER);

        int color = ContextCompat.getColor(mContext, R.color.blue);
        tvNoneComment.setTextColor(color);

        tvNoneComment.setTag("noneComment");

        headView.addView(tvNoneComment);

        tvNoneComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCommentClickListener != null) {
                    onCommentClickListener.onClickView(ClickName.NONE_COMMENT);
                }
            }
        });
    }

    /**
     * 弹出回复的PopWindow
     */
    public void showReplyMessageView(View showAtLocation) {
        if (replyMessagePopup == null) {

            replyMessagePopup = new PopupUtil((Activity) mContext);

            replyMessagePresenter = new ReplyMessagePresenter(iBaseView);
            View replyMessageView = replyMessagePopup.createPopupView(R.layout.view_reply_message);

            replyMessagePresenter.initView(replyMessageView, replyMessagePopup, onCommentPublishListener);

            PopupUtil.Config config = new PopupUtil.Config();

            config.outsideTouchable = true;
            config.alpha = 0.5f;
            config.bgColor = 0X00000000;

            config.animationStyle = R.style.PopupWindow_Style2;
            config.width = WindowManager.LayoutParams.MATCH_PARENT;
            config.height = WindowManager.LayoutParams.WRAP_CONTENT;

            replyMessagePopup.setConfig(config);

            SoftKeyBoardListener.setListener(mBaseActivity, this);
        }
        replyMessagePresenter.isShowEmoJiView = false;

        replyMessagePopup.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);

        InputMethodManager imm = (InputMethodManager) mBaseActivity.getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

        replyMessagePopup.showAtLocation(showAtLocation, Gravity.BOTTOM, 0, 0);

        replyMessagePopup.setOnDismissListener(new PopupUtil.OnDismissListener() {
            @Override
            public void onDismiss() {
                replyMessagePresenter.goneEmoJeView();
            }
        });
    }

    @Override
    public void keyBoardShow(int height) {
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
}
