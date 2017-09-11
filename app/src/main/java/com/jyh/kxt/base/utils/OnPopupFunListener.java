package com.jyh.kxt.base.utils;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.jyh.kxt.base.json.ShareItemJson;

public interface OnPopupFunListener {
    void onClickItem(View itemView, ShareItemJson mShareItemJson, RecyclerView.Adapter recyclerAdapter);
}
