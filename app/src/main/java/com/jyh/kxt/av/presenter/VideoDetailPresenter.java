package com.jyh.kxt.av.presenter;

import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.TextViewCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.av.adapter.CommentAdapter;
import com.jyh.kxt.av.json.CommentBean;
import com.jyh.kxt.av.json.VideoDetailBean;
import com.jyh.kxt.av.json.VideoListJson;
import com.jyh.kxt.av.ui.VideoDetailActivity;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.bean.SignInfoJson;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.json.UmengShareBean;
import com.jyh.kxt.base.util.PopupUtil;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.base.utils.NativeStore;
import com.jyh.kxt.base.utils.UmengShareUI;
import com.jyh.kxt.base.utils.UmengShareUtil;
import com.jyh.kxt.base.utils.collect.CollectUtils;
import com.jyh.kxt.base.widget.AdvertImageLayout;
import com.jyh.kxt.user.json.UserJson;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.bean.EventBusClass;
import com.library.util.JsonUtil;
import com.library.util.SystemUtil;
import com.library.widget.handmark.PullToRefreshBase;
import com.superplayer.library.SuperPlayer;
import com.trycatch.mysnackbar.Prompt;
import com.trycatch.mysnackbar.TSnackbar;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by Mr'Dai on 2017/3/31.
 */

public class VideoDetailPresenter extends BasePresenter {
    @BindObject
    public VideoDetailActivity videoDetailActivity;

    private LinearLayout headView;
    private CommentAdapter commentAdapter;
    private List<CommentBean> adapterCommentList = new ArrayList<>();

    private boolean isCollect, isAttention;

    public VideoDetailBean videoDetailBean;
    private VideoListJson videoListJson;

    public VideoDetailPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    /**
     * 请求初始化Video
     */
    public void requestInitVideo(final PullToRefreshBase.Mode pullMode) {
        VolleyRequest volleyRequest = new VolleyRequest(mContext, mQueue);
        JSONObject jsonParam = volleyRequest.getJsonParam();
        jsonParam.put("id", videoDetailActivity.videoId);

        if (pullMode == PullToRefreshBase.Mode.PULL_FROM_END) {
            CommentBean commentBean = adapterCommentList.get(adapterCommentList.size() - 1);
            jsonParam.put("last_id", commentBean.getId());
        } else if (pullMode == PullToRefreshBase.Mode.PULL_FROM_START) {
            UserJson userInfo = LoginUtils.getUserInfo(mContext);
            if (userInfo != null) {
                jsonParam.put("uid", userInfo.getUid());
                jsonParam.put("token", userInfo.getToken());
            }
        }
        jsonParam.put("type", "video");
        jsonParam.put("object_id", videoDetailActivity.videoId);

        String url = null;
        switch (pullMode) {
            case PULL_FROM_START:
                url = HttpConstant.VIDEO_DETAIL;
                break;
            case PULL_FROM_END:
                url = HttpConstant.COMMENT_LIST;
                break;
        }

        volleyRequest.doGet(url, jsonParam, new HttpListener<String>() {
            @Override
            protected void onResponse(String json) {
                if (pullMode == PullToRefreshBase.Mode.PULL_FROM_START) {
                    VideoDetailBean detailJson = JSONObject.parseObject(json, VideoDetailBean.class);
                    VideoDetailPresenter.this.videoDetailBean = detailJson;

                    videoListJson = new VideoListJson(videoDetailBean.getId(), videoDetailBean.getCategory_id(),
                            videoDetailBean.getTitle(),
                            videoDetailBean.getPicture(), videoDetailBean.getNum_comment(), videoDetailBean
                            .getNum_good(), videoDetailBean
                            .getNum_play(), "", videoDetailBean.getCreate_time(), false, false, false, 0);
                    videoListJson.setShare_image(videoDetailBean.getShare_image());

                    isCollect = CollectUtils.isCollect(mContext, VarConstant.COLLECT_TYPE_VIDEO, videoListJson);
                    isAttention = NativeStore.isThumbSucceed(mContext, VarConstant.GOOD_TYPE_VIDEO, videoDetailBean
                            .getId());
                    videoListJson.setIsCollect(isCollect);
                    videoListJson.setIsGood(isAttention);

                    videoDetailActivity.ivCollect.setSelected(isCollect);
                    videoDetailActivity.ivLike.setSelected(isAttention);

                    videoDetailActivity.pllContent.loadOver();
                    playVideo();

                    List<CommentBean> comment = detailJson.getComment();
                    adapterCommentList.addAll(comment);

                    commentAdapter = new CommentAdapter(
                            mContext,
                            adapterCommentList,
                            VideoDetailPresenter.this);

                    videoDetailActivity.rvMessage.setAdapter(commentAdapter);

                    videoDetailActivity.commentPresenter.bindListView(videoDetailActivity.rvMessage);

                    headView = videoDetailActivity.commentPresenter.getHeadView();

                    View detailTitle = LayoutInflater
                            .from(mContext)
                            .inflate(R.layout.activity_video_detail_title, headView, false);
                    headView.addView(detailTitle, 0);

                    TextView tvTitle = ButterKnife.findById(detailTitle, R.id.tv_title);
                    tvTitle.setText(detailJson.getTitle());

                    TextView tvPlayCount = ButterKnife.findById(detailTitle, R.id.tv_playCount);
                    tvPlayCount.setText(detailJson.getNum_play());

                    TextView tvCreateTime = ButterKnife.findById(detailTitle, R.id.tv_createTime);
                    long timeLong = Long.parseLong(detailJson.getCreate_time()) * 1000;
                    CharSequence formatData = DateFormat.format("yyyy-MM-dd", timeLong);
                    tvCreateTime.setText(formatData);

                    TextView tvSynopsisBtn = ButterKnife.findById(detailTitle, R.id.tv_synopsis_btn);
                    tvSynopsisBtn.setTag("n");

                    final TextView tvSynopsisContent = ButterKnife.findById(detailTitle, R.id.tv_synopsis_content);

                    if (TextUtils.isEmpty(detailJson.getIntroduce())) {
                        detailJson.setIntroduce("暂无介绍");
                    }
                    String introduce = detailJson.getIntroduce();
                    SpannableString introduceSpannable = new SpannableString("简介： " + introduce);
                    introduceSpannable.setSpan(
                            new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.font_color6)),
                            0,
                            3,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tvSynopsisContent.setText(introduceSpannable);

                    tvSynopsisBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TextView tvSynopsisBtn = (TextView) v;
                            if ("n".equals(v.getTag())) {
                                v.setTag("y");
                                TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                        tvSynopsisBtn,
                                        0,
                                        0,
                                        R.mipmap.icon_flash_arrow_top,
                                        0);
                                tvSynopsisContent.setVisibility(View.VISIBLE);
                            } else {
                                v.setTag("n");
                                TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                        tvSynopsisBtn,
                                        0,
                                        0,
                                        R.mipmap.icon_flash_arrow_down,
                                        0);
                                tvSynopsisContent.setVisibility(View.GONE);
                            }
                        }
                    });


                    videoDetailActivity.commentPresenter.createMoreVideoView(detailJson.getVideo());

                    videoDetailActivity.tvCommentCount.setVisibility(View.VISIBLE);
                    videoDetailActivity.tvCommentCount.setText(detailJson.getNum_comment());

                    videoDetailActivity.tvZanCount.setVisibility(View.VISIBLE);
                    videoDetailActivity.tvZanCount.setText(detailJson.getNum_good());

                    try {
                        boolean isGoneCommentCount = Integer.parseInt(detailJson.getNum_comment()) == 0;
                        videoDetailActivity.tvCommentCount.setVisibility(isGoneCommentCount ? View.GONE : View.VISIBLE);

                        boolean isGoneZanCount = Integer.parseInt(detailJson.getNum_good()) == 0;
                        videoDetailActivity.tvZanCount.setVisibility(isGoneZanCount ? View.GONE : View.VISIBLE);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                    if (adapterCommentList.size() == 0) {
                        videoDetailActivity.commentPresenter.createNoneComment();
                    } else {
                        videoDetailActivity.rvMessage.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
                    }
                } else {
                    List<CommentBean> comment = JsonUtil.parseArray(json, CommentBean.class);
                    videoDetailActivity.rvMessage.onRefreshComplete();
                    if (comment.size() == 0) {
                        videoDetailActivity.rvMessage.addFootNoMore();
                        return;
                    }
                    adapterCommentList.addAll(comment);
                    commentAdapter.notifyDataSetChanged();
                }
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                try {
                    videoDetailActivity.rvMessage.onRefreshComplete();
                    if (adapterCommentList == null || adapterCommentList.size() == 0) {
                        videoDetailActivity.pllContent.loadError();
                    } else {
                        videoDetailActivity.rvMessage.addFootNoMore();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 发表评论的
     *
     * @param popupWindow
     * @param commentEdit
     * @param commentBean
     * @param parentId
     */
    public void requestIssueComment(final PopupWindow popupWindow,
                                    final EditText commentEdit,
                                    CommentBean commentBean,
                                    int parentId) {

        String commentContent = commentEdit.getText().toString();

        if (commentContent.trim().length() == 0) {
            commentEdit.setText("");

            TSnackbar.make(commentEdit,
                    "评论好像为空喔,请检查",
                    TSnackbar.LENGTH_LONG,
                    TSnackbar.APPEAR_FROM_TOP_TO_DOWN)
                    .setPromptThemBackground(Prompt.WARNING).show();

            return;
        }

        UserJson userInfo = LoginUtils.getUserInfo(mContext);

        final TSnackbar snackBar = TSnackbar.make
                (
                        commentEdit,
                        "正在提交评论",
                        TSnackbar.LENGTH_INDEFINITE,
                        TSnackbar.APPEAR_FROM_TOP_TO_DOWN
                );
        snackBar.setPromptThemBackground(Prompt.SUCCESS);
        snackBar.addIconProgressLoading(0, true, false);
        snackBar.show();

        VolleyRequest volleyRequest = new VolleyRequest(mContext, mQueue);
        JSONObject jsonParam = volleyRequest.getJsonParam();
        jsonParam.put("type", VarConstant.VIDEO);
        jsonParam.put("object_id", videoDetailActivity.videoId);
        jsonParam.put("uid", userInfo.getUid());
        jsonParam.put("accessToken", userInfo.getToken());
        jsonParam.put("content", commentContent);

        if (parentId != 0) {
            jsonParam.put("parent_id", parentId);
        }

        volleyRequest.doPost(HttpConstant.COMMENT_PUBLISH, jsonParam, new HttpListener<CommentBean>() {
            @Override
            protected void onResponse(CommentBean mCommentBean) {
                popupWindow.dismiss();
                commentEdit.setText("");

                snackBar.setPromptThemBackground(Prompt.SUCCESS)
                        .setText("评论提交成功")
                        .setDuration(TSnackbar.LENGTH_LONG)
                        .setMinHeight(SystemUtil.getStatuBarHeight(mContext), mContext.getResources()
                                .getDimensionPixelOffset(R.dimen.actionbar_height))
                        .show();

                commentCommit(mCommentBean);
                if (popupWindow instanceof PopupUtil) {
                    ((PopupUtil) popupWindow).addLock(false);
                }
                //滚动到评论位置
//                int headerViewsCount = videoDetailActivity.rvMessage.getRefreshableView().getHeaderViewsCount();
//                videoDetailActivity.rvMessage.getRefreshableView().smoothScrollToPosition(headerViewsCount);
                int mHeaderView = headView.getHeight();
                videoDetailActivity.rvMessage.getRefreshableView().smoothScrollBy(mHeaderView, 100);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                if (popupWindow instanceof PopupUtil) {
                    ((PopupUtil) popupWindow).addLock(false);
                }
            }
        });
    }

    /**
     * 第一次提交
     *
     * @param mCommentBean
     */
    public void commentCommit(CommentBean mCommentBean) {
        try {
            if (adapterCommentList.size() == 0) {
                View noneComment = headView.findViewWithTag("noneComment");
                headView.removeView(noneComment);
            }

            adapterCommentList.add(0, mCommentBean);
            commentAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playVideo() {
        videoDetailActivity.spVideo
                .setNetChangeListener(true)//设置监听手机网络的变化
                .setOnNetChangeListener(new SuperPlayer.OnNetChangeListener() {//实现网络变化的回调
                    @Override
                    public void onWifi() {

                    }

                    @Override
                    public void onMobile() {

                    }

                    @Override
                    public void onDisConnect() {

                    }

                    @Override
                    public void onNoAvailable() {

                    }
                })
                .onPrepared(new SuperPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared() {
                        /**
                         * 监听视频是否已经准备完成开始播放。（可以在这里处理视频封面的显示跟隐藏）
                         */

                    }
                })
                .onComplete(new Runnable() {
                    @Override
                    public void run() {
                        /**
                         * 监听视频是否已经播放完成了。（可以在这里处理视频播放完成进行的操作）
                         */
                    }
                })
                .onInfo(new SuperPlayer.OnInfoListener() {
                    @Override
                    public void onInfo(int what, int extra) {
                    }
                })
                .onError(new SuperPlayer.OnErrorListener() {
                    @Override
                    public void onError(int what, int extra) {
                        /**
                         * 监听视频播放失败的回调
                         */

                    }
                })
                .setTitle(videoDetailBean.getTitle())
                .showCenterControl(true)
                .play(videoDetailBean.getUrl());

        int avHeight = videoDetailActivity.getResources().getDimensionPixelSize(R.dimen.av_video_height);

        videoDetailActivity.spVideo.setVideoPortraitHeight(avHeight);
        videoDetailActivity.spVideo.setScaleType(SuperPlayer.SCALETYPE_FITXY);
    }

    /**
     * 收藏
     */
    public void collect() {
        if (videoDetailBean != null && videoListJson != null) {
            if (isCollect) {
                CollectUtils.unCollect(mContext, VarConstant.COLLECT_TYPE_VIDEO, videoListJson, new ObserverData() {
                    @Override
                    public void callback(Object o) {
                        isCollect = false;
                        videoDetailActivity.ivCollect.setSelected(false);

                        EventBus.getDefault().post(new EventBusClass(EventBusClass.EVENT_COLLECT_VIDEO, videoListJson));
                    }

                    @Override
                    public void onError(Exception e) {
                        showMsg("取消收藏失败");
                    }
                }, null);
            } else {
                CollectUtils.collect(mContext, VarConstant.COLLECT_TYPE_VIDEO, videoListJson, new ObserverData() {
                    @Override
                    public void callback(Object o) {
                        isCollect = true;
                        videoDetailActivity.ivCollect.setSelected(true);
                        EventBus.getDefault().post(new EventBusClass(EventBusClass.EVENT_COLLECT_VIDEO, videoListJson));
                    }

                    @Override
                    public void onError(Exception e) {
                        showMsg("收藏失败");
                    }
                }, null);
            }
        }
    }

    /**
     * 点赞
     */
    public void attention() {
        if (videoDetailBean != null) {
            if (isAttention) {
            } else {
                videoDetailActivity.mThumbView.startGiveAnimation();

                NativeStore.addThumbID(mContext, VarConstant.GOOD_TYPE_VIDEO, videoDetailBean.getId(), new
                        ObserverData() {
                            @Override
                            public void callback(Object o) {
                                isAttention = true;
                                videoDetailActivity.ivLike.setSelected(true);

                                String dianZanCount = videoDetailActivity.tvZanCount.getText().toString();
                                String addDianZanCount = null;
                                try {
                                    addDianZanCount = String.valueOf(Integer.parseInt(dianZanCount) + 1);
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                    addDianZanCount = "1";
                                }
                                videoDetailActivity.tvZanCount.setVisibility(View.VISIBLE);
                                videoDetailActivity.tvZanCount.setText(addDianZanCount);

                                EventBusClass eventBusClass = new EventBusClass(EventBusClass.EVENT_VIDEO_ZAN, videoDetailBean.getId());
                                EventBus.getDefault().post(eventBusClass);
                            }

                            @Override
                            public void onError(Exception e) {
                            }
                        }, null);
            }
        }
    }

    /**
     * 分享
     */
    public void share() {
        if (videoDetailBean != null) {

            UmengShareBean umengShareBean = new UmengShareBean();
            umengShareBean.setTitle(videoDetailBean.getTitle());
            umengShareBean.setDetail(videoDetailBean.getIntroduce());
            umengShareBean.setSinaTitle(videoDetailBean.getShare_sina_title());
            umengShareBean.setImageUrl(videoDetailBean.getShare_image());
            umengShareBean.setWebUrl(videoDetailBean.getUrl_share().replace("{id}", videoDetailBean.getId()));
            umengShareBean.setFromSource(UmengShareUtil.SHARE_VIDEO);

            UmengShareUI umengShareUI = new UmengShareUI(videoDetailActivity);
            umengShareUI.showSharePopup(umengShareBean);
        }
    }

    private void showMsg(String str) {
        TSnackbar tSnackbar = TSnackbar.make(videoDetailActivity.spVideo, str, Snackbar.LENGTH_LONG, TSnackbar
                .APPEAR_FROM_TOP_TO_DOWN)
                .setMinHeight(SystemUtil.getStatuBarHeight(mContext), mContext.getResources()
                        .getDimensionPixelOffset(R.dimen.actionbar_height));

        int color = ContextCompat.getColor(mContext, R.color.red_btn_bg_color);
        tSnackbar.setBackgroundColor(color);
        tSnackbar.setPromptThemBackground(Prompt.WARNING);
        tSnackbar.show();
    }
}
