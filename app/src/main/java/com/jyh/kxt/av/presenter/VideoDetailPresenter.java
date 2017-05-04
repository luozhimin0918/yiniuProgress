package com.jyh.kxt.av.presenter;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.av.adapter.CommentAdapter;
import com.jyh.kxt.av.json.VideoDetailBean;
import com.jyh.kxt.av.ui.VideoDetailActivity;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;
import com.superplayer.library.SuperPlayer;

/**
 * Created by Mr'Dai on 2017/3/31.
 */

public class VideoDetailPresenter extends BasePresenter {
    @BindObject VideoDetailActivity videoDetailActivity;

    private VideoDetailBean videoDetailBean;

    public VideoDetailPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    /**
     * 请求初始化Video
     */
    public void requestInitVideo() {
        VolleyRequest volleyRequest = new VolleyRequest(mContext, mQueue);
        JSONObject jsonParam = volleyRequest.getJsonParam();
        jsonParam.put("id", videoDetailActivity.videoId);
        volleyRequest.doGet(HttpConstant.VIDEO_DETAIL, jsonParam, new HttpListener<VideoDetailBean>() {
            @Override
            protected void onResponse(VideoDetailBean videoDetailBean) {
                VideoDetailPresenter.this.videoDetailBean = videoDetailBean;
                playVideo();

                CommentAdapter commentAdapter = new CommentAdapter(mContext, videoDetailBean.getComment());
                videoDetailActivity.rvMessage.setAdapter(commentAdapter);

                videoDetailActivity.commentPresenter.createHeadView(videoDetailActivity.rvMessage);
                videoDetailActivity.commentPresenter.createMoreVideoView(videoDetailBean.getVideo());
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
            }
        });
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

}
