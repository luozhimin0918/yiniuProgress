package com.jyh.kxt.index.presenter;

import android.content.Intent;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.base.utils.JumpUtils;
import com.jyh.kxt.index.json.MainInitJson;
import com.jyh.kxt.index.ui.MainActivity;
import com.jyh.kxt.index.ui.WelcomeActivity;
import com.library.util.SPUtils;

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
    public boolean isClickToWebAd = false;

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
                if (loadAd != null) {
                    String adImageUrl = SPUtils.getString(mContext, SpConstant.AD_IMAGE_URL);

                    if (loadAd.getPicture() != null &&
                            !"".equals(adImageUrl) &&
                            adImageUrl.equals(loadAd.getPicture())) {

                        welcomeActivity.ivWelcome.setVisibility(View.VISIBLE);
                        welcomeActivity.tvAdvertTime.setVisibility(View.VISIBLE);

                        Glide.with(welcomeActivity)
                                .load(adImageUrl)
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .into(welcomeActivity.ivWelcome);

                        welcomeActivity.ivWelcome.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                isClickToWebAd = true;

                                startToActivity(MainActivity.class);
                                JumpUtils.jump(welcomeActivity,
                                        loadAd,
                                        loadAd.getHref());
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
        Observable<String> stringObservable = Observable.create(new Observable.OnSubscribe<String>() {
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
        });
        stringObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        if (!isClickToWebAd) {
                            startToActivity(MainActivity.class);
                        }
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
//        request.doGet(HttpConstant.INDEX_MAIN, jsonParam, new HttpListener<List<TypeDataJson>>() {
//            @Override
//            protected void onResponse(List<TypeDataJson> mTypeDataList) {
//                PreloadIndex.getInstance().setTypeDataList(mTypeDataList);
//                EventBus.getDefault().post(new EventBusClass(EventBusClass.EVENT_REQUEST_MAIN_INIT, mTypeDataList));
//            }
//
//            @Override
//            protected void onErrorResponse(VolleyError error) {
//                super.onErrorResponse(error);
//                EventBus.getDefault().post(new EventBusClass(EventBusClass.EVENT_REQUEST_MAIN_INIT, null));
//            }
//        });
    }
}
