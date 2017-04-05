package com.jyh.kxt.av.presenter;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.jyh.kxt.R;
import com.jyh.kxt.av.ui.VideoDetailActivity;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindActivity;
import com.superplayer.library.SuperPlayer;

/**
 * Created by Mr'Dai on 2017/3/31.
 */

public class VideoDetailPresenter extends BasePresenter {
    @BindActivity VideoDetailActivity videoDetailActivity;

    private String url = "http://baobab.wandoujia.com/api/v1/playUrl?vid=9502&editionType=normal";

    public VideoDetailPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void initVideo() {
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
                .setTitle(url)
                .showCenterControl(true)
                .play(url);

        int avHeight = videoDetailActivity.getResources().getDimensionPixelSize(R.dimen.av_video_height);
        videoDetailActivity.spVideo.setVideoPortraitHeight(avHeight);

        videoDetailActivity.spVideo.setScaleType(SuperPlayer.SCALETYPE_FITXY);
    }

    private void playVideoInquiry() {
        AlertDialog.Builder builder = new AlertDialog.Builder(videoDetailActivity)
                .setTitle("播放提示")
                .setMessage("当前网络为移动网络,是否继续播放?")
                .setPositiveButton("是",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                initVideo();
                            }
                        })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.setCancelable(false);
        builder.create();
        builder.show();
    }
}
