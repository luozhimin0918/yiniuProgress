package com.jyh.kxt.index.presenter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.RequestQueue;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.index.json.ConfigJson;
import com.jyh.kxt.index.json.LoadADJson;
import com.jyh.kxt.index.ui.MainActivity;
import com.jyh.kxt.index.ui.WebActivity;
import com.jyh.kxt.index.ui.WelcomeActivity;
import com.library.base.http.VolleyRequest;
import com.library.util.SPUtils;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/20.
 */

public class WelcomePresenter extends BasePresenter {

    @BindObject WelcomeActivity welcomeActivity;
    private RequestQueue queue;
    private VolleyRequest request;

    private boolean isShowAd = false;

    public WelcomePresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void initConfig() {
        queue = welcomeActivity.getQueue();
        if (request == null) {
            request = new VolleyRequest(mContext, queue);
        }
        initView();

        handler.sendEmptyMessage(1);
        /*request.doGet(HttpConstant.CONFIG, new HttpListener<String>() {
            @Override
            protected void onResponse(String configStr) {
                if (configStr != null) {
                    SPUtils.save(mContext, SpConstant.CONFIG, configStr);
                    if (!isShowAd) {
                        handler.sendEmptyMessage(1);
                    }
                }
            }

            @Override
            protected void onErrorResponse(VolleyError error) {

                super.onErrorResponse(error);
                if (!isShowAd) {
                    handler.sendEmptyMessage(1);
                } else {

                }
            }
        });*/
    }

    public void initView() {
        String configStr = SPUtils.getString(mContext, SpConstant.CONFIG);
        if (TextUtils.isEmpty(configStr)) {
            isShowAd = false;
            return;
        }
        ConfigJson config = JSONObject.parseObject(configStr, ConfigJson.class);
        if (config == null) {
            isShowAd = false;
            return;
        }
        final LoadADJson load_ad = config.getLoad_ad();
        if (load_ad == null) {
            isShowAd = false;
            return;
        }

        String picture = load_ad.getPicture();
        final String href = load_ad.getHref();
        final String o_action = load_ad.getO_action();
        final String o_class = load_ad.getO_class();
        final String o_id = load_ad.getO_id();

        if (picture != null) {
            isShowAd = true;
            int showTime = 5;
            try {
                showTime = Integer.parseInt(load_ad.getShowTime());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            final int finalShowTime = showTime;
            Glide.with(mContext).load(picture).listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean
                        isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target,
                                               boolean
                                                       isFromMemoryCache, boolean isFirstResource) {
                    handler.sendEmptyMessageDelayed(1, finalShowTime * 1000);
                    return false;
                }

            }).into(new GlideDrawableImageViewTarget(welcomeActivity.ivWelcome, 1));
            welcomeActivity.ivWelcome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message message = new Message();
                    message.what = 2;
                    Bundle bundle = new Bundle();
                    bundle.putString(IntentConstant.O_CLASS, o_class);
                    bundle.putString(IntentConstant.O_ACTION, o_action);
                    bundle.putString(IntentConstant.O_ID, o_id);
                    bundle.putString(IntentConstant.WEBURL, href);
                    bundle.putSerializable(IntentConstant.CONFIG, load_ad);
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
            });
        } else {
            isShowAd = false;
        }
    }

    /**
     * 广告跳转
     *
     * @param href     跳转url
     * @param load_ad
     * @param o_class  跳转模块
     * @param o_action 跳转子模块
     * @param o_id     跳转详情id
     */
    private void jumpAD(String href, LoadADJson load_ad, String o_class, String o_action, String o_id) {
        Intent intent = null;
        if (TextUtils.isEmpty(href)) {
            //跳转至应用中的某一模块
            intent = new Intent(mContext, MainActivity.class);
            intent.putExtra(IntentConstant.O_CLASS, o_class);
            intent.putExtra(IntentConstant.O_ACTION, o_action);
            intent.putExtra(IntentConstant.O_ID, o_id);
        } else {
            //跳转至广告页
            intent = new Intent(mContext, WebActivity.class);
            intent.putExtra(IntentConstant.NAME, load_ad.getTitle());
            intent.putExtra(IntentConstant.WEBURL, href);
            intent.putExtra(IntentConstant.SOURCE, WelcomeActivity.class.getName());
        }
        mContext.startActivity(intent);
        welcomeActivity.finish();
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    //跳转至主页
                    welcomeActivity.finish();
                    mContext.startActivity(new Intent(mContext, MainActivity.class));
                    break;
                case 2:
                    //跳转至广告页
                    handler.removeMessages(1);
                    Bundle bundle = msg.getData();
                    jumpAD(bundle.getString(IntentConstant.WEBURL), (LoadADJson) bundle.getSerializable
                            (IntentConstant.CONFIG), bundle
                            .getString
                                    (IntentConstant.O_CLASS), bundle.getString(IntentConstant.O_ACTION), bundle
                            .getString(IntentConstant
                                    .O_ID));
                    break;
            }
            return false;
        }
    });

    public void onDestory() {
        handler.removeCallbacksAndMessages(null);
        handler = null;
    }
}
