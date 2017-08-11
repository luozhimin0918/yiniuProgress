package com.jyh.kxt.index.adapter;

import android.content.Context;

import com.jyh.kxt.av.adapter.CommentAdapter;
import com.jyh.kxt.av.json.CommentBean;
import com.jyh.kxt.base.BasePresenter;

import java.util.List;

/**
 * Created by Mr'Dai on 2017/8/11.
 */

public class CommentVideoAdapter extends CommentAdapter {

    public CommentVideoAdapter(Context mContext, List<CommentBean> videoDetailList, BasePresenter basePresenter) {
        super(mContext, videoDetailList, basePresenter);
    }
}