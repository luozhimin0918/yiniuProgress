package com.jyh.kxt.base.utils;

import android.widget.PopupWindow;

import com.alibaba.fastjson.JSONObject;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.json.UmengShareBean;
import com.jyh.kxt.user.json.UserJson;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;
import com.library.util.LogUtil;
import com.library.widget.window.ToastView;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 * 分享回调实现类
 */
public class MyUmShareListener implements UMShareListener {
    private BaseActivity mActivity;
    private PopupWindow popupWindow;
    private UmengShareBean mUmengShareBean;


    public MyUmShareListener(BaseActivity mActivity, PopupWindow popupWindow, UmengShareBean mUmengShareBean) {
        this.mActivity = mActivity;
        this.popupWindow = popupWindow;
        this.mUmengShareBean = mUmengShareBean;
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
//        postAddCoins();
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
        try {
            mActivity.dismissWaitDialog();
//        String platformName = getPlatformName(platform);
//        if (SHARE_MEDIA.WEIXIN_FAVORITE.equals(platform)) {
//            ToastView.makeText3(mActivity, platformName + "分享取消");
//        } else {
//            ToastView.makeText3(mActivity, platformName + "分享取消");
//        }
            if (popupWindow != null) {
                popupWindow.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                platformName = "微博";
                break;
        }
        return platformName;
    }

    private void postAddCoins() {
        LogUtil.e(LogUtil.TAG, "请求分享积分增加");

        UserJson userInfo = LoginUtils.getUserInfo(mActivity);
        if (userInfo == null) {
            return;
        }

        VolleyRequest volleyRequest = new VolleyRequest(mActivity.getContext(), mActivity.getQueue());

        JSONObject jsonParam = volleyRequest.getJsonParam();
        jsonParam.put("uid", userInfo.getUid());

        switch (mUmengShareBean.getFromSource()) {
            case UmengShareUtil.SHARE_ARTICLE:
                jsonParam.put("type", "share_article");
                break;
            case UmengShareUtil.SHARE_VIDEO:
                jsonParam.put("type", "share_video");
                break;
            case UmengShareUtil.SHARE_KX:
                jsonParam.put("type", "share_kx");
                break;
            case UmengShareUtil.SHARE_MARKET:
                jsonParam.put("type", "share_market");
                break;
            case UmengShareUtil.SHARE_VIEWPOINT:
                jsonParam.put("type", "share_viewpoint");
                break;
            case UmengShareUtil.SHARE_INVITE:
                jsonParam.put("type", "share_invite");
                break;
            case UmengShareUtil.SHARE_ADVERT:
                jsonParam.put("type", "share_ad");
                break;
        }

        volleyRequest.doPost(HttpConstant.COINS_ADD, jsonParam, new HttpListener<String>() {
            @Override
            protected void onResponse(String jsonData) {

            }
        });
    }
}