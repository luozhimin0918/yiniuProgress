package com.jyh.kxt.index.presenter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
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
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.alibaba.fastjson.JSONObject;
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
import com.library.base.http.VolleySyncHttp;
import com.library.util.SPUtils;
import com.library.util.disklrucache.DiskLruCacheUtils;

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

        if ("normal".equals(indexAd.getType())) {
            ImageView imgAdView = (ImageView) contentView.findViewById(R.id.iv_adimg);
            imgAdView.setVisibility(View.VISIBLE);
            String pictureUrl = HttpConstant.IMG_URL + indexAd.getPicture();
            Glide.with(mContext).load(pictureUrl).override(100, 100).into(imgAdView);

            imgAdView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JumpUtils.jump(mMainActivity, indexAd, indexAd.getHref());
                }
            });
        } else if ("download".equals(indexAd.getType())) {

        } else {
            WebView wvContent = (WebView) contentView.findViewById(R.id.wv_ad_content);
            wvContent.setVisibility(View.VISIBLE);
            wvContent.loadUrl(/*indexAd.getHref()*/"https://www.baidu.com/");
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
