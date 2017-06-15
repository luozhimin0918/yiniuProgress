package com.jyh.kxt.av.presenter;

import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.av.adapter.CommentAdapter;
import com.jyh.kxt.av.json.CommentBean;
import com.jyh.kxt.av.json.VideoDetailBean;
import com.jyh.kxt.av.json.VideoListJson;
import com.jyh.kxt.av.ui.VideoDetailActivity;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.json.ShareJson;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.base.utils.NativeStore;
import com.jyh.kxt.base.utils.UmengShareTool;
import com.jyh.kxt.base.utils.collect.CollectUtils;
import com.jyh.kxt.user.json.UserJson;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.util.SystemUtil;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.window.ToastView;
import com.superplayer.library.SuperPlayer;
import com.trycatch.mysnackbar.Prompt;
import com.trycatch.mysnackbar.TSnackbar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.jyh.kxt.base.constant.HttpConstant.VIDEO_DETAIL;

/**
 * Created by Mr'Dai on 2017/3/31.
 */

public class VideoDetailPresenter extends BasePresenter {
    @BindObject public VideoDetailActivity videoDetailActivity;

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

                    videoDetailActivity.commentPresenter.createMoreVideoView(detailJson.getVideo());
                    videoDetailActivity.tvCommentCount.setText(detailJson.getNum_comment());

                    if (adapterCommentList.size() == 0) {
                        videoDetailActivity.commentPresenter.createNoneComment();
                    } else {
                        videoDetailActivity.rvMessage.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
                    }
                } else {
                    List<CommentBean> comment = JSONArray.parseArray(json, CommentBean.class);
                    videoDetailActivity.rvMessage.onRefreshComplete();
                    if (comment.size() == 0) {
                        ToastView.makeText3(mContext, "暂无更多评论");
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
                        ToastView.makeText3(mContext, "暂无更多评论");
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
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
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
                        /**
                         * 监听视频的相关信息。
                         */

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
                showMsg("已经赞过了喔");
            } else {
                NativeStore.addThumbID(mContext, VarConstant.GOOD_TYPE_VIDEO, videoDetailBean.getId(), new
                        ObserverData() {
                            @Override
                            public void callback(Object o) {
                                isAttention = true;
                                videoDetailActivity.ivLike.setSelected(true);
                            }

                            @Override
                            public void onError(Exception e) {
                                showMsg("点赞失败");
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
            UmengShareTool.initUmengLayout((BaseActivity) mContext, new ShareJson(videoDetailBean.getTitle(),
                            videoDetailBean.getUrl(),
                            "", HttpConstant.IMG_URL + videoDetailBean
                            .getPicture(), null, UmengShareTool.TYPE_DEFAULT, videoDetailBean.getId(), null, null,
                            false, false),
                    videoDetailBean,
                    videoDetailActivity.spVideo, null);
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
