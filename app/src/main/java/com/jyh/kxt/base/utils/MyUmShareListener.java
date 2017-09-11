package com.jyh.kxt.base.utils;

import android.widget.PopupWindow;

import com.jyh.kxt.base.BaseActivity;
import com.library.widget.window.ToastView;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 * 分享回调实现类
 */
public class MyUmShareListener implements UMShareListener {
    private BaseActivity mActivity;
    private PopupWindow popupWindow;

    public MyUmShareListener(BaseActivity mActivity) {
        this.mActivity = mActivity;
    }

    public MyUmShareListener(BaseActivity mActivity, PopupWindow popupWindow) {
        this.mActivity = mActivity;
        this.popupWindow = popupWindow;
    }

    @Override
    public void onStart(SHARE_MEDIA share_media) {
        mActivity.showWaitDialog(null);
    }

    @Override
    public void onResult(SHARE_MEDIA platform) {
        mActivity.dismissWaitDialog();
        String platformName = getPlatformName(platform);
        if (SHARE_MEDIA.WEIXIN_FAVORITE.equals(platform)) {
            ToastView.makeText3(mActivity, platformName + " 收藏成功啦");
        } else {
            ToastView.makeText3(mActivity, platformName + " 分享成功啦");
        }
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

    @Override
    public void onError(SHARE_MEDIA platform, Throwable t) {
        mActivity.dismissWaitDialog();
        String platformName = getPlatformName(platform);
        if (SHARE_MEDIA.WEIXIN_FAVORITE.equals(platform)) {
            ToastView.makeText3(mActivity, platformName + " 收藏失败啦");
        } else {
            ToastView.makeText3(mActivity, platformName + " 分享失败啦");
        }
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

    @Override
    public void onCancel(SHARE_MEDIA platform) {
        mActivity.dismissWaitDialog();
        String platformName = getPlatformName(platform);
        if (SHARE_MEDIA.WEIXIN_FAVORITE.equals(platform)) {
            ToastView.makeText3(mActivity, platformName + "分享取消");
        } else {
            ToastView.makeText3(mActivity, platformName + "分享取消");
        }
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

    /**
     * 得到平台名称
     *
     * @param platform
     * @return
     */
    private String getPlatformName(SHARE_MEDIA platform) {
        String platformName = null;
        switch (platform) {
            case WEIXIN:
                platformName = "微信";
                break;
            case WEIXIN_CIRCLE:
                platformName = "微信朋友圈";
                break;
            case QQ:
                platformName = "QQ";
                break;
            case QZONE:
                platformName = "QQ空间";
                break;
            case SINA:
                platformName = "新浪微博";
                break;
        }
        return platformName;
    }
}