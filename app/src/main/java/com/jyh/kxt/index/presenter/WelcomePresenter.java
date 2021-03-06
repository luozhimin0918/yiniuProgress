package com.jyh.kxt.index.presenter;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.base.utils.JumpUtils;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.index.json.MainInitJson;
import com.jyh.kxt.index.json.TypeDataJson;
import com.jyh.kxt.index.ui.CommentListActivity;
import com.jyh.kxt.index.ui.MainActivity;
import com.jyh.kxt.index.ui.WelcomeActivity;
import com.jyh.kxt.user.json.UserJson;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;
import com.library.util.SPUtils;
import com.library.util.SystemUtil;

import java.util.List;

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
    private boolean isShowResidueTime = false;

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
                    int bottomBarHeight=loadAd.getBottom_screen_size();
                    String adImageUrl = SPUtils.getString(mContext, SpConstant.AD_IMAGE_URL);
                    if (loadAd.getPicture() != null &&
                            !"".equals(adImageUrl) &&
                            adImageUrl.equals(loadAd.getPicture())) {

                        welcomeActivity.ivWelcome.setVisibility(View.VISIBLE);
                        if(bottomBarHeight>0){

                            ViewGroup.LayoutParams layoutParams = welcomeActivity.bottomBar.getLayoutParams();
                            layoutParams.height= SystemUtil.dp2px(mContext,bottomBarHeight);
                            welcomeActivity.bottomBar.setLayoutParams(layoutParams);

                        }
//                        Glide.with(welcomeActivity)
//                                .load(adImageUrl)
//                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                                .into(welcomeActivity.ivWelcome);
                        Glide.with(welcomeActivity)
                                .load(adImageUrl)
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .into(new GlideDrawableImageViewTarget(welcomeActivity.ivWelcome) {
                                    @Override
                                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                                        super.onResourceReady(resource, animation);
                                        advertTimeManage();
                                    }

                                    @Override
                                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                        super.onLoadFailed(e, errorDrawable);
                                        delayToMainActivity();
                                    }
                                });

                        welcomeActivity.ivWelcome.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                isClickToWebAd = true;

                                startToActivity(MainActivity.class);
                                JumpUtils.jump(welcomeActivity,
                                        loadAd,
                                        loadAd.getTitle(),
                                        loadAd.getHref());
                            }
                        });
                        return;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        delayToMainActivity();
    }

    public void advertTimeManage() {
        if (isShowResidueTime) {
            return;
        }
        isShowResidueTime = true;

        Observable<String> stringObservable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
//                    Thread.sleep(1500);

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
                            Thread.sleep(1000);
                            subscriber.onNext("跳过" + i + "S");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
                        if (!welcomeActivity.tvAdvertTime.isShown()) {
                            welcomeActivity.tvAdvertTime.setVisibility(View.VISIBLE);
                        }
                        welcomeActivity.tvAdvertTime.setText(t);
                    }
                });
    }


    private void delayToMainActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startToActivity(MainActivity.class);
            }
        },2000);
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

    public void requestMemberInfo() {
        UserJson userInfo = LoginUtils.getUserInfo(mContext);
        if (userInfo == null) {
            return;
        }

        VolleyRequest request = new VolleyRequest(mContext, mQueue);
        JSONObject jsonParam = request.getJsonParam();
        jsonParam.put("uid", userInfo.getUid());
        jsonParam.put("accessToken", userInfo.getToken());

        request.setTag("memberInfo");
        request.doPost(HttpConstant.MEMBER_INFO, jsonParam, new HttpListener<UserJson>() {
            @Override
            protected void onResponse(UserJson mUserJson) {
                LoginUtils.changeUserInfo(mContext, mUserJson);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
            }
        });
    }

    public void requestPreNews() {
        VolleyRequest request = new VolleyRequest(mContext, mQueue);
        request.setTag("prenews");
        request.enablePreCache();
        request.doPost(HttpConstant.INDEX_MAIN, new HttpListener<List<TypeDataJson>>() {
            @Override
            protected void onResponse(List<TypeDataJson> preCache) {

            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
            }
        });
    }
}
