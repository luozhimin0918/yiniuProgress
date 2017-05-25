package com.jyh.kxt.base.utils;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.module.GlideModule;

/**
 * Created by Mr'Dai on 2017/3/17.
 */

public class MyGlideModule implements GlideModule {


    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
//        ViewTarget.setTagId(R.id.glide_tag_id);
        int maxMemory = (int) Runtime.getRuntime().maxMemory();//获取系统分配给应用的总内存大小
        int memoryCacheSize = maxMemory / 20;//设置图片内存缓存占用八分之一
        builder.setMemoryCache(new LruResourceCache(memoryCacheSize));//设置内存缓存大小
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
    }
}