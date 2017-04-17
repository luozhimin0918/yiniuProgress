package com.jyh.kxt.av.presenter;

import android.view.LayoutInflater;

import com.jyh.kxt.R;
import com.jyh.kxt.av.ui.VideoDetailActivity;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.superplayer.library.SuperPlayer;

/**
 * Created by Mr'Dai on 2017/3/31.
 */

public class VideoDetailPresenter extends BasePresenter {
    @BindObject VideoDetailActivity videoDetailActivity;

    private String url = "http://baobab.wandoujia.com/api/v1/playUrl?vid=9502&editionType=normal";

    public VideoDetailPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    /**
     * 请求更多视屏
     */
    public void requestMoreVideo() {
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        for (int i = 0; i < 5; i++) {
            mInflater.inflate(R.layout.item_more_video, videoDetailActivity.llMoreVideo);
        }
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
}
