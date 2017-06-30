package com.jyh.kxt.index.presenter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.base.utils.JumpUtils;
import com.jyh.kxt.index.json.MainInitJson;
import com.jyh.kxt.index.ui.MainActivity;
import com.jyh.kxt.index.ui.WebActivity;
import com.jyh.kxt.index.ui.WelcomeActivity;
import com.library.util.LogUtil;
import com.library.util.SPUtils;
import com.library.util.disklrucache.DiskLruCacheUtils;

import java.io.IOException;

import pl.droidsonroids.gif.AnimationListener;
import pl.droidsonroids.gif.GifDrawable;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/20.
 */

public class WelcomePresenter extends BasePresenter {

    @BindObject WelcomeActivity welcomeActivity;

    public WelcomePresenter(IBaseView iBaseView) {
        super(iBaseView);
    }


    public void checkIsShowAdvert() {
        try {
            //网络请求,如果有图片连接并且图片已经存储在本地了 则显示
            String indexConfig = SPUtils.getString(mContext, SpConstant.INIT_LOAD_APP_CONFIG);
            if (!"".equals(indexConfig)) {
                MainInitJson initConfig = JSONObject.parseObject(indexConfig, MainInitJson.class);
                final MainInitJson.LoadAdBean loadAd = initConfig.getLoad_ad();

                DiskLruCacheUtils instance = DiskLruCacheUtils.getInstance(mContext);
                if (loadAd.getPicture() != null && !"".equals(loadAd.getHref())) {

                    String pictureUrl = HttpConstant.IMG_URL + loadAd.getPicture();

                    Bitmap diskLruCache = instance.getDiskLruCache(pictureUrl);
                    if (diskLruCache != null && !diskLruCache.isRecycled()) {
                        welcomeActivity.ivWelcome.setVisibility(View.VISIBLE);
                        welcomeActivity.ivWelcome.setImageBitmap(diskLruCache);

                        welcomeActivity.tvAdvertTime.setVisibility(View.VISIBLE);


                        welcomeActivity.ivWelcome.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                Intent intent = null;
//                                if (TextUtils.isEmpty(loadAd.getHref())) {
//                                    //跳转至应用中的某一模块
//                                    intent = new Intent(mContext, MainActivity.class);
//                                    intent.putExtra(IntentConstant.O_CLASS, loadAd.getO_class());
//                                    intent.putExtra(IntentConstant.O_ACTION, loadAd.getO_action());
//                                    intent.putExtra(IntentConstant.O_ID, loadAd.getO_id());
//                                } else {
//                                    //跳转至广告页
//                                    intent = new Intent(mContext, WebActivity.class);
//                                    intent.putExtra(IntentConstant.NAME, loadAd.getTitle());
//                                    intent.putExtra(IntentConstant.WEBURL, loadAd.getHref());
//                                    intent.putExtra(IntentConstant.SOURCE, WelcomeActivity.class.getName());
//                                }
//                                mContext.startActivity(intent);
                                JumpUtils.jump(welcomeActivity,loadAd.getO_class(),loadAd.getO_action(),loadAd.getO_id(),loadAd.getHref());
                            }
                        });
                        advertTimeManage();
                        return;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        showGif();
    }

    public void advertTimeManage() {

        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                int showTime = 0;
                try {

                    String appConfig = SPUtils.getString(mContext, SpConstant.INIT_LOAD_APP_CONFIG);
                    MainInitJson mainInitJson = JSONObject.parseObject(appConfig, MainInitJson.class);
                    showTime = mainInitJson.getLoad_ad().getShowTime();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                showTime = showTime == 0 ? 5 : showTime;

                for (int i = showTime; i > 0; i--) {
                    try {
                        subscriber.onNext("跳过" + i + "S");
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        startToActivity(MainActivity.class);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(String t) {
                        welcomeActivity.tvAdvertTime.setText(t);
                    }
                });
    }


    private void showGif() {
        welcomeActivity.ivWelcome.setVisibility(View.VISIBLE);

        GifDrawable drawable = null;
        try {
            drawable = new GifDrawable(mContext.getResources(), R.raw.qidong);
            welcomeActivity.ivWelcome.setImageDrawable(drawable);
            welcomeActivity.ivWelcome.getDrawable();

            drawable.setLoopCount(1);
            drawable.addAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationCompleted(int loopNumber) {
                    startToActivity(MainActivity.class);
                }
            });
            drawable.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

       /* Glide
                .with(welcomeActivity)
                .load(R.raw.qidong)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE).into(
                new GlideDrawableImageViewTarget(welcomeActivity.ivWelcome, 1) {
                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                    }

                    @Override
                    public void onDestroy() {
                        super.onDestroy();
                    }

                    @Override
                    public void onStop() {
                        super.onStop();
                    }

                    @Override
                    public void onLoadCleared(Drawable placeholder) {
                        super.onLoadCleared(placeholder);
                    }

                    @Override
                    public void onResourceReady(GlideDrawable resource,
                                                GlideAnimation<? super GlideDrawable> animation) {
                        super.onResourceReady(resource, animation);

//                        int duration = 0;
//                        GifDrawable drawable = (GifDrawable) resource;
//                        GifDecoder decoder = drawable.getDecoder();
//                        for (int i = 0; i < drawable.getFrameCount(); i++) {
//                            duration += decoder.getDelay(i);
//                        }
//
//                        welcomeActivity.ivWelcome.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                startToActivity(MainActivity.class);
//                            }
//                        }, duration*//* - 300*//*);
                    }
                });*/
    }

    public void startToActivity(Class<?> activityClass) {
        Intent intent = new Intent(mContext, activityClass);
        mContext.startActivity(intent);
        welcomeActivity.finish();
    }

    /**
     * 第一次安装初始化默认值
     */
    public void initSharedPreferences() {
        Boolean initSp = SPUtils.getBoolean(mContext, SpConstant.INIT_SP);
        if (!initSp) {
            SPUtils.save(mContext, SpConstant.INIT_SP, true);

            //初始化默认值
            SPUtils.save(mContext, SpConstant.SETTING_PUSH, true);
            SPUtils.save(mContext, SpConstant.SETTING_SOUND, true);
        }
    }

    public void requestMainData() {
//        VolleyRequest request = new VolleyRequest(mContext, mQueue);
//        JSONObject jsonParam = request.getJsonParam();
//        request.setTag("mainRequest");
//        request.doGet(HttpConstant.INDEX_MAIN, jsonParam, new HttpListener<Object>() {
//            @Override
//            protected void onResponse(Object o) {
//
//            }
//
//            @Override
//            protected void onErrorResponse(VolleyError error) {
//                super.onErrorResponse(error);
//            }
//        });
    }
}
