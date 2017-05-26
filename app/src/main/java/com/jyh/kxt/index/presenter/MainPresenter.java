package com.jyh.kxt.index.presenter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateOvershootInterpolator;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.base.utils.JumpUtils;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.index.json.MainInitJson;
import com.jyh.kxt.index.json.SingleThreadJson;
import com.jyh.kxt.index.ui.MainActivity;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;
import com.library.base.http.VolleySyncHttp;
import com.library.util.FileUtils;
import com.library.util.LogUtil;
import com.library.util.SPUtils;
import com.library.util.disklrucache.DiskLruCacheUtils;
import com.library.widget.window.ToastView;

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
               /* try {
                    Thread.sleep(1 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/

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

        String webPage = FileUtils.getWebPage(mContext, indexAd.getHref());

        if (webPage == null) {
            //去读取webView 的文件缓存  如果存在并且没有过期  则显示
            VolleyRequest volleyRequest = new VolleyRequest(mContext, mQueue);
            volleyRequest.setDefaultDecode(false);
            volleyRequest.doGet(indexAd.getHref(), new HttpListener<String>() {
                @Override
                protected void onResponse(String htmlWeb) {
                    LogUtil.e(LogUtil.TAG, "onResponse() called with: htmlWeb = [" + htmlWeb + "]");
                    FileUtils.saveWebPage(mContext, indexAd.getHref(), htmlWeb);
                }

                @Override
                protected void onErrorResponse(VolleyError error) {
                    super.onErrorResponse(error);
                }
            });
        } else {
            View contentView = LayoutInflater.from(mContext).inflate(R.layout.pop_index_ad, null);
            final PopupWindow popWnd = new PopupWindow(mContext);
            popWnd.setContentView(contentView);
            popWnd.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            popWnd.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
            popWnd.setFocusable(true);

            popWnd.setOutsideTouchable(true);
            popWnd.showAtLocation(contentView, Gravity.CENTER, 0, 0);

            WindowManager.LayoutParams lp = mMainActivity.getWindow().getAttributes();
            lp.alpha = 0.5f;
            mMainActivity.getWindow().setAttributes(lp);

            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.pop_window1_in);
            animation.setDuration(1 * 1000);
            animation.setInterpolator(new AnticipateOvershootInterpolator());
            contentView.startAnimation(animation);


            View topView = contentView.findViewById(R.id.tv_top);
            View bottomView = contentView.findViewById(R.id.tv_bottom);


            if ("normal".equals(indexAd.getType()) || "download".equals(indexAd.getType())) {

                topView.setVisibility(View.VISIBLE);
                bottomView.setVisibility(View.VISIBLE);

                ImageView imgAdView = (ImageView) contentView.findViewById(R.id.iv_adimg);
                imgAdView.setVisibility(View.VISIBLE);
                String pictureUrl = HttpConstant.IMG_URL + indexAd.getPicture();
                Glide.with(mContext).load(pictureUrl).override(100, 100).into(imgAdView);

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
                WebView wvContent = (WebView) contentView.findViewById(R.id.wv_ad_content);
                wvContent.setVisibility(View.VISIBLE);
                wvContent.loadDataWithBaseURL("", webPage, "text/html", "utf-8", "");
                wvContent.setWebViewClient(new WebViewClient(){
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        JumpUtils.jump(mMainActivity, indexAd, url);
                        popWnd.dismiss();
                        return true;
                    }
                });
            }

            ImageView ivCloseView = (ImageView) contentView.findViewById(R.id.iv_close);
            ivCloseView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popWnd.dismiss();
                }
            });

            popWnd.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    mMainActivity.homeFragment.closePopWindowAdvert();
                    WindowManager.LayoutParams lp = mMainActivity.getWindow().getAttributes();
                    lp.alpha = 1.0f;
                    mMainActivity.getWindow().setAttributes(lp);
                }
            });

            contentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popWnd.dismiss();
                }
            });
        }
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
}
