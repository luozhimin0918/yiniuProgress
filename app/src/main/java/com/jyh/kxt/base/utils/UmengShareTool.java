package com.jyh.kxt.base.utils;

import android.app.Activity;
import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.adapter.FunctionAdapter;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.json.ShareBtnJson;
import com.jyh.kxt.base.json.ShareJson;
import com.jyh.kxt.base.utils.collect.CollectUtils;
import com.library.base.http.VarConstant;
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
import com.umeng.socialize.media.UMWeb;

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
    private static PopupWindow shareLayout;

    public static MyUMShareListener umShareListener;

    /**
     * @param activity
     * @param shareBean
     * @param view
     * @param observerData
     */
    public static void initUmengLayout(final BaseActivity activity, ShareJson shareBean, Object o, View view, ObserverData observerData) {

        View rootView = LayoutInflater.from(activity).inflate(R.layout.dialog_umeng_share, null, false);
        rootView.findViewById(R.id.ll_rootView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissShareWindow();
            }
        });

        umShareListener = new MyUMShareListener(activity) {
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

        shareLayout.setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0x00000000);
        shareLayout.setBackgroundDrawable(dw);
        //设置分享按钮
        setShareBtn(activity, shareBean, o, rootView, observerData);

        int statuBarHeight = SystemUtil.getStatuBarHeight(activity);
        rootView.setPadding(0, 0, 0, statuBarHeight);
        shareLayout.showAtLocation(view, Gravity.BOTTOM, 0, 0);

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
                                       String thumb, SHARE_MEDIA share_media) {
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
        }
    }

    /**
     * 纯图片分享
     *
     * @param activity
     * @param bitmap      分享图片
     * @param share_media 分享平台
     */
    public static void setShareContent(Activity activity, Bitmap
            bitmap, SHARE_MEDIA share_media) {
        try {
            application = activity.getApplication();
            UMImage urlImage;
            if (null != bitmap) {
                urlImage = new UMImage(activity, bitmap);
            } else {
                urlImage = new UMImage(activity, R.mipmap.ic_launcher);
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
        dismissLoadView((BaseActivity) context);
        UMShareAPI.get(context).onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 设置分享按钮
     *
     * @param activity
     * @param shareBean
     * @param o
     * @param rootView
     */
    private static void setShareBtn(final BaseActivity activity, final ShareJson shareBean, final Object o, final
    View rootView, final ObserverData observerData) {

        final UMShareAPI umShareAPI = UMShareAPI.get(activity);
        RecyclerView rvShare = (RecyclerView) rootView.findViewById(R.id.rv_share);
        RecyclerView rvFunction = (RecyclerView) rootView.findViewById(R.id.rv_function);
        View line = rootView.findViewById(R.id.v_line);

        LinearLayoutManager manager = new LinearLayoutManager(activity);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        LinearLayoutManager manager2 = new LinearLayoutManager(activity);
        manager2.setOrientation(LinearLayoutManager.HORIZONTAL);

        rvShare.setLayoutManager(manager);
        rvFunction.setLayoutManager(manager2);

        final List<ShareBtnJson> list = new ArrayList<>();
        list.add(new ShareBtnJson(R.mipmap.icon_share_qyq, "朋友圈"));
        list.add(new ShareBtnJson(R.mipmap.icon_share_wx, "微信好友"));
        list.add(new ShareBtnJson(R.mipmap.icon_share_sina, "新浪"));
        list.add(new ShareBtnJson(R.mipmap.icon_share_qq, "QQ"));
        list.add(new ShareBtnJson(R.mipmap.icon_share_zone, "QQ空间"));

        FunctionAdapter functionAdapter = new FunctionAdapter(list, activity);
        rvShare.setAdapter(functionAdapter);

        List<ShareBtnJson> list2 = new ArrayList();
        list2.add(new ShareBtnJson(R.mipmap.icon_share_link, "复制链接"));
        list2.add(new ShareBtnJson(R.drawable.sel_share_collect, "收藏", shareBean.isFavor()));
        list2.add(new ShareBtnJson(R.drawable.sel_share_ding, "赞", shareBean.isGood()));
        list2.add(new ShareBtnJson(R.mipmap.icon_share_close, "取消"));

        final FunctionAdapter functionAdapter2 = new FunctionAdapter(list2, activity);
        rvFunction.setAdapter(functionAdapter2);

        boolean isHq = false;

        if (shareBean.getType() == null)
            shareBean.setType(TYPE_DEFAULT);

        switch (shareBean.getType()) {
            case TYPE_DEFAULT:
                line.setVisibility(View.GONE);
                rvFunction.setVisibility(View.GONE);
                isHq = false;
                break;
            case TYPE_MARKET:
                isHq = true;
                line.setVisibility(View.GONE);
                rvFunction.setVisibility(View.GONE);
                break;
            case TYPE_VIDEO:
                line.setVisibility(View.VISIBLE);
                rvFunction.setVisibility(View.VISIBLE);
                isHq = false;
                break;
        }

        final boolean finalIsHq = isHq;
        functionAdapter.setOnClickListener(new FunctionAdapter.OnClickListener() {
            @Override
            public void onClick(View view, int position) {
                switch (position) {
                    case 0:
                        //朋友圈
                        if (umShareAPI.isInstall(activity, SHARE_MEDIA.WEIXIN_CIRCLE)) {
                            if (finalIsHq) {
                                setShareContent(activity, shareBean.getBitmap(), SHARE_MEDIA
                                        .WEIXIN_CIRCLE);
                            } else {
                                setShareContent(activity, shareBean.getTitle(), shareBean.getShareUrl(), shareBean.getDiscription(),
                                        shareBean.getThumb(), SHARE_MEDIA.WEIXIN_CIRCLE);
                            }
                        } else {
                            ToastView.makeText3(activity, "未安装微信");
                        }
                        break;
                    case 1:
                        //微信
                        if (umShareAPI.isInstall(activity, SHARE_MEDIA.WEIXIN)) {
                            if (finalIsHq) {
                                setShareContent(activity, shareBean.getBitmap(), SHARE_MEDIA.WEIXIN);
                            } else {
                                setShareContent(activity, shareBean.getTitle(), shareBean.getShareUrl(), shareBean.getDiscription(),
                                        shareBean.getThumb(), SHARE_MEDIA.WEIXIN);
                            }
                        } else {
                            ToastView.makeText3(activity, "未安装微信");
                        }
                        break;
                    case 2:
                        //新浪
                        if (finalIsHq) {
                            setShareContent(activity, shareBean.getBitmap(), SHARE_MEDIA.SINA);
                        } else {
                            setShareContent(activity, shareBean.getTitle(), shareBean.getShareUrl(), shareBean.getDiscription(),
                                    shareBean.getThumb(), SHARE_MEDIA.SINA);
                        }
                        break;
                    case 3:
                        //QQ
                        if (umShareAPI.isInstall(activity, SHARE_MEDIA.QQ)) {
                            if (finalIsHq) {
                                setShareContent(activity, shareBean.getBitmap(), SHARE_MEDIA.QQ);
                            } else {
                                setShareContent(activity, shareBean.getTitle(), shareBean.getShareUrl(), shareBean.getDiscription(),
                                        shareBean.getThumb(), SHARE_MEDIA.QQ);
                            }
                        } else {
                            ToastView.makeText3(activity, "未安装QQ");
                        }
                        break;
                    case 4:
                        //QQ空间
                        if (umShareAPI.isInstall(activity, SHARE_MEDIA.QZONE)) {
                            if (finalIsHq) {
                                setShareContent(activity, shareBean.getBitmap(), SHARE_MEDIA.QZONE);
                            } else {
                                setShareContent(activity, shareBean.getTitle(), shareBean.getShareUrl(), shareBean.getDiscription(),
                                        shareBean.getThumb(), SHARE_MEDIA.QZONE);
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
            public void onClick(View view, final int position) {
                switch (position) {
                    case 0:
                        //复制链接
                        try {
                            //获取剪切板服务
                            ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context
                                    .CLIPBOARD_SERVICE);
                            //然后把数据放在ClipData对象中
//                            ClipData clip = ClipData.newUri(activity.getContentResolver(), "URI", Uri.parse(shareBean.getShareUrl()));
                            ClipData clip = ClipData.newPlainText("simple text", shareBean.getShareUrl());
                            //把clip对象放在剪贴板中
                            clipboard.setPrimaryClip(clip);
                            ToastView.makeText3(activity, "链接复制成功");
                        } catch (Exception e) {
                            e.printStackTrace();
                            ToastView.makeText3(activity, "复制链接失败，请重试");
                        }
                        try {
                            dismissShareWindow();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 1:
                        //收藏
                        if (functionAdapter2.getBtnStatus(position)) {
                            CollectUtils.unCollect(activity, VarConstant.COLLECT_TYPE_VIDEO, o,
                                    observerData, new
                                            ObserverData<Boolean>() {
                                                @Override
                                                public void callback(Boolean o) {
                                                    //改变umeng面板收藏按钮状态
                                                    functionAdapter2.changeStatus(position, o);
                                                }

                                                @Override
                                                public void onError(Exception e) {
                                                    ToastView.makeText3(activity, "收藏失败");
                                                }
                                            });
                        } else
                            CollectUtils.collect(activity, VarConstant.COLLECT_TYPE_VIDEO, o,
                                    observerData, new
                                            ObserverData<Boolean>() {
                                                @Override
                                                public void callback(Boolean o) {
                                                    //改变umeng面板收藏按钮状态
                                                    functionAdapter2.changeStatus(position, o);
                                                }

                                                @Override
                                                public void onError(Exception e) {
                                                    ToastView.makeText3(activity, "收藏失败");
                                                }
                                            });
                        break;
                    case 2:
                        //赞
                        if (functionAdapter2.getBtnStatus(position)) {
                            ToastView.makeText3(activity, "已点赞");
                            return;
                        } else
                            GoodUtils.addGood(activity, shareBean.getId(), shareBean.getGoodType(), observerData, new
                                    ObserverData<Boolean>() {

                                        @Override
                                        public void callback(Boolean aBoolean) {
                                            //改变umeng面板点赞按钮状态
                                            functionAdapter2.changeStatus(position, aBoolean);
                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            ToastView.makeText3(activity, "点赞失败");
                                        }
                                    });
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
            }
        });
    }

    /**
     * 隐藏分享框
     */
    public static void dismissShareWindow() {
        if (shareLayout != null && shareLayout.isShowing()) {
            shareLayout.dismiss();
        }
    }

    /**
     * 显示加载动画
     */
    public static void showLoadView(BaseActivity activity) {
        activity.showWaitDialog(null);
    }

    /**
     * 隐藏加载动画
     */
    public static void dismissLoadView(BaseActivity activity) {
        activity.dismissWaitDialog();
    }

    static class MyUMShareListener implements UMShareListener {

        private BaseActivity baseActivity;

        public MyUMShareListener(BaseActivity baseActivity) {
            this.baseActivity = baseActivity;
        }

        @Override
        public void onStart(SHARE_MEDIA share_media) {
            showLoadView(baseActivity);
        }

        @Override
        public void onResult(SHARE_MEDIA share_media) {
            dismissLoadView(baseActivity);
        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
            dismissLoadView(baseActivity);
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {
            dismissLoadView(baseActivity);
        }
    }
}
