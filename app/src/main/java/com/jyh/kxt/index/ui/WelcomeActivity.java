package com.jyh.kxt.index.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;

import butterknife.BindView;

/**
 * 启动-欢迎界面
 */
public class WelcomeActivity extends BaseActivity {

    @BindView(R.id.iv_welcome) ImageView ivWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Glide.with(this).load(R.raw.qidong).listener(new RequestListener<Integer, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, Integer model, Target<GlideDrawable> target, boolean
                    isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, Integer model, Target<GlideDrawable> target,
                                           boolean isFromMemoryCache, boolean isFirstResource) {
                int duration = 0;
                GifDrawable drawable = (GifDrawable) resource;
                GifDecoder decoder = drawable.getDecoder();
                for (int i = 0; i < drawable.getFrameCount(); i++) {
                    duration += decoder.getDelay(i);
                }
                ivWelcome.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        WelcomeActivity.this.finish();
                        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                    }
                }, duration + 1000);

                return false;
            }
        }).into(new GlideDrawableImageViewTarget(ivWelcome, 1));
    }
}
