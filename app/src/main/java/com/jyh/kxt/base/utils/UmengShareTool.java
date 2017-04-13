package com.jyh.kxt.base.utils;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import com.jyh.kxt.R;
import com.jyh.kxt.base.adapter.FunctionAdapter;
import com.jyh.kxt.base.constant.VarConstant;
import com.library.util.SystemUtil;
import com.library.widget.window.ToastView;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareConfig;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMMin;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.utils.SocializeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名:Kxt
 * 类描述:分享工具类
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/12.
 */

public class UmengShareTool {

    public static final String TYPE_MARKET = "type_market"; //行情
    public static final String TYPE_DEFAULT = "type_default"; //默认
    public static final String TYPE_VIDEO = "type_video"; //视频列表

    /**
     * 初始化umeng
     */
    public static void initUmeng(Context context) {
        PlatformConfig.setWeixin(VarConstant.WX_APPID, VarConstant.WX_APPSECRET);
        PlatformConfig.setQQZone(VarConstant.QQ_APPID, VarConstant.QQ_APPKEY);
        PlatformConfig.setSinaWeibo(VarConstant.SINA_APPKEY, VarConstant.SINA_APPSECRET, VarConstant.SINA_CALLBACK);
        Config.DEBUG = true;
        UMShareConfig config = new UMShareConfig();
        config.isNeedAuthOnGetUserInfo(false);
        config.isOpenShareEditActivity(true);
        config.setSinaAuthType(UMShareConfig.AUTH_TYPE_WEBVIEW);
        UMShareAPI.get(context).setShareConfig(config);
    }


    private static Application application;
    private static PopupWindow screenPopWindow;
    private static PopupWindow shareLayout;

    public static MyUMShareListener umShareListener;

    /**
     * @param activity
     * @param title
     * @param weburl
     * @param discription
     * @param thumb
     * @param bitmap
     * @param view
     * @param type
     */
    public static void initUmengLayout(final Activity activity, final String title, final String weburl, final String
            discription,
                                       final String thumb, final Bitmap bitmap, View view, String type) {

        View rootView = LayoutInflater.from(activity).inflate(R.layout.dialog_umeng_share, null, false);
        boolean isHq = false;
        if (type == null) {
            isHq = false;
        } else {
            switch (type) {
                case TYPE_DEFAULT:
                    isHq = false;
                    break;
                case TYPE_MARKET:
                    isHq = true;
                    break;
                case TYPE_VIDEO:
                    isHq = false;
                    break;
            }
        }
        ProgressDialog dialog = new ProgressDialog(activity);
        umShareListener = new MyUMShareListener(dialog) {
            @Override
            public void onStart(SHARE_MEDIA share_media) {
                super.onStart(share_media);
            }

            @Override
            public void onResult(SHARE_MEDIA platform) {
                super.onResult(platform);
                if (platform == null) {
                    ToastView.makeText3(application, "分享失败");
                    return;
                }
                String platformStr = "";
                switch (platform) {
                    case WEIXIN:
                        platformStr = "微信";
                        break;
                    case WEIXIN_CIRCLE:
                        platformStr = "微信朋友圈";
                        break;
                    case QQ:
                        platformStr = "QQ";
                        break;
                    case QZONE:
                        platformStr = "QQ空间";
                        break;
                    case SINA:
                        platformStr = "新浪微博";
                        break;
                }
                if (platform.name().equals("WEIXIN_FAVORITE")) {
                    ToastView.makeText3(application, platformStr + " 收藏成功啦");
                } else {
                    ToastView.makeText3(application, platformStr + " 分享成功啦");
                }
            }

            @Override
            public void onError(SHARE_MEDIA platform, Throwable t) {
                super.onError(platform, t);
                String platformStr = "";
                switch (platform) {
                    case WEIXIN:
                        platformStr = "微信";
                        break;
                    case WEIXIN_CIRCLE:
                        platformStr = "微信朋友圈";
                        break;
                    case QQ:
                        platformStr = "QQ";
                        break;
                    case QZONE:
                        platformStr = "QQ空间";
                        break;
                    case SINA:
                        platformStr = "新浪微博";
                        break;
                }
            }

            @Override
            public void onCancel(SHARE_MEDIA platform) {
                super.onCancel(platform);
                String platformStr = "";
                switch (platform) {
                    case WEIXIN:
                        platformStr = "微信";
                        break;
                    case WEIXIN_CIRCLE:
                        platformStr = "微信朋友圈";
                        break;
                    case QQ:
                        platformStr = "QQ";
                        break;
                    case QZONE:
                        platformStr = "QQ空间";
                        break;
                    case SINA:
                        platformStr = "新浪微博";
                        break;
                }
            }
        };
        DisplayMetrics metrics = SystemUtil.getScreenDisplay(activity);
        shareLayout = new PopupWindow(rootView, metrics.widthPixels, metrics.heightPixels);
        //设置分享按钮
        setShareBtn(activity, title, weburl, discription, thumb, bitmap, isHq, rootView, dialog);
        int statuBarHeight = SystemUtil.getStatuBarHeight(activity);
        rootView.setPadding(0, 0, 0, statuBarHeight);
        shareLayout.showAtLocation(view, Gravity.BOTTOM, 0, 0);

    }

    public static void dismissWindow() {
        if (screenPopWindow != null && screenPopWindow.isShowing()) {
            screenPopWindow.dismiss();
            screenPopWindow = null;
        }
    }

    /**
     * 设置分享内容
     *
     * @param activity    展示界面
     * @param title       标题
     * @param weburl      分享网址
     * @param discription 描述
     * @param thumb       分享图片
     * @param share_media 分享平台
     */
    public static void setShareContent(Activity activity, String title, String weburl, String discription,
                                       String thumb, SHARE_MEDIA share_media, ProgressDialog dialog) {
        try {
            application = activity.getApplication();
            UMImage urlImage;
            if (null != thumb && !"".equals(thumb)) {
                urlImage = new UMImage(activity, thumb);
            } else {
                urlImage = new UMImage(activity, R.mipmap.ic_launcher);
            }
            if (discription == null || "".equals(discription.trim())) {
                discription = title;
            }

            UMWeb umWeb = new UMWeb(weburl);
            umWeb.setThumb(urlImage);
            umWeb.setDescription(discription);
            umWeb.setTitle(title);

            new ShareAction(activity)
                    .withMedia(umWeb)
                    .setPlatform(share_media)
                    .setCallback(umShareListener)
                    .share();

        } catch (Exception e) {
            ToastView.makeText3(activity, "分享失败");
            SocializeUtils.safeCloseDialog(dialog);
        }
    }

    /**
     * 纯图片分享
     *
     * @param activity
     * @param title       分享标题
     * @param url         分享地址
     * @param bitmap      分享图片
     * @param share_media 分享平台
     */
    public static void setShareContent(Activity activity, String title, String contentText, String url, Bitmap
            bitmap, SHARE_MEDIA share_media, ProgressDialog dialog) {
        try {
            application = activity.getApplication();
            UMImage urlImage;
            if (null != bitmap) {
                urlImage = new UMImage(activity, bitmap);
            } else {
                urlImage = new UMImage(activity, R.mipmap.ic_launcher);
            }
            if (TextUtils.isEmpty(title)) {
                title = SystemUtil.getAppName(activity);
            }
            if (TextUtils.isEmpty(contentText)) {
                contentText = SystemUtil.getAppName(activity);
            }

            new ShareAction(activity)
                    .withMedia(urlImage)
                    .setPlatform(share_media)
                    .setCallback(umShareListener)
                    .share();

        } catch (Exception e) {
            ToastView.makeText3(activity, "分享失败");
        }
    }

    public static void onActivityResult(Context context, int requestCode, int resultCode, Intent data) {
        dismissWindow();
        UMShareAPI.get(context).onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 设置分享按钮
     *
     * @param activity
     * @param title
     * @param weburl
     * @param discription
     * @param thumb
     * @param bitmap
     * @param isHq
     * @param rootView
     */
    private static void setShareBtn(final Activity activity, final String title, final String weburl, final String
            discription, final String thumb, final Bitmap bitmap, final boolean isHq, final
                                    View rootView, final ProgressDialog dialog) {

        final UMShareAPI umShareAPI = UMShareAPI.get(activity);
        RecyclerView rvShare = (RecyclerView) rootView.findViewById(R.id.rv_share);
        RecyclerView rvFunction = (RecyclerView) rootView.findViewById(R.id.rv_function);

        LinearLayoutManager manager = new LinearLayoutManager(activity);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        LinearLayoutManager manager2 = new LinearLayoutManager(activity);
        manager2.setOrientation(LinearLayoutManager.HORIZONTAL);

        rvShare.setLayoutManager(manager);
        rvFunction.setLayoutManager(manager2);

        final List<String> list = new ArrayList<>();
        list.add("朋友圈");
        list.add("微信好友");
        list.add("新浪");
        list.add("QQ");
        list.add("QQ空间");

        FunctionAdapter functionAdapter = new FunctionAdapter(list, activity);
        rvShare.setAdapter(functionAdapter);

        List<String> list2 = new ArrayList();
        list2.add("复制链接");
        list2.add("收藏");
        list2.add("赞");
        list2.add("取消");
        FunctionAdapter functionAdapter2 = new FunctionAdapter(list2, activity);
        rvFunction.setAdapter(functionAdapter2);

        functionAdapter.setOnClickListener(new FunctionAdapter.OnClickListener() {
            @Override
            public void onClick(View view, int position) {
                switch (position) {
                    case 0:
                        //朋友圈
                        if (umShareAPI.isInstall(activity, SHARE_MEDIA.WEIXIN_CIRCLE)) {
                            if (isHq) {
                                setShareContent(activity, title, discription, weburl, bitmap, SHARE_MEDIA
                                        .WEIXIN_CIRCLE, dialog);
                            } else {
                                setShareContent(activity, title, weburl, discription, thumb, SHARE_MEDIA.WEIXIN_CIRCLE, dialog);
                            }
                        } else {
                            ToastView.makeText3(activity, "未安装微信");
                        }
                        break;
                    case 1:
                        //微信
                        if (umShareAPI.isInstall(activity, SHARE_MEDIA.WEIXIN)) {
                            if (isHq) {
                                setShareContent(activity, title, discription, weburl, bitmap, SHARE_MEDIA.WEIXIN, dialog);
                            } else {
                                setShareContent(activity, title, weburl, discription, thumb, SHARE_MEDIA.WEIXIN, dialog);
                            }
                        } else {
                            ToastView.makeText3(activity, "未安装微信");
                        }
                        break;
                    case 2:
                        //新浪
                        if (isHq) {
                            setShareContent(activity, title, discription, weburl, bitmap, SHARE_MEDIA.SINA, dialog);
                        } else {
                            setShareContent(activity, title, weburl, discription, thumb, SHARE_MEDIA.SINA, dialog);
                        }
                        break;
                    case 3:
                        //QQ
                        if (umShareAPI.isInstall(activity, SHARE_MEDIA.QQ)) {
                            if (isHq) {
                                setShareContent(activity, title, discription, weburl, bitmap, SHARE_MEDIA.QQ, dialog);
                            } else {
                                setShareContent(activity, title, weburl, discription, thumb, SHARE_MEDIA.QQ, dialog);
                            }
                        } else {
                            ToastView.makeText3(activity, "未安装QQ");
                        }
                        break;
                    case 4:
                        //QQ空间
                        if (umShareAPI.isInstall(activity, SHARE_MEDIA.QZONE)) {
                            if (isHq) {
                                setShareContent(activity, title, discription, weburl, bitmap, SHARE_MEDIA.QZONE, dialog);
                            } else {
                                setShareContent(activity, title, weburl, discription, thumb, SHARE_MEDIA.QZONE, dialog);
                            }
                        } else {
                            ToastView.makeText3(activity, "未安装QQ控件");
                        }
                        break;
                }
                try {
                    dismissShareWindow();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        functionAdapter2.setOnClickListener(new FunctionAdapter.OnClickListener() {
            @Override
            public void onClick(View view, int position) {
                switch (position) {
                    case 0:
                        //复制链接
                        try {
                            //获取剪切板服务
                            ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context
                                    .CLIPBOARD_SERVICE);
                            //然后把数据放在ClipData对象中
//                            ClipData clip = ClipData.newUri(activity.getContentResolver(), "URI", Uri.parse(weburl));
                            ClipData clip = ClipData.newPlainText("simple text", weburl);
                            //把clip对象放在剪贴板中
                            clipboard.setPrimaryClip(clip);
                            ToastView.makeText3(activity, "链接复制成功");
                        } catch (Exception e) {
                            e.printStackTrace();
                            ToastView.makeText3(activity, "复制链接失败，请重试");
                        }
                        break;
                    case 1:
                        //收藏
                        break;
                    case 2:
                        //赞
                        break;
                    case 3:
                        //取消
                        try {
                            dismissShareWindow();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
                try {
                    dismissShareWindow();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 隐藏分享框
     */
    private static void dismissShareWindow() {
        if (shareLayout != null && shareLayout.isShowing()) {
            shareLayout.dismiss();
        }
    }

    static class MyUMShareListener implements UMShareListener {

        private ProgressDialog dialog;

        public MyUMShareListener(ProgressDialog dialog) {
            this.dialog = dialog;
        }

        @Override
        public void onStart(SHARE_MEDIA share_media) {
            SocializeUtils.safeShowDialog(dialog);
        }

        @Override
        public void onResult(SHARE_MEDIA share_media) {
            SocializeUtils.safeCloseDialog(dialog);
        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
            SocializeUtils.safeCloseDialog(dialog);
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {
            SocializeUtils.safeCloseDialog(dialog);
        }
    }
}