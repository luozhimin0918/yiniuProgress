package com.jyh.kxt.index.presenter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateOvershootInterpolator;
import android.webkit.JavascriptInterface;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.base.utils.JumpUtils;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.base.widget.night.ThemeUtil;
import com.jyh.kxt.index.json.MainInitJson;
import com.jyh.kxt.index.json.SingleThreadJson;
import com.jyh.kxt.index.ui.MainActivity;
import com.jyh.kxt.user.ui.LoginOrRegisterActivity;
import com.library.base.http.VolleySyncHttp;
import com.library.util.SPUtils;
import com.library.util.disklrucache.DiskLruCacheUtils;
import com.library.widget.window.ToastView;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class MainPresenter extends BasePresenter {

    @BindObject MainActivity mMainActivity;
    private AlertDialog logoutDialog;

    public MainPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    /**
     * 切换Fragment
     *
     * @param toFragment
     */
    public void switchToFragment(BaseFragment toFragment) {
        BaseFragment currentFragment = mMainActivity.currentFragment;
        //当前用户调用显示

        if (toFragment != currentFragment) {

            MainActivity mainActivity = (MainActivity) mContext;
            FragmentTransaction transaction = mainActivity.getSupportFragmentManager().beginTransaction();

            if (currentFragment == null) {
                if (!toFragment.isAdded()) {
                    transaction.add(R.id.center_content, toFragment);
                }
            } else {
                if (!toFragment.isAdded()) {
                    transaction.hide(currentFragment).add(R.id.center_content, toFragment);
                } else {
                    transaction.hide(currentFragment).show(toFragment);
                }
            }

            toFragment.onResume();

            mMainActivity.currentFragment = toFragment;
            transaction.commitAllowingStateLoss();
        }
    }

    /**
     * 发送一个延迟请求, 放一些不重要的 但是必须要请求的网络信息
     */
    public void postDelayRequest() {

        Observable.create(new Observable.OnSubscribe<SingleThreadJson>() {
            @Override
            public void call(Subscriber<? super SingleThreadJson> subscriber) {
                try {
                    Thread.sleep(3 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                VolleySyncHttp volleySyncHttp = VolleySyncHttp.getInstance();
                try {
                    String result1 = volleySyncHttp.syncGet(mQueue, HttpConstant.CONFIG);
                    SingleThreadJson singleThreadJson = new SingleThreadJson(0, result1);
                    subscriber.onNext(singleThreadJson);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<SingleThreadJson>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(SingleThreadJson jsonStr) {
                        switch (jsonStr.code) {
                            case 0: //初始配置信息
                                initLoadAppConfig(jsonStr.json);
                                break;
                            default:
                                break;
                        }
                    }
                });
    }

    /**
     * 初始加载App 的配置信息
     *
     * @param jsonStr
     */
    private void initLoadAppConfig(String jsonStr) {
        SPUtils.save(mContext, SpConstant.INIT_LOAD_APP_CONFIG, jsonStr);

        MainInitJson mainInitJson = JSONObject.parseObject(jsonStr, MainInitJson.class);
        MainInitJson.IndexAdBean indexAd = mainInitJson.getIndex_ad();
        showPopAdvertisement(indexAd);

        MainInitJson.LoadAdBean loadAd = mainInitJson.getLoad_ad();
        String pictureUrl = HttpConstant.IMG_URL + loadAd.getPicture();

        Bitmap diskLruCache1 = DiskLruCacheUtils.getInstance(mContext).getDiskLruCache(pictureUrl);
        if (diskLruCache1 == null) {
            Glide.with(mContext)
                    .load(pictureUrl)
                    .asBitmap()
                    .into(new MySimpleTarget(mContext, pictureUrl));
        }
    }


    public void showPopAdvertisement(final MainInitJson.IndexAdBean indexAd) {

        /*final PopupWindow popWnd = new PopupWindow(mContext);
        popWnd.setContentView(contentView);
        popWnd.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popWnd.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        popWnd.setFocusable(true);

        popWnd.setOutsideTouchable(true);
        popWnd.showAtLocation(contentView, Gravity.CENTER, 0, 0);*/


      /*  final PopupUtil popWnd = new PopupUtil(mMainActivity);
        View contentView = popWnd.createPopupView(R.layout.pop_index_ad);
        PopupUtil.Config config = new PopupUtil.Config();

        config.outsideTouchable = true;
        config.alpha = 0.5f;
        config.bgColor = 0X00000000;

        config.animationStyle = R.style.PopupWindow_Style2;
        config.width = WindowManager.LayoutParams.MATCH_PARENT;
        config.height = WindowManager.LayoutParams.MATCH_PARENT;
        popWnd.setConfig(config);
        popWnd.showAtLocation(mMainActivity.rbHome, Gravity.BOTTOM, 0, 0);*/

        View contentView = LayoutInflater.from(mContext).inflate(R.layout.pop_index_ad, null);

        AlertDialog.Builder advertBuilderDialog = new AlertDialog.Builder(mContext, R.style.dialog3);
        final AlertDialog alertDialog = advertBuilderDialog.create();
        alertDialog.setView(contentView);
        alertDialog.show();

        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.pop_window1_in);
        animation.setDuration(1 * 1000);
        animation.setInterpolator(new AnticipateOvershootInterpolator());
        contentView.startAnimation(animation);


        View topView = contentView.findViewById(R.id.tv_top);
        View bottomView = contentView.findViewById(R.id.tv_bottom);
        FrameLayout flMengBan = (FrameLayout) contentView.findViewById(R.id.fl_mengban);

        ImageView imgAdView = (ImageView) contentView.findViewById(R.id.iv_adimg);
        WebView wvContent = (WebView) contentView.findViewById(R.id.wv_ad_content);

        int theme = ThemeUtil.getAlertTheme(mContext);
        switch (theme) {
            case android.support.v7.appcompat.R.style.Theme_AppCompat_DayNight_Dialog_Alert:
                flMengBan.setBackgroundColor(0x88000000);
                break;
            case android.support.v7.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert:
                flMengBan.setBackgroundColor(0x00000000);
                break;
        }
        if ("normal".equals(indexAd.getType()) || "download".equals(indexAd.getType())) {

            topView.setVisibility(View.VISIBLE);
            bottomView.setVisibility(View.VISIBLE);

            imgAdView.setVisibility(View.VISIBLE);
            String pictureUrl = HttpConstant.IMG_URL + indexAd.getPicture();
            Glide.with(mContext).load(pictureUrl).into(imgAdView);

            topView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (indexAd.getHref() == null || indexAd.getHref().trim().length() == 0) {
                        return;
                    }

                    if ("normal".equals(indexAd.getType())) {
                        JumpUtils.jump(mMainActivity, indexAd, indexAd.getHref());
                    } else {
                        Uri uri = Uri.parse(indexAd.getHref());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        mContext.startActivity(intent);
                    }
                }
            });

            bottomView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String title = indexAd.getTitle();

                    ClipboardManager clipboard = (ClipboardManager) mMainActivity
                            .getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("title", title);
                    clipboard.setPrimaryClip(clip);    //把clip对象放在剪贴板中
                    ToastView.makeText3(mContext, "复制成功");
                }
            });
        } else {
            wvContent.setVisibility(View.VISIBLE);

            WebSettings settings = wvContent.getSettings();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                settings.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            }

            settings.setLoadWithOverviewMode(true);
            settings.setBlockNetworkImage(false);

            settings.setJavaScriptEnabled(true);
            settings.setAppCacheEnabled(true);

            settings.setDefaultTextEncodingName("utf-8");
            settings.setLoadWithOverviewMode(true);

            wvContent.addJavascriptInterface(new getShareInfoInterface(), "shareInfoInterface");

            wvContent.loadUrl(indexAd.getHref());
            wvContent.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    JumpUtils.jump(mMainActivity, indexAd, url);
                    alertDialog.dismiss();
                    return true;
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    view.loadUrl("javascript:function getShareInfo(){" +
                            "var shareVal=document.getElementById(\"webView_share\").innerText;" +
                            "window.shareInfoInterface.getShareInfo(shareVal);" +
                            "}");
                    view.loadUrl("javascript:getShareInfo()");
                    super.onPageFinished(view, url);
                }
            });
        }

        ImageView ivCloseView = (ImageView) contentView.findViewById(R.id.iv_close);
        ivCloseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

//        popWnd.setOnDismissListener(new PopupUtil.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                mMainActivity.homeFragment.closePopWindowAdvert();
//                WindowManager.LayoutParams lp = mMainActivity.getWindow().getAttributes();
//                lp.alpha = 1.0f;
//                mMainActivity.getWindow().setAttributes(lp);
//            }
//        });
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mMainActivity.homeFragment.closePopWindowAdvert();
                WindowManager.LayoutParams lp = mMainActivity.getWindow().getAttributes();
                lp.alpha = 1.0f;
                mMainActivity.getWindow().setAttributes(lp);
            }
        });

        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    /**
     * 退出弹窗
     */
    public void showQuitDialog() {
        if (logoutDialog == null) {
            logoutDialog = new AlertDialog.Builder(mContext)
                    .setTitle("提醒")
                    .setMessage("确认退出当前账号?")
                    .setNegativeButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            LoginUtils.logout(mContext);
                        }
                    }).setPositiveButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create();
        }
        if (logoutDialog.isShowing()) {
            logoutDialog.dismiss();
        }
        logoutDialog.show();
    }

    private AlertDialog loginPop;

    /**
     * 显示登录dialog
     */
    public void showLoginDialog() {
        if (loginPop == null) {
            loginPop = new AlertDialog.Builder(mContext)
                    .setTitle("提醒")
                    .setMessage("请先登录")
                    .setNegativeButton("登录", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mContext.startActivity(new Intent(mContext, LoginOrRegisterActivity.class));
                        }
                    }).setPositiveButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create();
        }
        if (loginPop.isShowing()) {
            loginPop.dismiss();
        }
        loginPop.show();
    }

    /**
     * 缓存图片
     */
    public class MySimpleTarget extends SimpleTarget<Bitmap> {
        private String url;
        private Context context;

        public MySimpleTarget(Context context, String url) {
            this.context = context;
            this.url = url;
        }

        @Override
        public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
            DiskLruCacheUtils.getInstance(context).putDiskLruCache(url, bitmap);
        }
    }

    public class getShareInfoInterface {
        @JavascriptInterface
        public void getShareInfo(String shareInfo) {
//            "id":"2966",
//                    "title":"豹子头理财：原油四连阳多头延续，黄金回落修正1236依旧是关键",
//                    "url":"http://test.kxtadi.kuaixun56.com/webview/view/id/2966",
//                    "create_time":1494901314,
//                    "picture":"http://img.kuaixun360.com/Member/56833/cover/591a607c452ca.png",
//                    "type":"blog"
        }

        @JavascriptInterface
        public void getClickInfo(String clickInfo) {
            JSONObject clickJson = JSON.parseObject(clickInfo);
//            "o_id":"2968",
//                    "o_class":"blog",
//                    "o_action":"detail",
//                    "href":""
            JumpUtils.jump((BaseActivity) mContext, clickJson.getString("o_class"), clickJson.getString("o_action"),
                    clickJson.getString
                            ("o_id"),
                    clickJson.getString("href"));
        }
    }
}
