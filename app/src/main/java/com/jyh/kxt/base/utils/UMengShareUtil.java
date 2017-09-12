package com.jyh.kxt.base.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.PopupWindow;

import com.alibaba.fastjson.JSON;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.base.json.UmengShareBean;
import com.jyh.kxt.index.json.MainInitJson;
import com.library.base.http.VarConstant;
import com.library.util.SPUtils;
import com.library.widget.window.ToastView;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.ShareContent;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareConfig;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

/**
 * Created by Mr'Dai on 2017/9/8.
 */

public class UmengShareUtil {
    /**
     * 文章
     */
    public static final short SHARE_ARTICLE = 0;
    /**
     * 视屏
     */
    public static final short SHARE_VIDEO = 1;
    /**
     * 快讯
     */
    public static final short SHARE_KX = 2;
    /**
     * 行情
     */
    public static final short SHARE_MARKET = 3;
    /**
     * 交易圈
     */
    public static final short SHARE_VIEWPOINT = 4;
    /**
     * 分享邀请
     */
    public static final short SHARE_INVITE = 5;


    /**
     * 功能 复制连接
     */
    public static final int FUN_COPY_URL = 1001;

    /**
     * 功能 关闭弹窗
     */
    public static final int FUN_CLOSE_POP = 1002;

    /**
     * 功能 收藏
     */
    public static final int FUN_COLLECT = 1003;

    /**
     * 功能 点赞
     */
    public static final int FUN_FAVOUR = 1004;

    /**
     * 初始化umeng
     */
    public static void initUmeng(Context context) {
        PlatformConfig.setWeixin(VarConstant.WX_APPID, VarConstant.WX_APPSECRET);
        PlatformConfig.setQQZone(VarConstant.QQ_APPID, VarConstant.QQ_APPKEY);
        PlatformConfig.setSinaWeibo(VarConstant.SINA_APPKEY, VarConstant.SINA_APPSECRET, VarConstant.SINA_CALLBACK);
        UMShareConfig config = new UMShareConfig();
        config.isNeedAuthOnGetUserInfo(false);
        config.isOpenShareEditActivity(true);
        config.setSinaAuthType(UMShareConfig.AUTH_TYPE_WEBVIEW);
        UMShareAPI.get(context).setShareConfig(config);
    }

    private BaseActivity mActivity;
    private UMShareAPI mUmShareAPI;
    private MyUmShareListener umShareListener;

    public UmengShareUtil(BaseActivity mActivity) {
        this(mActivity, null);
    }

    public UmengShareUtil(BaseActivity mActivity, PopupWindow popupWindow) {
        this.mActivity = mActivity;
        this.mUmShareAPI = UMShareAPI.get(mActivity);
        this.umShareListener = new MyUmShareListener(mActivity, popupWindow);
    }

    /**
     * 分享类型1
     *
     * @param shareMedia
     * @param umengShareBean
     */
    public void shareContent1(SHARE_MEDIA shareMedia, UmengShareBean umengShareBean) {
        if (!mUmShareAPI.isInstall(mActivity, shareMedia)) {
            ToastView.makeText3(mActivity, "未安装" + shareMedia.name());
            return;
        }

        try {
            UMImage urlImage;
            if (null != umengShareBean.getImageUrl() && !"".equals(umengShareBean.getImageUrl())) {
                urlImage = new UMImage(mActivity, umengShareBean.getImageUrl());
            } else {
                urlImage = new UMImage(mActivity, R.mipmap.ic_launcher);
            }
            if (TextUtils.isEmpty(umengShareBean.getDetail())) {
                umengShareBean.setDetail(umengShareBean.getTitle());
            }

            UMWeb umWeb = new UMWeb(umengShareBean.getWebUrl());
            umWeb.setThumb(urlImage);
            umWeb.setDescription(umengShareBean.getDetail());
            umWeb.setTitle(umengShareBean.getTitle());

            new ShareAction(mActivity)
                    .withMedia(umWeb)
                    .setPlatform(shareMedia)
                    .setCallback(umShareListener)
                    .share();

        } catch (Exception e) {
            ToastView.makeText3(mActivity, "分享失败");
        }
    }

    /**
     * 纯图片分享
     * 分享新浪
     */
    public void shareContent2(SHARE_MEDIA shareMedia, UmengShareBean umengShareBean) {
        if (!mUmShareAPI.isInstall(mActivity, shareMedia)) {
            ToastView.makeText3(mActivity, "未安装" + shareMedia.name());
            return;
        }

        try {
            UMImage urlImage;

            String config = SPUtils.getString(mActivity, SpConstant.INIT_LOAD_APP_CONFIG);

            MainInitJson mainInitJson = JSON.parseObject(config, MainInitJson.class);
            if (mainInitJson != null && mainInitJson.getDownload_QR_code() != null) {
                urlImage = new UMImage(mActivity, mainInitJson.getDownload_QR_code());
            } else {
                urlImage = new UMImage(mActivity, R.mipmap.share_weibo);
            }

            ShareAction shareAction = new ShareAction(mActivity);

            ShareContent shareContent = new ShareContent();
            shareContent.mText = umengShareBean.getDetail();
            shareContent.mMedia = urlImage;
            shareAction.setShareContent(shareContent);

            shareAction
                    .withMedia(urlImage)
                    .setPlatform(shareMedia)
                    .setCallback(umShareListener)
                    .share();

        } catch (Exception e) {
            ToastView.makeText3(mActivity, "分享失败");
        }
    }


}
