package com.jyh.kxt.base.presenter;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.TextViewCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jyh.kxt.R;
import com.jyh.kxt.av.json.CommentBean;
import com.jyh.kxt.av.json.VideoDetailVideoBean;
import com.jyh.kxt.av.presenter.ReplyMessagePresenter;
import com.jyh.kxt.av.ui.VideoDetailActivity;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.util.PopupUtil;
import com.jyh.kxt.base.util.SoftKeyBoardListener;
import com.jyh.kxt.base.utils.JumpUtils;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.base.widget.night.heple.SkinnableTextView;
import com.jyh.kxt.main.json.NewsJson;
import com.jyh.kxt.user.ui.LoginActivity;
import com.library.util.LogUtil;
import com.library.util.SystemUtil;
import com.library.widget.handmark.PullToRefreshListView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mr'Dai on 2017/5/4.
 */

public class CommentPresenter extends BasePresenter implements SoftKeyBoardListener.OnSoftKeyBoardChangeListener {
    public void onChangeTheme() {
        try {
            SkinnableTextView skinnableTextView = (SkinnableTextView) tvRecommendLabel;
            skinnableTextView.applyDayNight();

            replyMessagePresenter.onChangeTheme();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
        /**
         * @param popupWindow
         * @param etContent
         * @param commentBean
         * @param parentId    0 表示回复的新评论  1 回复别人的评论  2 回复别人的回复评论
         */
        void onPublish(PopupWindow popupWindow, EditText etContent, CommentBean commentBean, int parentId);
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

    @BindView(R.id.tv_comment_count_title) public TextView tvCommentCountTitle;
    @BindView(R.id.tv_reply_message) TextView tvReplyMessage;
    @BindView(R.id.tv_recommend_label) TextView tvRecommendLabel;
    @BindView(R.id.ll_more_video) LinearLayout llMoreVideo;
    @BindView(R.id.rl_recommend_layout) RelativeLayout rlRecommendLayout;

    private LinearLayout headView;
    private PopupUtil replyMessagePopup;
    private ReplyMessagePresenter replyMessagePresenter;
    private boolean isOnlyAllowSmallEmoJe = false;

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
        replyMessageView();
    }

    public void bindListView(LinearLayout listHeadView) {
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        headView = (LinearLayout) mInflater.inflate(R.layout.view_comment_list, listHeadView, false);

        ButterKnife.bind(this, headView);

        listHeadView.addView(headView);

        tvReplyMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReplyMessageView(tvReplyMessage);
            }
        });
        replyMessageView();
    }


    public LinearLayout getHeadView() {
        return headView;
    }

    /**
     * 创建  填充更多的视图
     */
    public void createMoreVideoView(List<VideoDetailVideoBean> videoList) {
        if (videoList == null || videoList.size() == 0) {
            rlRecommendLayout.setVisibility(View.GONE);
            return;
        }
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        for (final VideoDetailVideoBean videoDetailVideoBean : videoList) {

            String imageUrl = HttpConstant.IMG_URL + videoDetailVideoBean.getPicture();

            View moreVideoView = mInflater.inflate(R.layout.item_more_video, llMoreVideo, false);
            ImageView ivVideoCover = (ImageView) moreVideoView.findViewById(R.id.iv_video_cover);
            TextView ivVideoName = (TextView) moreVideoView.findViewById(R.id.iv_video_name);

            Glide.with(mContext).load(imageUrl).error(R.mipmap.icon_def_news).placeholder(R.mipmap.icon_def_news)
                    .override(200, 200).into(ivVideoCover);

            ivVideoName.setText(videoDetailVideoBean.getTitle());

            llMoreVideo.addView(moreVideoView);

            moreVideoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, VideoDetailActivity.class);
                    intent.putExtra(IntentConstant.O_ID, videoDetailVideoBean.getId());
                    mContext.startActivity(intent);
                }
            });
        }
    }

    /**
     * 创建  填充更多的视图
     */
    public void createMoreView(List<NewsJson> articleList) {
        if (articleList == null || articleList.size() == 0) {
            rlRecommendLayout.setVisibility(View.GONE);
            return;
        }

        LayoutInflater mInflater = LayoutInflater.from(mContext);
        for (final NewsJson mArticleJson : articleList) {

            String imageUrl = HttpConstant.IMG_URL + mArticleJson.getPicture();

            View moreVideoView = mInflater.inflate(R.layout.item_more_video, llMoreVideo, false);
            ImageView ivVideoCover = (ImageView) moreVideoView.findViewById(R.id.iv_video_cover);
            TextView ivVideoName = (TextView) moreVideoView.findViewById(R.id.iv_video_name);

            Glide.with(mContext).load(HttpConstant.IMG_URL + imageUrl).error(R.mipmap.icon_def_news).placeholder(R
                    .mipmap.icon_def_news)
                    .override(200, 200).into
                    (ivVideoCover);
            ivVideoName.setText(mArticleJson.getTitle());

            llMoreVideo.addView(moreVideoView);

            moreVideoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Activity singleActivity = ActivityManager
//                            .getInstance()
//                            .getSingleActivity(NewsContentActivity.class);
//                    singleActivity.finish();

//                    Intent intent = new Intent(mContext, NewsContentActivity.class);
//                    intent.putExtra(IntentConstant.O_ID, mArticleJson.getO_id());
//                    mContext.startActivity(intent);
                    JumpUtils.jump((BaseActivity) mContext, mArticleJson.getO_class(), mArticleJson.getO_action(),
                            mArticleJson.getO_id()
                            , mArticleJson
                                    .getHref());
                }
            });
        }
    }

    /**
     * 暂无评论
     */
    public void createNoneComment() {
        int noneCommentHeight = SystemUtil.dp2px(mContext, 150);
        LinearLayout.LayoutParams commentParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, noneCommentHeight);

        commentParams.gravity = Gravity.CENTER;

        TextView tvNoneComment = new TextView(mContext);
        tvNoneComment.setLayoutParams(commentParams);
        tvNoneComment.setText("暂无评论");
        tvNoneComment.setGravity(Gravity.CENTER);

        TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(
                tvNoneComment,
                0,
                R.mipmap.icon_comment_not,
                0,
                0);

        tvNoneComment.setGravity(Gravity.CENTER);

        int color = ContextCompat.getColor(mContext, R.color.blue);
        tvNoneComment.setTextColor(color);

        tvNoneComment.setTag("noneComment");

        headView.addView(tvNoneComment);

        tvNoneComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean loginEd = LoginUtils.isLogined(mContext);
                if (loginEd) {
                    if (onCommentClickListener != null) {
                        onCommentClickListener.onClickView(ClickName.NONE_COMMENT);
                    }
                } else {
                    mContext.startActivity(new Intent(mContext, LoginActivity.class));
                }
            }
        });
    }


    private CommentBean commentBean;
    private int commentWho;
    private String hintContent;

    public void setContentEditHint(String hintContent) {
        this.hintContent = hintContent;
    }

    /**
     * 弹出回复的PopWindow
     */
    public void showReplyMessageView(View showAtLocation) {
        showReplyMessageView(showAtLocation, null, 0);
    }

    /**
     * @param showAtLocation
     * @param commentBean
     * @param commentWho     0 表示回复的新评论  1 回复别人的评论  2 回复别人的回复评论
     */
    public void showReplyMessageView(View showAtLocation, CommentBean commentBean, int commentWho) {
        this.commentBean = commentBean;
        this.commentWho = commentWho;

//        mBaseActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        mBaseActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);

        boolean loginEd = LoginUtils.isLogined(mContext);
        if (!loginEd) {
            mContext.startActivity(new Intent(mContext, LoginActivity.class));
            return;

        }
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
        replyMessagePresenter.setCommentBean(commentBean);
        replyMessagePresenter.setCommentWho(commentWho);
        replyMessagePresenter.isShowEmoJiView = false;

        replyMessagePresenter.setOnlyAllowSmallEmoJe(isOnlyAllowSmallEmoJe);
        replyMessagePresenter.setContentEditHint(hintContent);
        hintContent = "来发表您的伟大评论吧";

        replyMessagePopup.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        replyMessagePopup.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
//        replyMessagePopup.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        InputMethodManager imm = (InputMethodManager) mBaseActivity.getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);

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
        LogUtil.e(LogUtil.TAG, "keyBoardShow() called with: height = [" + height + "]");
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

    private void replyMessageView() {
        if (tvReplyMessage != null) {
            boolean loginEd = LoginUtils.isLogined(mContext);
            if (loginEd) {
                tvReplyMessage.setText("发表你的看法...");
            } else {
                tvReplyMessage.setText("一秒登录,发表你的看法...");
            }
        }
    }

    public void setRecommendLabel(int recommendType) {
        switch (recommendType) {
            case 0:
                tvRecommendLabel.setText("相关文章");
                break;
            case 1:
                tvRecommendLabel.setText("往期节目");
                break;
        }
    }

    public void setOnlyAllowSmallEmoJe(boolean isOnlyAllowSmallEmoJe) {
        this.isOnlyAllowSmallEmoJe = isOnlyAllowSmallEmoJe;
    }

    public void onResume() {
        replyMessageView();
    }
}
