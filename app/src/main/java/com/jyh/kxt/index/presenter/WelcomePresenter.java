package com.jyh.kxt.index.presenter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.index.json.MainInitJson;
import com.jyh.kxt.index.ui.MainActivity;
import com.jyh.kxt.index.ui.WebActivity;
import com.jyh.kxt.index.ui.WelcomeActivity;
import com.library.util.SPUtils;
import com.library.util.disklrucache.DiskLruCacheUtils;

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
                                Intent intent = null;
                                if (TextUtils.isEmpty(loadAd.getHref())) {
                                    //跳转至应用中的某一模块
                                    intent = new Intent(mContext, MainActivity.class);
                                    intent.putExtra(IntentConstant.O_CLASS, loadAd.getO_class());
                                    intent.putExtra(IntentConstant.O_ACTION, loadAd.getO_action());
                                    intent.putExtra(IntentConstant.O_ID, loadAd.getO_id());
                                } else {
                                    //跳转至广告页
                                    intent = new Intent(mContext, WebActivity.class);
                                    intent.putExtra(IntentConstant.NAME, loadAd.getTitle());
                                    intent.putExtra(IntentConstant.WEBURL, loadAd.getHref());
                                    intent.putExtra(IntentConstant.SOURCE, WelcomeActivity.class.getName());
                                }
                                mContext.startActivity(intent);
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
                for (int i = 5; i > 0; i--) {
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
        Glide
                .with(welcomeActivity)
                .load(R.raw.qidong)
                .listener(new RequestListener<Integer, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e,
                                               Integer model,
                                               Target<GlideDrawable> target,
                                               boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource,
                                                   Integer model,
                                                   Target<GlideDrawable> target,
                                                   boolean isFromMemoryCache,
                                                   boolean isFirstResource) {

                        int duration = 0;
                        GifDrawable drawable = (GifDrawable) resource;
                        GifDecoder decoder = drawable.getDecoder();
                        for (int i = 0; i < drawable.getFrameCount(); i++) {
                            duration += decoder.getDelay(i);
                        }

                        welcomeActivity.ivWelcome.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startToActivity(MainActivity.class);
                            }
                        }, duration - 300);

                        return false;
                    }
                }).into(new GlideDrawableImageViewTarget(welcomeActivity.ivWelcome, 1));
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
}
