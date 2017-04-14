package com.jyh.kxt.base.utils;

import com.jyh.kxt.base.BaseActivity;
import com.library.widget.window.ToastView;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

/**
 * 项目名:Kxt
 * 类描述:登录工具类
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/12.
 */

public class UmengLoginTool {

    public void umenglogin(final BaseActivity context, SHARE_MEDIA share_media) {

        UMShareAPI umShareAPI = UMShareAPI.get(context);
        UMAuthListener umAuthListener = new UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA platform) {
                //授权开始的回调
                context.showWaitDialog("登录中");
            }

            @Override
            public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
                ToastView.makeText3(context, "Authorize succeed");
                UmengLoginTool.this.onComplete(context, platform, action, data);
            }

            @Override
            public void onError(SHARE_MEDIA platform, int action, Throwable t) {
                ToastView.makeText3(context, "Authorize fail");
                context.dismissWaitDialog();
            }

            @Override
            public void onCancel(SHARE_MEDIA platform, int action) {
                ToastView.makeText3(context, "Authorize cancel");
                context.dismissWaitDialog();
            }
        };
        umShareAPI.getPlatformInfo(context, share_media, umAuthListener);

    }

    private void onComplete(BaseActivity context, SHARE_MEDIA platform, int action, Map<String, String> data) {

        switch (platform) {
            case QQ:
                break;
            case SINA:
                break;
            case WEIXIN:
                break;
        }
        login();

    }

    private void login() {

    }
}
