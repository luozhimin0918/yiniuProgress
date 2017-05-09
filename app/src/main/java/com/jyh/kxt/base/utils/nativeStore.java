package com.jyh.kxt.base.utils;

import android.content.Context;

import com.jyh.kxt.base.constant.SpConstant;
import com.library.util.SPUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Mr'Dai on 2017/5/5.
 * 本地变量值存储
 */

public class NativeStore {
    private static NativeStore nativeStore;

    public static NativeStore getInstance(Context mContext) {
        if (nativeStore == null) {
            nativeStore = new NativeStore(mContext);
        }
        return nativeStore;
    }


    public NativeStore(Context mContext) {
        initThumbItemSet(mContext);
    }

    /**
     * 视屏列表点赞, 详细内容点赞等
     * 点赞的ID
     */
    private Set<String> thumbItemSet;

    public Set<String> initThumbItemSet(Context mContext) {
        thumbItemSet = SPUtils.getStringSet(mContext, SpConstant.THUMB_ITEM_SET);
        if (thumbItemSet == null) {
            thumbItemSet = new HashSet<>();
        }
        return thumbItemSet;
    }

    public void addThumbID(Context mContext, int id) {
        thumbItemSet.add(String.valueOf(id));
        SPUtils.save(mContext, SpConstant.THUMB_ITEM_SET, thumbItemSet);
    }

    public void removeThumbId(Context mContext, int id) {
        thumbItemSet.remove(String.valueOf(id));
        SPUtils.save(mContext, SpConstant.THUMB_ITEM_SET, thumbItemSet);
    }

    public boolean isThumbSucceed(int id) {
        return thumbItemSet.contains(String.valueOf(id));
    }
}
